/** @type {import('tailwindcss').Config} */
export default {

    safelist: [
      'bg-[#4E7351]',
      'hover:bg-[#3D5A3F]',
      // 다른 동적 클래스들도 추가
    ],
    // 다른 설정들...
  content: [
    
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      spacing: {
        '1': '4px',
        '2': '8px',
        '3': '12px',
        '4': '16px',
        '5': '20px',
        '6': '24px',
        '8': '32px',
        '10': '40px',
      },
    },
  },
  plugins: [],
}