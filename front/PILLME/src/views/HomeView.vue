<template>
  <div class="flex flex-col w-full">
    <undertopbar class="bg-[#B5CCB7] rounded-bl-xl rounded-br-lg">
      <div class="flex flex-row items-center justify-between px-4 py-1">
        <!-- ✅ 햄버거 메뉴 컴포넌트 -->
        <div class="flex-1 flex">
          <HamBugerMenu />
        </div>

        <!-- ✅ 사용자 이름 드롭다운 (컴포넌트 사용) -->
        <NameDropdown />

        <!-- ✅ 공백 (햄버거 아이콘과 크기 맞춤) -->
        <div class="flex-1"></div>
      </div>
    </undertopbar>

    <div class="grid gap-4 grid-cols-3 p-4">
      <BaseButton class="whitespace-nowrap text-lg font-base" @click="openFamilyModal"> 인원추가 </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base"> 약정보검색 </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base"> 알림설정 </BaseButton>
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
        <p class="text-xl font-bold">복용 내역</p>
        <WhiteCard overrideClass="bg-white">
          <div class="flex flex-row items-center">
            <img src="../assets/logi_nofont.png" alt="알약이미지" class="w-16 h-16">
            <div class="flex flex-col">
              <p>병명</p>
              <p>기간</p>
              <p>약이름</p>
            </div>
          </div>
        </WhiteCard>
      </div>

      <div class="m-4 flex flex-col">
        <VCalendar class="w-full"/>
      </div>
    </mainview>
  </div>
  <FamilyAddModal :isOpen="isFamilyModalOpen" @close="isFamilyModalOpen = false" />

</template>

<script setup>
import { ref } from 'vue';
import BaseButton from '../components/BaseButton.vue';
import YellowCard from '../layout/YellowCard.vue';
import WhiteCard from '../layout/WhiteCard.vue';
import HamBugerMenu from '../components/HamBugerMenu.vue';
import NameDropdown from '../components/NameDropdown.vue';
import FamilyAddModal from '../components/FamilyAddModal.vue';
// ✅ 모달 상태
const isFamilyModalOpen = ref(false);

// ✅ 모달 열기 함수
const openFamilyModal = () => {
  isFamilyModalOpen.value = true;
};


const selectedDate = ref(null);
</script>
