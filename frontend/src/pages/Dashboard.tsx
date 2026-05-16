import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import api from '../lib/api';
import type { AppUser, MyLeaderboardStatsResponse, ResponseEmailDto, Reward } from '../types';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

interface DashboardProps {
  user: AppUser | null;
}

export function Dashboard({ user }: DashboardProps) {
  const { data: stats, isLoading: statsLoading } = useQuery<MyLeaderboardStatsResponse>({
    queryKey: ['my-stats'],
    queryFn: () => api.get('/v1/leaderboard/my-status').then((r) => r.data),
    enabled: !!user,
  });

  const { data: emails = [] } = useQuery<ResponseEmailDto[]>({
    queryKey: ['emails'],
    queryFn: () => api.get('/v1/email').then((r) => r.data),
    enabled: !!user,
  });

  const { data: rewards = [] } = useQuery<Reward[]>({
    queryKey: ['my-rewards', user?.id],
    queryFn: () => api.get(`/v1/rewards/user/${user!.id}`).then((r) => r.data),
    enabled: !!user?.id,
  });

  const myEmails = emails.filter((e) => e.userId === user?.id);
  const pending = myEmails.filter((e) => !e.submitted);
  const totalPoints = rewards.reduce((sum, r) => sum + r.points, 0);

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">
          Welcome back, {user?.firstName} 👋
        </h1>
        <p className="text-slate-500 mt-1">Here's your phishing awareness training overview.</p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-2 gap-4 md:grid-cols-4 mb-6">
        <StatCard label="Weekly Points" value={user?.weeklyScore ?? 0} accent="text-amber-500" />
        <StatCard label="Monthly Points" value={user?.monthlyScore ?? 0} accent="text-blue-600" />
        <StatCard label="Total Points" value={totalPoints} accent="text-green-600" />
        <StatCard label="Pending Emails" value={pending.length} accent="text-red-500" />
      </div>

      {/* Rank cards */}
      {statsLoading ? (
        <LoadingSpinner />
      ) : stats ? (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5 mb-6">
          <h2 className="text-base font-semibold text-slate-700 mb-4">Your Rankings</h2>
          <div className="grid grid-cols-3 gap-4 text-center">
            <RankCard period="This Week" rank={stats.weeklyRank} correct={stats.weeklyCorrect} wrong={stats.weeklyWrong} />
            <RankCard period="This Month" rank={stats.monthlyRank} correct={stats.monthlyCorrect} wrong={stats.monthlyWrong} />
            <RankCard period="All Time" rank={stats.allTimeRank} correct={stats.allTimeCorrect} wrong={stats.allTimeWrong} />
          </div>
        </div>
      ) : null}

      {/* Quick actions */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h2 className="text-base font-semibold text-slate-700 mb-2">Training Inbox</h2>
          <p className="text-slate-500 text-sm mb-4">
            {pending.length > 0
              ? `You have ${pending.length} email${pending.length > 1 ? 's' : ''} waiting for review.`
              : 'No pending emails. Request a new one to practice!'}
          </p>
          <Link
            to="/inbox"
            className="inline-block bg-[#1a2744] text-white text-sm font-medium px-4 py-2 rounded-lg hover:opacity-90 transition-opacity"
          >
            Go to Inbox
          </Link>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h2 className="text-base font-semibold text-slate-700 mb-2">Leaderboard</h2>
          <p className="text-slate-500 text-sm mb-4">
            {stats ? `You're ranked #${stats.weeklyRank} this week.` : 'See how you rank against colleagues.'}
          </p>
          <Link
            to="/leaderboard"
            className="inline-block bg-amber-500 text-white text-sm font-medium px-4 py-2 rounded-lg hover:opacity-90 transition-opacity"
          >
            View Leaderboard
          </Link>
        </div>
      </div>
    </div>
  );
}

function StatCard({ label, value, accent }: { label: string; value: number; accent: string }) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-4">
      <p className="text-slate-500 text-xs mb-1">{label}</p>
      <p className={`text-2xl font-bold ${accent}`}>{value.toLocaleString()}</p>
    </div>
  );
}

function RankCard({ period, rank, correct, wrong }: { period: string; rank: number; correct: number; wrong: number }) {
  const total = correct + wrong;
  const accuracy = total > 0 ? Math.round((correct / total) * 100) : 0;
  return (
    <div className="p-3 rounded-lg bg-slate-50 border border-slate-100">
      <p className="text-slate-500 text-xs mb-1">{period}</p>
      <p className="text-xl font-bold text-slate-800">#{rank}</p>
      <p className="text-xs text-green-600 mt-1">{accuracy}% accuracy</p>
    </div>
  );
}
