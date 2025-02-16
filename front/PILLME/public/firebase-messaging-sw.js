importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

const firebaseConfig = {
  apiKey: "AIzaSyCKU41WUvCo9DU7GWOuWqVJ-SURbeyl0Es",
  authDomain: "pillme-d16b6.firebaseapp.com",
  projectId: "pillme-d16b6",
  storageBucket: "pillme-d16b6.firebasestorage.app",
  messagingSenderId: "256685141880",
  appId: "1:256685141880:web:8fe6c1e8c62513a0e6628d",
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

// API 요청 처리 함수
async function handleApiRequest(code, action, senderId) {
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
  const url = `https://i12a606.p.ssafy.io/api/v1/${apiPath}/${endpoint}`;

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${await self.registration.active.clients.get().localStorage.getItem('accessToken')}`
    },
    body: JSON.stringify(bodyData)
  });

  if (!response.ok) {
    throw new Error(`${action} 처리 실패`);
  }

  return response;
}

// 백그라운드 메시지 처리
messaging.onBackgroundMessage((payload) => {
  console.log('백그라운드 메시지 수신:', payload);

  if (payload.data) {
    const notificationOptions = {
      body: payload.data.body,
      icon: '/notification-icon.png',
      badge: '/badge-icon.png',
      data: {
        url: '/',
        code: payload.data.code,
        senderId: payload.data.senderId
      },
      requireInteraction: ['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(payload.data.code)
    };

    // 액션 버튼이 필요한 알림 타입 처리
    if (['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(payload.data.code)) {
      notificationOptions.actions = [
        {
          action: 'accept',
          title: '동의'
        },
        {
          action: 'reject',
          title: '거절'
        }
      ];
    }

    self.registration.showNotification(payload.data.title, notificationOptions);
  }
});

// 알림 클릭 이벤트 처리
self.addEventListener('notificationclick', async (event) => {
  const notification = event.notification;
  const action = event.action;
  const data = notification.data || {};

  notification.close();

  // 동의/거절 액션 버튼 클릭 시
  if (action === 'accept' || action === 'reject') {
    try {
      await handleApiRequest(data.code, action, data.senderId);
      // 성공 시 추가 알림 표시
      self.registration.showNotification(
        '알림',
        {
          body: action === 'accept' ? '요청이 수락되었습니다.' : '요청이 거절되었습니다.',
          icon: '/notification-icon.png',
          badge: '/badge-icon.png',
        }
      );
    } catch (error) {
      console.error('요청 처리 중 오류:', error);
      self.registration.showNotification(
        '오류',
        {
          body: '처리 중 오류가 발생했습니다.',
          icon: '/notification-icon.png',
          badge: '/badge-icon.png',
        }
      );
    }
    return; // 동의/거절 처리 후 여기서 종료 (페이지 이동 없음)
  }

  // 알림 카드 클릭 시 홈 화면으로 이동
  try {
    const windowClients = await clients.matchAll({
      type: 'window',
      includeUncontrolled: true
    });

    // 이미 열린 창이 있다면 해당 창을 포커스하고 홈으로 이동
    for (const client of windowClients) {
      if (client.url.includes(self.location.origin)) {
        await client.focus();
        return client.navigate('/');
      }
    }

    // PWA로 이동
    await clients.openWindow(self.location.origin + '/');
  } catch (error) {

    // 열린 창이 없다면 웹으로 새 창 열기
    await clients.openWindow('/');
  }
});