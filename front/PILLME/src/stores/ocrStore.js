import { defineStore } from 'pinia';

export const useOcrStore = defineStore('ocr', {
  state: () => ({
    results: JSON.parse(localStorage.getItem('ocrResults')) ?? [], // ✅ OCR 분석 결과 (새로고침 유지)
    showResultsDialog: JSON.parse(localStorage.getItem('ocrShowResultsDialog')) ?? false,
    showNextDialog: JSON.parse(localStorage.getItem('ocrShowNextDialog')) ?? false,
    showMedicationDialog: JSON.parse(localStorage.getItem('ocrShowMedicationDialog')) ?? false,
    hospitalName: localStorage.getItem('ocrHospitalName') ?? '',
    diseaseName: localStorage.getItem('ocrDiseaseName') ?? '',
    dateRange: JSON.parse(localStorage.getItem('ocrDateRange')) ?? [],
    isLoading: JSON.parse(localStorage.getItem('ocrIsLoading')) ?? false, // ✅ OCR 진행 중 상태 유지
  }),

  actions: {
    /** ✅ OCR 분석 시작 (로딩 상태 유지) */
    startLoading() {
      this.isLoading = true;
      this.saveToLocalStorage();
    },

    /** ✅ OCR 분석 완료 (결과 저장 + 다이얼로그 표시) */
    setResults(results) {
      this.results = results;
      this.showResultsDialog = true;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
      this.isLoading = false; // ✅ 로딩 상태 해제
      this.saveToLocalStorage();
    },

    /** ✅ OCR 분석 실패 또는 취소 시 로딩 해제 */
    stopLoading() {
      this.isLoading = false;
      this.saveToLocalStorage();
    },

    /** ✅ 다이얼로그 닫기 */
    closeDialog() {
      this.showResultsDialog = false;
      this.showNextDialog = false;
      this.showMedicationDialog = false;
      this.saveToLocalStorage();
    },

    /** ✅ 추가 정보 입력 다이얼로그 열기 */
    openNextDialog() {
      this.showResultsDialog = false;
      this.showNextDialog = true;
      this.saveToLocalStorage();
    },

    /** ✅ 복약 시간 입력 다이얼로그 열기 */
    openMedicationDialog() {
      this.showNextDialog = false;
      this.showMedicationDialog = true;
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
      localStorage.setItem('ocrIsLoading', JSON.stringify(this.isLoading)); // ✅ 로딩 상태 저장
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
      this.isLoading = JSON.parse(localStorage.getItem('ocrIsLoading')) ?? false; // ✅ 로딩 상태 유지
    },
  },
});
