import { cn } from '@/lib/utils';
import { BrandLogo } from './brand-logo';

export interface MarketingHeroProps {
  variant?: 'auth' | 'landing';
  testimonial?: {
    quote: string;
    name: string;
    role: string;
    emoji?: string;
  };
  stats?: ReadonlyArray<{ value: string; label: string }>;
  className?: string;
}

const defaultAuthTestimonial = {
  quote: 'SkillSeed helped me learn React in exchange for teaching cooking. Now I run both as side gigs.',
  name: 'Mai Tran',
  role: 'Frontend Dev · HCMC',
  emoji: '👩'
};

const defaultStats = [
  { value: '200K+', label: 'learners' },
  { value: '4.8★', label: 'rating' },
  { value: '120+', label: 'countries' }
] as const;

export function MarketingHero({
  variant = 'auth',
  testimonial = defaultAuthTestimonial,
  stats = defaultStats,
  className
}: MarketingHeroProps) {
  return (
    <aside
      className={cn(
        'relative hidden flex-col justify-between overflow-hidden bg-brand-hero-strong p-10 text-white lg:flex lg:w-[42%] xl:w-[40%]',
        className
      )}
      aria-hidden={variant === 'auth'}
    >
      <BrandLogo variant="hero" size="md" />

      <div className="space-y-8">
        {testimonial ? (
          <figure className="rounded-2xl bg-white/10 p-6 shadow-brand-card backdrop-blur-sm">
            <span className="block text-5xl font-bold leading-none text-white/90">&ldquo;</span>
            <blockquote className="mt-1 space-y-1 text-lg font-medium leading-relaxed">
              {testimonial.quote.split('\n').map((line, idx) => (
                <p key={idx}>{line}</p>
              ))}
            </blockquote>
            <figcaption className="mt-4 flex items-center gap-3">
              <span
                className="inline-flex h-9 w-9 items-center justify-center rounded-full bg-brand-on-hero text-base"
                aria-hidden
              >
                {testimonial.emoji ?? '🙂'}
              </span>
              <div className="text-sm">
                <div className="font-semibold">{testimonial.name}</div>
                <div className="text-brand-on-hero">{testimonial.role}</div>
              </div>
            </figcaption>
          </figure>
        ) : null}
      </div>

      {stats?.length ? (
        <div className="space-y-2 text-brand-on-hero">
          <p className="text-xs font-semibold uppercase tracking-widest text-white/80">Trusted by</p>
          <dl className="grid grid-cols-3 gap-6">
            {stats.map((stat) => (
              <div key={stat.label}>
                <dt className="text-xl font-bold text-white">{stat.value}</dt>
                <dd className="text-xs text-brand-on-hero">{stat.label}</dd>
              </div>
            ))}
          </dl>
        </div>
      ) : null}
    </aside>
  );
}
