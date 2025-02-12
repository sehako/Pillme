import { defineConfig } from 'vite';
import mkcert from 'vite-plugin-mkcert'
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import { VitePWA } from 'vite-plugin-pwa';
import { writeFileSync } from 'fs';
export default defineConfig({
  server: {
    allowedHosts: "all",
  },
  
  plugins: [
    vue(),
    vueDevTools(),
    mkcert(), // ✅ HTTPS 지원을 위한 mkcert 플러그인 추가
    VitePWA({
      registerType: 'autoUpdate',
      devOptions: {
        enabled: false, // 이 부분을 false로 함으로써 개발단계에서 pwa를 사용하지 않음.
        type: 'module',
        selfDestroying: true,
      },
      includeAssets: ['favicon.ico', 'robots.txt', 'apple-touch-icon.png', 'offline.html'], // ✅ 캐싱할 정적 파일 추가
      manifest: {
        name: 'PILLME',
        short_name: 'PILLME',
        start_url: '/',
        display: 'standalone',
        background_color: '#ffffff',
        theme_color: '#42b883',
      },
      workbox: {
        globPatterns: [
          '**/*.{js,css,html,png,svg}', // ✅ 모든 정적 파일 자동 캐싱
          'offline.html', // ✅ 오프라인 안내 페이지 캐싱
        ],
        runtimeCaching: [
          {
            urlPattern: /^\/(calendar|home)$/, // ✅ 오프라인에서도 접근 가능한 페이지
            handler: 'StaleWhileRevalidate', // ✅ 캐시된 데이터 제공 후 최신 데이터 업데이트
            options: {
              cacheName: 'allowed-pages',
              expiration: { maxEntries: 10, maxAgeSeconds: 60 * 60 * 24 * 30 },
            },
          },
          {
            urlPattern: /\/api\/.*$/, // ✅ API 요청은 항상 최신 데이터 우선
            handler: 'NetworkFirst',
            options: {
              cacheName: 'api-data',
              expiration: { maxEntries: 10, maxAgeSeconds: 60 * 5 },
              networkTimeoutSeconds: 5, // ✅ 네트워크가 5초 안에 응답하지 않으면 캐시 사용
            },
          },
          {
            urlPattern: /\.(?:js|css|html|png|svg)$/, // ✅ 정적 리소스 캐싱
            handler: 'CacheFirst',
            options: {
              cacheName: 'static-resources',
              expiration: { maxEntries: 30, maxAgeSeconds: 60 * 60 * 24 * 7 },
            },
          },
          {
            urlPattern: ({ url }) => !/^\/(calendar|home)$/.test(url.pathname), // ❌ 허용되지 않은 페이지는 offline.html 제공
            handler: 'NetworkOnly', // ✅ 인터넷 연결 없을 시 offline.html로 대체
            options: {
              cacheName: 'offline-pages',
            },
          },
        ],
      },
    }),
    {
      name: 'generate-service-worker',
      buildStart() {
        const swContent = `
          const firebaseConfig = {
            apiKey: "${process.env.VITE_FIREBASE_API_KEY}",
            authDomain: "${process.env.VITE_FIREBASE_AUTH_DOMAIN}",
            projectId: "${process.env.VITE_FIREBASE_PROJECT_ID}",
            storageBucket: "${process.env.VITE_FIREBASE_STORAGE_BUCKET}",
            messagingSenderId: "${process.env.VITE_FIREBASE_MESSAGING_SENDER_ID}",
            appId: "${process.env.VITE_FIREBASE_APP_ID}"
          };

          importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
          importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

          firebase.initializeApp(firebaseConfig);
          
          const messaging = firebase.messaging();
        `;

        writeFileSync('public/firebase-messaging-sw.js', swContent);
      }
    },
  ],
  
});

/**
 * 🚨 [개발 중에만 사용해야 하는 코드] 🚨
 *
 * - `devOptions.enabled = true` → 개발 중에도 PWA 테스트 가능 (배포 시 false로 변경 또는 삭제)
 * - `devOptions.type = "module"` → 개발 서버에서 서비스 워커 모듈 방식으로 실행 (배포 시 삭제)
 * - `devOptions.selfDestroying = true` → 개발 중 캐시 자동 초기화 (배포 시 삭제)
 *
 * 🎯 배포 전 주석 처리해야 하는 이유:
 * - `enabled: true`를 유지하면 PWA가 개발 모드로 실행되므로 실제 배포 환경에서 예상과 다르게 동작할 수 있음.
 * - `selfDestroying: true`가 있으면, 배포 후에도 새로고침 시마다 캐시가 초기화되어 PWA의 오프라인 기능이 비활성화됨.
 * - `type: "module"`은 Vite 개발 서버에서는 필요하지만, 프로덕션 빌드에서는 사용되지 않음.
 */

/**
 * 🛠 [PWA 관련 주요 문제 및 디버깅 방법] 🛠
 *
 * 1️⃣ **PWA가 설치되지 않음**
 *    🔹 해결 방법:
 *       - `devOptions.enabled = true`로 설정 후 확인 (`npm run dev`)
 *       - `vite-plugin-pwa`가 정상적으로 동작하는지 확인 (`Application > Service Workers` 탭에서 상태 체크)
 *
 * 2️⃣ **오프라인 상태에서 캐싱된 페이지가 안 뜸**
 *    🔹 해결 방법:
 *       - `chrome://serviceworker-internals/`에 들어가서 서비스 워커 상태 확인
 *       - DevTools > Application > Cache Storage에서 `allowed-pages` 캐시가 제대로 등록되었는지 확인
 *       - `vite.config.js`에서 `globPatterns`이 올바르게 설정되었는지 점검
 *
 * 3️⃣ **PWA 업데이트 후 변경 사항이 반영되지 않음**
 *    🔹 해결 방법:
 *       - `Application > Service Workers > Update on reload` 체크 후 새로고침
 *       - Chrome DevTools에서 `Unregister` 후 다시 방문하여 서비스 워커 재등록
 *       - `registerType: 'autoUpdate'`가 설정되어 있는지 확인
 *
 * 4️⃣ **새로운 페이지를 추가했지만, PWA에서 인식하지 못함**
 *    🔹 해결 방법:
 *       - `vite.config.js`의 `runtimeCaching`에 새로운 경로 추가
 *       - `npm run build && npx serve -s dist`로 PWA 다시 배포 후 테스트
 *
 * 5️⃣ **서비스 워커가 예상과 다르게 동작함**
 *    🔹 해결 방법:
 *       - `chrome://serviceworker-internals/`에서 서비스 워커 로그 확인
 *       - `navigator.serviceWorker.getRegistrations()` 실행하여 등록된 서비스 워커 확인
 *       - `navigator.serviceWorker.register('/sw.js', { scope: '/' })` 실행 후 정상 등록되는지 확인
 */
