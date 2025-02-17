<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-[100]"
  >
    <div class="bg-white p-6 rounded-lg shadow-lg w-96 relative z-[101]">
      <h2 class="text-lg font-semibold mb-4">약물 검색</h2>

      <div class="flex items-center space-x-2 mb-4">
        <input
          v-model="searchQuery"
          @input="onChange"
          type="text"
          placeholder="약물 이름을 입력하세요"
          class="w-full p-2 border rounded"
        />
      </div>

      <div v-if="medications.length > 0" class="max-h-60 overflow-y-auto">
        <ul>
          <li
            v-for="(med, index) in medications"
            :key="index"
            class="flex items-center p-3 hover:bg-gray-100 cursor-pointer rounded-lg"
          >
            <img
              :src="med.image || defaultImage"
              alt="약 이미지"
              class="w-12 h-12 object-cover rounded-md"
            />
            <div class="ml-3">
              <p class="text-sm font-medium">{{ med.name }}</p>
              <p class="text-xs text-gray-500">{{ med.company }}</p>
            </div>
          </li>
        </ul>
      </div>

      <p v-else-if="searchQuery && medications.length === 0" class="text-sm text-gray-500">
        검색 결과가 없습니다.
      </p>

      <div class="flex justify-end mt-4">
        <button @click="closeDialog" class="px-4 py-2 bg-gray-400 text-white rounded">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import debounce from 'lodash.debounce';
import { searchMedications } from '../api/search'; // ✅ API 함수 불러오기
import defaultImage from '../assets/logi_nofont_x.png';

const isOpen = ref(false);
const searchQuery = ref('');
const medications = ref([]);

// ✅ `debounce` 적용하여 불필요한 API 요청 방지 (300ms 동안 입력이 멈추면 실행)
const fetchMedications = async () => {
  if (!searchQuery.value.trim()) {
    medications.value = [];
    return;
  }

  const response = await searchMedications(searchQuery.value);
  medications.value = response.length > 0 ? response : [];
};


// ✅ 입력 시 즉시 검색 실행
const onChange = (event) => {
  searchQuery.value = event.target.value;
  fetchMedications(); // API 요청 실행
};

// ✅ `watch`를 사용하여 `searchQuery`가 변경될 때 `fetchMedications` 실행
watch(searchQuery, fetchMedications);

const openDialog = () => {
  isOpen.value = true;
};

// ✅ **닫힐 때 검색어와 결과 초기화**
const closeDialog = () => {
  isOpen.value = false;
  searchQuery.value = ''; // ✅ 검색어 초기화
  medications.value = []; // ✅ 검색 결과 초기화
};

// `defineExpose` 사용하여 부모 컴포넌트에서 `openDialog()` 호출 가능하게 설정
defineExpose({ openDialog });
</script>
