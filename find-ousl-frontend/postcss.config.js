/**
 * PostCSS Configuration
 * Required for Tailwind CSS to work with Vite
 */
export default {
  plugins: {
    tailwindcss: {},    // Process Tailwind directives
    autoprefixer: {},   // Add vendor prefixes for browser compatibility
  },
}