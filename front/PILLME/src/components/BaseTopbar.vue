<template>
  <div class="w-full flex items-center bg-[#A3BFA5] px-4 h-16">
    <!-- 왼쪽: 햄버거 메뉴 -->
    <div class="flex items-center">
      <HamBugerMenu />
    </div>

    <!-- 중앙: 사용자 이름 드롭다운 -->
    <div class="flex-1 flex justify-center">
      <NameDropdown />
    </div>

    <!-- 오른쪽: 알림 아이콘 -->
    <div class="flex items-center">
      <BaseNotificationIcon />
    </div>
  </div>
</template>

<script setup>
import BaseNotificationIcon from "./BaseNotificationIcon.vue"
import NameDropdown from "./NameDropdown.vue"
import HamBugerMenu from "./HamBugerMenu.vue"
</script>