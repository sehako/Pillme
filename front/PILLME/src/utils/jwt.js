// utils/jwt.js
import { jwtDecode } from "jwt-decode";

export const decodeToken = (token) => {
  try {
    if (!token) {
      throw new Error("토큰이 없습니다.");
    }

    const decoded = jwtDecode(token);
    console.log("🔑 디코딩된 JWT 정보:", decoded);
    return decoded;
  } catch (error) {
    console.error("❌ JWT 디코딩 실패:", error);
    return null;
  }
};

// 토큰 만료 체크 함수 추가
export const isTokenExpired = (token) => {
  try {
    if (!token) return true;
    
    const decoded = decodeToken(token);
    if (!decoded) return true;
    
    // exp는 초 단위로 저장되어 있으므로 1000을 곱해 밀리초로 변환
    const expirationTime = decoded.exp * 1000;
    const currentTime = Date.now();
    
    console.log("⏰ 토큰 만료 시간:", new Date(expirationTime));
    console.log("⏰ 현재 시간:", new Date(currentTime));
    
    return currentTime >= expirationTime;
  } catch (error) {
    console.error("❌ 토큰 만료 체크 실패:", error);
    return true; // 에러 발생 시 만료된 것으로 처리
  }
};