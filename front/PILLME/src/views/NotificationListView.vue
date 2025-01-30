<template>
    <div class="min-h-screen w-full flex flex-col items-center" :style="{ marginTop: `${topbarHeight}px` }">
      
      <div class="w-full px-4">
        <p
          class="text-[#4E7351] font-semibold text-center py-3 cursor-pointer bg-white border-b border-gray-300 shadow-sm w-full"
        >
          전체 알림 삭제
        </p>
      </div>
  
      <div class="w-full px-4 space-y-2 mt-2">
        <template v-for="(notification, index) in notifications" :key="index">
          <AdminRequestItem
            v-if="notification.type === 'admin_request'"
            :title="notification.title"
            :username="notification.username"
            :message="notification.message"
            :date="notification.date"
            :is-read="notification.isRead"
            class="w-full"
          />
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
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import NotificationItem from "../components/NotificationItem.vue";
  import AdminRequestItem from "../components/AdminRequestItem.vue";
  
  const notifications = ref([
    {
      type: "comment",
      title: "댓글이 달렸습니다.",
      message: "나 지금 먹는 약 있는데 이거...",
      date: "2025.01.21",
      isRead: true,
    },
    {
      type: "admin_request",
      title: "관리자 추가 알림",
      username: "이싸피",
      message: "님이 관리자가 되고 싶어 합니다. 동의하시겠습니까?",
      date: "2025.01.21",
      isRead: false,
    },
    {
      type: "medication_reminder",
      title: "약을 복용하셨나요?",
      message: "복용 상태를 완료하세요!",
      date: "2025.01.21",
      isRead: true,
    },
  ]);
  
  const topbarHeight = ref(0);
  
  onMounted(() => {
    const topbar = document.querySelector("#topbar");
    if (topbar) {
      topbarHeight.value = topbar.offsetHeight; 
    }
  });
  </script>
  