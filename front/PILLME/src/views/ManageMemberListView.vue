<template>
    <div class="min-h-screen w-full flex flex-col" :style="{ marginTop: `${topbarHeight}px` }">
      <div class="w-full text-center">
        <BaseText 
          textBefore="" 
          highlightText="관리 회원 목록" 
          textColor="text-gray-900" 
          highlightColor="text-black"
          class="block w-full text-lg sm:text-xl md:text-2xl"
        />
      </div>

      <div class="bg-white shadow mt-4 w-full px-4 sm:px-6">
        <template v-if="members.length > 0">
          <div 
            v-for="(member, index) in members"
            :key="member.id"
            class="flex items-center justify-between py-3 w-full border-b"
          >
            <MemberItem 
              :name="member.name"
              :birthdate="member.birthdate"
              :age="member.age"
              class="w-full"
            />

            <button 
              @click="goToMemberDetail(member.id)" 
              class="text-gray-500 hover:text-gray-700"
            >
              ▶
            </button>
          </div>
        </template>
        <p v-else class="text-center text-gray-500 py-4">등록된 회원이 없습니다.</p>
      </div>

      <div class="flex justify-center mt-4 w-full">
        <BaseButton
          textColor="text-gray-800"
          size="lg"
          overrideClass="!w-4/5 !px-4 !py-3.5 !text-md !bg-gray-200 hover:!bg-gray-300"
          @click="addMember"
        >
          인원 추가
        </BaseButton>
      </div>
    </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from "vue";
import MemberItem from "../components/MemberItem.vue";
import BaseButton from "../components/BaseButton.vue";
import BaseText from "../components/BaseText.vue"; 
import { useRouter } from "vue-router";

const router = useRouter();
const topbarHeight = ref(0); 

const members = ref([
    { id: 1, name: "이싸피", birthdate: "2025.01.17", age: 0 },
    { id: 2, name: "김철수", birthdate: "1998.05.21", age: 27 },
    { id: 3, name: "박영희", birthdate: "2001.10.03", age: 24 },
]);

const addMember = async () => {
    console.log("인원 추가 버튼 클릭됨");
    await nextTick();
    router.push("/register");
};

// ✅ 상단바 높이 계산 후 적용
onMounted(() => {
    const topbar = document.querySelector("#topbar");
    topbarHeight.value = topbar ? topbar.offsetHeight : 0;
});
</script>
