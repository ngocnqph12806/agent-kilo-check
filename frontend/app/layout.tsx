import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';

import { CookieConsentBanner } from '@/components/shared/cookie-consent-banner';
import { AuthInitializer } from '@/modules/auth/components/auth-initializer';
import { QueryProvider } from '@/lib/query-provider';

const inter = Inter({ subsets: ['latin'], variable: '--font-inter' });

export const metadata: Metadata = {
  title: 'SkillSeed',
  description: 'P2P skill-exchange platform — teach what you know, learn what you love.',
  metadataBase: new URL(process.env.NEXT_PUBLIC_APP_URL || 'http://localhost:3000')
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={inter.variable} suppressHydrationWarning>
      <body>
        <QueryProvider>
          <AuthInitializer />
          {children}
          <CookieConsentBanner />
        </QueryProvider>
      </body>
    </html>
  );
}
