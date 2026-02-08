import shared from "@repo/tailwind-config";

export default {
  plugins: {
    "postcss-preset-mantine": {},
    "postcss-simple-vars": {
      silent: true,
    },
    "@tailwindcss/postcss": {
      config: shared,
    },
    autoprefixer: {},
  },
};
