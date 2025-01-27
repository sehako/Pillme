<template>
  <button class="w-full flex items-center gap-2 p-3 rounded-lg border transition-all duration-300 hover:bg-gray-100">
    <img :src="iconSrc" class="w-5 h-5" />
    <slot />
  </button>
</template>

<script setup>
defineProps({
  iconSrc: { type: String, required: true },
});
</script>
