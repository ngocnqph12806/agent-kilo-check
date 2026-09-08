import type { Metadata } from 'next';

const DEFAULT_APP_URL = process.env.NEXT_PUBLIC_APP_URL || 'http://localhost:3000';

export interface PageMetadataOptions {
  title: string;
  description?: string;
  path?: string;
  imagePath?: string;
  type?: 'website' | 'article' | 'profile';
  twitterCard?: 'summary' | 'summary_large_image';
  noIndex?: boolean;
  keywords?: string[];
}

const DEFAULT_DESCRIPTION =
  'P2P skill-exchange platform — teach what you know, learn what you love.';

export function buildPageMetadata({
  title,
  description = DEFAULT_DESCRIPTION,
  path = '/',
  imagePath = '/og-default.png',
  type = 'website',
  twitterCard = 'summary_large_image',
  noIndex = false,
  keywords
}: PageMetadataOptions): Metadata {
  const fullTitle = title.includes('SkillSeed') ? title : `${title} · SkillSeed`;
  const canonical = new URL(path, DEFAULT_APP_URL).toString();
  const imageUrl = new URL(imagePath, DEFAULT_APP_URL).toString();

  return {
    title: fullTitle,
    description,
    keywords,
    alternates: { canonical },
    openGraph: {
      title: fullTitle,
      description,
      url: canonical,
      siteName: 'SkillSeed',
      images: [{ url: imageUrl, width: 1200, height: 630, alt: fullTitle }],
      locale: 'en_US',
      type
    },
    twitter: {
      card: twitterCard,
      title: fullTitle,
      description,
      images: [imageUrl],
      creator: '@skillseed'
    },
    robots: noIndex ? { index: false, follow: false } : undefined
  };
}

export const APP_URL = DEFAULT_APP_URL;