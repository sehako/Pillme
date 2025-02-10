<template>
  <div class="flex flex-col w-full max-h-full !items-start !justify-start">
    <!-- 상단 (70%) -->
    <div class="w-full">
      <VCalendar trim-weeks expanded/>
    </div>

    <div class="grid grid-cols-3 divide-x-0"></div>

    <!-- ✅ 하단 (30%) - 스크롤 가능 -->
    <div class="flex flex-col w-full overflow-y-auto p-4">
      <!-- ✅ 블록 요소 사용 -->
      <div v-for="i in 50" :key="i" class="mb-4">
        <WhiteCard overrideClass="bg-white">
          <div class="flex flex-row items-center">
            <img src="../assets/logi_nofont.svg" alt="알약이미지" class="w-16 h-16">
            <div class="flex flex-col">
              <p> 스크롤 테스트용 카드 {{ i }}</p>
            </div>
          </div>
        </WhiteCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import WhiteCard from '../layout/WhiteCard.vue';
</script>

<style scoped></style>
