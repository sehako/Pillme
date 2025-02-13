<template>
  <!-- 부모에서 h-full 또는 h-screen을 설정해 주었다고 가정 -->
  <div class="flex flex-col w-full h-full">
    <!-- (1) 상단 달력 영역 (60%) -->
    <div class="flex-none h-3/5 min-h-0 overflow-hidden flex">
      <BaseCalendar mode="simple" />
    </div>

    <!-- (2) 구분 영역 (간단한 제목+문구) -->
    <div class="flex-none px-4 py-3 border-b bg-gray-100 text-gray-700">
      <h2 class="font-semibold text-lg">복용 약 리스트</h2>
      <p class="text-sm mt-1">
        선택한 날짜: 
        <span class="font-bold text-blue-500">
          {{ selectedDate || '미선택' }}
        </span>
      </p>
    </div>

    <!-- (3) 스크롤 가능한 하단 약물 카드 영역 (40%) -->
    <div class="flex-auto overflow-y-auto p-4">
      <div 
        v-for="i in 50" 
        :key="i" 
        class="mb-4"
      >
        <WhiteCard overrideClass="bg-white">
          <div class="flex flex-row items-center">
            <img
              src="../assets/logi_nofont.svg"
              alt="알약이미지"
              class="w-16 h-16 mr-4"
            />
            <div class="flex flex-col">
              <p>스크롤 테스트용 카드 {{ i }}</p>
            </div>
          </div>
        </WhiteCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BaseCalendar from '../components/BaseCalendar.vue';
import WhiteCard from '../layout/WhiteCard.vue';
import { transformPrescriptionsToEvents } from '../composables/useCalendarEvents.js';
// 달력에서 날짜를 선택했을 때 바뀔 것으로 가정
const selectedDate = ref(null);

// 달력 컴포넌트에서 @dateSelected="onDateSelected" 이벤트로 넘어온 값 받기
function onDateSelected(date) {
  selectedDate.value = date;
}
</script>

<style scoped>
/* 필요한 추가 스타일이 있다면 여기에 작성 */
</style>
