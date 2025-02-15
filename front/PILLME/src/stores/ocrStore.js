import { defineStore } from 'pinia';
import { nextTick } from 'vue';
import apiClient from '../api/index'; // ✅ API 클라이언트
import { decodeToken } from '../utils/jwt'; // ✅ JWT 디코딩 유틸 추가
export const useOcrStore = defineStore('ocr', {
  state: () => ({
    results: JSON.parse(localStorage.getItem('ocrResults')) ?? [],
    showResultsDialog: JSON.parse(localStorage.getItem('ocrShowResultsDialog')) ?? false,
    showNextDialog: JSON.parse(localStorage.getItem('ocrShowNextDialog')) ?? false,
    showMedicationDialog: JSON.parse(localStorage.getItem('ocrShowMedicationDialog')) ?? false,
    hospitalName: '',
    diseaseName: '',
    dateRange: [],
    totalDays: 0,
    isLoading: JSON.parse(localStorage.getItem('ocrIsLoading')) ?? false,
    username: '', // ✅ 사용자 이름 저장
    userId: null, // ✅ 사용자 ID 저장
  }),

  actions: {
    /** ✅ OCR 분석 시작 (로딩 상태 유지) */
    startLoading() {
      this.isLoading = true;
      this.saveToLocalStorage();
    },

    /** ✅ OCR 분석 완료 (결과 저장 + 다이얼로그 표시) */
    async setResults(results) {
      this.results = results;
      this.showResultsDialog = true;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
      this.isLoading = false;
      await nextTick(); // UI 업데이트 보장
      this.saveToLocalStorage();
    },

    /** ✅ OCR 분석 실패 또는 취소 시 로딩 해제 */
    stopLoading() {
      this.isLoading = false;
      this.saveToLocalStorage();
    },

    /** ✅ 다이얼로그 닫기 */
    async closeDialog() {
      // ✅ 일시적으로 true 값을 넣었다가 다시 false로 변경하여 Vue의 반응형 감지 유도
      this.showResultsDialog = true;
      this.showNextDialog = true;
      this.showMedicationDialog = true;

      await nextTick(); // UI 업데이트 보장

      this.showResultsDialog = false;
      this.showNextDialog = false;
      this.showMedicationDialog = false;

      await nextTick(); // UI 업데이트 반영

      this.saveToLocalStorage(); // ✅ localStorage 저장
    },

    /** ✅ 추가 정보 입력 다이얼로그 열기 */
    async openNextDialog() {
      this.showResultsDialog = false;
      this.showNextDialog = true;
      this.showMedicationDialog = false;

      // ✅ 입력 필드 초기화
      this.hospitalName = '';
      this.diseaseName = '';
      this.dateRange = [];
      this.totalDays = 0;

      await nextTick();
      this.saveToLocalStorage();
    },

    /** ✅ 복약 시간 입력 다이얼로그 열기 */
    async openMedicationDialog() {
      this.showResultsDialog = false;
      this.showNextDialog = false;
      this.showMedicationDialog = true;
      await nextTick();
      this.saveToLocalStorage();
    },

    /** ✅ 이전 버튼 - 추가 정보 입력으로 돌아가기 */
    async goBackToNextDialog() {
      this.showResultsDialog = false;
      this.showNextDialog = true;
      this.showMedicationDialog = false;
      await nextTick();
      this.saveToLocalStorage();
    },

    async goBackToResultDialog() {
      this.showResultsDialog = true;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
      await nextTick();
      this.saveToLocalStorage();
    },

    /** ✅ `localStorage`에 OCR 상태 저장 */
    saveToLocalStorage() {
      localStorage.setItem('ocrResults', JSON.stringify(this.results));
      localStorage.setItem('ocrHospitalName', this.hospitalName);
      localStorage.setItem('ocrDiseaseName', this.diseaseName);
      localStorage.setItem('ocrDateRange', JSON.stringify(this.dateRange));
      localStorage.setItem('ocrIsLoading', JSON.stringify(this.isLoading));
    },

    /** ✅ `localStorage`에서 상태 불러오기 */
    loadFromLocalStorage() {
      this.results = JSON.parse(localStorage.getItem('ocrResults')) ?? [];
      this.hospitalName = localStorage.getItem('ocrHospitalName') ?? '';
      this.diseaseName = localStorage.getItem('ocrDiseaseName') ?? '';
      this.dateRange = JSON.parse(localStorage.getItem('ocrDateRange')) ?? [];
      this.isLoading = JSON.parse(localStorage.getItem('ocrIsLoading')) ?? false;

      // ✅ 다이얼로그 상태는 새로고침 시 항상 닫히도록 설정
      this.showResultsDialog = false;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
    },

    /** ✅ JWT 토큰에서 사용자 정보 가져오기 */
    fetchUserInfo() {
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          console.error('❌ 액세스 토큰이 없습니다.');
          return;
        }

        const decoded = decodeToken(token);
        if (!decoded || !decoded.memberId) {
          console.error('❌ JWT에서 memberId를 찾을 수 없습니다.');
          return;
        }

        // ✅ 사용자 정보 설정
        this.userId = decoded.memberId;
        this.username = decoded.name ?? '';

        // console.log('✅ 사용자 정보 불러오기 성공:', decoded);
      } catch (error) {
        console.error('❌ 사용자 정보 가져오기 실패:', error);
      }
    },

    /** ✅ OCR 분석 상태 초기화 */
    resetOcrState() {
      this.results = [];
      this.showResultsDialog = false;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
      this.isLoading = false;
      this.hospitalName = '';
      this.diseaseName = '';
      this.dateRange = [];
      this.totalDays = 0;
      this.saveToLocalStorage();
    },

    async saveOcrDataToDB() {
      try {
        this.isLoading = true;

        // ✅ JWT에서 사용자 ID 가져오기 (memberId)
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) {
          console.error('❌ Access Token 없음');
          return;
        }

        const decodedToken = decodeToken(accessToken);
        if (!decodedToken || !decodedToken.memberId) {
          console.error('❌ JWT 디코딩 실패 또는 memberId 없음', decodedToken);
          return;
        }

        this.userId = decodedToken.memberId; // ✅ 현재 로그인된 사용자 ID 저장
        // console.log('✅ 로그인된 사용자 ID:', this.userId);

        // ✅ 날짜를 "yyyy-MM-dd" 형식으로 변환하는 함수
        const formatDate = (date) => {
          if (!date) return null;
          const d = new Date(date);
          return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
        };

        // ✅ startDate와 endDate 차이로 `period` 계산
        const startDate = this.dateRange?.[0] ? new Date(this.dateRange[0]) : null;
        const endDate = this.dateRange?.[1] ? new Date(this.dateRange[1]) : null;

        let period = 1; // 기본값: 최소 1일
        if (startDate && endDate) {
          const diffTime = Math.abs(endDate - startDate);
          period = Math.max(1, Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1); // 일 수 계산 후 최소 1일 이상 유지
        }

        // ✅ API 요청 데이터 준비
        const requestData = {
          hospital: this.hospitalName || '', // null 방지
          diseaseName: this.diseaseName || '', // null 방지
          reader: this.userId, // ✅ 현재 로그인된 사용자 ID
          startDate: formatDate(this.dateRange?.[0]), // yyyy-MM-dd 형식 변환
          endDate: formatDate(this.dateRange?.[1]), // yyyy-MM-dd 형식 변환
          medications: this.results.map((med) => ({
            medicationName: med.matched_drug || 'Unknown', // ✅ 기본값 설정
            period: this.totalDays || 1, // 기본값: 1일
            morning: med.breakfast ?? false,
            lunch: med.lunch ?? false,
            dinner: med.dinner ?? false,
            sleep: med.bedtime ?? false,
          })),
        };

        // ✅ API 요청 전 데이터 확인
        console.log('📤 API 요청 데이터:', JSON.stringify(requestData, null, 2));

        // ✅ API 요청 (apiClient 사용)
        const response = await apiClient.post('/api/v1/management', requestData);
        console.log('✅ OCR 데이터 저장 성공:', response.data);

        // ✅ 성공 시 다이얼로그 닫기
        await this.closeDialog();
        await nextTick();
      } catch (error) {
        if (error.response) {
          console.error('❌ 백엔드 응답 오류:', error.response.data);
        } else {
          console.error('❌ API 요청 실패:', error);
        }
      } finally {
        this.isLoading = false;
      }
    },
  },
});
