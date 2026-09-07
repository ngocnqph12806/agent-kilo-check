'use client';

import { useEffect, useState, type ReactNode } from 'react';

import { Alert, AlertDescription } from '@/components/ui/alert';
import { FieldError } from 'react-hook-form';

export interface FormErrorProps {
  message?: string | ReactNode;
}

export function FormError({ message }: FormErrorProps) {
  if (!message) return null;
  return (
    <Alert variant="destructive">
      <AlertDescription>{message}</AlertDescription>
    </Alert>
  );
}

export interface FormAlertProps {
  message?: string | null;
}

export function FormAlert({ message }: FormAlertProps) {
  if (!message) return null;
  return (
    <Alert>
      <AlertDescription>{message}</AlertDescription>
    </Alert>
  );
}

export function useFormServerError(initial?: string | null) {
  const [serverError, setServerError] = useState<string | null>(initial ?? null);
  useEffect(() => {
    setServerError(initial ?? null);
  }, [initial]);
  return { serverError, setServerError };
}

export type { FieldError };
