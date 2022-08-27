const colors = require("tailwindcss/colors");

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js,jsx,tsx,svg}", "./*.html"],
  theme: {
    fontFamily: {
      sans: ["poppins"],
    },
    extend: {
      colors: {
        primary: colors.cyan,
      },
    },
  },
  plugins: [],
};
