'use client';

import Link from 'next/link';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormError, useFormServerError } from '@/modules/auth/components/form-status';
import { useResetPasswordMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { resetPasswordSchema, type ResetPasswordInput } from '@/modules/auth/lib/schemas';

import { useResetTokenFromQuery } from './use-reset-token';

export function ResetPasswordForm() {
  const tokenFromQuery = useResetTokenFromQuery();

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting }
  } = useForm<ResetPasswordInput>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { token: '', newPassword: '', confirmNewPassword: '' }
  });

  const resetMutation = useResetPasswordMutation();
  const { serverError, setServerError } = useFormServerError();
  const watchedToken = watch('token');

  if (!watchedToken && tokenFromQuery) {
    setValue('token', tokenFromQuery);
  }

  const onSubmit = handleSubmit(async (values) => {
    try {
      setServerError(null);
      await resetMutation.mutateAsync(values);
    } catch (error) {
      setServerError(error instanceof Error ? error.message : 'Unable to reset password.');
    }
  });

  const hasToken = Boolean(watchedToken || tokenFromQuery);

  return (
    <AuthShell
      title="Set a new password"
      subtitle={hasToken ? undefined : 'Missing or invalid reset link.'}
      footer={
        <p>
          Changed your mind?{' '}
          <Link href="/login" className="font-semibold text-primary underline-offset-4 hover:underline">
            Back to sign in
          </Link>
        </p>
      }
      hero={{ variant: 'auth' }}
    >
      <FormError message={serverError} />

      <form onSubmit={onSubmit} className="space-y-4" noValidate>
        <input type="hidden" {...register('token')} value={watchedToken || tokenFromQuery} readOnly />

        {!hasToken ? (
          <p className="rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-700">
            We could not find a reset token in the link. Please request a new one from the sign-in page.
          </p>
        ) : null}

        <div className="space-y-2">
          <Label htmlFor="newPassword">New password</Label>
          <Input
            id="newPassword"
            type="password"
            autoComplete="new-password"
            aria-invalid={Boolean(errors.newPassword)}
            disabled={!hasToken}
            {...register('newPassword')}
          />
          {errors.newPassword ? (
            <p className="text-sm text-destructive">{errors.newPassword.message}</p>
          ) : (
            <p className="text-xs text-muted-foreground">
              Minimum 8 characters with at least one letter and one number.
            </p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="confirmNewPassword">Confirm new password</Label>
          <Input
            id="confirmNewPassword"
            type="password"
            autoComplete="new-password"
            aria-invalid={Boolean(errors.confirmNewPassword)}
            disabled={!hasToken}
            {...register('confirmNewPassword')}
          />
          {errors.confirmNewPassword ? (
            <p className="text-sm text-destructive">{errors.confirmNewPassword.message}</p>
          ) : null}
        </div>

        <Button
          type="submit"
          variant="brand" className="h-12 w-full rounded-full text-base font-semibold"
          disabled={!hasToken || isSubmitting || resetMutation.isPending}
        >
          {resetMutation.isPending ? 'Updating…' : 'Update password'}
        </Button>
      </form>
    </AuthShell>
  );
}
