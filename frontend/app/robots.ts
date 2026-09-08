import type { MetadataRoute } from 'next';

import { APP_URL } from '@/lib/metadata';

export default function robots(): MetadataRoute.Robots {
  return {
    rules: [
      {
        userAgent: '*',
        allow: ['/', '/discover', '/privacy', '/terms', '/register', '/login'],
        disallow: ['/api/', '/onboarding', '/users/me', '/bookings', '/wallet', '/session', '/settings']
      }
    ],
    sitemap: `${APP_URL}/sitemap.xml`,
    host: APP_URL
  };
}