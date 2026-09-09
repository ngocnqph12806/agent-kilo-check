'use client';

import Link from 'next/link';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, useWatch } from 'react-hook-form';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  PasswordStrength,
  scorePassword
} from '@/components/shared/password-strength';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormError, useFormServerError } from '@/modules/auth/components/form-status';
import { GoogleSignInButton } from '@/modules/auth/components/google-sign-in-button';
import { AppleSignInButton } from '@/modules/auth/components/apple-sign-in-button';
import { useRegisterMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { registerSchema, type RegisterInput } from '@/modules/auth/lib/schemas';

export function RegisterForm() {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isSubmitting }
  } = useForm<RegisterInput>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      fullName: '',
      email: '',
      password: '',
      confirmPassword: '',
      acceptTerms: false as unknown as true
    }
  });

  const passwordValue = useWatch({ control, name: 'password' }) ?? '';
  const passwordScore = scorePassword(passwordValue);

  const registerMutation = useRegisterMutation();
  const { serverError, setServerError } = useFormServerError();

  const onSubmit = handleSubmit(async (values) => {
    try {
      setServerError(null);
      await registerMutation.mutateAsync({
        email: values.email,
        password: values.password,
        fullName: values.fullName,
        confirmPassword: values.confirmPassword
      });
    } catch (error) {
      setServerError(error instanceof Error ? error.message : 'Unable to create account.');
    }
  });

  return (
    <AuthShell
      title="Create your account"
      subtitle="Trade skills, not money. Free starter seeds included."
      footer={
        <p>
          Already have an account?{' '}
          <Link href="/login" className="font-semibold text-primary underline-offset-4 hover:underline">
            Sign in
          </Link>
        </p>
      }
      hero={{ variant: 'auth' }}
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
          <PasswordStrength value={passwordScore} />
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

        <label className="flex items-start gap-2 text-xs text-brand-muted">
          <input
            type="checkbox"
            required
            className="mt-0.5 h-4 w-4 rounded border-brand-default accent-primary"
            {...register('acceptTerms')}
          />
          <span>
            I agree to the{' '}
            <Link href="/terms" className="font-semibold text-primary underline-offset-4 hover:underline">
              Terms of Service
            </Link>{' '}
            and{' '}
            <Link href="/privacy" className="font-semibold text-primary underline-offset-4 hover:underline">
              Privacy Policy
            </Link>
            .
          </span>
        </label>

        <Button
          type="submit"
          variant="brand" className="h-12 w-full rounded-full text-base font-semibold"
          disabled={isSubmitting || registerMutation.isPending}
        >
          {registerMutation.isPending || isSubmitting ? 'Creating account…' : 'Create account'}
        </Button>
      </form>

      <div className="relative">
        <div className="absolute inset-0 flex items-center" aria-hidden="true">
          <div className="w-full border-t border-brand-default" />
        </div>
        <div className="relative flex justify-center text-xs uppercase">
          <span className="bg-background px-2 text-brand-subtle">Or sign up with</span>
        </div>
      </div>

      <GoogleSignInButton mode="signup" />
      <AppleSignInButton mode="signup" />
    </AuthShell>
  );
}
