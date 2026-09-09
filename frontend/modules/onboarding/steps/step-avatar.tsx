'use client';

import { useRef } from 'react';
import { Camera, Loader2, SkipForward } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { useOnboardingWizard } from '../hooks/use-onboarding-wizard';
import { useUploadAvatarMutation } from '@/modules/profile/hooks/use-upload-avatar';
import { useAuthStore } from '@/modules/auth/stores/auth-store';

export function StepAvatar() {
  const draft = useOnboardingWizard((s) => s.draft);
  const update = useOnboardingWizard((s) => s.update);
  const profile = draft.profile;
  const fileInputRef = useRef<HTMLInputElement>(null);

  const upload = useUploadAvatarMutation();
  const setUser = useAuthStore((s) => s.setUser);

  function handleFile(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file) return;
    upload.mutate(file, {
      onSuccess: (data) => {
        update({ profile: { ...profile, avatarUrl: data.avatarUrl } });
        const current = useAuthStore.getState().user;
        if (current) {
          setUser({ ...current });
        }
      }
    });
  }

  const preview = upload.data?.avatarUrl ?? profile.avatarUrl;
  const initials = (profile.fullName || '?').trim().slice(0, 2).toUpperCase() || '?';

  return (
    <div className="space-y-6">
      <header className="text-center">
        <h2 className="text-3xl font-extrabold tracking-tight text-brand-strong">
          Add a profile photo
        </h2>
        <p className="mt-2 text-sm text-brand-muted">
          Profiles with photos get 5× more matches.
        </p>
      </header>

      <div className="flex flex-col items-center gap-4 py-4">
        <div
          className="relative flex h-32 w-32 items-center justify-center overflow-hidden rounded-full bg-brand-hero-soft text-3xl font-bold text-primary shadow-brand-card ring-4 ring-white"
          aria-hidden
        >
          {preview ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img src={preview} alt="" className="h-full w-full object-cover" />
          ) : (
            initials
          )}
          {upload.isPending ? (
            <div className="absolute inset-0 flex items-center justify-center bg-black/40">
              <Loader2 className="h-8 w-8 animate-spin text-white" />
            </div>
          ) : null}
        </div>

        <input
          ref={fileInputRef}
          type="file"
          accept="image/png,image/jpeg,image/webp"
          className="hidden"
          onChange={handleFile}
        />

        <Button
          type="button"
          variant="outline"
          size="lg"
          className="gap-2 rounded-full"
          onClick={() => fileInputRef.current?.click()}
          disabled={upload.isPending}
        >
          <Camera className="h-4 w-4" />
          {preview ? 'Change photo' : 'Upload photo'}
        </Button>

        {upload.isError ? (
          <p className="text-sm text-destructive">
            {upload.error instanceof Error ? upload.error.message : 'Upload failed.'}
          </p>
        ) : null}
        <p className="text-xs text-brand-subtle">
          PNG, JPEG or WebP · max 5 MB
        </p>
      </div>

      <p className="flex items-center justify-center gap-2 text-xs text-brand-subtle">
        <SkipForward className="h-3 w-3" /> You can also skip this and add a photo later from your profile.
      </p>
    </div>
  );
}
