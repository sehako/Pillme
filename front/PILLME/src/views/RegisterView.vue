<template>
    <div class="min-h-screen w-full flex flex-col items-center">
      
      <!-- ✅ 상단 탭 메뉴 -->
      <div class="w-full flex border-b">
        <button 
          class="flex-1 text-lg font-semibold py-2 border-b-2"
          :class="isNonMember ? 'text-green-700 border-green-700' : 'text-gray-400 border-transparent'"
          @click="isNonMember = true"
        >
          비회원
        </button>
        <button 
          class="flex-1 text-lg font-semibold py-2 border-b-2"
          :class="!isNonMember ? 'text-green-700 border-green-700' : 'text-gray-400 border-transparent'"
          @click="isNonMember = false"
        >
          회원
        </button>
      </div>
  
      <!-- ✅ 현재 선택된 뷰 렌더링 -->
      <component :is="currentComponent" />
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from "vue";
  import NonMemberRegisterView from "../views/NonMemberRegisterView.vue";
  import MemberRegisterView from "../views/MemberRegisterView.vue";
  
  const isNonMember = ref(true);
  const topbarHeight = ref(0);
  
  // ✅ 현재 선택된 컴포넌트 반환 (회원 or 비회원)
  const currentComponent = computed(() => (isNonMember.value ? NonMemberRegisterView : MemberRegisterView));
  
  </script>
  