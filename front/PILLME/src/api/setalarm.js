// api/setalarm.js
import apiClient from './index';

// 알림 설정 생성 (활성화 시 POST 요청)
export const createNotificationSetting = async (settings) => {
  try {
    const response = await apiClient.post('/api/v1/notification/setting', settings);
    console.log('알림 설정 생성 성공:', response.data);
    return response.data;
  } catch (error) {
    console.error('알림 설정 생성 실패:', error);
    throw error;
  }
};

// 알림 설정 수정 (PUT 요청)
export const updateNotificationSetting = async (settings) => {
  try {
    const response = await apiClient.put('/api/v1/notification/setting', settings);
    console.log('알림 설정 수정 성공:', response.data);
    return response.data;
  } catch (error) {
    console.error('알림 설정 수정 실패:', error);
    throw error;
  }
};

// 알림 설정 삭제 (비활성화 시 DELETE 요청)
export const deleteNotificationSetting = async () => {
  try {
    const response = await apiClient.delete('/api/v1/notification/setting');
    console.log('알림 설정 삭제 성공:', response.data);
    return response.data;
  } catch (error) {
    console.error('알림 설정 삭제 실패:', error);
    throw error;
  }
};

// 알림 설정 불러오기 (GET 요청)
export const fetchNotificationSettings = async () => {
  try {
    const response = await apiClient.get('/api/v1/notification/setting'); // ✅ GET 요청으로 변경
    console.log('🔔 알림 설정 불러오기 성공:', response.data);
    return response.data.result;
  } catch (error) {
    console.error('❌ 알림 설정 불러오기 실패:', error);
    throw error;
  }
};
