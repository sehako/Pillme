<template>
  <button
    :class="[buttonClass, overrideClass]"
    :disabled="disabled"
    @click="onClick"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  bgColor: { type: String, default: "bg-[#4E7351]" }, // 배경색
  textColor: { type: String, default: "text-white" }, // 텍스트 색상
  hoverColor: { type: String, default: "hover:bg-[#3D5A3F]" }, // 호버 효과 색상
  size: { type: String, default: "sm" }, // sm, md, lg
  disabled: { type: Boolean, default: false },
  onClick: Function,
  overrideClass: { type: String, default: "" }, // ✅ 추가된 부분 (스타일 덮어쓰기)
});

const buttonClass = computed(() => [
  "min-w-full rounded-full transition-all duration-300 flex justify-center items-center font-semibold",
  props.bgColor, // 배경색
  props.textColor, // 텍스트 색상
  props.hoverColor, // 호버 효과
  {
    "py-2 px-4 text-[10px] sm:text-sm md:text-base": props.size === "sm",
    "py-3 px-6 text-[12px] sm:text-base md:text-lg": props.size === "md",
    "py-4 px-8 text-[14px] sm:text-lg md:text-xl": props.size === "lg",
    "opacity-50 cursor-not-allowed": props.disabled,
  },
]);

</script>
