/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        ministry: {
          deepGreen: '#0B5D3B',
          forestDark: '#073B25',
          emerald: '#16A34A',
          emeraldHover: '#15803D',
          gold: '#D4A72C',
          goldHover: '#B88E1E',
          lightGold: '#FEF3C7',
          lightGreen: '#ECFDF5',
          charcoal: '#1F2937',
          slateBg: '#F8FAFC',
          borderSubtle: '#E2E8F0',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
