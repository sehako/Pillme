<template>
    <div class="min-h-screen w-full flex flex-col items-center relative" :style="{ marginTop: `${topbarHeight}px` }">
      
      <!-- 전체 알림 삭제 -->
      <div class="w-full px-4">
        <p
          class="text-[#4E7351] font-semibold text-center py-3 cursor-pointer bg-white border-b border-gray-300 shadow-sm w-full"
        >
          전체 알림 삭제
        </p>
      </div>
  
      <!-- 알림 리스트 -->
      <div class="w-full px-4 space-y-2 mt-2">
        <template v-for="(notification, index) in notifications" :key="index">
          <div 
            v-if="notification.type === 'admin_request'"
            ref="notificationRefs"
            @click="openDialog(notification, index, $event)"
            class="cursor-pointer"
          >
            <AdminRequestItem
              :title="notification.title"
              :username="notification.username"
              :message="notification.message"
              :date="notification.date"
              :is-read="notification.isRead"
              class="w-full"
            />
          </div>
          <NotificationItem
            v-else
            :title="notification.title"
            :message="notification.message"
            :date="notification.date"
            :is-read="notification.isRead"
            class="w-full"
          />
        </template>
      </div>

      <!-- ✅ 관리자 요청 다이얼로그 (해당 알림 중앙에 오버레이) -->
      <div 
        v-if="isDialogOpen"
        class="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50"
        @click.self="isDialogOpen = false"
      >
        <AdminRequestDialog
          class="absolute transition-transform duration-300 bg-white rounded-lg p-6 shadow-lg"
          :style="{ 
            width: dialogSize.width, 
            maxWidth: '90%', 
            top: `${dialogPosition.top}px`, 
            left: `${dialogPosition.left}px` 
          }"
          :username="selectedNotification.username"
          @close="isDialogOpen = false"
          @accept="handleAccept"
          @reject="handleReject"
        />
      </div>
    </div>
</template>

  
<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import NotificationItem from "../components/NotificationItem.vue";
import AdminRequestItem from "../components/AdminRequestItem.vue";
import AdminRequestDialog from "../components/AdminRequestDialog.vue";

const notifications = ref([
  { type: "comment", title: "댓글이 달렸습니다.", message: "나 지금 먹는 약 있는데 이거...", date: "2025.01.21", isRead: true },
  { type: "admin_request", title: "관리자 추가 알림", username: "이싸피", message: "님이 관리자가 되고 싶어 합니다. 동의하시겠습니까?", date: "2025.01.21", isRead: false },
  { type: "medication_reminder", title: "약을 복용하셨나요?", message: "복용 상태를 완료하세요!", date: "2025.01.21", isRead: true },
]);

const topbarHeight = ref(0);
const isDialogOpen = ref(false);
const selectedNotification = ref(null);
const dialogPosition = ref({ top: 0, left: 0 });
const dialogSize = ref({ width: "320px" }); // 기본 다이얼로그 크기
let activeNotificationRect = null; // 현재 선택된 알림의 위치 저장

// ✅ 상단바 높이 동적 계산
onMounted(() => {
  const topbar = document.querySelector("#topbar");
  if (topbar) {
    topbarHeight.value = topbar.offsetHeight;
  }
});

// ✅ 다이얼로그 위치 및 크기 업데이트 함수
const updateDialogPosition = () => {
  if (!isDialogOpen.value || !activeNotificationRect) return;

  // ✅ 클릭된 알림의 위치 계산
  const rect = activeNotificationRect;

  // ✅ 다이얼로그 크기 반응형 조절 (화면 크기에 맞게 변경)
  const maxDialogWidth = Math.min(window.innerWidth * 0.9, 400); // 최대 400px, 작은 화면은 90%
  const dialogHeight = 220; // 다이얼로그 예상 높이
  dialogSize.value.width = `${maxDialogWidth}px`; // 반응형으로 조절된 너비 적용

  let top = rect.top + window.scrollY + rect.height / 2 - dialogHeight / 2;
  let left = rect.left + window.scrollX + rect.width / 2 - maxDialogWidth / 2;

  // ✅ 화면 밖으로 넘어가지 않도록 조정
  top = Math.max(20, Math.min(top, window.innerHeight - dialogHeight - 20));
  left = Math.max(20, Math.min(left, window.innerWidth - maxDialogWidth - 20));

  dialogPosition.value = { top, left };
};

// ✅ 다이얼로그 열기 (관리자 요청일 때만)
const openDialog = async (notification, index, event) => {
  if (notification.type !== "admin_request") return; // ✅ 관리자 요청이 아닐 경우 종료

  activeNotificationRect = event.currentTarget.getBoundingClientRect(); // 위치 저장
  selectedNotification.value = notification;
  isDialogOpen.value = true;

  await nextTick(); // DOM 업데이트 후 실행
  updateDialogPosition();
};

// ✅ 창 크기 & 스크롤 변경 시 다이얼로그 위치 업데이트
onMounted(() => {
  window.addEventListener("resize", updateDialogPosition);
  window.addEventListener("scroll", updateDialogPosition, true);
});

// ✅ 컴포넌트 언마운트 시 이벤트 제거
onUnmounted(() => {
  window.removeEventListener("resize", updateDialogPosition);
  window.removeEventListener("scroll", updateDialogPosition, true);
});

// ✅ 승인/거절 처리
const handleAccept = () => {
  console.log("✅ 관리자 요청 승인됨");
  isDialogOpen.value = false;
  activeNotificationRect = null;
};

const handleReject = () => {
  console.log("❌ 관리자 요청 거절됨");
  isDialogOpen.value = false;
  activeNotificationRect = null;
};
</script>
