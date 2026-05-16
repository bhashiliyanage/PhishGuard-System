import { useQuery } from '@tanstack/react-query';
import api from '../lib/api';
import type { AppUser, MyLeaderboardStatsResponse } from '../types';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

interface MyStatsProps {
  user: AppUser | null;
}

export function MyStats({ user }: MyStatsProps) {
  const { data: stats, isLoading } = useQuery<MyLeaderboardStatsResponse>({
    queryKey: ['my-stats'],
    queryFn: () => api.get('/v1/leaderboard/my-status').then((r) => r.data),
    enabled: !!user,
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800 mb-6">My Stats</h1>

      {!stats ? (
        <div className="bg-white rounded-xl border border-slate-200 p-12 text-center text-slate-400 text-sm">
          No stats available yet. Complete some training emails to see your progress!
        </div>
      ) : (
        <div className="space-y-6">
          <StatPeriod
            title="This Week"
            rank={stats.weeklyRank}
            points={stats.weeklyPoints}
            correct={stats.weeklyCorrect}
            wrong={stats.weeklyWrong}
            color="amber"
          />
          <StatPeriod
            title="This Month"
            rank={stats.monthlyRank}
            points={stats.monthlyPoints}
            correct={stats.monthlyCorrect}
            wrong={stats.monthlyWrong}
            color="blue"
          />
          <StatPeriod
            title="All Time"
            rank={stats.allTimeRank}
            points={stats.allTimePoints}
            correct={stats.allTimeCorrect}
            wrong={stats.allTimeWrong}
            color="green"
          />
        </div>
      )}
    </div>
  );
}

function StatPeriod({
  title, rank, points, correct, wrong, color,
}: {
  title: string; rank: number; points: number; correct: number; wrong: number; color: 'amber' | 'blue' | 'green';
}) {
  const total = correct + wrong;
  const accuracy = total > 0 ? Math.round((correct / total) * 100) : 0;
  const pct = accuracy;

  const barColor = { amber: 'bg-amber-500', blue: 'bg-blue-500', green: 'bg-green-500' }[color];
  const textColor = { amber: 'text-amber-600', blue: 'text-blue-600', green: 'text-green-600' }[color];

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-base font-semibold text-slate-700">{title}</h2>
        <span className={`text-sm font-bold ${textColor}`}>Rank #{rank}</span>
      </div>
      <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
        <Metric label="Points" value={points.toLocaleString()} />
        <Metric label="Correct" value={correct.toString()} sub="emails" />
        <Metric label="Incorrect" value={wrong.toString()} sub="emails" />
        <Metric label="Accuracy" value={`${accuracy}%`} />
      </div>
      {/* Accuracy bar */}
      <div className="mt-4">
        <div className="flex items-center justify-between text-xs text-slate-400 mb-1">
          <span>Accuracy</span>
          <span>{pct}%</span>
        </div>
        <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
          <div className={`h-full ${barColor} rounded-full transition-all`} style={{ width: `${pct}%` }} />
        </div>
      </div>
    </div>
  );
}

function Metric({ label, value, sub }: { label: string; value: string; sub?: string }) {
  return (
    <div className="text-center">
      <p className="text-2xl font-bold text-slate-800">{value}</p>
      <p className="text-xs text-slate-400 mt-0.5">{label}{sub ? ` ${sub}` : ''}</p>
    </div>
  );
}
