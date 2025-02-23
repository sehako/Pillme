<template>
  <div class="min-h-screen">
    <div class="container p-6 mx-auto">
      <h2 class="mb-6 text-2xl font-bold text-[#4E7351]">1:1 문의 내역</h2>

      <!-- 검색 및 필터 영역 -->
      <div class="flex flex-wrap gap-4 mb-6">
        <div class="relative flex-1">
          <span class="absolute left-2 top-2.5">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 text-gray-500"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </span>
          <input
            type="text"
            v-model="searchTerm"
            placeholder="제목 또는 내용으로 검색"
            class="w-full pl-8 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
          />
        </div>
        <select
          v-model="statusFilter"
          class="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
        >
          <option value="all">전체 상태</option>
          <option value="pending">대기중</option>
          <option value="completed">처리완료</option>
        </select>
      </div>

      <!-- 문의 목록 테이블 -->
      <div class="overflow-x-auto bg-white rounded-lg shadow">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-xs font-medium text-left text-gray-500 uppercase">번호</th>
              <th class="px-6 py-3 text-xs font-medium text-left text-gray-500 uppercase">제목</th>
              <th class="px-6 py-3 text-xs font-medium text-left text-gray-500 uppercase">
                작성자
              </th>
              <th class="px-6 py-3 text-xs font-medium text-left text-gray-500 uppercase">
                작성일
              </th>
              <th class="px-6 py-3 text-xs font-medium text-left text-gray-500 uppercase">상태</th>
              <th class="px-6 py-3 text-xs font-medium text-left text-gray-500 uppercase">관리</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="inquiry in filteredInquiries" :key="inquiry.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm text-gray-500 whitespace-nowrap">
                {{ inquiry.id }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-900 whitespace-nowrap">
                {{ inquiry.title }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-500 whitespace-nowrap">
                {{ inquiry.author }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-500 whitespace-nowrap">
                {{ inquiry.date }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                    inquiry.status === 'pending'
                      ? 'bg-yellow-100 text-yellow-800'
                      : 'bg-green-100 text-green-800',
                  ]"
                >
                  {{ inquiry.status === 'pending' ? '대기중' : '처리완료' }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-500 whitespace-nowrap">
                <button
                  @click="handleViewDetail(inquiry)"
                  class="text-[#4E7351] hover:text-[#9DBB9F] mr-2"
                >
                  상세보기
                </button>
                <button
                  v-if="inquiry.status === 'pending'"
                  @click="handleComplete(inquiry)"
                  class="text-[#4E7351] hover:text-[#9DBB9F]"
                >
                  처리완료
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 상태 관리
const searchTerm = ref('')
const statusFilter = ref('all')

// 더미 데이터
const inquiries = ref([
  {
    id: 1,
    title: '약 복용 시간 문의드립니다',
    author: '김환자',
    date: '2024-03-15',
    status: 'pending',
  },
  {
    id: 2,
    title: '알람 설정이 안됩니다',
    author: '이용자',
    date: '2024-03-14',
    status: 'pending',
  },
  {
    id: 3,
    title: '회원정보 수정 관련 문의',
    author: '박고객',
    date: '2024-03-13',
    status: 'completed',
  },
  {
    id: 4,
    title: '약 정보 업데이트 요청',
    author: '정회원',
    date: '2024-03-12',
    status: 'completed',
  },
])

// 필터링된 문의 목록
const filteredInquiries = computed(() => {
  return inquiries.value.filter((inquiry) => {
    // 상태 필터링
    if (statusFilter.value !== 'all') {
      if (statusFilter.value !== inquiry.status) return false
    }
    // 검색어 필터링
    if (searchTerm.value) {
      return inquiry.title.toLowerCase().includes(searchTerm.value.toLowerCase())
    }
    return true
  })
})

// 이벤트 핸들러
const handleViewDetail = (inquiry) => {
  // 상세보기 구현
  // console.log('상세보기:', inquiry)
  alert(`상세보기 - ${inquiry.title}`)
}

const handleComplete = (inquiry) => {
  // 처리완료 구현
  inquiry.status = 'completed'
  alert(`처리완료 - ${inquiry.title}`)
}
</script>
