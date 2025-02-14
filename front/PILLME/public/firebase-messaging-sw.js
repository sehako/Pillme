importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

const firebaseConfig = {
  apiKey: "AIzaSyCKU41WUvCo9DU7GWOuWqVJ-SURbeyl0Es",        // 웹 클라이언트용 API 키 - Firebase에서 공개용으로 설계됨
  authDomain: "pillme-d16b6.firebaseapp.com",               // 공개 도메인 정보
  projectId: "pillme-d16b6",                                // 프로젝트 식별자 - 공개 정보
  storageBucket: "pillme-d16b6.firebasestorage.app",        // Storage 버킷 - 공개 정보
  messagingSenderId: "256685141880",                        // FCM 발신자 ID - 공개 정보
  appId: "1:256685141880:web:8fe6c1e8c62513a0e6628d",       // 앱 ID - 공개 정보
};

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

console.log("fcm 서비스워커 등록");

messaging.onBackgroundMessage((payload) => {
  console.log('백그라운드 메시지 수신:', payload);
  
    // data 메시지 처리
    if (payload.data) {
      const notificationOptions = {
        body: payload.data.body,
        icon: '/notification-icon.png', // 알림에 표시되는 메인 이미지(브라우저/OS의 알림 영역에서 크게 보이는 이미지, 192*192px 이상 권장)
        badge: '/badge-icon.png',       // 주로 모바일에서 사용되는 작은 아이콘(알림이 축소되었을 때 표시되는 작은 이미지, 72*72px 정도 권장)
        data: {
          url: '/',
          code: payload.data.code,
          senderId: payload.data.senderId
        },
        // 알림의 지속성을 제어하는 옵션 (true일 경우 자동으로 사라지지 않음)
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
    // notification 메시지 처리
    else if (payload.notification) {
      const notificationOptions = {
        body: payload.notification.body,
        icon: '/notification-icon.png',
        badge: '/badge-icon.png',
        data: {
          url: '/'
        }
      };

      self.registration.showNotification(payload.notification.title, notificationOptions);
    }
});

self.addEventListener('notificationclick', async (event) => {
  const notification = event.notification;
  const action = event.action;
  const data = notification.data;

  notification.close();

  // data 메시지의 액션 버튼 클릭 처리
  if (data.code && ['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(data.code) &&
    (action === 'accept' || action === 'reject')) {
    let endpoint = '';
    let apiPath = '';
    let bodyData = {};

    // API 엔드포인트와 요청 데이터 결정
    switch (data.code) {
      case 'DEPENDENCY_REQUEST':
        apiPath = 'dependency';
        bodyData = {
          protectorId: data.senderId
        };
        break;
      case 'MEDICINE_REQUEST':
        // 약 요청 컨트롤러 존재 X
        apiPath = 'medicine';
        bodyData = {
          protectorId: data.senderId
        };
        break;
      case 'DEPENDENCY_DELETE_REQUEST':
        apiPath = 'dependency/delete';
        bodyData = {
          senderId: data.senderId
        };
        break;
    }

    endpoint = action === 'accept' ? 'accept' : 'reject';

    const url = `https://i12a606.p.ssafy.io/api/v1/${apiPath}/${endpoint}`;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify(bodyData)
      });

      if (!response.ok) {
        throw new Error(`${action} 처리 실패`);
      }

      // 성공 시 해당 상태 페이지로 이동
      const successPath = data.code.startsWith('DEPENDENCY') ? '/dependency/status' : '/medicine/status';
      clients.openWindow(successPath);
    } catch (error) {
      console.error('요청 처리 중 오류:', error);
      clients.openWindow('/error');
    }
    return;
  }

  // data 메시지의 일반 클릭 처리
  if (data.code) {
    let targetUrl = '/';
    switch (data.code) {
      // 관계 관련
      case 'DEPENDENCY_REQUEST':
      case 'DEPENDENCY_DELETE_REQUEST':
        targetUrl = '/dependency/requests';
        break;
      case 'DEPENDENCY_ACCEPT':
      case 'DEPENDENCY_REJECT':
      case 'DEPENDENCY_DELETE_ACCEPT':
      case 'DEPENDENCY_DELETE_REJECT':
        targetUrl = '/dependency/status';
        break;

      // 약 관련
      case 'MEDICINE_REQUEST':
        targetUrl = '/medicine/requests';
        break;
      case 'MEDICINE_ACCEPT':
      case 'MEDICINE_REJECT':
        targetUrl = '/medicine/status';
        break;
      case 'MEDICINE_TAKE_REMINDER':
        targetUrl = '/medicine/schedule';
        break;
    }

    // 열려있는 윈도우 찾기
    const windowClients = await clients.matchAll({
      type: 'window',
      includeUncontrolled: true
    });

    // 이미 열린 윈도우가 있다면 포커스
    for (const client of windowClients) {
      if (client.url.includes(targetUrl)) {
        client.focus();
        return;
      }
    }

    // 열린 윈도우가 없다면 새로 열기
    clients.openWindow(targetUrl);
  }
  // notification 메시지 클릭 처리
  else {
    // 기본 페이지로 이동
    clients.openWindow('/');
  }
});