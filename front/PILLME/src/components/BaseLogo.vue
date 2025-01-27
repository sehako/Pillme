<template>
  <img :src="src" alt="PILLME Logo" :class="logoClass" />
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  src: { type: String, required: true }, // 로고 이미지 경로
  size: { type: String, default: "md" }, // sm, md, lg, xl (기본값: md)
});

const logoClass = computed(() => ({
  "w-24 h-24 sm:w-32 sm:h-32 md:w-40 md:h-40 lg:w-48 lg:h-48": props.size === "md",
  "w-16 h-16 sm:w-24 sm:h-24 md:w-32 md:h-32 lg:w-40 lg:h-40": props.size === "sm",
  "w-32 h-32 sm:w-40 sm:h-40 md:w-48 md:h-48 lg:w-56 lg:h-56": props.size === "lg",
  "w-40 h-40 sm:w-48 sm:h-48 md:w-56 md:h-56 lg:w-64 lg:h-64": props.size === "xl",
}));
</script>
