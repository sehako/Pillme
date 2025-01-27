<template>
  <button
    :class="buttonClass"
    :disabled="disabled"
    @click="onClick"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  bgColor: { type: String, default: "bg-green-700" }, // 배경색
  textColor: { type: String, default: "text-white" }, // 텍스트 색상
  hoverColor: { type: String, default: "hover:bg-green-800" }, // 호버 효과 색상
  size: { type: String, default: "md" }, // sm, md, lg
  disabled: { type: Boolean, default: false },
  onClick: Function,
});

const buttonClass = computed(() => [
  "w-full rounded-full transition-all duration-300 flex justify-center items-center font-semibold",
  props.bgColor, // 배경색
  props.textColor, // 텍스트 색상
  props.hoverColor, // 호버 효과
  {
    // ✅ 최소 10px 유지 & 반응형 설정 (text-[10px] ~ text-lg)
    "py-2 px-4 text-[10px] sm:text-sm md:text-base":
      props.size === "sm", // 모바일 최소 10px
    "py-3 px-6 text-[12px] sm:text-base md:text-lg":
      props.size === "md", // 기본값: 12px 이상
    "py-4 px-8 text-[14px] sm:text-lg md:text-xl":
      props.size === "lg", // 큰 버튼 14px 이상 유지
    "opacity-50 cursor-not-allowed": props.disabled,
  },
]);
</script>
