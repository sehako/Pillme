import { ref } from 'vue'
import { getMessaging, getToken, onMessage } from 'firebase/messaging'
import { app } from './fcminit'

const messaging = getMessaging(app)

export function useFCM() {
  const fcmToken = ref(null)
  const notifications = ref([])

  // 포그라운드 메시지 핸들러
  const handleForegroundMessage = (payload) => {
    console.log('포그라운드 메시지 수신:', payload)
    
    // 브라우저가 포커스되어 있고, document가 visible 상태일 때만 인앱 알림 표시
    if (document.hasFocus() && document.visibilityState === 'visible') {
      const notificationData = {
        id: Date.now(),
        title: payload.data.title || payload.notification.title,
        body: payload.data.body || payload.notification.body,
        data: {
          code: payload.data.code,
          sender_id: payload.data.sender_id,
          url: '/'
        }
      }

      // URL 설정
      switch (payload.data.code) {
        case 'DEPENDENCY_REQUEST':
          notificationData.data.url = 'api/v1/dependency/requests'
          break
        case 'DEPENDENCY_ACCEPT':
          notificationData.data.url = 'api/v1/dependency/status'
          break
      }

      notifications.value.push({
        ...notificationData,
        show: true
      })

      // DEPENDENCY_REQUEST가 아닌 경우 5초 후 자동으로 제거
      if (payload.data.code !== 'DEPENDENCY_REQUEST') {
        setTimeout(() => {
          const index = notifications.value.findIndex(n => n.id === notificationData.id)
          if (index !== -1) {
            notifications.value[index].show = false
            setTimeout(() => {
              removeNotification(notificationData.id)
            }, 300)
          }
        }, 5000)
      }

      // 소리 재생 (선택적)
      try {
        const audio = new Audio('/notification-sound.mp3')
        audio.play()
      } catch (error) {
        console.log('소리 재생 실패:', error)
      }
    }
  }

  // FCM 메시지 리스너 등록
  onMessage(messaging, handleForegroundMessage)

  const getFCMToken = async () => {
    try {
      const permission = await Notification.requestPermission()
      
      if (permission === 'granted') {
        const token = await getToken(messaging, {
          vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY
        })
        
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
      console.log(import.meta.env.VITE_API_URL)
      const response = await fetch(`${import.meta.env.VITE_API_URL}api/v1/fcm`, {
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
      const response = await fetch(`${import.meta.env.VITE_API_URL}api/v1/dependency/accept`, {
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
      const response = await fetch(`${import.meta.env.VITE_API_URL}api/v1/dependency/reject`, {
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
    rejectDependency
  }
}