import type { MetadataRoute } from 'next';

import { APP_URL } from '@/lib/metadata';

export default function sitemap(): MetadataRoute.Sitemap {
  const now = new Date();
  return [
    {
      url: `${APP_URL}/`,
      lastModified: now,
      changeFrequency: 'weekly',
      priority: 1.0
    },
    {
      url: `${APP_URL}/discover`,
      lastModified: now,
      changeFrequency: 'daily',
      priority: 0.9
    },
    {
      url: `${APP_URL}/register`,
      lastModified: now,
      changeFrequency: 'monthly',
      priority: 0.8
    },
    {
      url: `${APP_URL}/login`,
      lastModified: now,
      changeFrequency: 'monthly',
      priority: 0.4
    },
    {
      url: `${APP_URL}/privacy`,
      lastModified: now,
      changeFrequency: 'yearly',
      priority: 0.3
    },
    {
      url: `${APP_URL}/terms`,
      lastModified: now,
      changeFrequency: 'yearly',
      priority: 0.3
    }
  ];
}