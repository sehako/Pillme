import localforage from 'localforage';

// 토큰 저장소 설정
const tokenStore = localforage.createInstance({
    name: 'authDB',
    storeName: 'tokens'
});

// ✅ Access Token 저장
export const saveAccessToken = async (token) => {
    try {
        await tokenStore.setItem('accessToken', token);
        // localStorage도 함께 사용 (빠른 접근용)
        localStorage.setItem('accessToken', token);
        console.log("✅ Access Token 저장 완료");
    } catch (error) {
        console.error("❌ Access Token 저장 중 오류 발생:", error);
    }
};

// ✅ Access Token 가져오기 (빠르게 가져옴)
export const getAccessToken = async () => {
    console.log('[localForage] getAccessToken 호출');
    try {
        const token = await tokenStore.getItem('accessToken');
        console.log('[localForage] 토큰 조회 결과:', token);
        return token;
    } catch (error) {
        console.error('[localForage] 토큰 조회 실패:', error);
        throw error;
    }
};

// ✅ Access Token 삭제
export const deleteAccessToken = async () => {
    try {
        await tokenStore.removeItem('accessToken');
        localStorage.removeItem('accessToken');
        console.log("✅ Access Token 삭제 완료");
    } catch (error) {
        console.error("❌ Access Token 삭제 중 오류 발생:", error);
    }
};
