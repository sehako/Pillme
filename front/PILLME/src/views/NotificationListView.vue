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
          <div ref="notificationRefs" @click="openDialog(notification, index, $event)">
            <AdminRequestItem
              v-if="notification.type === 'admin_request'"
              :title="notification.title"
              :username="notification.username"
              :message="notification.message"
              :date="notification.date"
              :is-read="notification.isRead"
              class="w-full cursor-pointer"
            />
            <NotificationItem
              v-else
              :title="notification.title"
              :message="notification.message"
              :date="notification.date"
              :is-read="notification.isRead"
              class="w-full"
            />
          </div>
        </template>
      </div>

      <!-- ✅ 관리자 요청 다이얼로그 (해당 알림 위에 오버레이) -->
      <div 
        v-if="isDialogOpen"
        class="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50"
        @click.self="isDialogOpen = false"
      >
        <AdminRequestDialog
          class="absolute transition-transform duration-300 bg-white rounded-lg p-6 shadow-lg"
          :style="{ top: `${dialogPosition.top}px`, left: `${dialogPosition.left}px` }"
          :username="selectedNotification.username"
          @close="isDialogOpen = false"
          @accept="handleAccept"
          @reject="handleReject"
        />
      </div>
    </div>
</template>

  
<script setup>
import { ref, onMounted, nextTick } from "vue";
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

// ✅ 상단바 높이 동적 계산
onMounted(() => {
  const topbar = document.querySelector("#topbar");
  if (topbar) {
    topbarHeight.value = topbar.offsetHeight;
  }
});

// ✅ 다이얼로그 위치 조정
const openDialog = async (notification, index, event) => {
  selectedNotification.value = notification;
  isDialogOpen.value = true;

  await nextTick(); // DOM 업데이트 후 실행

  // ✅ 클릭된 알림의 위치 계산
  const rect = event.currentTarget.getBoundingClientRect();
  const dialogWidth = 320; // 다이얼로그 예상 너비
  const dialogHeight = 200; // 다이얼로그 예상 높이

  let top = rect.top + window.scrollY - dialogHeight / 2 + rect.height / 2;
  let left = rect.left + window.scrollX - dialogWidth / 2 + rect.width / 2;

  // ✅ 화면 밖으로 넘어가지 않도록 조정
  top = Math.max(20, Math.min(top, window.innerHeight - dialogHeight - 20));
  left = Math.max(20, Math.min(left, window.innerWidth - dialogWidth - 20));

  dialogPosition.value = { top, left };
};

// ✅ 승인/거절 처리
const handleAccept = () => {
  console.log("✅ 관리자 요청 승인됨");
  isDialogOpen.value = false;
};

const handleReject = () => {
  console.log("❌ 관리자 요청 거절됨");
  isDialogOpen.value = false;
};
</script>
