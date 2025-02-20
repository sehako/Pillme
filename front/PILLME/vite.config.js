import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import { VitePWA } from 'vite-plugin-pwa';

export default defineConfig({
  server: {
    allowedHosts: "all",
  },
  
  plugins: [
    vue(),
    vueDevTools(),
    VitePWA({
      registerType: "autoUpdate",
      devOptions: {
        enabled: true,
      },
      workbox: {
        clientsClaim: true,
        skipWaiting: false,
        cleanupOutdatedCaches: true,
        navigationPreload: true,
        additionalManifestEntries: [
          { url: '/index.html', revision: null }
        ],
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/pillme\.site\/.*/i,
            handler: 'NetworkFirst',
            options: {
              cacheName: 'api-cache',
              networkTimeoutSeconds: 20,
              cacheableResponse: {
                statuses: [0, 200]
              }
            }
          }
        ]
      },
      injectRegister: 'auto',
      manifest: {
        name: "PILLME",
        short_name: "PILLME",
        start_url: "/",
        display: "standalone",
        background_color: "#ffffff",
        lang: "ko",
        scope: "/",
        theme_color: "#A3BFA5",
        description: "💊복약 관리의 새로운 방법, PILLME",
        icons: [
          // Android Icons
          { src: "/icons/android/android-launchericon-512-512.png", sizes: "512x512", type: "image/png" },
          { src: "/icons/android/android-launchericon-192-192.png", sizes: "192x192", type: "image/png" },
          { src: "/icons/android/android-launchericon-144-144.png", sizes: "144x144", type: "image/png" },
          { src: "/icons/android/android-launchericon-96-96.png", sizes: "96x96", type: "image/png" },
          { src: "/icons/android/android-launchericon-72-72.png", sizes: "72x72", type: "image/png" },
          { src: "/icons/android/android-launchericon-48-48.png", sizes: "48x48", type: "image/png" },
    
          // iOS Icons
          { src: "/icons/ios/16.png", sizes: "16x16", type: "image/png" },
          { src: "/icons/ios/20.png", sizes: "20x20", type: "image/png" },
          { src: "/icons/ios/29.png", sizes: "29x29", type: "image/png" },
          { src: "/icons/ios/32.png", sizes: "32x32", type: "image/png" },
          { src: "/icons/ios/40.png", sizes: "40x40", type: "image/png" },
          { src: "/icons/ios/50.png", sizes: "50x50", type: "image/png" },
          { src: "/icons/ios/57.png", sizes: "57x57", type: "image/png" },
          { src: "/icons/ios/58.png", sizes: "58x58", type: "image/png" },
          { src: "/icons/ios/60.png", sizes: "60x60", type: "image/png" },
          { src: "/icons/ios/64.png", sizes: "64x64", type: "image/png" },
          { src: "/icons/ios/72.png", sizes: "72x72", type: "image/png" },
          { src: "/icons/ios/76.png", sizes: "76x76", type: "image/png" },
          { src: "/icons/ios/80.png", sizes: "80x80", type: "image/png" },
          { src: "/icons/ios/87.png", sizes: "87x87", type: "image/png" },
          { src: "/icons/ios/100.png", sizes: "100x100", type: "image/png" },
          { src: "/icons/ios/114.png", sizes: "114x114", type: "image/png" },
          { src: "/icons/ios/120.png", sizes: "120x120", type: "image/png" },
          { src: "/icons/ios/128.png", sizes: "128x128", type: "image/png" },
          { src: "/icons/ios/144.png", sizes: "144x144", type: "image/png" },
          { src: "/icons/ios/152.png", sizes: "152x152", type: "image/png" },
          { src: "/icons/ios/167.png", sizes: "167x167", type: "image/png" },
          { src: "/icons/ios/180.png", sizes: "180x180", type: "image/png" },
          { src: "/icons/ios/192.png", sizes: "192x192", type: "image/png" },
          { src: "/icons/ios/256.png", sizes: "256x256", type: "image/png" },
          { src: "/icons/ios/512.png", sizes: "512x512", type: "image/png" },
          { src: "/icons/ios/1024.png", sizes: "1024x1024", type: "image/png" },
    
          // Windows Icons
          { src: "/icons/windows11/SmallTile.scale-100.png", sizes: "71x71", type: "image/png" },
          { src: "/icons/windows11/SmallTile.scale-125.png", sizes: "89x89", type: "image/png" },
          { src: "/icons/windows11/SmallTile.scale-150.png", sizes: "107x107", type: "image/png" },
          { src: "/icons/windows11/SmallTile.scale-200.png", sizes: "142x142", type: "image/png" },
          { src: "/icons/windows11/SmallTile.scale-400.png", sizes: "284x284", type: "image/png" },
          { src: "/icons/windows11/Square150x150Logo.scale-100.png", sizes: "150x150", type: "image/png" },
          { src: "/icons/windows11/Square150x150Logo.scale-125.png", sizes: "188x188", type: "image/png" },
          { src: "/icons/windows11/Square150x150Logo.scale-150.png", sizes: "225x225", type: "image/png" },
          { src: "/icons/windows11/Square150x150Logo.scale-200.png", sizes: "300x300", type: "image/png" },
          { src: "/icons/windows11/Square150x150Logo.scale-400.png", sizes: "600x600", type: "image/png" },
          { src: "/icons/windows11/Wide310x150Logo.scale-100.png", sizes: "310x150", type: "image/png" },
          { src: "/icons/windows11/Wide310x150Logo.scale-125.png", sizes: "388x188", type: "image/png" },
          { src: "/icons/windows11/Wide310x150Logo.scale-150.png", sizes: "465x225", type: "image/png" },
          { src: "/icons/windows11/Wide310x150Logo.scale-200.png", sizes: "620x300", type: "image/png" },
          { src: "/icons/windows11/Wide310x150Logo.scale-400.png", sizes: "1240x600", type: "image/png" },
          { src: "/icons/windows11/LargeTile.scale-100.png", sizes: "310x310", type: "image/png" },
          { src: "/icons/windows11/LargeTile.scale-125.png", sizes: "388x388", type: "image/png" },
          { src: "/icons/windows11/LargeTile.scale-150.png", sizes: "465x465", type: "image/png" },
          { src: "/icons/windows11/LargeTile.scale-200.png", sizes: "620x620", type: "image/png" },
          { src: "/icons/windows11/LargeTile.scale-400.png", sizes: "1240x1240", type: "image/png" }
        ],
      },
      // 알림 관련 추가 설정
      useNotifications: false,  // 알림 기능 비활성화
      notifications: {
        onUpdate: null,  // 업데이트 알림 비활성화
        onInstall: null  // 설치 알림 비활성화
      }
    }),

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
