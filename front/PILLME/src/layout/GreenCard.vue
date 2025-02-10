<script setup>
import { computed } from "vue";

const props = defineProps({
  bgColor: { type: String, default: "bg-[#9DBB9F26]" },
  textColor: { type: String, default: "text-gray-900" },
  shadow: { type: String, default: "shadow-md" },
  rounded: { type: String, default: "rounded-lg" },
  border: { type: String, default: "border border-gray-200" },
  overrideClass: { type: String, default: "" },
});

// ✅ 반응형 고정 길이 설정 (min-w, max-w) 
const cardClass = computed(() => [
  "min-w-[200px]",  // 최소 너비 200px
  "max-w-[200px]",  // 최대 너비 300px
  "w-full",         // 기본적으로 가능한 최대 너비 사용 (부모에 따라 유동적)
  props.bgColor,
  props.textColor,
  props.shadow,
  props.rounded,
  props.border,
  props.overrideClass,
].join(" "));
</script>

<template>
  <div :class="cardClass">
    <slot />
  </div>
</template>
