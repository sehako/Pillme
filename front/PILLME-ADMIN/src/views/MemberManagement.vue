<template>
  <div class="min-h-screen bg-[var(--color-background)]">
    <div class="container p-6 mx-auto">
      <!-- 검색 바 -->
      <div class="flex gap-4 mb-6">
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
            placeholder="이름 또는 이메일로 검색"
            class="w-full pl-8 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
          />
        </div>
      </div>

      <!-- 회원 목록 테이블 -->
      <div class="overflow-hidden bg-white rounded-lg shadow">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th
                v-for="header in tableHeaders"
                :key="header"
                class="px-6 py-3 text-xs font-medium tracking-wider text-left text-gray-500 uppercase"
              >
                {{ header }}
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="user in users" :key="user.id">
              <td class="px-6 py-4 whitespace-nowrap">{{ user.id }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.name }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.email }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.phone }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.nickname }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.birthday }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.gender }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.oauth ? '소셜' : '일반' }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ user.provider }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 rounded text-sm',
                    user.status === '활성' ? 'bg-[#9DBB9F] text-white' : 'bg-[#EF7C8E] text-white',
                  ]"
                >
                  {{ user.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <button
                  @click="openEditDialog(user)"
                  class="p-2 transition-opacity duration-200 hover:opacity-80"
                >
                  <EditIcon class="w-5 h-5 text-[#4E7351]" />
                </button>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <button
                  @click="toggleUserStatus(user.id)"
                  :class="[
                    'px-3 py-1 rounded-lg',
                    user.status === '활성'
                      ? 'bg-[#EF7C8E] text-white hover:bg-[#FF8C9E]'
                      : 'bg-[#9DBB9F] text-white hover:bg-[#8DAA8F]',
                  ]"
                >
                  {{ user.status === '활성' ? '탈퇴' : '복구' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <Pagination
      v-if="pagination.totalPages > 0"
      :current-page="pagination.page"
      :total-pages="pagination.totalPages"
      @page-change="handlePageChange"
    />

    <!-- 수정 다이얼로그 -->
    <div
      v-if="showEditDialog"
      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
    >
      <div class="w-full max-w-md p-6 bg-white rounded-lg">
        <h2 class="mb-4 text-lg font-semibold">회원 정보 수정</h2>
        <div class="space-y-4">
          <div class="grid items-center grid-cols-4 gap-4">
            <label class="text-sm">이름</label>
            <input
              v-model="editingUser.name"
              type="text"
              class="col-span-3 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
            />
          </div>
          <div class="grid items-center grid-cols-4 gap-4">
            <label class="text-sm">이메일</label>
            <input
              v-model="editingUser.email"
              type="email"
              :disabled="editingUser.oauth"
              :class="[
                'col-span-3 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]',
                editingUser.oauth ? 'bg-gray-100 cursor-not-allowed' : '',
              ]"
            />
            <div v-if="editingUser.oauth" class="col-span-3 mt-1 text-sm text-[#EF7C8E]">
              소셜 회원은 이메일을 수정할 수 없습니다.
            </div>
          </div>
          <div class="grid items-center grid-cols-4 gap-4">
            <label class="text-sm">휴대전화</label>
            <input
              v-model="editingUser.phone"
              type="tel"
              class="col-span-3 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
            />
          </div>
          <div class="grid items-center grid-cols-4 gap-4">
            <label class="text-sm">닉네임</label>
            <input
              v-model="editingUser.nickname"
              type="text"
              class="col-span-3 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
            />
          </div>
          <div class="grid items-center grid-cols-4 gap-4">
            <label class="text-sm">생년월일</label>
            <input
              v-model="editingUser.birthday"
              type="date"
              class="col-span-3 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
            />
          </div>
          <div class="grid items-center grid-cols-4 gap-4">
            <label class="text-sm">성별</label>
            <select
              v-model="editingUser.gender"
              class="col-span-3 p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[#9DBB9F]"
            >
              <option value="M">남</option>
              <option value="F">여</option>
            </select>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showEditDialog = false"
            class="px-4 py-2 border rounded-lg hover:bg-gray-100"
          >
            취소
          </button>
          <button
            @click="handleEditUser"
            class="px-4 py-2 bg-[#9DBB9F] text-white rounded-lg hover:bg-[#8DAA8F]"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getAdminStats,
  getMembers,
  searchMembers,
  updateMember,
  updateMemberStatus,
} from '@/api/admin'
import EditIcon from '@/components/icons/EditIcon.vue'
import Pagination from '@/components/Pagination.vue'

export default {
  name: 'MemberManagement',
  components: {
    EditIcon,
    Pagination,
  },
  data() {
    return {
      users: [],
      searchTerm: '',
      showEditDialog: false,
      editingUser: null,
      tableHeaders: [
        'ID',
        '이름',
        '이메일',
        '휴대전화',
        '닉네임',
        '생년월일',
        '성별',
        '소셜회원',
        '소셜제공자',
        '상태',
        '수정',
        '탈퇴처리',
      ],
      pagination: {
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
      },
      stats: {
        total: 0,
        active: 0,
        deleted: 0,
      },
    }
  },
  created() {
    this.fetchStats()
    this.fetchMembers()
  },
  methods: {
    async fetchMembers() {
      try {
        const result = await getMembers(this.pagination.page, this.pagination.size)
        console.log('원본 회원 데이터:', result)

        // 회원 데이터 매핑
        const mappedContent = result.content.map((user) => ({
          id: user.id,
          name: user.name,
          email: user.email,
          phone: user.phone || '-',
          nickname: user.nickname || '-',
          // yyyymmdd -> yyyy-mm-dd 형식으로 변환
          birthday: user.birthday
            ? `${user.birthday.slice(0, 4)}-${user.birthday.slice(4, 6)}-${user.birthday.slice(6, 8)}`
            : '-',
          gender: user.gender || '-',
          oauth: user.oauth || false,
          provider: user.provider === 'FORM' ? '-' : user.provider,
          status: user.deleted ? '탈퇴' : '활성',
        }))

        console.log('매핑된 회원 데이터:', mappedContent)

        this.users = mappedContent
        this.pagination.totalElements = result.totalElements
        this.pagination.totalPages = result.totalPages
      } catch (error) {
        console.error('회원 목록 조회 실패:', error)
      }
    },

    async fetchStats() {
      try {
        const result = await getAdminStats()
        console.log('통계 데이터:', result)
        this.stats = result
      } catch (error) {
        console.error('통계 조회 실패:', error)
      }
    },

    async searchMembers() {
      if (!this.searchTerm) {
        return this.fetchMembers()
      }
      try {
        console.log('검색 요청 파라미터:', {
          keyword: this.searchTerm,
          page: this.pagination.page,
          size: this.pagination.size,
        })

        const result = await searchMembers(
          this.searchTerm,
          this.pagination.page,
          this.pagination.size,
        )
        console.log('검색 결과 원본:', result)

        const mappedContent = result.content.map((user) => ({
          id: user.id,
          name: user.name,
          email: user.email,
          phone: user.phone || '-',
          nickname: user.nickname || '-',
          birthday: user.birthday || '-',
          gender: user.gender || '-',
          oauth: false,
          provider: '-',
          status: user.deleted ? '탈퇴' : '활성',
        }))

        console.log('검색 결과 매핑 후:', mappedContent)

        this.users = mappedContent
        this.pagination.totalElements = result.totalElements
        this.pagination.totalPages = result.totalPages
      } catch (error) {
        console.error('회원 검색 실패:', error)
        console.error('검색 에러 상세:', {
          message: error.message,
          response: error.response?.data,
        })
      }
    },

    async toggleUserStatus(userId) {
      try {
        const user = this.users.find((u) => u.id === userId)
        const newStatus = user.status === '활성' // true면 탈퇴, false면 활성

        console.log('상태 변경 시도:', {
          userId,
          현재_회원_정보: user,
          현재상태: user.status,
          변경될상태: newStatus ? '탈퇴' : '활성',
          요청할_deleted값: newStatus,
        })

        await updateMemberStatus(userId, newStatus)
        console.log('상태 변경 API 호출 완료')

        // 상태 변경 후 목록 새로고침
        await this.fetchMembers()

        // 성공 여부 확인을 위해 변경된 유저 정보 로깅
        const updatedUser = this.users.find((u) => u.id === userId)
        console.log('상태 변경 후 회원 정보:', updatedUser)
      } catch (error) {
        console.error('회원 상태 변경 실패:', error)
        console.error('에러 상세:', {
          message: error.message,
          response: error.response?.data,
        })
      }
    },

    openEditDialog(user) {
      console.log('수정할 유저 정보:', user)
      const editingUser = { ...user }
      this.editingUser = editingUser
      this.showEditDialog = true
    },

    closeEditDialog() {
      this.showEditDialog = false
      this.editingUser = null
    },

    async handleEditUser() {
      try {
        if (!this.editingUser) return

        // birthday 형식 변환 (yyyy-mm-dd -> yyyymmdd)
        const formattedBirthday = this.editingUser.birthday?.replace(/-/g, '') || null

        console.log('수정 요청할 데이터:', {
          ...this.editingUser,
          birthday: formattedBirthday,
        })

        await updateMember(this.editingUser.id, {
          name: this.editingUser.name,
          email: this.editingUser.email,
          phone: this.editingUser.phone,
          nickname: this.editingUser.nickname,
          birthday: formattedBirthday, // 변환된 날짜 형식 사용
          gender: this.editingUser.gender,
        })

        this.closeEditDialog()
        await this.fetchMembers()
      } catch (error) {
        console.error('회원 정보 수정 실패:', error)
      }
    },

    handlePageChange(newPage) {
      this.pagination.page = newPage
      if (this.searchTerm) {
        this.searchMembers()
      } else {
        this.fetchMembers()
      }
    },
  },
  watch: {
    searchTerm(newVal) {
      this.pagination.page = 0
      if (newVal) {
        this.searchMembers()
      } else {
        this.fetchMembers()
      }
    },
  },
}
</script>
