import type { Metadata } from 'next';

const sections: Array<{ title: string; body: string[] }> = [
  {
    title: '1. Acceptance',
    body: [
      'By creating a SkillSeed account or using skillseed.app you agree to these Terms of Service. If you do not agree, do not use the service.'
    ]
  },
  {
    title: '2. Eligibility',
    body: [
      'You must be at least 16 years old and able to enter a binding contract in your jurisdiction.'
    ]
  },
  {
    title: '3. The Seed economy',
    body: [
      'SkillSeed uses an internal unit called a Seed. Seeds have no fiat value and cannot be exchanged for money, crypto, or anything outside the platform.',
      'Every hour you teach earns you 60 Seeds (pro-rated by session length).',
      'Seeds expire 12 months after they are earned if unused.',
      'We may grant promotional Seeds at our discretion; these are not refundable for fiat.'
    ]
  },
  {
    title: '4. Acceptable use',
    body: [
      'No illegal, harmful, harassing, hateful, sexually explicit, or misleading content.',
      'No impersonation of other people or entities.',
      'No spam, scraping, or unauthorised commercial activity.',
      'No attempts to circumvent the Seed economy (for example, paying for outside sessions to avoid Seed costs).',
      'No teaching of skills whose provision is regulated and where you lack the required licence.'
    ]
  },
  {
    title: '5. Bookings & sessions',
    body: [
      'A booking is a commitment between two members. Late cancellations (less than 24 hours before the start) may forfeit Seeds.',
      'No-shows are flagged automatically. Repeated no-shows may result in account restrictions.',
      'Daily.co provides the video room. Their usage policy applies during sessions.'
    ]
  },
  {
    title: '6. Ratings & reviews',
    body: [
      'Ratings are left voluntarily and reviewed for abuse. We may remove ratings that violate these Terms.',
      'Auto-ratings (no review submitted within 48 hours) count as a default 4-star rating with no written comment.'
    ]
  },
  {
    title: '7. Intellectual property',
    body: [
      'You retain ownership of content you create. You grant SkillSeed a worldwide, royalty-free licence to host and display it for the purpose of operating the service.',
      'You may not copy, redistribute, or resell SkillSeed source, branding, or design without written permission.'
    ]
  },
  {
    title: '8. Suspension & termination',
    body: [
      'We may suspend or terminate accounts that breach these Terms or the Privacy Policy.',
      'You may delete your account at any time from Settings → Delete my account. Soft-deleted data is purged after 30 days.'
    ]
  },
  {
    title: '9. Disclaimers & liability',
    body: [
      'SkillSeed is provided "as is" without warranties of any kind. To the maximum extent permitted by law, our aggregate liability is limited to the Seeds you have paid in the 12 months preceding the claim, or USD 100, whichever is greater.'
    ]
  },
  {
    title: '10. Governing law',
    body: [
      'These Terms are governed by the laws of Singapore. Disputes are resolved by arbitration in Singapore under the SIAC rules.'
    ]
  },
  {
    title: '11. Changes',
    body: [
      'We may update these Terms. Material changes will be announced at least 14 days in advance by email and in-app banner. Continued use after the effective date constitutes acceptance.'
    ]
  },
  {
    title: '12. Contact',
    body: [
      'SkillSeed Pte. Ltd. · legal@skillseed.app'
    ]
  }
];

export const metadata: Metadata = {
  title: 'Terms of Service · SkillSeed',
  description: 'Rules for using SkillSeed — eligibility, Seed economy, bookings, ratings, liability.',
  alternates: { canonical: '/terms' },
  openGraph: {
    title: 'Terms of Service · SkillSeed',
    description: 'Rules for using SkillSeed.',
    url: '/terms',
    type: 'article'
  }
};

export default function TermsPage() {
  return (
    <main className="container max-w-3xl py-12">
      <header className="mb-10 space-y-2">
        <p className="text-xs uppercase tracking-widest text-muted-foreground">Legal</p>
        <h1 className="text-3xl font-bold tracking-tight">Terms of Service</h1>
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
        <a href="mailto:legal@skillseed.app" className="text-primary underline-offset-4 hover:underline">
          legal@skillseed.app
        </a>
        .
      </p>
    </main>
  );
}