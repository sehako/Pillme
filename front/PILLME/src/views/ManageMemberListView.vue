<template>
  <div class="flex flex-col w-full h-full overflow-hidden">
    <!-- ✅ 상단바 (고정 높이) -->
    <div class="flex-none bg-[#B5CCB7] py-3 px-4 text-lg font-semibold text-gray-900 text-center">
      관리 회원 목록
    </div>

    <!-- ✅ 스크롤 가능한 본문 -->
    <div class="flex-auto overflow-y-auto px-4 py-2">
      <template v-if="members.length > 0">
        <div
          v-for="(member, index) in members"
          :key="member.dependencyId" 
          class="flex items-center justify-between py-3 w-full border-b"
        >
          <MemberItem
            :dependencyId="member.dependencyId"  
            :name="member.dependentName"
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
import { ref, onMounted } from "vue";
import MemberItem from "../components/MemberItem.vue";
import BaseButton from "../components/BaseButton.vue";
import FamilyAddModal from "../components/FamilyAddModal.vue";
// ✅ 올바른 API import
import { fetchDependents } from "../api/dependentmember"; // 가족 목록 불러오기
import { requestDependencyDelete } from "../api/dependency"; // 가족 삭제 요청

const members = ref([]);
const isModalOpen = ref(false);

// ✅ 가족 목록 불러오기
const loadDependents = async () => {
  try {
    members.value = await fetchDependents();
    console.log("✅ 불러온 가족 목록:", members.value);
  } catch (error) {
    console.error("❌ 가족 목록을 불러오는 중 오류 발생:", error);
  }
};

// ✅ 관계 삭제 요청 (dependencyId 사용)
const confirmDelete = async (dependencyId) => {
  console.log(`🛠 삭제 요청: dependencyId = ${dependencyId}`);

  if (!dependencyId) {
    console.error("❌ dependencyId가 없습니다!", dependencyId);
    return;
  }

  const member = members.value.find(m => m.dependencyId === dependencyId);
  if (!member) {
    console.error("❌ 해당 dependencyId에 해당하는 member가 없습니다.", dependencyId);
    return;
  }

  if (confirm(`${member.dependentName}님과의 관계를 삭제 요청하시겠습니까?`)) {
    const success = await requestDependencyDelete(dependencyId); // ✅ dependencyId 사용
    if (success) {
      alert("삭제 요청이 전송되었습니다. 상대방의 승인 후 삭제됩니다.");
      members.value = members.value.filter(member => member.dependencyId !== dependencyId);
    } else {
      alert("삭제 요청에 실패했습니다. 다시 시도해주세요.");
    }
  }
};

onMounted(loadDependents);
</script>

<style scoped>
/* ✅ 부모 높이를 유지하여 상단바 아래로 내용이 정상 표시되도록 함 */
.flex-auto {
  min-height: 0; /* ✅ flex 아이템이 컨테이너를 넘지 않도록 설정 */
}
</style>
