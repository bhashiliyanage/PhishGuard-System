import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { useQuery } from '@tanstack/react-query';
import api from '../../lib/api';
import type { ResponseEmailDto, AppUser } from '../../types';

interface LayoutProps {
  user: AppUser | null;
  isAdmin: boolean;
  onLogout: () => void;
}

export function Layout({ user, isAdmin, onLogout }: LayoutProps) {
  const { data: emails = [] } = useQuery<ResponseEmailDto[]>({
    queryKey: ['emails'],
    queryFn: () => api.get('/v1/email').then((r) => r.data),
    enabled: !!user,
  });

  const pendingCount = emails.filter((e) => !e.submitted && e.userId === user?.id).length;

  return (
    <div className="flex min-h-screen">
      <Sidebar user={user} isAdmin={isAdmin} pendingCount={pendingCount} onLogout={onLogout} />
      <main className="flex-1 bg-slate-100 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
