import { NetworkErrorScreen } from '@/components/shared/network-error-screen';

export const metadata = {
  title: "You're offline · SkillSeed",
  robots: { index: false, follow: false }
};

export default function OfflinePage() {
  return <NetworkErrorScreen />;
}