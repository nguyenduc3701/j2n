const path = require("path");

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    path
      .join(__dirname, "../../apps/web/about/app/**/*.{js,ts,jsx,tsx}")
      .replace(/\\/g, "/"),
    path
      .join(__dirname, "../ui/src/components/**/*.{ts,tsx,js,jsx}")
      .replace(/\\/g, "/"),
  ],
  theme: {},
  plugins: [],
};
