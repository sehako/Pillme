<template>
  <div :class="`${cardClass.join(' ')} ${overrideClass}`">
    <slot />
  </div>
</template>


<script setup>
import { computed } from "vue";

const props = defineProps({
  bgColor: { type: String, default: "bg-white" }, // 기본 배경색
  textColor: { type: String, default: "text-gray-900" }, // 기본 텍스트 색상
  shadow: { type: String, default: "shadow-md" }, // 기본 그림자 효과
  padding: { type: String, default: "p-4" }, // 기본 패딩 설정
  rounded: { type: String, default: "rounded-lg" }, // 둥근 모서리 설정
  border: { type: String, default: "border border-gray-200" }, // 기본 테두리 설정
  overrideClass: { type: String, default: "" }, // ✅ 추가된 부분 (스타일 덮어쓰기)
});

const cardClass = computed(() => [
  "transition-all duration-300",
  props.bgColor, // 배경색
  props.textColor, // 텍스트 색상
  props.shadow, // 그림자 효과
  props.padding, // 패딩 설정
  props.rounded, // 둥근 모서리
  props.border, // 테두리 스타일
]);
</script>
