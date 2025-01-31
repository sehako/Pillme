<template>
  <div class="flex items-center border border-gray-300 rounded-lg px-3 py-2 shadow-sm">
    <!-- 아이콘이 있을 경우에만 이미지 표시 -->
    <img v-if="icon" :src="icon" alt="input-icon" class="w-5 h-5 mr-2" />
    
    <!-- 입력 필드 -->
    <input 
      :type="type" 
      :placeholder="placeholder" 
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      class="w-full text-gray-700 outline-none placeholder-gray-400 bg-transparent"
      required
    />
  </div>
</template>

<script setup>
defineProps({
  modelValue: String, // 부모 컴포넌트에서 값을 내려받음
  type: { type: String, default: "text" },
  placeholder: String,
  icon: String, // 아이콘 경로
});
defineEmits(["update:modelValue"]); // 값 변경을 부모에게 알리는 이벤트
</script>
