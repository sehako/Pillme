<template>
  <img
    :src="src"
    alt="PILLME Logo"
    :class="[
      logoClass,                // 사이즈별 너비(w-XX) 지정
      'object-contain',         // 이미지 전체 표시, 비율 유지
      'object-center',          // 이미지를 수직/수평 중앙 정렬
    ]"
  />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  src: { type: String, required: true },    // 로고 이미지 경로
  size: { type: String, default: 'md' },    // sm, md, lg, xl (기본값: md)
})

/**
 * - 고정된 높이(h-XX)를 없애고 h-auto로 설정하여
 *   이미지 비율을 유지하면서 높이를 자동으로 조절.
 * - object-contain + object-center 조합을 사용해
 *   이미지 전체가 가로 너비 안에 깔끔하게 표시되도록 함.
 */
const logoClass = computed(() => {
  switch (props.size) {
    case 'sm':
      return 'w-16 sm:w-24 md:w-32 lg:w-40 h-auto'
    case 'lg':
      return 'w-32 sm:w-40 md:w-48 lg:w-56 h-auto'
    case 'xl':
      return 'w-40 sm:w-48 md:w-56 lg:w-64 h-auto'
    // 기본값: 'md'
    default:
      return 'w-24 sm:w-32 md:w-40 lg:w-48 h-auto'
  }
})
</script>
