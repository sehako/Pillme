<template>
  <button @click="showNotifications">
    <img src="../assets/headnotice.svg" alt="알림" class="w-6 h-6" />
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const showNotifications = () => {
  router.push('/notificationlist');
};
</script>
