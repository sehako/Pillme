import { ref } from 'vue';
import { fetchDependents } from '../api/dependentmember';

export function useDependents() {
  const dependents = ref([]);

  // ✅ 가족 목록 가져오는 함수
  const loadDependents = async () => {
    try {
      dependents.value = await fetchDependents();
    } catch (error) {
      console.error("가족 목록 불러오기 실패:", error);
    }
  };

  return {
    dependents,
    loadDependents
  };
}
