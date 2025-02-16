<template>
    <!-- ✅ z-index를 높여 다른 UI 요소보다 위에 표시 -->
    <div v-if="isOpen" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-[100]">
      <div class="bg-white p-6 rounded-lg shadow-lg w-96 relative z-[101]">
        <h2 class="text-lg font-semibold mb-4">약물 검색</h2>
  
        <!-- 검색 입력창 -->
        <div class="flex items-center space-x-2 mb-4">
          <input 
            v-model="searchQuery"
            @input="fetchMedications"
            type="text"
            placeholder="약물 이름을 입력하세요"
            class="w-full p-2 border rounded"
          />
        </div>
  
        <!-- 검색 결과 리스트 -->
        <div v-if="medications.length > 0" class="max-h-60 overflow-y-auto">
          <ul>
            <li 
              v-for="med in medications" 
              :key="med.name"
              class="flex items-center p-3 hover:bg-gray-100 cursor-pointer rounded-lg"
              @click="selectMedication(med)"
            >
              <img :src="med.image || defaultImage" alt="약 이미지" class="w-12 h-12 object-cover rounded-md" />
              <div class="ml-3">
                <p class="text-sm font-medium">{{ med.name }}</p>
                <p class="text-xs text-gray-500">{{ med.company }}</p>
              </div>
            </li>
          </ul>
        </div>
  
        <!-- 검색 결과 없음 -->
        <p v-else-if="searchQuery && medications.length === 0" class="text-sm text-gray-500">
          검색 결과가 없습니다.
        </p>
  
        <!-- 닫기 버튼 -->
        <div class="flex justify-end mt-4">
          <button @click="closeDialog" class="px-4 py-2 bg-gray-400 text-white rounded">닫기</button>
        </div>
      </div>
    </div>
  </template>  
  
  <script setup>
  import { ref } from "vue";
  import axios from "axios";
  
  const isOpen = ref(false);
  const searchQuery = ref("");
  const medications = ref([]); // ✅ 초기값을 빈 배열([])로 설정
  const defaultImage = "https://via.placeholder.com/50"; // 기본 이미지 설정
  
  const fetchMedications = async () => {
    if (!searchQuery.value.trim()) {
      medications.value = [];
      return;
    }
  
    try {
      const response = await axios.get(`/api/v1/search?keyword=${searchQuery.value}`);
      medications.value = response.data?.data || []; // ✅ 응답이 없을 경우 빈 배열을 반환
    } catch (error) {
      console.error("약물 검색 실패:", error);
      medications.value = []; // ✅ 에러 발생 시 빈 배열로 설정하여 오류 방지
    }
  };
  
  const selectMedication = (med) => {
    console.log("선택한 약물:", med);
    closeDialog();
  };
  
  const openDialog = () => {
    isOpen.value = true;
  };
  
  const closeDialog = () => {
    isOpen.value = false;
    searchQuery.value = "";
    medications.value = [];
  };
  
  // `defineExpose`를 사용하여 부모 컴포넌트에서 `openDialog()`를 호출할 수 있도록 설정
  defineExpose({ openDialog });
  </script>
  