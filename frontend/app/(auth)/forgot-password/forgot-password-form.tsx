'use client';

import Link from 'next/link';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormAlert, FormError, useFormServerError } from '@/modules/auth/components/form-status';
import { useForgotPasswordMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { forgotPasswordSchema, type ForgotPasswordInput } from '@/modules/auth/lib/schemas';

export function ForgotPasswordForm() {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<ForgotPasswordInput>({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: { email: '' }
  });

  const forgotMutation = useForgotPasswordMutation();
  const { serverError, setServerError } = useFormServerError();

  const onSubmit = handleSubmit(async (values) => {
    try {
      setServerError(null);
      await forgotMutation.mutateAsync(values);
      forgotMutation.reset();
    } catch (error) {
      setServerError(error instanceof Error ? error.message : 'Unable to request password reset.');
    }
  });

  const submitted = forgotMutation.isSuccess;

  return (
    <AuthShell
      title="Reset your password"
      subtitle="Enter your email and we will send you a reset link."
      footer={
        <p>
          Remembered it?{' '}
          <Link href="/login" className="font-semibold text-primary underline-offset-4 hover:underline">
            Back to sign in
          </Link>
        </p>
      }
      hero={{ variant: 'auth' }}
    >
      {submitted ? (
        <FormAlert message="If an account exists for that email, we have sent a reset link. Check your inbox." />
      ) : null}

      <FormError message={serverError} />

      <form onSubmit={onSubmit} className="space-y-4" noValidate>
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="you@example.com"
            aria-invalid={Boolean(errors.email)}
            {...register('email')}
          />
          {errors.email ? <p className="text-sm text-destructive">{errors.email.message}</p> : null}
        </div>

        <Button
          type="submit"
          className="h-12 w-full rounded-full bg-brand-cta text-base font-semibold text-white shadow-brand-cta hover:opacity-95"
          disabled={isSubmitting || forgotMutation.isPending || submitted}
        >
          {forgotMutation.isPending ? 'Sending…' : submitted ? 'Email sent' : 'Send reset link'}
        </Button>
      </form>
    </AuthShell>
  );
}
