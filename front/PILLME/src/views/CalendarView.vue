<template>
  <div class="flex flex-col w-full h-screen pt-16">
    <!-- 상단 (70%) -->
    <div class="flex-none h-[70%] w-full overflow-hidden">
      <VCalendar class="w-full h-auto" />
    </div>

    <!-- 하단 (30%) - 스크롤 가능 -->
    <div class="flex-1 h-[30%] w-full overflow-y-auto bg-gray-100 p-4">
      <p v-for="i in 50" :key="i">
        📌 스크롤 테스트용 텍스트 {{ i }}
      </p>
    </div>
  </div>
</template>


<script setup></script>

<style scoped></style>
