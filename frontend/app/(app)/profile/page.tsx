import { redirect } from 'next/navigation';

/**
 * Alias for /users/me. Kept so legacy links, browser bookmarks and any UI
 * that still points at `/profile` land on the current user's public profile
 * instead of a 404. Resolution happens client-side once /users/me hydrates,
 * so this stays a plain server redirect.
 */
export default function ProfileAliasPage(): never {
  redirect('/users/me');
}
