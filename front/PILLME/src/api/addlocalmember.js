import apiClient from "./index";

export const addLocalMember = async ({ name, gender, birthday,
  //  member
  // Token
   }) => {
  try {
    // ✅ 요청 데이터 확인
    const response = await apiClient.post("/api/v1/dependency/local-member", {
      name,
      gender,
      birthday,
      // member,
      // Token,
    }, 
    // {memberId}
  );
    return response.data;
  } catch (error) {
    console.error("비회원 추가 실패:", error.response ? error.response.data : error);
    throw error;
  }
};
