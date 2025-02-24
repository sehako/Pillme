// api/setalarm.js
import apiClient from './index';

// 알림 설정 생성 (활성화 시 POST 요청)
export const createNotificationSetting = async (settings) => {
  try {
    const response = await apiClient.post('/api/v1/notification/setting', settings);
    // console.log('알림 설정 생성 성공:', response.data);
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
    // console.log('알림 설정 수정 성공:', response.data);
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
    // console.log('알림 설정 삭제 성공:', response.data);
    return response.data;
  } catch (error) {
    console.error('알림 설정 삭제 실패:', error);
    throw error;
  }
};

// 알림 설정 불러오기 (GET 요청)
export const fetchNotificationSettings = async () => {
  try {
    const response = await apiClient.get('/api/v1/notification/setting'); // ✅ GET 요청

    // 배열을 "HH:MM" 형식으로 변환하는 함수 (null 값은 그대로 유지)
    const formatTime = (value) => {
      if (Array.isArray(value) && value.length === 2) {
        // 시간이 1자리면 앞에 0 붙이기 (01:03 형식 유지)
        const hours = String(value[0]).padStart(2, "0");
        const minutes = String(value[1]).padStart(2, "0");
        return `${hours}:${minutes}`;
      }
      return value; // null이면 그대로 반환
    };

    const result = response.data.result;

    // 변환된 데이터 반환 (null은 그대로 유지)
    return {
      morning: formatTime(result.morning),
      lunch: formatTime(result.lunch),
      dinner: formatTime(result.dinner),
      bedtime: formatTime(result.sleep),
    };

  } catch (error) {
    console.error('알림 설정 불러오기 실패:', error);
    throw error;
  }
};

