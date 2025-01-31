<template>
  <button @click="showNotifications">
    <img src="../assets/headnotice.svg" alt="알림" class="w-6 h-6" />
  </button>
</template>

<script setup>
const showNotifications = () => {
  console.log("알림 창 열기");
};
</script>
