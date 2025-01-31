<template>
<div class="flex flex-col w-full">
<undertopbar class="bg-[#B5CCB7] rounded-bl-lg rounded-br-lg">
  <div class="flex flex-row items-center justify-between p-4 ">
  <!-- ✅ 햄버거 아이콘 -->
  <div class="flex-1 flex justify-start">
    <img src="../assets/Group 33627.svg" alt="메뉴" class="w-6 h-6">
  </div>

  <!-- ✅ 사용자 이름 (가운데) -->
  <div class="relative">
      <p class="text-center">사용자이름</p>
      <img src="../assets/namedropdown.svg" alt="이름드롭다운" class="absolute right-[-20px] top-1/2 transform -translate-y-1/2 w-4 h-4">
    </div>

  <!-- ✅ 공백 (햄버거 아이콘과 크기 맞춤) -->
  <div class="flex-1"></div>
</div>


</undertopbar>
<div class="grid gap-4 grid-cols-3 p-4">
  <BaseButton class="whitespace-nowrap">
    인원추가
  </BaseButton>
  <BaseButton class="whitespace-nowrap">
    약정보검색
  </BaseButton>
  <BaseButton class="whitespace-nowrap">
    알림설정
  </BaseButton>
</div>

<mainview>
  <YellowCard class="m-4 flex flex-col">
  <div class="flex flex-row items-end">
    <p class="text-sm font-bold">오늘의 복약 내역</p>
    <span class="text-xs ml-2">어떤어떤약</span>
  </div>

  <div class="flex flex-row items-end">
    <p class="font-bold text-lg"><시간대>약을 드셨나요?</p>
    <img src="../assets/CheckCircle.svg" alt="약물복용체크">
  </div>
</YellowCard>

<div class="m-4 flex flex-col">
  <p class="text-lg font-bold">복용 내역</p>
  <WhiteCard overrideClass="bg-white">
  
  </WhiteCard>
</div>

</mainview>
</div>
</template>

<script setup>
import BaseButton from '../components/BaseButton.vue';
import YellowCard from '../layout/YellowCard.vue';
import WhiteCard from '../layout/WhiteCard.vue';

</script>

<style scoped>

</style>