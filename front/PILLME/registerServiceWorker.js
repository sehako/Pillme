import { registerSW } from 'virtual:pwa-register';



const CACHE_NAME = 'pillme-cache-v8'; // 🔹 새로운 버전의 캐시
const OFFLINE_PAGE = '/offline.html'; // ✅ 오프라인 안내 페이지
const API_CACHE = 'pillme-api-cache'; // ✅ API 응답 캐싱
const STATIC_CACHE = 'pillme-static-cache'; // ✅ 정적 파일 캐싱

const ALLOWED_OFFLINE_PAGES = ['/calendar', '/']; // ✅ 오프라인에서도 접근 가능한 페이지
const STATIC_FILES = [
  '/',
  '/index.html',
  OFFLINE_PAGE,
  '/assets/main.css',
  '/assets/main.js',
  '/assets/logo.png',
];

// ✅ PWA 업데이트 감지
const updateSW = registerSW({
  onNeedRefresh() {
    console.log('[PWA] 새로운 버전이 감지되었습니다.'); // 🚨 배포 시 주석 처리 필요

    // ✅ UI를 통해 업데이트 안내
    const updateNotification = document.createElement('div');
    updateNotification.innerHTML = `
      <div style="
        position: fixed;
        bottom: 10px;
        left: 50%;
        transform: translateX(-50%);
        background: #42b883;
        color: white;
        padding: 10px 20px;
        border-radius: 5px;
        font-size: 14px;
        cursor: pointer;
      ">
        새로운 버전이 있습니다. 새로고침하시겠습니까?
      </div>
    `;

    updateNotification.onclick = () => {
      updateSW();
      updateNotification.remove();
      window.location.reload(); // ✅ 최신 데이터 반영
    };

    document.body.appendChild(updateNotification);
  },

  onOfflineReady() {
    console.log('[PWA] 오프라인 모드가 활성화되었습니다.');
  },
});

// ✅ 서비스 워커 설치 (정적 파일 및 오프라인 파일 캐싱)
self.addEventListener('install', (event) => {
  console.log('[PWA] 서비스 워커 설치 중...');
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(STATIC_FILES);
    })
  );
});

// ✅ 네트워크 요청 처리 (정적 파일은 캐싱, API는 최신 데이터 유지)
self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);

  // ✅ 페이지 이동 요청 (HTML 요청)
  if (event.request.mode === 'navigate') {
    event.respondWith(
      caches.match(event.request).then((cachedResponse) => {
        if (cachedResponse) {
          return cachedResponse; // ✅ 캐싱된 페이지가 있다면 반환
        }

        if (ALLOWED_OFFLINE_PAGES.includes(url.pathname)) {
          return fetch(event.request)
            .then((response) => {
              return caches.open(CACHE_NAME).then((cache) => {
                cache.put(event.request, response.clone());
                return response;
              });
            })
            .catch(() => caches.match(OFFLINE_PAGE));
        }

        return caches.match(OFFLINE_PAGE);
      })
    );
    return;
  }

  // ✅ API 요청 (항상 최신 데이터 유지)
  if (url.pathname.startsWith('/api/')) {
    event.respondWith(
      fetch(event.request)
        .then((response) => {
          return caches.open(API_CACHE).then((cache) => {
            cache.put(event.request, response.clone());
            return response;
          });
        })
        .catch(() => caches.match(event.request))
    );
    return;
  }

  // ✅ 정적 리소스 요청 (CSS, JS, 이미지 등)
  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      return (
        cachedResponse ||
        fetch(event.request).then((response) => {
          return caches.open(STATIC_CACHE).then((cache) => {
            cache.put(event.request, response.clone());
            return response;
          });
        })
      );
    })
  );
});

// ✅ 오래된 캐시 정리 (서비스 워커 활성화 시)
self.addEventListener('activate', (event) => {
  console.log('[PWA] 오래된 캐시 정리 중...');
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (![CACHE_NAME, API_CACHE, STATIC_CACHE].includes(cacheName)) {
            console.log(`[PWA] 오래된 캐시 삭제: ${cacheName}`);
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});
