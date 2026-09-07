'use client';

import Link from 'next/link';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormError, useFormServerError } from '@/modules/auth/components/form-status';
import { GoogleSignInButton } from '@/modules/auth/components/google-sign-in-button';
import { useRegisterMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { registerSchema, type RegisterInput } from '@/modules/auth/lib/schemas';

export default function RegisterClient() {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<RegisterInput>({
    resolver: zodResolver(registerSchema),
    defaultValues: { fullName: '', email: '', password: '', confirmPassword: '' }
  });

  const registerMutation = useRegisterMutation();
  const { serverError, setServerError } = useFormServerError();

  const onSubmit = handleSubmit(async (values) => {
    try {
      setServerError(null);
      await registerMutation.mutateAsync(values);
    } catch (error) {
      setServerError(error instanceof Error ? error.message : 'Unable to create account.');
    }
  });

  return (
    <AuthShell
      title="Create your account"
      description="Trade skills, not money. Free starter seeds included."
      footer={
        <p>
          Already have an account?{' '}
          <Link href="/login" className="font-medium text-primary underline-offset-4 hover:underline">
            Sign in
          </Link>
        </p>
      }
    >
      <FormError message={serverError} />

      <form onSubmit={onSubmit} className="space-y-4" noValidate>
        <div className="space-y-2">
          <Label htmlFor="fullName">Full name</Label>
          <Input
            id="fullName"
            autoComplete="name"
            placeholder="Jane Doe"
            aria-invalid={Boolean(errors.fullName)}
            {...register('fullName')}
          />
          {errors.fullName ? <p className="text-sm text-destructive">{errors.fullName.message}</p> : null}
        </div>

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

        <div className="space-y-2">
          <Label htmlFor="password">Password</Label>
          <Input
            id="password"
            type="password"
            autoComplete="new-password"
            aria-invalid={Boolean(errors.password)}
            {...register('password')}
          />
          {errors.password ? (
            <p className="text-sm text-destructive">{errors.password.message}</p>
          ) : (
            <p className="text-xs text-muted-foreground">
              Minimum 8 characters with at least one letter and one number.
            </p>
          )}
        </div>

        <div className="space-y-2">
          <Label htmlFor="confirmPassword">Confirm password</Label>
          <Input
            id="confirmPassword"
            type="password"
            autoComplete="new-password"
            aria-invalid={Boolean(errors.confirmPassword)}
            {...register('confirmPassword')}
          />
          {errors.confirmPassword ? (
            <p className="text-sm text-destructive">{errors.confirmPassword.message}</p>
          ) : null}
        </div>

        <Button type="submit" className="w-full" disabled={isSubmitting || registerMutation.isPending}>
          {registerMutation.isPending || isSubmitting ? 'Creating account…' : 'Create account'}
        </Button>
      </form>

      <div className="relative">
        <div className="absolute inset-0 flex items-center" aria-hidden="true">
          <div className="w-full border-t" />
        </div>
        <div className="relative flex justify-center text-xs uppercase">
          <span className="bg-card px-2 text-muted-foreground">Or sign up with</span>
        </div>
      </div>

      <GoogleSignInButton mode="signup" />
    </AuthShell>
  );
}
