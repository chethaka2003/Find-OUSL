/** @type {import('tailwindcss').Config} */
export default {
  // Specify files to scan for Tailwind classes
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // Custom colors for OUSL branding
      colors: {
        primary: '#1e3a5f',    // Dark blue - Main brand color
        secondary: '#2563eb',  // Bright blue - Accent color
      },
      // Custom fonts
      fontFamily: {
        // Add custom fonts here if needed
        
      },
      // Custom spacing (optional)
      spacing: {
        // Add custom spacing values if needed
      },
      // Custom breakpoints (optional)
      screens: {
        // sm: '640px',
        // md: '768px',
        // lg: '1024px',
        // xl: '1280px',
        // '2xl': '1536px',
      },
    },
  },
  plugins: [
    // Add Tailwind plugins here if needed
    // require('@tailwindcss/forms'),
    // require('@tailwindcss/typography'),
  ],
}