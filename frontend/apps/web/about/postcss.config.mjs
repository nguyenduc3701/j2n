const config = {
  plugins: {
    'postcss-preset-mantine': {}, // Thêm plugin này
    'postcss-simple-vars': {
      silent: true,
    },
    '@tailwindcss/postcss': {},
    'autoprefixer': {},
  },
};

export default config;
