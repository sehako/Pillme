<template>
  <button
    :class="buttonClass"
    @click="$emit('click')"
  >
    <img :src="iconSrc" alt="social-icon" class="w-5 h-5 sm:w-6 sm:h-6 md:w-7 md:h-7" />
    <span class="text-[10px] sm:text-sm md:text-base font-medium text-gray-700">
      <slot />
    </span>
  </button>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  iconSrc: { type: String, required: true }, // 아이콘 경로
  bgColor: { type: String, default: "bg-white" }, // 배경색
  textColor: { type: String, default: "text-gray-700" }, // 텍스트 색상
  borderColor: { type: String, default: "border-gray-300" }, // 테두리 색상
  hoverColor: { type: String, default: "hover:bg-gray-100" }, // 호버 색상
});

defineEmits(["click"]);

const buttonClass = computed(() => `
  w-full flex items-center justify-start gap-2 px-4 py-2 sm:py-3 rounded-full border transition-all duration-300
  active:scale-95 md:w-80 ${props.bgColor} ${props.textColor} ${props.borderColor} ${props.hoverColor}
`);
</script>
