'use client';

import { useMutation } from '@tanstack/react-query';

import { apiClient } from '@/lib/api-client';

export interface AvatarUploadResponse {
  avatarUrl: string;
}

export function useUploadAvatarMutation() {
  return useMutation<AvatarUploadResponse, Error, File>({
    mutationFn: async (file) => {
      const formData = new FormData();
      formData.append('file', file);
      const response = await apiClient.post<AvatarUploadResponse>('/users/me/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      return response.data;
    }
  });
}
