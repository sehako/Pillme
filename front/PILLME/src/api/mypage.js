import apiClient from './index';

// 마이페이지 정보 조회 API
export const getMyInfo = async () => {
  try {
    const response = await apiClient.get('/api/v1/members/me');
    return response.data.result;
  } catch (error) {
    console.error('[fetchMyInfo] 마이페이지 정보 조회 실패:', error);
    return null;
  }
};

// 이메일 유효성 검증
export const checkEmail = async (email) => {
  try {
    const response = await apiClient.get('/api/v1/members/me/check/email', {
      params: { newEmail: email },
    });
    return response.data.result;
  } catch (error) {
    throw new Error(error.response?.data?.message || '이메일 검증 중 오류가 발생했습니다.');
  }
};

// 이메일 인증번호 발송
export const sendEmailVerification = async (email) => {
  try {
    await apiClient.post('/api/v1/members/me/email/verification', {
      newEmail: email,
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
      code: code,
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
      newEmail: email,
    });
    return true;
  } catch (error) {
    throw new Error(error.response?.data?.message || '이메일 변경 중 오류가 발생했습니다.');
  }
};

// 전화번호 유효성 검증
export const checkPhone = async (phone) => {
  try {
    const response = await apiClient.get('/api/v1/members/me/check/phone', {
      params: { newPhoneNumber: phone },
    });
    return response.data.result;
  } catch (error) {
    throw new Error(error.response?.data?.message || '전화번호 검증 중 오류가 발생했습니다.');
  }
};

// 전화번호 인증번호 발송
export const sendPhoneVerification = async (phone) => {
  try {
    await apiClient.post('/api/v1/members/me/phone/verification', {
      newPhoneNumber: phone,
    });
    return true;
  } catch (error) {
    throw new Error(error.response?.data?.message || '인증번호 발송 중 오류가 발생했습니다.');
  }
};

// 전화번호 인증번호 확인
export const verifyPhoneCode = async (phone, code) => {
  try {
    await apiClient.post('/api/v1/members/me/phone/verify', {
      newPhoneNumber: phone,
      code: code,
    });
    return true;
  } catch (error) {
    throw new Error(error.response?.data?.message || '인증번호 확인 중 오류가 발생했습니다.');
  }
};

// 전화번호 변경
export const changePhone = async (phone) => {
  try {
    await apiClient.put('/api/v1/members/me/phone', {
      newPhoneNumber: phone,
    });
    return true;
  } catch (error) {
    throw new Error(error.response?.data?.message || '전화번호 변경 중 오류가 발생했습니다.');
  }
};

// 닉네임 중복 검사
export const checkNickname = async (nickname) => {
  try {
    const response = await apiClient.get('/api/v1/members/me/check/nickname', {
      params: { nickname: nickname },
    });
    return response.data.result;
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

// 현재 비밀번호 체크
export const checkCurrentPassword = async (currentPassword) => {
  try {
    const response = await apiClient.get('/api/v1/members/me/password', {
      params: { currentPassword: currentPassword },
    });
    return response.data.isSuccess;
  } catch (error) {
    throw new Error(error.response?.data?.message || '비밀번호 검증 중 오류가 발생했습니다.');
  }
};

// 비밀번호 유효성 검증
export const checkPassword = async (currentPassword, newPassword) => {
  try {
    const response = await apiClient.get('/api/v1/members/me/check/password', {
      params: {
        currentPassword: currentPassword,
        newPassword: newPassword,
      },
    });
    return response.data.result;
  } catch (error) {
    throw new Error(error.response?.data?.message || '비밀번호 검증 중 오류가 발생했습니다.');
  }
};

// 비밀번호 변경
export const changePassword = async (currentPassword, newPassword) => {
  try {
    await apiClient.put('/api/v1/members/me/password', {
      currentPassword: currentPassword,
      newPassword: newPassword,
    });
    return true;
  } catch (error) {
    throw new Error(error.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.');
  }
};

// 회원 탈퇴

export const deleteMember = async (password) => {
  try {
    await apiClient.delete('/api/v1/members/me', {
      data: { password: password },
    });
    return true;
  } catch (error) {
    throw new Error(error.response?.data?.message || '회원 탈퇴 중 오류가 발생했습니다.');
  }
};
