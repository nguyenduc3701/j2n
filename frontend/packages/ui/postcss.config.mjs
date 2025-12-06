import shared from "@repo/tailwind-config";

export default {
  plugins: {
    "@tailwindcss/postcss": {
      config: shared,
    },
    autoprefixer: {},
  },
};
