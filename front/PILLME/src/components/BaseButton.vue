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
  bgColor: { type: String, default: "bg-green-700" }, // 배경색 (기본값: 초록색)
  textColor: { type: String, default: "text-white" }, // 텍스트 색상
  hoverColor: { type: String, default: "hover:bg-green-800" }, // 호버 효과 색상
  size: { type: String, default: "md" }, // sm, md, lg
  disabled: { type: Boolean, default: false },
  onClick: Function,
});

const buttonClass = computed(() => ({
  "w-full rounded-full transition-all duration-300 flex justify-center items-center font-semibold":
    true,
  [props.bgColor]: true, // 배경색
  [props.textColor]: true, // 텍스트 색상
  [props.hoverColor]: true, // 호버 효과
  "py-[4px] px-[8px] text-sm": props.size === "sm",
  "py-[8px] px-[16px] text-base": props.size === "md",
  "py-[12px] px-[24px] text-lg": props.size === "lg",
  "opacity-50 cursor-not-allowed": props.disabled,
}));
</script>
