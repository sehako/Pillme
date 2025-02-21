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
    dependentId: null, // ✅ 보호자 또는 피보호자의 ID를 저장
  }),

  actions: {
    /** ✅ `dependentId`를 설정하는 메서드 */
    setDependentId(id) {
      if (typeof id !== 'number' || isNaN(id)) {
        console.warn('⚠️ 잘못된 dependentId 감지:', id);
        return;
      }
      console.log(`✅ [DEBUG] OCR Store에 dependentId 저장: ${id}`);
      this.dependentId = id;
    },

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
    
      // ✅ 기존 OCR 관련 데이터 삭제
      localStorage.removeItem('ocrResults');
      localStorage.removeItem('ocrHospitalName');
      localStorage.removeItem('ocrDiseaseName');
      localStorage.removeItem('ocrDateRange');
      localStorage.removeItem('ocrIsLoading');
      localStorage.removeItem('ocrShowResultsDialog');
      localStorage.removeItem('ocrShowNextDialog');
      localStorage.removeItem('ocrShowMedicationDialog');
    
    },

    /** ✅ OCR 데이터 저장 */
    async saveOcrDataToDB() {
      try {
        this.isLoading = true;

        // ✅ JWT에서 보호자 ID 가져오기
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) {
          console.error('❌ Access Token 없음');
          return;
        }

        const decodedToken = decodeToken(accessToken);
        const guardianId = decodedToken?.memberId; // ✅ 로그인한 보호자 ID

        // ✅ `dependentId`가 `null`이면 `guardianId`를 설정
        if (!this.dependentId) {
          console.warn('⚠️ dependentId가 null이므로 보호자 본인 ID 사용:', guardianId);
          this.dependentId = guardianId;
        }

        // ✅ 최종적으로 `dependentId`가 없으면 오류 발생
        if (!this.dependentId) {
          throw new Error('❌ 저장할 사용자 ID가 없습니다. OCR 데이터를 저장할 수 없습니다.');
        }

        // ✅ 날짜 변환 함수
        const formatDate = (date) => {
          if (!date) return null;
          const d = new Date(date);
          return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
        };

        // ✅ API 요청 데이터 구성
        const requestData = {
          hospital: this.hospitalName || '',
          diseaseName: this.diseaseName || '',
          reader: this.dependentId, // ✅ 피보호자 ID or 보호자 ID 자동 설정
          startDate: formatDate(this.dateRange?.[0]),
          endDate: formatDate(this.dateRange?.[1]),
          medications: this.results.map((med) => ({
            medicationName: med.matched_drug || 'Unknown',
            period: this.totalDays || 1,
            morning: med.breakfast ?? false,
            lunch: med.lunch ?? false,
            dinner: med.dinner ?? false,
            sleep: med.bedtime ?? false,
          })),
        };

        console.log('📤 [DEBUG] API 요청 데이터:', JSON.stringify(requestData, null, 2));

        // ✅ API 요청 실행
        const response = await apiClient.post('/api/v1/management', requestData);

        // ✅ 응답이 있는지 확인
        if (response && response.data) {
          console.log('✅ OCR 데이터 저장 성공:', response.data);
        } else {
          console.warn('⚠️ 응답 데이터가 없음. response:', response);
        }

        await this.closeDialog();

        // ✅ 사용자에게 알림 표시 (Toast 또는 alert)
        alert('✅ 복약 내역이 성공적으로 저장되었습니다!');
      } catch (error) {
        console.error('❌ API 요청 실패:', error);

        // ✅ API 요청이 실패했을 경우 정확한 메시지 출력
        if (error.response) {
          console.error('❌ 서버 응답 오류:', error.response.data);
        } else if (error.request) {
          console.error('❌ 요청이 서버에 도달하지 못함:', error.request);
        } else {
          console.error('❌ 요청 중 오류 발생:', error.message);
        }
      } finally {
        this.isLoading = false;
      }
    },
  },
});
