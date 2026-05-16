import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './lib/queryClient';
import { useAuth } from './hooks/useAuth';
import { Layout } from './components/layout/Layout';
import { Dashboard } from './pages/Dashboard';
import { TrainingInbox } from './pages/TrainingInbox';
import { EmailReview } from './pages/EmailReview';
import { Leaderboard } from './pages/Leaderboard';
import { MyStats } from './pages/MyStats';
import { Rewards } from './pages/Rewards';
import { AdminPanel } from './pages/admin/AdminPanel';
import { LoadingSpinner } from './components/ui/LoadingSpinner';

function AppRoutes() {
  const { initialized, authenticated, user, isAdmin, logout } = useAuth();

  if (!initialized) return <LoadingSpinner fullScreen />;
  if (!authenticated) return <LoadingSpinner fullScreen />;

  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout user={user} isAdmin={isAdmin} onLogout={logout} />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard user={user} />} />
          <Route path="/inbox" element={<TrainingInbox user={user} />} />
          <Route path="/inbox/:emailId" element={<EmailReview user={user} />} />
          <Route path="/leaderboard" element={<Leaderboard />} />
          <Route path="/stats" element={<MyStats user={user} />} />
          <Route path="/rewards" element={<Rewards user={user} />} />
          {isAdmin && <Route path="/admin" element={<AdminPanel />} />}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppRoutes />
    </QueryClientProvider>
  );
}
