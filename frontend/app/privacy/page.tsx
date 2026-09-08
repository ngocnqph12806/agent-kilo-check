import type { Metadata } from 'next';

const sections: Array<{ title: string; body: string[] }> = [
  {
    title: '1. Who we are',
    body: [
      'SkillSeed is operated by SkillSeed Pte. Ltd. ("we", "us", "our"). Our registered office is at [address]. You can contact us at privacy@skillseed.app.',
      'We are the data controller for the personal information described in this policy.'
    ]
  },
  {
    title: '2. What data we collect',
    body: [
      'Account data: name, email, phone (optional), country, timezone, languages, learning style.',
      'Skill DNA: skills you offer, skills you want to learn, experience level, hourly Seed rate.',
      'Availability: recurring weekly free slots you publish to other members.',
      'Bookings: who you booked with, when, duration, status, notes, cancellation reasons.',
      'Sessions: chat messages, optional Daily.co call metadata (no audio/video stored by us).',
      'Ratings: numerical scores and written reviews you give or receive.',
      'Wallet: Seed credits, debits, expiry, transaction descriptions.',
      'Device & log data: IP address, user agent, page views, error reports.'
    ]
  },
  {
    title: '3. How we use your data',
    body: [
      'Provide the marketplace: matching teachers and learners, scheduling sessions, processing the Seed ledger.',
      'Safety: prevent fraud, enforce community guidelines, investigate reported content.',
      'Improve SkillSeed: aggregate analytics to fix bugs and prioritise features. Analytics identifiers respect your cookie-consent choice.',
      'Communications: transactional email (bookings, reminders, password). Marketing email only with your opt-in consent.'
    ]
  },
  {
    title: '4. Legal basis (GDPR Art. 6)',
    body: [
      'Contract: providing the service you signed up for.',
      'Legitimate interests: keeping SkillSeed safe, preventing abuse, basic product analytics.',
      'Consent: optional analytics cookies and marketing email.',
      'Legal obligation: tax / accounting records.'
    ]
  },
  {
    title: '5. Sharing',
    body: [
      'Other members see your public profile (name, avatar, bio, skills offered, ratings). Email and phone are never shown to other members.',
      'Service providers acting as processors: hosting (Railway / Supabase / Vercel), email (Resend), video (Daily.co), error tracking (Sentry, optional). Each is bound by a data-processing agreement.',
      'Authorities when legally required.'
    ]
  },
  {
    title: '6. International transfers',
    body: [
      'We store data in the EU (Frankfurt) and may process it in other regions where our subprocessors operate. We rely on Standard Contractual Clauses for cross-border transfers.'
    ]
  },
  {
    title: '7. Retention',
    body: [
      'Account data: until you delete your account.',
      'After soft-delete: 30 days, then permanently purged.',
      'Booking & rating records: 24 months for dispute resolution.',
      'Backups: rolling 30 days.'
    ]
  },
  {
    title: '8. Your rights',
    body: [
      'Access — request a copy via Settings → Download my data.',
      'Rectification — edit your profile at any time.',
      'Erasure — Settings → Delete my account (T-M200).',
      'Portability — the JSON export above is machine-readable (T-M201).',
      'Object / restrict — contact privacy@skillseed.app.',
      'Complain — lodge a complaint with your local data protection authority.'
    ]
  },
  {
    title: '9. Cookies',
    body: [
      'Essential: session token, CSRF protection. Cannot be disabled.',
      'Analytics: page views, error context. Disabled until you click Accept all.'
    ]
  },
  {
    title: '10. Security',
    body: [
      'TLS in transit, encryption at rest in managed databases, hashed passwords (bcrypt), rate limiting on auth endpoints, principle-of-least-privilege for staff.'
    ]
  },
  {
    title: '11. Children',
    body: ['SkillSeed is not directed at children under 16. We do not knowingly collect their data.']
  },
  {
    title: '12. Changes',
    body: [
      'We will notify you by email at least 14 days before material changes take effect. The current version is always available at /privacy.'
    ]
  }
];

export const metadata: Metadata = {
  title: 'Privacy Policy · SkillSeed',
  description:
    'How SkillSeed collects, uses and protects your personal data, including GDPR rights.',
  alternates: { canonical: '/privacy' },
  openGraph: {
    title: 'Privacy Policy · SkillSeed',
    description: 'How SkillSeed handles your personal data.',
    url: '/privacy',
    type: 'article'
  }
};

export default function PrivacyPage() {
  return (
    <main className="container max-w-3xl py-12">
      <header className="mb-10 space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Legal</p>
        <h1 className="text-3xl font-bold tracking-tight">Privacy Policy</h1>
        <p className="text-sm text-muted-foreground">Last updated: 2026-09-08</p>
      </header>

      <div className="space-y-8 text-sm leading-relaxed text-foreground">
        {sections.map((section) => (
          <section key={section.title} className="space-y-2">
            <h2 className="text-lg font-semibold">{section.title}</h2>
            {section.body.map((paragraph) => (
              <p key={paragraph}>{paragraph}</p>
            ))}
          </section>
        ))}
      </div>

      <p className="mt-12 text-xs text-muted-foreground">
        Questions? Email{' '}
        <a href="mailto:privacy@skillseed.app" className="text-primary underline-offset-4 hover:underline">
          privacy@skillseed.app
        </a>
        .
      </p>
    </main>
  );
}