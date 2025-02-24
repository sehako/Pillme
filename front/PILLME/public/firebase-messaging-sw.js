importScripts('https://www.gstatic.com/firebasejs/11.3.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/11.3.0/firebase-messaging-compat.js');
importScripts("https://cdnjs.cloudflare.com/ajax/libs/localforage/1.10.0/localforage.min.js");
const firebaseConfig = {
  apiKey: "AIzaSyCKU41WUvCo9DU7GWOuWqVJ-SURbeyl0Es",
  authDomain: "pillme-d16b6.firebaseapp.com",
  projectId: "pillme-d16b6",
  storageBucket: "pillme-d16b6.firebasestorage.app",
  messagingSenderId: "256685141880",
  appId: "1:256685141880:web:8fe6c1e8c62513a0e6628d",
};

localforage.config({
  name: "authDB",
  storeName: "tokens"
});

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

const SERVICE_URL = "https://pillme.site";

// API 요청 처리 함수
async function handleApiRequest(code, action, senderId, dependencyId) {
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
        senderId: senderId,
        dependencyId: dependencyId
      };
      break;
  }
  const accessToken = await localforage.getItem("accessToken");

  if (!accessToken) {
    console.warn("⚠️ Access Token 없음, 인증이 필요합니다.");
    return fetch(event.request);
  }
  endpoint = action === 'accept' ? 'accept' : 'reject';
  const url = `https://i12a606.p.ssafy.io/api/v1/${apiPath}/${endpoint}`;
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`
    },
    body: JSON.stringify(bodyData)
  });

  if (!response.ok) {
    throw new Error(`${action} 처리 실패`);
  }

  return response;
}

// 알림 중복 체크를 위한 함수
async function isDuplicateNotification(payload) {
  const { code, messageId, senderId } = payload.data;

  // 채팅 메시지의 경우 messageId로 정확한 중복 체크
  if (code === 'CHAT_MESSAGE') {
    if (!messageId) return false;
    const recentChatMessages = await localforage.getItem('recentChatMessages') || [];

    const isDuplicate = recentChatMessages.includes(messageId);
    if (!isDuplicate) {
      // 새 메시지 ID 저장 (최근 50개만 유지)
      recentChatMessages.push(messageId);
      await localforage.setItem('recentChatMessages', recentChatMessages.slice(-50));
    }
    return isDuplicate;
  }

  // 채팅 외 다른 알림들은 시간 기반 중복 체크
  const notificationId = `${code}_${senderId}_${Date.now()}`;
  const recentNotifications = await localforage.getItem('recentNotifications') || [];

  const isDuplicate = recentNotifications.some(notification => {
    const timeDiff = Date.now() - notification.timestamp;
    return notification.id.includes(`${code}_${senderId}`) && timeDiff < 3000;
  });

  if (!isDuplicate) {
    recentNotifications.push({
      id: notificationId,
      timestamp: Date.now()
    });
    await localforage.setItem('recentNotifications', recentNotifications.slice(-10));
  }

  return isDuplicate;
}

// 백그라운드 메시지 처리
messaging.onBackgroundMessage(async (payload) => {
  // console.log('[Service Worker] 백그라운드 메시지 수신:', payload);

  if (payload.notification) {
    // console.log('Firebase notification 페이로드 감지, 추가 알림 표시하지 않음');
    return;
  }

  // 업데이트 관련 알림인지 확인 (title이나 body에 '업데이트' 문자열이 포함된 경우)
  if (
    payload.data?.title?.includes('업데이트') ||
    payload.data?.body?.includes('업데이트')
  ) {
    // console.log('업데이트 알림 필터링됨');
    return;
  }

  // 중복 알림 체크
  if (await isDuplicateNotification(payload)) {
    // console.log('중복 알림 감지됨, 무시합니다.');
    return;
  }

  if (payload.data) {
    const notificationOptions = {
      body: payload.data.body,
      icon: '/notification-icon.png',
      badge: '/badge-icon.png',
      data: {
        url: SERVICE_URL,
        code: payload.data.code,
        chatRoomId: payload.data.chatRoomId,
        senderId: payload.data.senderId,
        receiveUserId: payload.data.receiverId,
        sendUserName: payload.data.senderName,
        receiveUserName: payload.data.receiverName,
        dependencyId: payload.data.dependencyId
      },
      requireInteraction: ['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST', 'CHAT_MESSAGE'].includes(payload.data.code)
    };

    // 액션 버튼이 필요한 알림 타입 처리
    if (payload.data.code === 'CHAT_MESSAGE') {
      notificationOptions.requireInteraction = false;
      delete notificationOptions.actions; // 채팅은 수락/거절 버튼 불필요
    }
    else if (['DEPENDENCY_REQUEST', 'MEDICINE_REQUEST', 'DEPENDENCY_DELETE_REQUEST'].includes(payload.data.code)) {
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
      await handleApiRequest(data.code, action, data.senderId, data.dependencyId);
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

  // 페이지 이동 처리를 위한 waitUntil
  event.waitUntil(
    (async () => {
      const windowClients = await clients.matchAll({
        type: 'window',
        includeUncontrolled: true
      });

      let targetUrl = data.url || SERVICE_URL;

      // 채팅 메시지인 경우 채팅방 URL 생성
      if (data.code === 'CHAT_MESSAGE' && data.chatRoomId) {
        targetUrl = `${SERVICE_URL}/chat/individual?info=${encodeURIComponent(JSON.stringify({
          chatRoomId: data.chatRoomId,
          sendUserId: data.senderId,
          receiveUserId: data.receiveUserId,
          sendUserName: data.sendUserName,
          receiveUserName: data.receiveUserName,
        }))}`;
        const newClient = await clients.openWindow(targetUrl);
        if (newClient) {
          await newClient.focus();
        }
        return;
      }

      // 이미 열려있는 창 찾기
      const hadWindowToFocus = windowClients.some((windowClient) => {
        if (windowClient.url.includes(SERVICE_URL)) {
          return windowClient.focus().then(() => {
            if (windowClient.url !== targetUrl) {
              return windowClient.navigate(targetUrl);
            }
          });
        }
        return false;
      });

      // 열려있는 창이 없으면 새 창 열기
      if (!hadWindowToFocus) {
        const newClient = await clients.openWindow(targetUrl);
        if (newClient) {
          await newClient.focus();
        }
      }
    })()
  );
});

self.addEventListener('fetch', (event) => {
  if (event.preloadResponse) {
    event.respondWith(
      (async () => {
        const preloadResp = await event.preloadResponse;
        if (preloadResp) {
          return preloadResp;
        }
        return fetch(event.request);
      })()
    );
  }
});
