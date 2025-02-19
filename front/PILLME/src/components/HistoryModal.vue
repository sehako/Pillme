<template>
  <div @click.self="closeModal" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
    <div class="modal-container">
      <!-- 닫기 버튼 -->
      <button 
        @click="closeModal"
        class="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
      >
        ✕
      </button>

      <!-- ✅ 상세 정보가 없는 경우: 검색창 + 카드 목록 표시 -->
      <template v-if="!selectedPrescription">
        <div class="search-container">
          <h2 class="text-xl font-bold mb-4">복용 내역</h2>
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="검색 (병명, 병원, 기간)" 
            class="border p-2 rounded w-full"
          />
        </div>

        <!-- ✅ 검색 결과가 없을 때 -->
        <p v-if="filteredPrescriptions.length === 0" class="text-gray-500 text-center mt-4">
          검색 결과가 없습니다.
        </p>

        <!-- ✅ 필터링된 복용 내역 카드 목록 -->
        <div class="prescription-list">
          <WhiteCard 
            v-for="prescription in filteredPrescriptions" 
            :key="prescription.informationId"
            overrideClass="bg-white mb-4 rounded shadow cursor-pointer"
            @click="handleCardClick(prescription)"
          >
            <p class="font-bold text-lg">{{ prescription.diseaseName }}</p>
            <p class="text-sm text-gray-500">{{ prescription.hospital }}</p>
            <p class="text-sm text-gray-500">{{ prescription.startDate }} ~ {{ prescription.endDate }}</p>
          </WhiteCard>
        </div>
      </template>

      <!-- ✅ 상세 정보가 있는 경우: DrugHistoryDetailModal 표시 -->
      <template v-else>
        <button @click="selectedPrescription = null" class="ml-4 text-left text-gray-500 hover:text-gray-700">
          ← 뒤로가기
        </button>
        <DrugHistoryDetailModal :prescription="selectedPrescription"
        @close="handleclose" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import WhiteCard from "../layout/WhiteCard.vue";
import DrugHistoryDetailModal from "../components/DrugHistoryDetailModal.vue";

// Props (부모 컴포넌트에서 받은 복용 내역 데이터)
const props = defineProps({
  prescriptions: {
    type: Array,
    required: true
  }
});
console.log("📌 복용 내역 데이터:", props.prescriptions);
// Emits (부모로 모달 닫기 이벤트 전달)
const emit = defineEmits(["close"]);

// ✅ 검색어 상태 변수
const searchQuery = ref("");

// ✅ 선택된 처방전 (클릭하면 여기에 값이 들어감)
const selectedPrescription = ref(null);

// ✅ 검색 필터링 로직 (computed)
const filteredPrescriptions = computed(() => {
  if (!searchQuery.value.trim()) {
    return props.prescriptions;
  }

  const query = searchQuery.value.toLowerCase();
  
  return props.prescriptions.filter(prescription => 
    (prescription.diseaseName && prescription.diseaseName.toLowerCase().includes(query)) ||
    (prescription.hospital && prescription.hospital.toLowerCase().includes(query)) ||
    (prescription.startDate && prescription.startDate.includes(query)) ||
    (prescription.endDate && prescription.endDate.includes(query))
  );
});

// ✅ 모달 닫기
const closeModal = () => {
  emit("close");
};
const handleclose = () => {
  selectedPrescription.value = null;
};


// ✅ 카드 클릭 시 상세 모달로 전환
const handleCardClick = (prescription) => {
  console.log("📌 선택한 처방전:", prescription);
  selectedPrescription.value = prescription;
};
</script>

<style scoped>
/* ✅ 반응형 모달 크기 조정 */
.modal-container {
  width: 90vw !important; /* 모바일 대응 */
  max-width: 600px !important; /* 최대 크기 제한 */
  height: 80vh !important; /* 뷰포트 높이의 80% */
  max-height: 80vh !important; /* 높이 최대 제한 */
  background-color: white;
  border-radius: 10px;
  padding: 24px;
  overflow: hidden !important; /* 내부 스크롤 적용을 위해 */
  display: flex;
  flex-direction: column;
  position: relative;
}

/* ✅ 검색창을 고정 */
.search-container {
  position: sticky !important;
  top: 0 !important;
  background-color: white !important;
  padding-bottom: 12px !important;
  z-index: 10;
}

/* ✅ 처방전 목록 스크롤 가능하게 설정 */
.prescription-list {
  flex-grow: 1 !important; /* 검색창 제외 나머지 공간 차지 */
  overflow-y: auto !important; /* 스크롤 허용 */
  max-height: 100% !important;
  padding-top: 10px;
}

/* ✅ 스크롤 스타일링 */
.prescription-list::-webkit-scrollbar {
  width: 8px;
}

.prescription-list::-webkit-scrollbar-thumb {
  background-color: #ccc !important;
  border-radius: 4px !important;
}

.prescription-list::-webkit-scrollbar-track {
  background-color: transparent !important;
}
</style>
