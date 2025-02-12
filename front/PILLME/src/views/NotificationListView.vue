<template>
  <div class="!min-h-full !min-w-full flex flex-col items-center relative">
    <!-- 전체 알림 삭제 버튼 -->
    <div class="w-full px-4">
      <button 
        @click="handleDeleteAll"
        class="w-full py-3 text-center font-semibold text-[#4E7351] bg-white border-b border-gray-300 shadow-sm"
      >
        전체 알림 삭제
      </button>
    </div>

    <!-- 알림 리스트 -->
    <div class="w-full px-4 mt-2 space-y-2">
      <div v-for="(notification, index) in notifications" :key="notification.Id">
        <!-- 관리자 요청 알림 -->
        <!-- 'DEPENDENCY_REQUEST'이외 다른 다이얼로그 생성필요 -->
        <AdminRequestItem 
  v-if="['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(notification.code)"
  :title="notification.content"
  :date="formatDate(notification.createdAt)"
  :confirm="notification.confirm"
  class="w-full cursor-pointer"
  @click="openDialog(notification, index, $event)"
/>


        <!-- 일반 알림 -->
        <NotificationItem
    v-else
    :title="notification.content"
    :date="formatDate(notification.createdAt)"
      :confirm="notification.confirm"
        :notificationId="notification.notificationId"
  @deleteNotification="handleDelete"
    class="w-full"
  />

      </div>
    </div>

    <!-- 관리자 요청 다이얼로그 -->
    <div 
      v-if="isDialogOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30"
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
        :username="selectedNotification?.content"
        :id="selectedNotification?.senderId"
        @close="isDialogOpen = false"
        @accept="handleAccept"
        @reject="handleReject"
      />
    </div>
  </div>
</template>
 
<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { fetchNotifications, deleteNotification } from "../api/notify";
import NotificationItem from "../components/NotificationItem.vue";
import AdminRequestItem from "../components/AdminRequestItem.vue";
import AdminRequestDialog from "../components/AdminRequestDialog.vue";

const notifications = ref([]);
const isDialogOpen = ref(false);
const dialogPosition = ref({ top: 0, left: 0 });
const dialogSize = ref({ width: "320px" });
const selectedNotification = ref(null);
let activeNotificationRect = null;

const loadNotifications = async () => {
  notifications.value = await fetchNotifications();
  console.log("📌 Fetched Notifications:", JSON.stringify(notifications.value, null, 2));
};


const formatDate = (timestamp) => {
  if (!timestamp) return ""; // undefined 방지
  const date = new Date(timestamp);
  return date.toLocaleString("ko-KR", {  
    year: "numeric", month: "2-digit", day: "2-digit",
    hour: "2-digit", minute: "2-digit", second: "2-digit"
  }); 
};
// 전체 알림 삭제
// const handleDeleteAll = async () => {
//   if (await deleteAllNotifications()) {
//     notifications.value = [];
//   }
// };
const handleDelete = async (notificationId) => {
  if (!notificationId) {
    console.error("❌ 유효하지 않은 notificationId");
    return;
  }

  const success = await deleteNotification([notificationId]); // ✅ API 호출 (단일 ID만 포함)
  
  if (success) {
    console.log(`🚀 알림 삭제 성공: ${notificationId}`);
    notifications.value = notifications.value.filter(n => n.notificationId !== notificationId);
    isDialogOpen.value = false; // ✅ 다이얼로그 닫기
  } else {
    console.error("❌ 알림 삭제 실패");
  }
};


const handleReject = async ({ id }) => {
  console.log("🚨 거절된 관리자 요청 senderId:", id);

  if (!id) {
    console.error("❌ 유효하지 않은 senderId");
    return;
  }

  // ✅ senderId를 기반으로 notificationId 찾기
  const notificationIds = notifications.value
    .filter(n => n.senderId === id)
    .map(n => n.notificationId); // ✅ Array(Number) 형식으로 변환

  if (notificationIds.length === 0) {
    console.error("❌ 해당 senderId와 일치하는 notificationId를 찾을 수 없음.");
    return;
  }

  const success = await deleteNotification(notificationIds); // ✅ API 호출
  if (success) {
    console.log("🚀 관리자 요청 거절 후 알림 삭제 성공:", notificationIds);
    notifications.value = notifications.value.filter(n => !notificationIds.includes(n.notificationId));
    isDialogOpen.value = false; // ✅ 다이얼로그 닫기
  } else {
    console.error("❌ 알림 삭제 실패");
  }
};

const handleAccept = async ({ id }) => {
  console.log("✅ 승인된 관리자 요청 senderId:", id);

  if (!id) {
    console.error("❌ 유효하지 않은 senderId");
    return;
  }

  // ✅ senderId를 기반으로 notificationId 찾기
  const notificationIds = notifications.value
    .filter(n => n.senderId === id)
    .map(n => n.notificationId); // ✅ Array(Number) 형식으로 변환

  if (notificationIds.length === 0) {
    console.error("❌ 해당 senderId와 일치하는 notificationId를 찾을 수 없음.");
    return;
  }

  const success = await deleteNotification(notificationIds); // ✅ API 호출
  if (success) {
    console.log("🚀 관리자 요청 승인 후 알림 삭제 성공:", notificationIds);
    notifications.value = notifications.value.filter(n => !notificationIds.includes(n.notificationId));
    isDialogOpen.value = false; // ✅ 다이얼로그 닫기
  } else {
    console.error("❌ 알림 삭제 실패");
  }
};



// 관리자 요청 다이얼로그 열기
const openDialog = async (notification, index, event) => {
  console.log("📌 Clicked Notification:", notification);

  if (!['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(notification.code)) return;

  activeNotificationRect = event.currentTarget.getBoundingClientRect();
  selectedNotification.value = notification;
  isDialogOpen.value = true;

  await nextTick();
  updateDialogPosition();
};

// 다이얼로그 위치 업데이트
const updateDialogPosition = () => {
  if (!isDialogOpen.value || !activeNotificationRect) return;

  const rect = activeNotificationRect;
  const maxDialogWidth = Math.min(window.innerWidth * 0.9, 400);
  const dialogHeight = 220;
  dialogSize.value.width = `${maxDialogWidth}px`;

  let top = rect.top + window.scrollY + rect.height / 2 - dialogHeight / 2;
  let left = rect.left + window.scrollX + rect.width / 2 - maxDialogWidth / 2;

  top = Math.max(20, Math.min(top, window.innerHeight - dialogHeight - 20));
  left = Math.max(20, Math.min(left, window.innerWidth - maxDialogWidth - 20));

  dialogPosition.value = { top, left };
};

onMounted(() => {
  loadNotifications();
  window.addEventListener("resize", updateDialogPosition);
  window.addEventListener("scroll", updateDialogPosition, true);
});

onUnmounted(() => {
  window.removeEventListener("resize", updateDialogPosition);
  window.removeEventListener("scroll", updateDialogPosition, true);
});
</script>
