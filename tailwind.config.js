/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#E8E4F0',
          100: '#D4CCE0',
          200: '#B3A6C7',
          300: '#8C7BA8',
          400: '#6750A4',
          500: '#5D3E9C',
          600: '#4A2D7D',
          700: '#3A1F5E',
          800: '#2A1444',
          900: '#1A0B2A',
        },
        secondary: {
          50: '#F3EDF7',
          100: '#E8E0F0',
          200: '#D4C8E0',
          300: '#B8A4C7',
          400: '#9B7DAB',
          500: '#7C5A8F',
          600: '#5D3E70',
          700: '#3A1F4D',
          800: '#21005D',
          900: '#14003A',
        },
        accent: {
          gold: '#FFD700',
          amber: '#FFA000',
          teal: '#00796B',
          coral: '#D32F2F',
        },
        surface: {
          light: '#FFFFFF',
          dark: '#1D1B20',
          variant: '#F3EDF7',
        },
        background: {
          light: '#FCF8FF',
          dark: '#141218',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        serif: ['Georgia', 'Cambria', 'Times New Roman', 'serif'],
      },
      spacing: {
        'safe-bottom': 'env(safe-area-inset-bottom)',
        'safe-top': 'env(safe-area-inset-top)',
      },
      animation: {
        'breathing': 'breathing 8s ease-in-out infinite',
        'fade-in': 'fadeIn 0.3s ease-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'pulse-slow': 'pulse 3s ease-in-out infinite',
      },
      keyframes: {
        breathing: {
          '0%, 100%': { transform: 'scale(1)' },
          '50%': { transform: 'scale(1.15)' },
        },
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(20px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
    },
  },
  plugins: [],
}
