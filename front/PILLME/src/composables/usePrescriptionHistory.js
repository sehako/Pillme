import { ref } from "vue";
import { fetchHistory, fetchPrescriptionPeriod } from "../api/drughistory";

export function usePrescriptionHistory() {
  const modalData = ref([]);
  const showModal = ref(false);

  async function fetchPrescriptionHistory() {
    try {
      const historyResponse = await fetchHistory();
      const periodResponse = await fetchPrescriptionPeriod();

      const groupedHistory = {};
      // console.log("✅ 처방전 기간 조회 결과:", periodResponse);
      historyResponse.result.forEach((item) => {
        const id = item.informationId;
        if (!groupedHistory[id]) {
          groupedHistory[id] = {
            informationId: id,
            diseaseName: item.diseaseName,
            hospital: item.hospital || "병원 정보 없음",
            takingDate: item.takingDate,
          };
        }
      });

      Object.keys(groupedHistory).forEach((id) => {
        const period = periodResponse.find((p) => p.informationId === Number(id));
        if (period) {
          groupedHistory[id].startDate = period.startDate;
          groupedHistory[id].endDate = period.endDate;
        } else {
          groupedHistory[id].startDate = "기간 정보 없음";
          groupedHistory[id].endDate = "기간 정보 없음";
        }
      });

      modalData.value = Object.values(groupedHistory);
      showModal.value = true;
    } catch (error) {
      console.error("❌ 복용 내역 조회 실패:", error);
    }
  }

  function handleModalClose() {
    showModal.value = false;
    modalData.value = [];
  }

  return { modalData, showModal, fetchPrescriptionHistory, handleModalClose };
}
