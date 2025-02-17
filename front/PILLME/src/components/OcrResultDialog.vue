<template>
  <div v-if="ocrStore.showResultsDialog" class="dialog-overlay">
    <div class="dialog-box">
      <h2 class="text-lg font-semibold mb-2 text-center">📄 분석 결과</h2>

      <!-- ✅ 분석된 약 리스트 -->
      <ul class="medication-list">
        <li
          v-for="(result, index) in ocrStore.results"
          :key="index"
          class="medication-row"
        >
          <span class="med-name">{{ result.matched_drug }}</span>
          <button @click="removeDrug(index)" class="delete-btn" title="삭제">🗑</button>
        </li>
      </ul>


      <!-- ✅ 약물 검색 + 직접 추가 -->
      <div class="mt-4 flex flex-col sm:flex-row gap-2">
        <input
          v-model="searchQuery"
          @input="fetchMedications"
          type="text"
          placeholder="약물 검색 또는 입력"
          class="border p-2 rounded w-full sm:w-3/4 md:w-2/3 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
        <button @click="openSearchDialog" class="search-btn w-full sm:w-auto">🔍 검색</button>
      </div>

      <!-- ✅ 검색 결과 표시 -->
      <div v-if="medications.length > 0" class="max-h-40 overflow-y-auto border p-2 rounded bg-white shadow">
        <ul>
          <li
            v-for="(med, index) in medications"
            :key="index"
            @click="selectDrug(med)"
            class="cursor-pointer hover:bg-gray-100 p-2 rounded"
          >
            {{ med.name }} ({{ med.company }})
          </li>
        </ul>
      </div>

      <!-- ✅ 직접 추가 -->
      <div class="mt-4 flex justify-center">
        <button @click="addDrug" class="add-btn w-full sm:w-auto">➕ 직접 추가</button>
      </div>

      <div class="button-group">
        <button @click="ocrStore.openNextDialog" class="primary-btn">다음</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { searchMedications } from '../api/search';
import { useOcrStore } from '../stores/ocrStore';

const ocrStore = useOcrStore();
const newDrug = ref(''); // ✅ 새로운 약물 추가 입력 필드
const searchQuery = ref('');
const medications = ref([]);

// ✅ DB에서 약물 검색 (debounce 적용)
const fetchMedications = async () => {
  if (!searchQuery.value.trim()) {
    medications.value = [];
    return;
  }

  const response = await searchMedications(searchQuery.value);
  medications.value = response.length > 0 ? response : [];
};

// ✅ 검색된 약물 선택 시 추가
const selectDrug = (med) => {
  ocrStore.results.push({ matched_drug: med.name });
  searchQuery.value = ''; // 검색 필드 초기화
  medications.value = []; // 검색 결과 초기화
};

// ✅ 직접 입력하여 추가
const addDrug = () => {
  if (searchQuery.value.trim() !== '') {
    ocrStore.results.push({ matched_drug: searchQuery.value.trim() });
    searchQuery.value = ''; // 입력 필드 초기화
  }
};

// ✅ 약물 삭제 기능
const removeDrug = (index) => {
  ocrStore.results.splice(index, 1);
};

// ✅ 약물 검색 다이얼로그 열기 (추후 확장 가능)
const openSearchDialog = () => {
  fetchMedications();
};
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9999;
}

.dialog-box {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  max-height: 75vh;
  overflow-y: auto;
}

.medication-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 8px;
}

.medication-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #ddd;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.med-name {
  font-weight: bold;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}

.primary-btn {
  background-color: #f48fb1;
  color: white;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 16px;
  width: auto;
}

.delete-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #ff4d4d;
}

.delete-btn:hover {
  color: #d63031;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 16px;
  width: auto;
}

.add-btn:hover {
  background-color: #45a049;
}

.drug-name {
  text-align: left;
}
</style>
