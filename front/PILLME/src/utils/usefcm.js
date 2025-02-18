import { ref } from 'vue'
import { getMessaging, getToken, onMessage } from 'firebase/messaging'
import { app } from './fcminit'

const messaging = getMessaging(app)

export function useFCM() {
  const fcmToken = ref(null)
  const notifications = ref([])
  
  // FCM 초기화
  const initializeFCM = async () => {
    try {
      // Service Worker 등록 상태 확인
      const registration = await navigator.serviceWorker.getRegistration('/firebase-messaging-sw.js');
      console.log('Service Worker registration:', registration);

      const token = await getFCMToken();
      if (token) {
        // onMessage 핸들러를 Service Worker 등록 후에 설정
        console.log('FCM 토큰 발급 완료, 메시지 핸들러 등록');
        onMessage(messaging, handleForegroundMessage);
      }
    } catch (error) {
      console.error('FCM 초기화 실패:', error);
    }
  };

  // FCM 토큰 발급
  const getFCMToken = async () => {
    try {
      console.log('현재 알림 권한:', Notification.permission);
      const permission = await Notification.requestPermission()
      console.log('요청 후 알림 권한:', permission);

      if (permission === 'granted') {
        const token = await getToken(messaging, {
          vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY
        })
        console.log('FCM 토큰:', token);

        fcmToken.value = token
        await registerTokenToServer(token)
        return token
      }

      console.log('알림 권한이 거부되었습니다.')
      return null
    } catch (error) {
      console.error('FCM 토큰 발급 중 오류 발생:', error)
      return null
    }
  }

  // 토큰 서버 등록
  const registerTokenToServer = async (token) => {
    try {
      console.log('서버 URL:', import.meta.env.VITE_API_URL);
      console.log('토큰:', token);
      console.log('accessToken:', localStorage.getItem('accessToken'));

      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/v1/fcm`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ token })
      })

      if (!response.ok) {
        throw new Error('토큰 등록 실패')
      }

      console.log('FCM 토큰이 서버에 등록되었습니다.')
    } catch (error) {
      console.error('토큰 서버 등록 중 오류 발생:', error)
      throw error
    }
  }

  // API 요청 처리 함수
  const handleApiRequest = async (code, action, senderId) => {
    let endpoint = '';
    let apiPath = '';
    let bodyData = {};

    // API 엔드포인트와 요청 데이터 설정
    switch (code) {
      case 'DEPENDENCY_REQUEST':
        apiPath = 'dependency';
        bodyData = {
          protectorId: senderId
        };
        break;
      case 'MEDICINE_REQUEST':
        apiPath = 'medicine';
        bodyData = {
          protectorId: senderId
        };
        break;
      case 'DEPENDENCY_DELETE_REQUEST':
        apiPath = 'dependency/delete';
        bodyData = {
          senderId: senderId
        };
        break;
    }

    endpoint = action === 'accept' ? 'accept' : 'reject';
    const url = `${import.meta.env.VITE_API_URL}/api/v1/${apiPath}/${endpoint}`;

    let response = "";
    if (url === `${import.meta.env.VITE_API_URL}/api/v1/dependency/delete/accept`) {
      response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify(bodyData)
      });
    }
    else {
      response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify(bodyData)
      });
    }

    if (!response.ok) {
      throw new Error(`${action} 처리 실패`);
    }

    return response;
  };

  // 알림 동의 처리
  const handleAccept = async (notification) => {
    try {
      await handleApiRequest(notification.data.code, 'accept', notification.data.senderId);
      removeNotification(notification.id);
      showToast('요청이 수락되었습니다.');
    } catch (error) {
      console.error('동의 처리 중 오류:', error);
      showToast('처리 중 오류가 발생했습니다.');
    }
  };

  // 알림 거절 처리
  const handleReject = async (notification) => {
    try {
      await handleApiRequest(notification.data.code, 'reject', notification.data.senderId);
      removeNotification(notification.id);
      showToast('요청이 거절되었습니다.');
    } catch (error) {
      console.error('거절 처리 중 오류:', error);
      showToast('처리 중 오류가 발생했습니다.');
    }
  };

  // 포그라운드 메시지 핸들러
  const handleForegroundMessage = (payload) => {
    console.log('포그라운드 메시지 수신:', payload);

    if (document.visibilityState === 'visible') {
      let notificationData;

      // data 메시지 처리
      if (payload.data) {
        notificationData = {
          id: Date.now(),
          title: payload.data.title,
          body: payload.data.body,
          data: {
            code: payload.data.code,
            senderId: payload.data.senderId
          }
        };
      }
      // notification 메시지 처리
      else if (payload.notification) {
        notificationData = {
          id: Date.now(),
          title: payload.notification.title || 'PILLME 알림',
          body: payload.notification.body || '',
          data: {}
        };
      }

      // 알림 표시
      const notification = {
        ...notificationData,
        show: true
      };
      notifications.value.push(notification);

      // REQUEST 타입이 아닌 경우 자동 제거
      if (!payload.data?.code ||
        !['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(payload.data.code)) {
        setTimeout(() => {
          const index = notifications.value.findIndex(n => n.id === notification.id);
          if (index !== -1) {
            notifications.value[index].show = false;
            setTimeout(() => {
              removeNotification(notification.id);
            }, 300);
          }
        }, 5000);
      }
    }
  };

  // 알림 제거
  const removeNotification = (notificationId) => {
    notifications.value = notifications.value.filter(n => n.id !== notificationId);
  };

  // 토스트 메시지 표시
  const showToast = (message) => {
    const toastNotification = {
      id: Date.now(),
      title: '알림',
      body: message,
      show: true,
      isToast: true
    };

    notifications.value.push(toastNotification);
    

    // 3초 후 토스트 메시지 제거
    setTimeout(() => {
      const index = notifications.value.findIndex(n => n.id === toastNotification.id);
      if (index !== -1) {
        notifications.value[index].show = false;
        setTimeout(() => {
          removeNotification(toastNotification.id);
        }, 300);
      }
    }, 3000);
  };

  return {
    fcmToken,
    notifications,
    getFCMToken,
    removeNotification,
    handleAccept,
    handleReject,
    initializeFCM
  }
}