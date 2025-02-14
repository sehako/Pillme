import {initializeApp} from 'firebase/app'

const firebaseConfig = {
  apiKey: "AIzaSyCKU41WUvCo9DU7GWOuWqVJ-SURbeyl0Es",        // 웹 클라이언트용 API 키 - Firebase에서 공개용으로 설계됨
  authDomain: "pillme-d16b6.firebaseapp.com",               // 공개 도메인 정보
  projectId: "pillme-d16b6",                                // 프로젝트 식별자 - 공개 정보
  storageBucket: "pillme-d16b6.firebasestorage.app",        // Storage 버킷 - 공개 정보
  messagingSenderId: "256685141880",                        // FCM 발신자 ID - 공개 정보
  appId: "1:256685141880:web:8fe6c1e8c62513a0e6628d",       // 앱 ID - 공개 정보
};

console.log("FCM", firebaseConfig);

export const app = initializeApp(firebaseConfig)