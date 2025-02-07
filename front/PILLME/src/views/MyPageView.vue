<template>
  <div class="flex flex-row h-screen">
    <!-- 왼쪽 메뉴 (대분류) -->
    <aside 
      class="left-0 top-0 p-4 flex flex-col border-r border-gray-200"
      :style="{ height: `calc(100vh - ${navbarHeight * 1.6}px)`, width: '33.33%' }"
    >
      <!-- ✅ 검색창 (검색어 입력 시 모든 세부 메뉴에서 필터링) -->
      <div class="pb-4 border-b border-gray-300">
        <input 
          v-model="searchQuery"
          type="text" 
          placeholder="메뉴 검색" 
          class="w-full py-2 pl-2 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#3D5A3F]
                  text-[clamp(12px,2vw,16px)]"
        />
      </div>

      <!-- ✅ 대분류 메뉴 (검색어가 없을 때만 보이게 처리) -->
      <nav class="mt-4 flex-1" v-if="!searchQuery">
        <ul>
          <li v-for="(item, index) in menuItems" 
              :key="index" 
              @click="selectedMenu = item"
              class="py-2 px-2 rounded cursor-pointer text-[clamp(12px,2vw,16px)] whitespace-nowrap"
              :class="{ 'font-bold text-[#3D5A3F] bg-[#A3BFA5]': selectedMenu === item }">
            {{ item }}
          </li>
        </ul>
      </nav>
    </aside>

    <!-- 오른쪽 콘텐츠 (검색 결과 / 세부 메뉴) -->
    <div class="flex-1 flex flex-col justify-start items-start border-l border-gray-200 p-4"
         :style="{ width: '66.66%' }">
      <h1 class="text-xl font-bold mb-4">
        {{ searchQuery ? "검색 결과" : selectedMenu || "메뉴 선택" }}
      </h1>

      <!-- ✅ 검색 결과가 있을 때 -->
      <ul v-if="searchQuery && filteredSubMenus.length > 0" class="space-y-4">
        <li v-for="(menu, index) in filteredSubMenus" 
            :key="index"
            class="cursor-pointer text-lg hover:text-[#3D5A3F]"
            @click="$router.push(menu.route)">
          {{ menu.name }}
        </li>
      </ul>

      <!-- ✅ 검색 결과가 없을 때 -->
      <p v-if="searchQuery && filteredSubMenus.length === 0" class="text-gray-500">
        검색 결과가 없습니다.
      </p>

      <!-- ✅ 기존 대분류 선택 시 표시 (검색어 없을 때만) -->
      <ul v-if="!searchQuery && selectedMenu" class="space-y-4">
        <li v-for="(menu, index) in subMenus[selectedMenu]" 
            :key="index"
            class="cursor-pointer text-lg hover:text-[#3D5A3F]"
            @click="$router.push(menu.route)">
          {{ menu.name }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed} from 'vue';

const props = defineProps({
  navbarHeight: {
    type: Number,
    required: true
  }
});

// ✅ 대분류 메뉴
const menuItems = ["계정 및 보안", "설정", "고객 지원"];
const selectedMenu = ref(null); // 선택된 대분류 메뉴

// ✅ 세부 메뉴 목록 (라우트 포함)
const subMenus = ref({
  "계정 및 보안": [
    { name: "개인정보 관리", route: "/mypage/personal-info" },
    { name: "로그인 및 보안", route: "/mypage/login-security" },
    { name: "비밀번호 변경", route: "/mypage/pw-change" }
  ],
  "설정": [
    { name: "알림 설정", route: "/mypage/alarm" },
    { name: "디스플레이 설정", route: "/mypage/display-settings" }
  ],
  "고객 지원": [
    { name: "문의하기", route: "/mypage/support" },
    { name: "FAQ", route: "/mypage/faq" }
  ]
});

// ✅ 검색어 상태
const searchQuery = ref("");

// ✅ 검색 필터링된 세부 메뉴 목록 (대분류 관계없이 전체에서 검색)
const filteredSubMenus = computed(() => {
  if (!searchQuery.value) return []; // 검색어가 없으면 빈 배열 반환

  return Object.values(subMenus.value)
    .flat()
    .filter(menu => menu.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
});
</script>
