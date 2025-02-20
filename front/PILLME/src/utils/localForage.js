import localforage from 'localforage';

// ✅ localForage 설정 (IndexedDB를 기본 저장소로 사용)
localforage.config({
    name: "authDB",
    storeName: "tokens"
});

// ✅ Access Token 저장
export const saveAccessToken = async (accessToken) => {
    try {
        await localforage.setItem("accessToken", accessToken);
        console.log("✅ Access Token 저장 완료");
    } catch (error) {
        console.error("❌ Access Token 저장 중 오류 발생:", error);
    }
};

// ✅ Access Token 가져오기 (빠르게 가져옴)
export const getAccessToken = async () => {
    try {
        return await localforage.getItem("accessToken");
    } catch (error) {
        console.error("❌ Access Token 조회 중 오류 발생:", error);
        return null;
    }
};

// ✅ Access Token 삭제
export const deleteAccessToken = async () => {
    try {
        await localforage.removeItem("accessToken");
        console.log("✅ Access Token 삭제 완료");
    } catch (error) {
        console.error("❌ Access Token 삭제 중 오류 발생:", error);
    }
};
