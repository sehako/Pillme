<template>
  <div class="flex items-center p-4 w-full">
    <BaseLogo :src="logoSrc" size="xxs" />

    <div class="ml-4">
      <p class="text-lg font-semibold">{{ name }}</p>
      <p class="text-sm text-gray-500">{{ birthdate }} ({{ age }}세)</p>
    </div>
  </div>
</template>

<script setup>
import BaseLogo from "../components/BaseLogo.vue";
import logoSrc from "../assets/logi_nofont.svg";

defineProps({
  name: String,
  birthdate: String,
  age: Number
});
</script>