<template>
  <div class="flex flex-col w-full h-full overflow-hidden">
    <!-- ✅ 상단바 (고정 높이) -->
    <div class="flex-none bg-[#B5CCB7] py-3 px-4 text-lg font-semibold text-gray-900 text-center">
      가족 회원 목록
    </div>

    <!-- ✅ 필터 버튼 추가 -->
    <div class="flex justify-center space-x-2 mt-4">
      <button 
        v-for="option in filterOptions" 
        :key="option.value" 
        @click="selectedFilter = option.value"
        :class="[
          'px-4 py-2 rounded-lg border transition', 
          selectedFilter === option.value ? 'bg-[#4E7351] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
        ]"
      >
        {{ option.label }}
      </button>
    </div>

    <!-- ✅ 스크롤 가능한 본문 -->
    <div class="flex-auto overflow-y-auto px-4 py-2">
      <template v-if="filteredMembers.length > 0">
        <div
          v-for="(member, index) in filteredMembers"
          :key="member.dependencyId" 
          class="flex items-center justify-between py-3 w-full border-b"
        >
          <MemberItem
            :dependencyId="member.dependencyId"
            :name="member.name"
            :role="member.role"
            @deleteMember="confirmDelete"
            class="w-full"
          />
        </div>
      </template>
      <p v-else class="text-center text-gray-500 py-4">등록된 회원이 없습니다.</p>
    </div>

    <div class="flex justify-center mt-4 w-full pb-4">
      <BaseButton
        textColor="text-gray-800"
        size="sm"
        overrideClass="!w-1/2 !px-2 !py-2 !text-sm !bg-gray-200 hover:!bg-gray-300"
        @click="isModalOpen = true"
      >
        인원 추가
      </BaseButton>
    </div>

    <!-- ✅ FamilyAddModal 사용 -->
    <FamilyAddModal :isOpen="isModalOpen" @close="isModalOpen = false" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import MemberItem from "../components/MemberItem.vue";
import BaseButton from "../components/BaseButton.vue";
import FamilyAddModal from "../components/FamilyAddModal.vue";
import { fetchRelationships } from "../api/relationmember"; // 가족 목록 불러오기
import { requestDependencyDelete } from "../api/notify"; // 삭제 요청 API
const members = ref([]);
const isModalOpen = ref(false);
const selectedFilter = ref("all"); // ✅ 선택된 필터 (기본: 전체 보기)

const filterOptions = [
  { label: "전체", value: "all" },
  { label: "보호자", value: "protector" },
  { label: "피보호자", value: "dependent" },
];

// ✅ 가족 목록 불러오기 (보호자/피보호자 포함)
const loadMembers = async () => {
  try {
    const { dependents, protectors } = await fetchRelationships();

    // ✅ 보호자 및 피보호자 데이터를 하나의 리스트로 합침
    members.value = [
      ...dependents.map((item) => ({
        dependencyId: item.dependencyId,
        name: item.dependentName,
        role: "피보호자",
        type:"dependent",
      })),
      ...protectors.map((item) => ({
        dependencyId: item.dependencyId,
        name: item.protectorName,
        role: "보호자",
        type: "protector",
      })),
    ];

    console.log("✅ 불러온 가족 목록:", members.value);
  } catch (error) {
    console.error("❌ 가족 목록을 불러오는 중 오류 발생:", error);
  }
};

// ✅ 필터링된 멤버 리스트 (computed 사용)
const filteredMembers = computed(() => {
  if (selectedFilter.value === "all") return members.value;
  return members.value.filter((member) => member.type === selectedFilter.value);
});

// ✅ 관계 삭제 요청 (dependencyId 사용)
const confirmDelete = async (dependencyId) => {
  console.log(`🛠 삭제 요청: dependencyId = ${dependencyId}`);

  if (!dependencyId) {
    console.error("❌ dependencyId가 없습니다!", dependencyId);
    return;
  }

  const memberIndex = members.value.findIndex(m => m.dependencyId === dependencyId);
  if (memberIndex === -1) {
    console.error(`❌ dependencyId=${dependencyId}에 해당하는 멤버를 찾을 수 없습니다.`);
    return;
  }

  // ✅ 삭제 요청 중 상태를 업데이트하여 사용자에게 요청이 진행 중임을 알림
  members.value[memberIndex].isRequestPending = true;

  // ✅ 삭제 요청 실행
  const success = await requestDependencyDelete(dependencyId);
  if (success) {
    console.log("✅ 삭제 요청이 성공적으로 전송되었습니다.");
  } else {
    console.error(`❌ dependencyId=${dependencyId} 삭제 요청 실패`);
    members.value[memberIndex].isRequestPending = false; // ❌ 실패 시 요청 중 상태 해제
  }
};

onMounted(loadMembers);
</script>

<style scoped>
/* ✅ 부모 높이를 유지하여 상단바 아래로 내용이 정상 표시되도록 함 */
.flex-auto {
  min-height: 0; /* ✅ flex 아이템이 컨테이너를 넘지 않도록 설정 */
}
</style>
