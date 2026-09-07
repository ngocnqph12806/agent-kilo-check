import { PublicProfileView } from '@/modules/profile/components/public-profile-view';

interface ProfilePageProps {
  params: Promise<{ id: string }>;
}

export default async function ProfilePage({ params }: ProfilePageProps) {
  const { id } = await params;
  return <PublicProfileView userId={id} />;
}
