<template>
  <div 
    class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50" 
    @click.self="closeModal"
  >
    <div :class="['bg-white p-6 rounded-lg shadow-lg relative', modalClass]">
      <button 
        class="absolute top-2 right-2 text-gray-500 hover:text-gray-700" 
        @click="closeModal"
      >
        ✕
      </button>

      <!-- 헤더와 전체 복약 체크 -->
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">복약 정보 수정</h2>
        <div class="flex items-center space-x-2">
          <span class="text-sm">전체 복약</span>
          <ToggleSwitch 
            :modelValue="allMedicationsChecked" 
            @update:modelValue="handleAllMedicationsToggle"
          />
        </div>
      </div>

      <!-- 일반 정보 영역 -->
      <div class="mb-4 text-sm text-gray-700">
        <dl class="grid grid-cols-2 gap-x-4 gap-y-2">
          <div>
            <dt class="font-semibold">병원:</dt>
            <dd class="whitespace-nowrap">{{ info.hospital || '없음' }}</dd>
          </div>
          <div>
            <dt class="font-semibold">병명:</dt>
            <dd class="whitespace-nowrap">{{ info.diseaseName || '미등록' }}</dd>
          </div>
          <div>
            <dt class="font-semibold">기간:</dt>
            <dd class="whitespace-nowrap break-before-tilde" 
                v-html="info.medicationPeriod.replace('~', '<br>종료일:')?.replace(/^/, '시작일: ') || '없음'">
            </dd>
          </div>
          <div>
            <dt class="font-semibold">약 정보:</dt>
            <dd class="whitespace-nowrap">{{ info.medications || '없음' }}</dd>
          </div>
        </dl>
      </div>

      <!-- 약 정보 카드 영역 -->
      <div class="w-full px-4 scroll-area space-y-4">
        <div 
          v-for="(medication, idx) in medicationList" 
          :key="medication.id" 
          class="border p-3 rounded shadow-sm bg-white"
        >
          <p class="font-bold whitespace-nowrap mb-2">{{ medication.name }}</p>
          <div class="mt-2 space-y-1">
            <div class="flex items-center space-x-2">
              <span class="whitespace-nowrap">아침 복약 여부:</span>
              <span class="text-sm font-semibold" v-text="morningToggles[idx] ? 'O' : 'X'"></span>
              <ToggleSwitch 
                v-model="morningToggles[idx]"
                @update:modelValue="(val) => handleIndividualToggle(medication.id, 'morning', val)"
              />
            </div>
            <div class="flex items-center space-x-2">
              <span class="whitespace-nowrap">점심 복약 여부:</span>
              <span class="text-sm font-semibold" v-text="lunchToggles[idx] ? 'O' : 'X'"></span>
              <ToggleSwitch 
                v-model="lunchToggles[idx]"
                @update:modelValue="(val) => handleIndividualToggle(medication.id, 'lunch', val)"
              />
            </div>
            <div class="flex items-center space-x-2">
              <span class="whitespace-nowrap">저녁 복약 여부:</span>
              <span class="text-sm font-semibold" v-text="dinnerToggles[idx] ? 'O' : 'X'"></span>
              <ToggleSwitch 
                v-model="dinnerToggles[idx]"
                @update:modelValue="(val) => handleIndividualToggle(medication.id, 'dinner', val)"
              />
            </div>
            <div class="flex items-center space-x-2">
              <span class="whitespace-nowrap">수면 복약 여부:</span>
              <span class="text-sm font-semibold" v-text="sleepToggles[idx] ? 'O' : 'X'"></span>
              <ToggleSwitch 
                v-model="sleepToggles[idx]"
                @update:modelValue="(val) => handleIndividualToggle(medication.id, 'sleep', val)"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 하단 버튼 영역 -->
      <div class="mt-6 flex justify-end">
        <button 
          class="text-gray-500 hover:underline" 
          @click="closeModal"
        >
          닫기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import ToggleSwitch from '../components/ToggleSwitch.vue';

const props = defineProps({
  info: {
    type: Object,
    required: true,
  },
  modalClass: {
    type: String,
    default: "w-full max-w-md mx-4"
  }
});

const emit = defineEmits(['close', 'alldrugcheck', 'thisdrugcheck']);

const closeModal = () => {
  emit('close');
};

const splitField = (field) => {
  if (!field || field === "정보 없음" || field === "약 정보 없음") return [];
  return field.split("◎").map(item => item.trim());
};
// medications와 medicationIds를 함께 처리
const medicationList = computed(() => {
  const names = splitField(props.info.medications);
  const ids = splitField(props.info.medicationIds);
  return names.map((name, index) => ({
    id: ids[index] || String(index),
    name
  }));
});

// 개별 토글 상태
const morningToggles = ref([]);
const lunchToggles = ref([]);
const dinnerToggles = ref([]);
const sleepToggles = ref([]);

// 전체 복약 체크 상태
const allMedicationsChecked = computed(() => {
  return medicationList.value.every((_, idx) => 
    morningToggles.value[idx] && 
    lunchToggles.value[idx] && 
    dinnerToggles.value[idx] && 
    sleepToggles.value[idx]
  );
});

const handleIndividualToggle = (medicationId, timeSlot, value) => {
  const idx = medicationList.value.findIndex(med => med.id == medicationId);
  if (idx === -1) return;

  // actualIds 배열 생성 및 유효성 검사
  const actualIds = splitField(props.info.medicationsId);
  
  // ✅ 실제 약물 데이터가 있는 경우만 매핑
  const medications = medicationList.value
    .filter((med, index) => actualIds[index]) // 유효한 ID가 있는 항목만 필터링
    .map((med, index) => {
      const currentToggles = {
        morning: morningToggles.value[index] ?? false,
        lunch: lunchToggles.value[index] ?? false,
        dinner: dinnerToggles.value[index] ?? false,
        sleep: sleepToggles.value[index] ?? false
      };

      // 선택된 약물인 경우에만 해당 시간대 값을 업데이트
      if (index === idx) {
        currentToggles[timeSlot] = value;
      }

      return {
        managementId: Number(actualIds[index]),
        ...currentToggles
      };
    })
    .filter(med => !isNaN(med.managementId)); // NaN 값을 가진 항목 제거

  // console.log("📌 개별 토글 - 전송할 medications:", medications);
  
  const ifid = props.info.informationId;
  emit('thisdrugcheck', medications, ifid);

  // UI 상태 업데이트
  if (timeSlot === 'morning') morningToggles.value[idx] = value;
  if (timeSlot === 'lunch') lunchToggles.value[idx] = value;
  if (timeSlot === 'dinner') dinnerToggles.value[idx] = value;
  if (timeSlot === 'sleep') sleepToggles.value[idx] = value;
};

// 전체 복약 토글 핸들러
const handleAllMedicationsToggle = (value) => {
  // medicationsId를 ◎로 구분된 배열로 변환
  const managementIds = props.info.medicationsId.split("◎").map(id => id.trim());

  // ✅ medications 배열 생성 (시간대는 value 상태에 따라 설정)
  const medications = managementIds.map(id => ({
    managementId: Number(id), // 숫자로 변환
    morning: value,  // 토글 값에 따라 모두 true 또는 false
    lunch: value,
    dinner: value,
    sleep: value
  }));
  const ifid = props.info.informationId;
  // console.log("📌 전체 토글 - 전송할 medications:", medications);

  // ✅ 부모로 `medications` 객체 배열 전달
  emit('alldrugcheck', medications, ifid);

  // ✅ 로컬 상태 업데이트 (UI 즉시 반영)
  medicationList.value.forEach((_, idx) => {
    morningToggles.value[idx] = value;
    lunchToggles.value[idx] = value;
    dinnerToggles.value[idx] = value;
    sleepToggles.value[idx] = value;
  });
};

// 초기 상태 설정
watch(medicationList, (newVal) => {
  morningToggles.value = splitField(props.info.morning).map(val => val === "true");
  lunchToggles.value = splitField(props.info.lunch).map(val => val === "true");
  dinnerToggles.value = splitField(props.info.dinner).map(val => val === "true");
  sleepToggles.value = splitField(props.info.sleep).map(val => val === "true");
}, { immediate: true });

</script>

<style scoped>
.scroll-area {
  max-height: calc(100vh - 400px) !important;
  overflow-y: auto !important;
}
.break-before-tilde {
  white-space: pre-line;
}
</style>