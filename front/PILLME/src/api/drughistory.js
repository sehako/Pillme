import apiClient from '../api';

export const fetchManagementData = async (target) => {
  try {
    const response = await apiClient.get('/api/v1/management', {
      params: { target }
    });
    return response.data; // 응답 데이터 반환
  } catch (error) {
    console.error('❌ Error fetching management data:', error);
    throw error;
  }
};
