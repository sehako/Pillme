export function useRealVH() {
  const isMobile = (() => {
    if (navigator.userAgentData && navigator.userAgentData.mobile) {
      return true;
    }
    return (
      window.matchMedia('(pointer:coarse)').matches ||
      /Android|iPhone|iPad|iPod|Windows Phone|webOS|BlackBerry|Opera Mini|IEMobile/i.test(navigator.userAgent)
    );
  })();

  const setRealVH = () => {
    if (!isMobile) return;
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
  };

  const initRealVH = () => {
    if (!isMobile) return;
    setRealVH();
    window.addEventListener('resize', setRealVH);
  };

  const cleanUpRealVH = () => {
    if (!isMobile) return;
    window.removeEventListener('resize', setRealVH);
  };

  return {
    initRealVH,
    cleanUpRealVH,
  };
}
