module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'prettier', // ESLint와 Prettier 충돌 방지
  ],
  plugins: ['vue', 'prettier'],
  rules: {
    'prettier/prettier': 'error', // Prettier 룰 위반 시 ESLint 오류 처리
    'vue/multi-word-component-names': 'off', // Vue 단일 단어 컴포넌트 허용
  },
};
