'use client';

import * as React from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';

import {
  deleteMyAccount,
  downloadExportAsFile,
  exportMyData,
  type DataExportResponse
} from '../lib/gdpr-api';

export function useDataExport() {
  return useQuery<DataExportResponse>({
    queryKey: ['gdpr', 'export'],
    queryFn: exportMyData,
    enabled: false,
    staleTime: 0
  });
}

export function useDownloadDataExport() {
  return useMutation<DataExportResponse, Error>({
    mutationFn: exportMyData,
    onSuccess: (payload) => downloadExportAsFile(payload)
  });
}

export function useDeleteMyAccount() {
  const router = useRouter();
  return useMutation<void, Error>({
    mutationFn: deleteMyAccount,
    onSuccess: () => {
      router.replace('/login?deleted=1');
    }
  });
}