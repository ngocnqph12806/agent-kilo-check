'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import { AuthShell } from '@/modules/auth/components/auth-shell';
import { FormError, useFormServerError } from '@/modules/auth/components/form-status';
import { GoogleSignInButton } from '@/modules/auth/components/google-sign-in-button';
import { useLoginMutation } from '@/modules/auth/hooks/use-auth-mutations';
import { loginSchema, type LoginInput } from '@/modules/auth/lib/schemas';

import { useLoginSearchState } from './use-login-search';

export function LoginForm() {
  const router = useRouter();
  const { registered, reset, verified } = useLoginSearchState();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '' }
  });

  const loginMutation = useLoginMutation();
  const { serverError, setServerError } = useFormServerError();

  const onSubmit = handleSubmit(async (values) => {
    try {
      setServerError(null);
      await loginMutation.mutateAsync(values);
    } catch (error) {
      setServerError(error instanceof Error ? error.message : 'Unable to sign in.');
    }
  });

  return (
    <AuthShell
      title="Welcome back"
      description="Sign in to keep trading skills."
      footer={
        <p>
          New here?{' '}
          <Link href="/register" className="font-medium text-primary underline-offset-4 hover:underline">
            Create an account
          </Link>
        </p>
      }
    >
      {registered ? (
        <p className="rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">
          Account created. Please verify your email before signing in.
        </p>
      ) : null}
      {reset ? (
        <p className="rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">
          Password updated. You can now sign in with the new password.
        </p>
      ) : null}
      {verified ? (
        <p className="rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">
          Email verified. Please sign in.
        </p>
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

        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label htmlFor="password">Password</Label>
            <Link
              href="/forgot-password"
              className="text-xs font-medium text-primary underline-offset-4 hover:underline"
            >
              Forgot password?
            </Link>
          </div>
          <Input
            id="password"
            type="password"
            autoComplete="current-password"
            aria-invalid={Boolean(errors.password)}
            {...register('password')}
          />
          {errors.password ? <p className="text-sm text-destructive">{errors.password.message}</p> : null}
        </div>

        <Button type="submit" className="w-full" disabled={isSubmitting || loginMutation.isPending}>
          {loginMutation.isPending || isSubmitting ? 'Signing in…' : 'Sign in'}
        </Button>
      </form>

      <div className="relative">
        <div className="absolute inset-0 flex items-center" aria-hidden="true">
          <div className="w-full border-t" />
        </div>
        <div className="relative flex justify-center text-xs uppercase">
          <span className="bg-card px-2 text-muted-foreground">Or continue with</span>
        </div>
      </div>

      <GoogleSignInButton mode="signin" />

      <Button variant="outline" type="button" className="w-full" onClick={() => router.push('/register')}>
        Create a new account
      </Button>
    </AuthShell>
  );
}
