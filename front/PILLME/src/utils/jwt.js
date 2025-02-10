import { jwtDecode } from "jwt-decode"; // ✅ named import 사용

// ✅ Access Token에서 유저 정보 디코딩
export const decodeToken = (token) => {
  try {
    if (!token) {
      throw new Error("토큰이 없습니다.");
    }

    const decoded = jwtDecode(token);
    console.log("🔑 디코딩된 JWT 정보:", decoded);
    return decoded; // { id: 123, name: "홍길동", email: "gildong@example.com" }
  } catch (error) {
    console.error("❌ JWT 디코딩 실패:", error);
    return null;
  }
};
