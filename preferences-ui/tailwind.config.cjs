const colors = require("tailwindcss/colors")

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js,jsx,tsx}", "./*.html"],
  theme: {
    extend: {
      colors: {
        "primary":colors.cyan
      }
    },
  },
  plugins: [],
};
