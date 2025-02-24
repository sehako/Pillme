import apiClient from './index'

export const getAdminStats = async () => {
  try {
    const response = await apiClient.get('/api/v1/admin/members/stats')
    return response.data.result
  } catch (error) {
    console.error('API 호출 실패:', error)
    throw error
  }
}

export const getMembers = async (page = 0, size = 10) => {
  try {
    // console.log('API 요청 파라미터:', { page, size })
    const response = await apiClient.get('/api/v1/admin/members', {
      params: { page, size },
    })
    // console.log('API 응답 전체:', response)
    // console.log('API 응답 데이터 구조:', response.data)
    // console.log('result 데이터:', response.data.result)
    return response.data.result
  } catch (error) {
    console.error('API 호출 실패:', error)
    throw error
  }
}

export const searchMembers = async (keyword, page = 0, size = 10) => {
  try {
    const response = await apiClient.get('/api/v1/admin/members/search', {
      params: { keyword, page, size },
    })
    return response.data.result
  } catch (error) {
    console.error('API 호출 실패:', error)
    throw error
  }
}

export const updateMember = async (id, memberData) => {
  try {
    const response = await apiClient.put(`/api/v1/admin/members/${id}`, memberData)
    return response.data.result
  } catch (error) {
    console.error('API 호출 실패:', error)
    throw error
  }
}

export const updateMemberStatus = async (id, isDeleted) => {
  try {
    // console.log('상태 변경 API 요청:', {
    //   id,
    //   isDeleted,
    //   url: `/api/v1/admin/members/${id}/status`,
    // })

    const response = await apiClient.patch(`/api/v1/admin/members/${id}/status`, null, {
      params: { isDeleted },
    })

    // console.log('상태 변경 API 응답:', response.data)
    return response.data.result
  } catch (error) {
    console.error('API 호출 실패:', error)
    throw error
  }
}
