<template>
  <Teleport to="body">
    <div class="fixed inset-0 flex justify-center items-center bg-black bg-opacity-50 z-50">
      <div
        class="bg-white w-[90%] max-w-[500px] p-6 rounded-lg shadow-lg relative h-auto max-h-[80vh] overflow-y-auto"
      >
        <!-- ✅ 헤더 -->
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold">{{ dependent.dependentName }}의 복용 내역</h2>
          <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">✕</button>
        </div>

        <!-- ✅ 복용 기록 (가로 스크롤) -->
        <div class="m-4 flex flex-col">
          <div class="flex justify-between items-center mb-2">
            <p class="text-xl font-bold">복용 내역</p>
            <button @click="fetchMedicationHistory" class="text-sm text-gray-500 hover:underline">
              과거 복용내역 조회 ▷
            </button>
          </div>

          <div class="scroll-container flex overflow-x-auto space-x-4 p-2">
            <WhiteCard
              v-for="(info, index) in prescriptionList"
              :key="index"
              class="bg-white min-w-[300px] max-w-[300px] flex-shrink-0 relative p-4 overflow-hidden"
            >
              <p class="absolute top-2 right-3 text-xs text-gray-400 truncate max-w-[150px]">
                {{ info.hospital || '병원 정보 없음' }}
              </p>

              <div class="flex flex-row items-center">
                <img src="../assets/logi_nofont.svg" alt="알약이미지" class="w-16 h-16" />
                <div class="flex flex-col ml-4 max-w-[200px]">
                  <p class="font-bold text-lg truncate max-w-[200px]">
                    {{ info.diseaseName || '병명 미등록' }}
                  </p>
                  <p class="text-xs text-gray-500 truncate max-w-[200px]">
                    {{ info.medicationPeriod }}
                  </p>
                  <p class="text-xs text-gray-500 mt-1 truncate max-w-[200px]">
                    {{ info.medications || '약 정보 없음' }}
                  </p>
                </div>
              </div>
            </WhiteCard>
          </div>
        </div>

        <!-- ✅ BaseDependentCalendar 사용 -->
        <div class="m-4 flex flex-col">
          <BaseDependentCalendar 
            :prescriptions="prescriptionList" 
            :dependentId="dependent.dependentId"
          />
        </div>

        <!-- ✅ "약 추가" 버튼 (하단 중앙) -->
        <div class="fixed bottom-12 left-1/2 -translate-x-1/2 z-50">
          <button
            @click="toggleDropdown"
            class="rounded-full bg-white shadow-lg p-3 border border-gray-300"
          >
            <img :src="navPlusIcon" alt="약 추가" class="w-12 h-12" />
          </button>

          <!-- ✅ 드롭다운 메뉴 -->
          <div
            v-if="isDropdownOpen"
            class="absolute bottom-16 left-1/2 -translate-x-1/2 bg-white shadow-lg rounded-lg p-2 w-40 border border-gray-200"
            @click.stop
          >
            <button
              @click="openCamera"
              class="py-2 text-left w-full text-gray-700 hover:bg-gray-100"
            >
              처방전 촬영
            </button>
            <button
              @click="triggerFileInput"
              class="py-2 text-left w-full text-gray-700 hover:bg-gray-100"
            >
              사진 업로드
            </button>
            <button
              @click="handleDirectAdd"
              class="py-2 text-left w-full text-gray-700 hover:bg-gray-100"
            >
              직접 추가하기
            </button>
            <input
              type="file"
              ref="fileInputRef"
              @change="handleFileChange"
              accept="image/*"
              class="hidden"
            />
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'; // ✅ watch 추가!
import { useRouter, useRoute } from 'vue-router'; // ✅ useRoute 추가
import WhiteCard from '../layout/WhiteCard.vue';
import BaseCalendar from '../components/BaseCalendar.vue';
import { fetchFormattedManagementInfo } from '../api/drugmanagement.js';
import navPlusIcon from '../assets/navplus.svg';
import { useOcrStore } from '../stores/ocrStore.js'; // ✅ OCR Store 추가
import BaseDependentCalendar from '../components/BaseDependentCalendar.vue';

const emit = defineEmits(['close']);

const props = defineProps({
  dependent: Object, // ✅ 선택한 가족 구성원 정보
});

const router = useRouter();
const route = useRoute(); // ✅ `route` 정의 추가
const prescriptionList = ref([]);
const isDropdownOpen = ref(false);
const fileInputRef = ref(null);
const ocrStore = useOcrStore(); // ✅ OCR Store 사용

// ✅ 드롭다운 메뉴 토글
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

// ✅ 카메라 열기 (카메라 페이지로 이동)
const openCamera = () => {
  isDropdownOpen.value = false; // 드롭다운 닫기
  router.push('/camera');
};

// ✅ 파일 업로드 트리거
const triggerFileInput = () => {
  isDropdownOpen.value = false; // 드롭다운 닫기
  fileInputRef.value.click();
};

// ✅ 선택한 가족의 복용 내역 가져오기
const loadMedicationData = async (dependentId) => {
  try {
    const { prescriptions } = await fetchFormattedManagementInfo(dependentId);
    prescriptionList.value = prescriptions || [];
  } catch (error) {
    console.error('❌ 복용 내역 불러오기 실패:', error);
  }
};

const saveOcrResultsForDependent = async () => {
  if (!props.dependent) {
    console.error('❌ 선택한 가족 구성원이 없습니다.');
    return;
  }

  // console.log(
  //   `📤 [DEBUG] 피보호자 OCR 데이터 저장 시작 - dependentId: ${props.dependent.dependentId}`
  // );

  try {
    await ocrStore.saveOcrDataToDB(props.dependent.dependentId); // ✅ 피보호자 ID 전달
    await loadMedicationData(props.dependent.dependentId); // ✅ 복약 내역 새로고침
    console.log('✅ [DEBUG] OCR 데이터 저장 완료');
  } catch (error) {
    console.error('❌ OCR 결과 저장 실패:', error);
  }
};

// ✅ 직접 추가 버튼 클릭 시 OCR 초기화 후 다이얼로그 열기
const handleDirectAdd = () => {
  isDropdownOpen.value = false;
  ocrStore.resetOcrState(); // ✅ OCR 결과 초기화
  ocrStore.setDependentId(props.dependent?.dependentId); // ✅ 선택한 피보호자 ID 저장
  ocrStore.showResultsDialog = true; // ✅ 다이얼로그 직접 띄우기
};

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (!file) {
    console.warn("파일이 선택되지 않았습니다.");
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    const base64Image = e.target.result;

    console.log(`📤 [DEBUG] 이미지 분석 페이지 이동 - dependentId: ${props.dependent?.dependentId}`);

    router.push({
      path: "/imageanalysis",
      query: {
        image: encodeURIComponent(base64Image),
        dependentId: props.dependent?.dependentId || ocrStore.dependentId,  // ✅ OCR Store에서 ID 가져오기
      },
    });
  };
  reader.readAsDataURL(file);
};

// ✅ 과거 복용내역 조회 함수 추가
const fetchMedicationHistory = async () => {
  try {
    if (!props.dependent?.dependentId) {
      console.error('❌ 피부양자 ID가 없습니다.');
      return;
    }

    console.log(`🔍 피부양자(${props.dependent.dependentId})의 과거 복용내역 조회 시작`);
    
    // ✅ 현재 날짜 기준 이전 달의 첫째 날 구하기
    const today = new Date();
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const formattedDate = `${lastMonth.getFullYear()}-${String(lastMonth.getMonth() + 1).padStart(2, '0')}-01`;

    // ✅ 피부양자 ID로 과거 복용내역 조회
    const { prescriptions } = await fetchFormattedManagementInfo(
      props.dependent.dependentId,
      formattedDate
    );
    
    prescriptionList.value = prescriptions || [];
    console.log('✅ 과거 복용내역 조회 완료:', prescriptions);
  } catch (error) {
    console.error('❌ 과거 복용내역 조회 실패:', error);
  }
};

// watch(
//   () => route.query,
//   async (newQuery) => {
//     console.log(`🔄 [DEBUG] route.query 변경 감지:`, newQuery);

//     if (newQuery.dependentId && newQuery.image) {
//       console.log(
//         `🔄 [DEBUG] OCR 분석 완료 후 복약 내역 저장 실행 - dependentId: ${newQuery.dependentId}`
//       );
//       await saveOcrResultsForDependent(props.dependent.dependentId);
//     }
//   }
// );

// ✅ 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadMedicationData(props.dependent.dependentId);
  if (props.dependent?.dependentId) {
    ocrStore.setDependentId(props.dependent.dependentId); // ✅ OCR Store에 보호자 ID 저장
  }
  // ✅ 페이지 이동 후 OCR 결과 저장
  if (route.query.dependentId && route.query.image) {
    // console.log('🔄 [DEBUG] OCR 분석 결과 자동 저장 실행');
    saveOcrResultsForDependent(props.dependent.dependentId);
  }
});
</script>
