import { ref } from 'vue'
import { getMessaging, getToken, onMessage } from 'firebase/messaging'
import { app } from './fcminit'

const messaging = getMessaging(app)

export function useFCM() {
  const fcmToken = ref(null)
  const notifications = ref([])

  // useFCM.js의 initializeFCM 함수
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

  // 포그라운드 메시지 핸들러
  const handleForegroundMessage = (payload) => {
    console.log('포그라운드 메시지 수신 전체:', payload);
    console.log('data:', payload.data);
    console.log('notification:', payload.notification);

    // 브라우저가 포커스되어 있고, document가 visible 상태일 때만 인앱 알림 표시
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
            senderId: payload.data.senderId,
            url: '/'
          }
        };

        // URL 설정
        switch (payload.data.code) {
          // 관계 관련
          case 'DEPENDENCY_REQUEST':
          case 'DEPENDENCY_DELETE_REQUEST':
            notificationData.data.url = '/dependency/requests';
            break;
          case 'DEPENDENCY_ACCEPT':
          case 'DEPENDENCY_REJECT':
          case 'DEPENDENCY_DELETE_ACCEPT':
          case 'DEPENDENCY_DELETE_REJECT':
            notificationData.data.url = '/dependency/status';
            break;

          // 약 관련
          case 'MEDICINE_REQUEST':
            notificationData.data.url = '/medicine/requests';
            break;
          case 'MEDICINE_ACCEPT':
          case 'MEDICINE_REJECT':
            notificationData.data.url = '/medicine/status';
            break;
          case 'MEDICINE_TAKE_REMINDER':
            notificationData.data.url = '/medicine/schedule';
            break;
        }
      }
      // notification 메시지 처리
      else if (payload.notification) {
        notificationData = {
          id: Date.now(),
          title: payload.notification.title,
          body: payload.notification.body,
          data: {
            url: '/'
          }
        };
      }

      // 인앱 알림 추가
      const notification = {
        ...notificationData,
        show: true
      };
      notifications.value.push(notification);

      // REQUEST 타입이 아닌 경우 5초 후 자동으로 제거
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

      // 소리 재생
      try {
        const audio = new Audio('/notification-sound.mp3');
        audio.play();
      } catch (error) {
        console.log('소리 재생 실패:', error);
      }
    }
  }


  const getFCMToken = async () => {
    try {
      console.log('현재 알림 권한:', Notification.permission);  // 추가
      const permission = await Notification.requestPermission()
      console.log('요청 후 알림 권한:', permission);  // 추가
      if (permission === 'granted') {
        const token = await getToken(messaging, {
          vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY
        })
        console.log('FCM 토큰:', token);  // 추가

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

  const removeNotification = (notificationId) => {
    notifications.value = notifications.value.filter(n => n.id !== notificationId)
  }

  const acceptDependency = async (senderId) => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/v1/dependency/accept`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ senderId })
      })

      if (!response.ok) {
        throw new Error('의존성 수락 실패')
      }

      return true
    } catch (error) {
      console.error('의존성 수락 중 오류:', error)
      return false
    }
  }

  const rejectDependency = async (senderId) => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/v1/dependency/reject`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ senderId })
      })

      if (!response.ok) {
        throw new Error('의존성 거절 실패')
      }

      return true
    } catch (error) {
      console.error('의존성 거절 중 오류:', error)
      return false
    }
  }

  return {
    fcmToken,
    notifications,
    getFCMToken,
    removeNotification,
    acceptDependency,
    rejectDependency,
    initializeFCM
  }
}