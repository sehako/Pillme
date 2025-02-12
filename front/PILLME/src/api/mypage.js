import apiClient from "./index";

// 마이페이지 정보 조회 API
export const getMyInfo = async () => {
  try {
    const response = await apiClient.get("/api/v1/members/me");
    return response.data.result;
  } catch (error) {
    console.error("[fetchMyInfo] 마이페이지 정보 조회 실패:", error);
    return null;
  }
};

// 마이페이지 정보 수정 API
// 이메일 유효성 검사
export const checkEmail = async (email) => {
    try {
      const response = await apiClient.get('/api/v1/members/me/check/email', {
        params: { newEmail: email }
      });
      return response.data.result;
    } catch (error) {
      throw new Error(error.response?.data?.message || '이메일 검사 중 오류가 발생했습니다.');
    }
  };
  
  // 이메일 인증번호 발송
  export const sendEmailVerification = async (email) => {
    try {
      await apiClient.post('/api/v1/members/me/email/verification', {
        newEmail: email
      });
      return true;
    } catch (error) {
      throw new Error(error.response?.data?.message || '인증번호 발송 중 오류가 발생했습니다.');
    }
  };
  
  // 이메일 인증번호 확인
  export const verifyEmailCode = async (email, code) => {
    try {
      await apiClient.post('/api/v1/members/me/email/verify', {
        newEmail: email,
        code: code
      });
      return true;
    } catch (error) {
      throw new Error(error.response?.data?.message || '인증번호 확인 중 오류가 발생했습니다.');
    }
  };
  
  // 이메일 변경
  export const changeEmail = async (email) => {
    try {
      await apiClient.put('/api/v1/members/me/email', {
        newEmail: email
      });
      return true;
    } catch (error) {
      throw new Error(error.response?.data?.message || '이메일 변경 중 오류가 발생했습니다.');
    }
  };

// 닉네임 중복 검사
export const checkNickname = async (nickname) => {
    try {
      const response = await apiClient.get('/api/v1/members/me/check/nickname', {
        params: { nickname: nickname },
      });
      return response.data.result; // { isSameAsCurrent: boolean, isAlreadyExists: boolean }
    } catch (error) {
      throw new Error(error.response?.data?.message || '닉네임 검사 중 오류가 발생했습니다.');
    }
  };
  
  // 닉네임 변경
  export const changeNickname = async (nickname) => {
    try {
      const response = await apiClient.put('/api/v1/members/me/nickname', {
        newNickname: nickname,
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || '닉네임 변경 중 오류가 발생했습니다.');
    }
  };
  
  // ✅ 전화번호 중복 검사
  export const isDuplicatePhone = async (phone) => {
    try {
      const response = await apiClient.get('/api/v1/member/me/check/phone', {
        params: { phone },
      });
      return response.data.result;
    } catch (error) {
      throw new Error(error.response?.data?.message || '전화번호 중복 검사 중 오류가 발생했습니다.');
    }
  };
// 

// 마이페이지 비밀번호 변경 API

// 회원 탈퇴 API