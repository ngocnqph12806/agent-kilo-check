import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';

import { CookieConsentBanner } from '@/components/shared/cookie-consent-banner';
import { IntercomFeedback } from '@/components/shared/feedback-launcher';
import { AuthInitializer } from '@/modules/auth/components/auth-initializer';
import { buildPageMetadata } from '@/lib/metadata';
import { QueryProvider } from '@/lib/query-provider';

const inter = Inter({ subsets: ['latin'], variable: '--font-inter' });

export const metadata: Metadata = buildPageMetadata({
  title: 'SkillSeed',
  description: 'P2P skill-exchange platform — teach what you know, learn what you love.',
  path: '/'
});

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={inter.variable} suppressHydrationWarning>
      <body>
        <QueryProvider>
          <AuthInitializer />
          {children}
          <CookieConsentBanner />
          <IntercomFeedback />
        </QueryProvider>
      </body>
    </html>
  );
}
