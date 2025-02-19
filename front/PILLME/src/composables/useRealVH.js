export function useRealVH() {
  // 모바일 기기 감지 로직
  const isMobile = (() => {
    if (navigator.userAgentData?.mobile) {
      return true;
    }
    return (
      window.matchMedia('(pointer:coarse)').matches ||
      /Android|iPhone|iPad|iPod|Windows Phone|webOS|BlackBerry|Opera Mini|IEMobile/i.test(navigator.userAgent)
    );
  })();

  // 디바운스 함수 추가
  const debounce = (fn, delay = 100) => {
    let timeoutId;
    return (...args) => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => fn.apply(this, args), delay);
    };
  };

  const setRealVH = () => {
    if (!isMobile) return;
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
  };

  // 디바운스된 setRealVH
  const debouncedSetRealVH = debounce(setRealVH);

  const initRealVH = () => {
    if (!isMobile) return;
    setRealVH(); // 초기 실행
    
    // 리사이즈 이벤트에는 디바운스된 함수 사용
    window.addEventListener('resize', debouncedSetRealVH);
    // 방향 전환 이벤트에는 즉시 실행
    window.addEventListener('orientationchange', setRealVH);
  };

  const cleanUpRealVH = () => {
    if (!isMobile) return;
    window.removeEventListener('resize', debouncedSetRealVH);
    window.removeEventListener('orientationchange', setRealVH);
  };

  return {
    initRealVH,
    cleanUpRealVH,
    isMobile, // 필요한 경우 외부에서 사용
  };
}