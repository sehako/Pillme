<template>
    <div :class="['flex items-center p-4 shadow-md rounded-lg w-full', isRead ? 'bg-white' : 'bg-[#EDF3FF]']">
        
        <BaseLogo :src="logoSrc" size="xxs" class="flex-shrink-0" />
  
        <div class="ml-4 flex-1">
          <p class="text-sm font-semibold">{{ title }}</p>
          <p class="text-xs text-gray-500 mt-1">
            <span class="font-medium text-gray-500">{{ username }}</span> {{ message }}
          </p>
          <p class="text-xs text-gray-400 mt-2">{{ date }}</p>
        </div>
  
        <div class="flex flex-col space-y-2 ml-6">
          <BaseButton textColor="text-white" size="sm" overrideClass="!bg-[#EF7C8E] !px-3 !py-1.5 !text-xs !w-14">
            동의
          </BaseButton>
          <BaseButton textColor="text-gray-700" size="sm" overrideClass="!bg-gray-200 !px-3 !py-1.5 !text-xs !w-14">
            거절
          </BaseButton>
        </div>
      </div>
    </template>
    
    <script setup>
    import BaseLogo from "../components/BaseLogo.vue";
    import BaseButton from "../components/BaseButton.vue";
    import logoSrc from "../assets/logi_nofont.png"; 
    
    defineProps({
      title: String,
      username: String,
      message: String,
      date: String,
      isRead: Boolean,
    });
    </script>
    