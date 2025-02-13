<template>
  <div class="flex flex-col w-full">
    <div class="bg-[#B5CCB7] rounded-bl-xl rounded-br-lg">
      <div class="flex flex-row items-center justify-between px-4 py-1">
        <!-- 베이스탑바 바로 밑의 바임 -->
        <!-- ✅ 햄버거 메뉴 컴포넌트 -->
        <div class="flex-1 flex">
          <HamBugerMenu />
        </div>

        <!-- ✅ 사용자 이름 드롭다운 (컴포넌트 사용) -->
        <NameDropdown />

        <!-- ✅ 공백 (햄버거 아이콘과 크기 맞춤) -->
        <div class="flex-1"></div>
      </div>
    </div>

    <div class="grid gap-4 grid-cols-3 p-4">
      <BaseButton class="whitespace-nowrap text-lg font-base" @click="openFamilyModal">
        인원추가
      </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base">
        약정보검색
      </BaseButton>
      <BaseButton class="whitespace-nowrap text-lg font-base">
        알림설정
      </BaseButton>
    </div>

    <main>
      <!-- 오늘의 복약 내역 카드 -->
      <YellowCard class="m-4 flex flex-col">
  <div class="flex flex-row items-end">
    <p class="text-sm font-bold">오늘의 복약 내역</p>
    <span class="text-xs ml-2">
      <template v-if="fetchFailed">
        마이페이지에서 알림 설정을 해야 오늘의 복약 알림을 받을 수 있습니다.
      </template>
      <template v-else>
        {{ todaysMedications.length > 0 ? todaysMedications.map(med => med.name).join(', ') : "약정보 없음" }}
      </template>
    </span>
  </div>
  <div class="flex flex-row items-end">
    <p class="font-bold text-lg">
      {{ fetchFailed ? '' : `${currentTimePeriod} 약을 드셨나요?` }}
    </p>
    <img v-if="!fetchFailed" src="../assets/CheckCircle.svg" alt="약물복용체크" @click="completeMedications" class="cursor-pointer">
  </div>
</YellowCard>



      <!-- 복용 내역 카드 (예시) -->
      <div class="m-4 flex flex-col">
        <p class="text-xl font-bold">복용 내역</p>
        <WhiteCard overrideClass="bg-white">
          <div class="flex flex-row items-center">
            <img src="../assets/logi_nofont.svg" alt="알약이미지" class="w-16 h-16">
            <div class="flex flex-col">
              <p>병명</p>
              <p>기간</p>
              <p>약이름</p>
            </div>
          </div>
        </WhiteCard>
      </div>

      <!-- 캘린더 (예시) -->
      <div class="m-4 flex flex-col">
        <BaseCalendar class="w-full"/>
      </div>
    </main>
  </div>
  <FamilyAddModal :isOpen="isFamilyModalOpen" @close="isFamilyModalOpen = false" />
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { fetchNotificationSettings } from '../api/setalarm';
import { fetchAllDrugCheck } from '../api/drugcheck';
import BaseButton from '../components/BaseButton.vue';
import YellowCard from '../layout/YellowCard.vue';
import WhiteCard from '../layout/WhiteCard.vue';
import HamBugerMenu from '../components/HamBugerMenu.vue';
import NameDropdown from '../components/NameDropdown.vue';
import FamilyAddModal from '../components/FamilyAddModal.vue';
import { useFCM } from '../utils/usefcm';
import BaseCalendar from '../components/BaseCalendar.vue';

// 필요에 따라 VCalendar 컴포넌트도 import 합니다.

defineProps({
  navbarHeight: Number, // ✅ props 정의
});

const { getFCMToken } = useFCM();

// 알림 설정 상태 (기본값은 null)
const notificationSettings = reactive({
  morning: null,
  lunch: null,
  dinner: null,
  sleep: null,
  notificationId: null // 응답에 포함된 notificationId 저장
});

const fetchFailed = ref(false); // 알림 설정 불러오기 실패 여부

// 서버에서 알림 설정 정보를 불러오는 함수
const loadNotificationSettings = async () => {
  try {
    const data = await fetchNotificationSettings();
    
    // 값이 null이면 그대로 유지
    notificationSettings.morning = data.morning ?? null;
    notificationSettings.lunch = data.lunch ?? null;
    notificationSettings.dinner = data.dinner ?? null;
    notificationSettings.sleep = data.sleep ?? null;
    notificationSettings.notificationId = data.notificationId ?? null;

    // 모든 값이 null이면 실패로 간주
    const allNull = [notificationSettings.morning, notificationSettings.lunch, notificationSettings.dinner, notificationSettings.sleep].every(v => v === null);
    fetchFailed.value = allNull;
  } catch (error) {
    console.error('🚨 알림 설정 로드 실패:', error);
    fetchFailed.value = true;
  }
};

// ✅ 모달 상태 관리
const isFamilyModalOpen = ref(false);
const openFamilyModal = () => {
  isFamilyModalOpen.value = true;
};

// ✅ 외부 클릭 감지 함수
const handleClickOutside = (event) => {
  // 예를 들어 특정 모달이 열려 있을 때, 모달 외부를 클릭하면 닫히도록 처리 가능
  if (isFamilyModalOpen.value) {
    const modal = document.querySelector('.modal-class'); // 실제 모달 클래스에 맞게 변경
    if (modal && !modal.contains(event.target)) {
      isFamilyModalOpen.value = false;
    }
  }
};

// 현재 시간대를 계산하는 computed 속성 (설정된 알림 시간만 기준으로)
const currentTimePeriod = computed(() => {
  const now = new Date();
  const currentMinutes = now.getHours() * 60 + now.getMinutes();

  // 문자열 "HH:MM"을 분 단위로 변환하는 함수
  const parseTime = (timeStr) => {
    if (!timeStr) return null;
    const [hour, minute] = timeStr.split(':').map(Number);
    return hour * 60 + minute;
  };

  // 설정된 시간대만 객체 배열로 생성 (null인 값은 제외)
  const periods = [];
  const morning = parseTime(notificationSettings.morning);
  if (morning !== null) {
    periods.push({ label: "아침", minutes: morning });
  }
  const lunch = parseTime(notificationSettings.lunch);
  if (lunch !== null) {
    periods.push({ label: "점심", minutes: lunch });
  }
  const dinner = parseTime(notificationSettings.dinner);
  if (dinner !== null) {
    periods.push({ label: "저녁", minutes: dinner });
  }
  const sleep = parseTime(notificationSettings.sleep);
  if (sleep !== null) {
    periods.push({ label: "자기전", minutes: sleep });
  }

  // 설정된 시간대가 하나도 없으면 빈 문자열 반환
  if (periods.length === 0) return "";

  // 시간 순서대로 정렬 (오름차순)
  periods.sort((a, b) => a.minutes - b.minutes);

  // 현재 시간이 첫 번째 설정된 시간보다 빠르면 첫 번째 시간대 반환
  if (currentMinutes < periods[0].minutes) {
    return periods[0].label;
  }

  // 설정된 시간대 중에서 현재 시간에 해당하는 시간대를 찾음
  for (let i = 0; i < periods.length; i++) {
    // 마지막 요소인 경우
    if (i === periods.length - 1) {
      return periods[i].label;
    }
    // 현재 시간이 두 시간대 사이에 있으면 앞쪽 시간대를 반환
    if (currentMinutes >= periods[i].minutes && currentMinutes < periods[i + 1].minutes) {
      return periods[i].label;
    }
  }

  // 기본적으로 마지막 시간대를 반환 (이론상 도달하지 않음)
  return periods[periods.length - 1].label;
});

// 오늘의 날짜를 'YYYY-MM-DD' 형태로 구하는 함수
const getTodayDate = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, '0');
  const date = today.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${date}`;
};

// 오늘의 복약 내역(약물 리스트)을 담는 ref
const todaysMedications = ref([]);

// 백엔드와 소통하는 것처럼 오늘의 복약 내역 데이터를 가져오는 함수 (더미 데이터 사용)
const fetchTodaysMedications = async () => {
  const todayDate = getTodayDate();
  // 실제 API 호출 예시:
  // const response = await axios.get(`/api/medications?date=${todayDate}&timePeriod=${currentTimePeriod.value}`);
  // 더미 데이터 예시:
  const response = [
    {
      // 처방전 = id, 개별약물 = prescriptionId, 약물명 = name, 시간대 = timePeriod, 복약여부 = taken
      id: 1,
      prescriptionId: 101,
      name: "약물A",
      timePeriod: currentTimePeriod.value,
      taken: false
    },
    {
      id: 2,
      prescriptionId: 102,
      name: "약물B",
      timePeriod: currentTimePeriod.value,
      taken: false
    }
  ];
  todaysMedications.value = response;
};

// 복약 완료 처리 함수 (사용자가 체크하면 호출)
const completeMedications = async () => {
  try {
    // ✅ 한글 시간대를 영어로 변환
    const periodMap = {
      "아침": "morning",
      "점심": "lunch",
      "저녁": "dinner",
      "자기전": "sleep",
    };
    
    const timePeriod = periodMap[currentTimePeriod.value] || "";
    
    if (!timePeriod) {
      alert("현재 시간대를 인식할 수 없습니다.");
      return;
    }

    // ✅ 디버그 로그 출력
    console.log("🔍 [completeMedications] 현재 시간대:", timePeriod);
    console.log("🔍 [completeMedications] 요청 바디:", { time: timePeriod });

    // ✅ API 호출 (timePeriod만 전송)
    await fetchAllDrugCheck(timePeriod);  // ✅ 올바른 형식으로 전달

    // ✅ 상태 업데이트
    todaysMedications.value.forEach((med) => (med.taken = true));

    alert("복약 완료 처리가 완료되었습니다.");
  } catch (error) {
    console.error("❌ [completeMedications] 복약 완료 처리 중 오류 발생:", error);
    alert("복약 완료 처리에 실패했습니다.");
  }
};



// ✅ 컴포넌트가 마운트되면 데이터 및 이벤트 리스너 등록
onMounted(async () => {
  // 오늘의 복약 내역 불러오기
  fetchTodaysMedications();

  // 알림 설정 불러오기
  await loadNotificationSettings();

  // 클릭 이벤트 리스너 등록
  document.addEventListener("click", handleClickOutside);

  // FCM 토큰 가져오기 (비동기 예외 처리)
  try {
    await getFCMToken();
  } catch (error) {
    console.error("FCM 초기화 실패:", error);
  }
});
</script>
