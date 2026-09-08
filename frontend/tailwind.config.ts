import type { Config } from 'tailwindcss';

const config: Config = {
  darkMode: ['class'],
  content: [
    './app/**/*.{ts,tsx}',
    './components/**/*.{ts,tsx}',
    './modules/**/*.{ts,tsx}'
  ],
  theme: {
    container: {
      center: true,
      padding: '2rem',
      screens: { '2xl': '1400px' }
    },
    extend: {
      colors: {
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))'
        },
        secondary: {
          DEFAULT: 'hsl(var(--secondary))',
          foreground: 'hsl(var(--secondary-foreground))'
        },
        muted: {
          DEFAULT: 'hsl(var(--muted))',
          foreground: 'hsl(var(--muted-foreground))'
        },
        accent: {
          DEFAULT: 'hsl(var(--accent))',
          foreground: 'hsl(var(--accent-foreground))'
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))'
        },
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))'
        },
        popover: {
          DEFAULT: 'hsl(var(--popover))',
          foreground: 'hsl(var(--popover-foreground))'
        }
      },
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)'
      },
      backgroundImage: {
        'brand-cta': 'linear-gradient(to right, var(--brand-cta-from), var(--brand-cta-to))',
        'brand-hero-soft': 'linear-gradient(135deg, var(--brand-hero-from), var(--brand-hero-to))',
        'brand-hero-strong': 'linear-gradient(135deg, var(--brand-cta-from), var(--brand-cta-to))'
      },
      boxShadow: {
        'brand-card': '0 6px 24px -6px rgb(17 24 39 / 0.08), 0 4px 8px -4px rgb(17 24 39 / 0.06)',
        'brand-cta': '0 8px 24px -8px rgb(16 185 129 / 0.45)'
      },
      fontSize: {
        'display': ['32px', { lineHeight: '40px', fontWeight: '800' }]
      }
    }
  },
  plugins: [require('tailwindcss-animate')]
};

export default config;
