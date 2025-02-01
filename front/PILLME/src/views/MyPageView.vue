<template>
  <aside 
    class="left-0 top-0 p-4 flex flex-col border-r border-gray-200 
           min-w-[120px] max-w-[200px] w-full"
    :style="{ height: `calc(100vh - ${navbarHeight*1.6}px)` }"
  >
  <!-- 1.6을 다른 곳에서도 활용하면 좋을 듯 함 -->
    <div class="pb-4 border-b border-gray-300">
      <input 
        type="text" 
        placeholder="검색" 
        class="w-full py-2 pl-2 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-500 
                text-[clamp(12px,2vw,16px)]"
      />
    </div>
    <nav class="mt-4 flex-1">
      <ul>
        <li v-for="(item, index) in menuItems" :key="index" 
            class="py-2 px-2 hover:bg-green-200 rounded cursor-pointer 
                   text-[clamp(12px,2vw,16px)] whitespace-nowrap">
          {{ item }}
        </li>
      </ul>
    </nav>
    <div class="mt-auto border-t border-gray-300 pt-4 text-gray-600">
      <p class="py-2 cursor-pointer hover:text-black text-[clamp(12px,2vw,16px)] whitespace-nowrap">로그아웃</p>
      <p class="py-2 cursor-pointer hover:text-black text-[clamp(12px,2vw,16px)] whitespace-nowrap">회원 탈퇴</p>
    </div>
  </aside>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  navbarHeight: {
    type: Number,
    required: true
  }
});

const menuItems = [
  "계정 및 보안",
  "설정",
  "고객 지원"
];
</script>
