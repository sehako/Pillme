<template>
  <div class="flex items-center justify-center gap-2 mt-4">
    <!-- 이전 페이지 버튼 -->
    <button
      @click="$emit('page-change', currentPage - 1)"
      :disabled="currentPage === 0"
      :class="[
        'px-3 py-2 text-sm font-medium rounded-lg',
        currentPage === 0
          ? 'text-gray-400 bg-gray-100 cursor-not-allowed'
          : 'text-black bg-white hover:bg-gray-100',
      ]"
    >
      이전
    </button>

    <!-- 페이지 번호 버튼들 -->
    <div class="flex gap-1">
      <button
        v-for="page in displayedPages"
        :key="page"
        @click="$emit('page-change', page - 1)"
        :class="[
          'px-3 py-2 text-sm font-medium rounded-lg',
          currentPage + 1 === page
            ? 'bg-[#9DBB9F] text-white'
            : 'bg-white text-black hover:bg-gray-100',
        ]"
      >
        {{ page }}
      </button>
    </div>

    <!-- 다음 페이지 버튼 -->
    <button
      @click="$emit('page-change', currentPage + 1)"
      :disabled="currentPage >= totalPages - 1"
      :class="[
        'px-3 py-2 text-sm font-medium rounded-lg',
        currentPage >= totalPages - 1
          ? 'text-gray-400 bg-gray-100 cursor-not-allowed'
          : 'text-black bg-white hover:bg-gray-100',
      ]"
    >
      다음
    </button>
  </div>
</template>

<script>
export default {
  name: 'PagePagination',
  props: {
    currentPage: {
      type: Number,
      required: true,
    },
    totalPages: {
      type: Number,
      required: true,
    },
  },
  computed: {
    displayedPages() {
      // 현재 페이지를 중심으로 최대 5개의 페이지 번호를 표시
      const pages = []
      const current = this.currentPage + 1
      const total = this.totalPages

      if (total <= 5) {
        // 전체 페이지가 5개 이하면 모두 표시
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        // 전체 페이지가 5개 초과일 때
        if (current <= 3) {
          // 현재 페이지가 앞쪽에 있을 때
          for (let i = 1; i <= 5; i++) {
            pages.push(i)
          }
        } else if (current >= total - 2) {
          // 현재 페이지가 뒤쪽에 있을 때
          for (let i = total - 4; i <= total; i++) {
            pages.push(i)
          }
        } else {
          // 현재 페이지가 중간에 있을 때
          for (let i = current - 2; i <= current + 2; i++) {
            pages.push(i)
          }
        }
      }
      return pages
    },
  },
}
</script>
