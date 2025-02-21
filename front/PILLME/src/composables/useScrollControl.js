import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

export function useScrollControl(alwaysScrollablePages = []) {
  const route = useRoute();
  const isScrollAllowed = ref(false);

  watch(
    () => route.path,
    (current) => {
      isScrollAllowed.value =
        alwaysScrollablePages.includes(current) || current.startsWith('/mypage');

      if (!isScrollAllowed.value && !current.startsWith('/mypage')) {
        document.documentElement.style.overflow = 'hidden';
        document.body.style.overflow = 'hidden';
        window.scrollTo(0, 0);
      } else {
        document.documentElement.style.overflow = '';
        document.body.style.overflow = '';
      }
    },
    { immediate: true }
  );

  return { isScrollAllowed };
}
