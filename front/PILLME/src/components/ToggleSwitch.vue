<template>
  <label class="relative inline-flex items-center cursor-pointer">
    <input 
      type="checkbox" 
      class="sr-only" 
      :checked="modelValue" 
      @change="onChange"
    />
    <!-- 토글 트랙 -->
    <div 
      :class="['w-10 h-4 rounded-full transition-colors duration-200', modelValue ? 'bg-blue-600' : 'bg-gray-300']"
    >
      <!-- 토글 노브 -->
      <div 
        :class="['absolute top-0 left-0 w-6 h-6 bg-white rounded-full shadow-md transition-transform duration-200 ease-in-out', modelValue ? 'translate-x-6' : 'translate-x-0']"
      ></div>
    </div>
  </label>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  }
});

const emit = defineEmits(['update:modelValue']);

function onChange(e) {
  emit('update:modelValue', e.target.checked);
}
</script>
