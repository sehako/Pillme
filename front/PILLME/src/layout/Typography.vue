<!-- src/components/layout/Typography.vue -->
<template>
  <component :is="tag" :class="computedClass">
    <slot></slot>
  </component>
</template>

<script>
export default {
  name: 'Typography',
  props: {
    variant: {
      type: String,
      default: 'body',
      validator: value => ['h1', 'h2', 'h3', 'body', 'caption'].includes(value),
    },
    color: {
      type: String,
      default: 'inherit',
    },
    tag: {
      type: String,
      default: 'p',
    },
  },
  computed: {
    computedClass() {
      return [
        `typography--${this.variant}`,
        { [`text--${this.color}`]: this.color !== 'inherit' },
      ];
    },
  },
};
</script>

<style scoped>
/* 4px 스타일 가이드 적용 */
.typography--h1 {
  font-size: 28px; /* 4px * 7 */
  font-weight: bold;
}
.typography--h2 {
  font-size: 24px; /* 4px * 6 */
  font-weight: semi-bold;
}
.typography--h3 {
  font-size: 20px; /* 4px * 5 */
  font-weight: semi-bold;
}
.typography--body {
  font-size: 16px; /* 4px * 4 */
  font-weight: normal;
}
.typography--caption {
  font-size: 12px; /* 4px * 3 */
  font-weight: light;
}

/* 색상 정의 */
.text--primary {
  color: #1976d2;
}
.text--secondary {
  color: #424242;
}
.text--error {
  color: #d32f2f;
}
</style>
