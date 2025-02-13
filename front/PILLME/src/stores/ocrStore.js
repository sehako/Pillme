import { defineStore } from 'pinia';
import { nextTick } from 'vue';

export const useOcrStore = defineStore('ocr', {
  state: () => ({
    results: JSON.parse(localStorage.getItem('ocrResults')) ?? [],
    showResultsDialog: JSON.parse(localStorage.getItem('ocrShowResultsDialog')) ?? false,
    showNextDialog: JSON.parse(localStorage.getItem('ocrShowNextDialog')) ?? false,
    showMedicationDialog: JSON.parse(localStorage.getItem('ocrShowMedicationDialog')) ?? false,
    hospitalName:'',
    diseaseName: '',
    dateRange: [],
    totalDays: 0,
    isLoading: JSON.parse(localStorage.getItem('ocrIsLoading')) ?? false,
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
      this.showResultsDialog = false;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
      await nextTick();
      this.saveToLocalStorage();
    },

    /** ✅ 추가 정보 입력 다이얼로그 열기 */
    async openNextDialog() {
      this.showResultsDialog = false;
      this.showNextDialog = true;
      this.showMedicationDialog = false;

      // ✅ 입력 필드 초기화
      this.hospitalName = '';  // 병원 이름 초기화
      this.diseaseName = '';   // 병명 초기화
      this.dateRange = [];     // 복용 기간 초기화
      this.totalDays = 0;      // 복용 일수 초기화

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
      localStorage.setItem('ocrShowResultsDialog', JSON.stringify(this.showResultsDialog));
      localStorage.setItem('ocrShowNextDialog', JSON.stringify(this.showNextDialog));
      localStorage.setItem('ocrShowMedicationDialog', JSON.stringify(this.showMedicationDialog));
      localStorage.setItem('ocrHospitalName', this.hospitalName);
      localStorage.setItem('ocrDiseaseName', this.diseaseName);
      localStorage.setItem('ocrDateRange', JSON.stringify(this.dateRange));
      localStorage.setItem('ocrIsLoading', JSON.stringify(this.isLoading));
    },

    /** ✅ `localStorage`에서 상태 불러오기 */
    loadFromLocalStorage() {
      this.results = JSON.parse(localStorage.getItem('ocrResults')) ?? [];
      this.showResultsDialog = JSON.parse(localStorage.getItem('ocrShowResultsDialog')) ?? false;
      this.showNextDialog = JSON.parse(localStorage.getItem('ocrShowNextDialog')) ?? false;
      this.showMedicationDialog = JSON.parse(localStorage.getItem('ocrShowMedicationDialog')) ?? false;
      this.hospitalName = localStorage.getItem('ocrHospitalName') ?? '';
      this.diseaseName = localStorage.getItem('ocrDiseaseName') ?? '';
      this.dateRange = JSON.parse(localStorage.getItem('ocrDateRange')) ?? [];
      this.isLoading = JSON.parse(localStorage.getItem('ocrIsLoading')) ?? false;
    },
  },
});
