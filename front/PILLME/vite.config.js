import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { VitePWA } from 'vite-plugin-pwa';

export default defineConfig({
  server: {
    allowedHosts: "all",
  },
  plugins: [
    vue(),
    VitePWA({
      registerType: "autoUpdate",
      workbox: {
        clientsClaim: true,
        skipWaiting: false,
        cleanupOutdatedCaches: true,
        navigationPreload: true,
        // additionalManifestEntries: [{ url: '/index.html', revision: null }],
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/pillme\.site\/.*/i,
            handler: 'NetworkFirst',
            options: {
              cacheName: 'api-cache',
              networkTimeoutSeconds: 20,
              cacheableResponse: { statuses: [0, 200] }
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
      useNotifications: false,
      notifications: {
        onUpdate: null,
        onInstall: null
      }
    }),
  ],
});