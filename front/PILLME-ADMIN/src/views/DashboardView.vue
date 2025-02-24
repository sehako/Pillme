<template>
  <div class="min-h-screen bg-[var(--color-background)]">
    <div class="container p-6 mx-auto">
      <h2 class="mb-6 text-2xl font-bold text-[#4E7351]">관리자 대시보드</h2>

      <div class="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-4">
        <!-- 전체 회원 수 카드 -->
        <div class="p-6 bg-white rounded-lg shadow min-w-[280px]">
          <div class="flex items-center justify-between">
            <div>
              <p class="mb-1 text-sm text-gray-500 whitespace-nowrap">전체 회원 수</p>
              <h3 class="text-3xl font-bold text-[#4E7351]">{{ memberStats?.total || 0 }}명</h3>
            </div>
            <div class="bg-[#9DBB9F] p-3 rounded-full">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-6 h-6 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
                />
              </svg>
            </div>
          </div>
        </div>

        <!-- 현재 서비스 이용 회원 수 카드 -->
        <div class="p-6 bg-white rounded-lg shadow min-w-[280px]">
          <div class="flex items-center justify-between">
            <div>
              <p class="mb-1 text-sm text-gray-500 whitespace-nowrap">현재 서비스 이용 회원</p>
              <h3 class="text-3xl font-bold text-[#4E7351]">{{ memberStats?.active || 0 }}명</h3>
            </div>
            <div class="bg-[#9DBB9F] p-3 rounded-full">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-6 h-6 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                />
              </svg>
            </div>
          </div>
        </div>

        <!-- 탈퇴 회원 수 카드 -->
        <div class="p-6 bg-white rounded-lg shadow min-w-[280px]">
          <div class="flex items-center justify-between">
            <div>
              <p class="mb-1 text-sm text-gray-500 whitespace-nowrap">탈퇴 회원</p>
              <h3 class="text-3xl font-bold text-[#4E7351]">{{ memberStats?.deleted || 0 }}명</h3>
            </div>
            <div class="bg-[#EF7C8E] p-3 rounded-full">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-6 h-6 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 7a4 4 0 11-8 0 4 4 0 018 0zM9 14a6 6 0 00-6 6v1h12v-1a6 6 0 00-6-6zM21 12h-6"
                />
              </svg>
            </div>
          </div>
        </div>

        <!-- 미처리 문의 카드 -->
        <div class="p-6 bg-white rounded-lg shadow min-w-[280px]">
          <div class="flex items-center justify-between">
            <div>
              <p class="mb-1 text-sm text-gray-500 whitespace-nowrap">처리되지 않은 1:1 문의</p>
              <h3 class="text-3xl font-bold text-[#4E7351]">{{ pendingInquiries }}건</h3>
            </div>
            <div class="bg-[#9DBB9F] p-3 rounded-full">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-6 h-6 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"
                />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 최근 문의 목록 -->
      <div class="mt-8 bg-white rounded-lg shadow">
        <div class="p-6">
          <h3 class="text-xl font-bold mb-4 text-[#4E7351]">최근 문의 내역</h3>
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead>
                <tr class="bg-gray-50">
                  <th
                    class="px-6 py-3 text-xs font-medium tracking-wider text-left text-gray-500 uppercase"
                  >
                    문의일자
                  </th>
                  <th
                    class="px-6 py-3 text-xs font-medium tracking-wider text-left text-gray-500 uppercase"
                  >
                    제목
                  </th>
                  <th
                    class="px-6 py-3 text-xs font-medium tracking-wider text-left text-gray-500 uppercase"
                  >
                    상태
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="inquiry in recentInquiries" :key="inquiry.id">
                  <td class="px-6 py-4 text-sm text-gray-500 whitespace-nowrap">
                    {{ inquiry.date }}
                  </td>
                  <td class="px-6 py-4 text-sm text-gray-900 whitespace-nowrap">
                    {{ inquiry.title }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      :class="[
                        'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                        inquiry.status === '대기중'
                          ? 'bg-yellow-100 text-yellow-800'
                          : 'bg-green-100 text-green-800',
                      ]"
                    >
                      {{ inquiry.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminStats } from '../api/admin'

// 회원 통계 상태
const memberStats = ref({
  total: 0,
  active: 0,
  deleted: 0,
})

// 문의 관련 데이터
const pendingInquiries = ref(5)
const recentInquiries = ref([
  {
    id: 1,
    date: '2024-03-15',
    title: '약 복용 시간 문의드립니다',
    status: '대기중',
  },
  {
    id: 2,
    date: '2024-03-14',
    title: '알람 설정이 안됩니다',
    status: '대기중',
  },
  {
    id: 3,
    date: '2024-03-13',
    title: '회원정보 수정 관련 문의',
    status: '처리완료',
  },
])

// 회원 통계 데이터 가져오기
const isLoading = ref(true)

const fetchMemberStats = async () => {
  try {
    isLoading.value = true
    const result = await getAdminStats()
    // console.log('통계 데이터:', result) // {total: 3, deleted: 2, active: 1}
    memberStats.value = result
  } catch (error) {
    console.error('에러 발생:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchMemberStats()
})
</script>
