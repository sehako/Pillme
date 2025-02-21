<template>
  <p :class="`text-sm sm:text-base md:text-lg font-medium ${textColor} md-2`">
    {{ textBefore }}
    <span :class="`text-lg sm:text-xl md:text-2xl font-bold ${highlightColor}`">
      {{ highlightText }}
    </span>
  </p>
</template>

<script setup>
defineProps({
  textBefore: { type: String, required: true }, // 앞부분 텍스트
  highlightText: { type: String, required: true }, // 강조할 텍스트 (ex: PILLME)
  textColor: { type: String, default: "text-gray-700" }, // 기본 텍스트 색상
  highlightColor: { type: String, default: "text-[#4E7351]" } // 강조 텍스트 색상
});
</script>
