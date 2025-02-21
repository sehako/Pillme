import { ref, onMounted, onUnmounted } from 'vue';

export function useNavbarHeight(navbarRef) {
  const navbarHeight = ref(0);
  let observer;

  const updateNavbarHeight = () => {
    if (navbarRef.value) {
      navbarHeight.value = navbarRef.value.offsetHeight;
    }
  };

  onMounted(() => {
    observer = new ResizeObserver(updateNavbarHeight);
    if (navbarRef.value) {
      observer.observe(navbarRef.value);
      updateNavbarHeight();
    }
  });

  onUnmounted(() => {
    if (observer && navbarRef.value) {
      observer.unobserve(navbarRef.value);
    }
  });

  return {
    navbarHeight,
  };
}
