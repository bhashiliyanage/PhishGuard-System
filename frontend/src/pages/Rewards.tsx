import { useQuery } from '@tanstack/react-query';
import api from '../lib/api';
import type { AppUser, Reward } from '../types';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

interface RewardsProps {
  user: AppUser | null;
}

export function Rewards({ user }: RewardsProps) {
  const { data: rewards = [], isLoading } = useQuery<Reward[]>({
    queryKey: ['my-rewards', user?.id],
    queryFn: () => api.get(`/v1/rewards/user/${user!.id}`).then((r) => r.data),
    enabled: !!user?.id,
  });

  const { data: totalPoints = 0 } = useQuery<number>({
    queryKey: ['total-points', user?.id],
    queryFn: () => api.get(`/v1/rewards/user/${user!.id}/points`).then((r) => r.data),
    enabled: !!user?.id,
  });

  const sorted = [...rewards].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800 mb-6">Rewards</h1>

      {/* Total points banner */}
      <div className="bg-gradient-to-r from-amber-500 to-amber-400 rounded-xl p-6 mb-6 text-white shadow-sm">
        <p className="text-sm font-medium opacity-90 mb-1">Total Points Earned</p>
        <p className="text-4xl font-bold">{totalPoints.toLocaleString()}</p>
        <p className="text-sm opacity-80 mt-1">{rewards.length} reward{rewards.length !== 1 ? 's' : ''} collected</p>
      </div>

      {sorted.length === 0 ? (
        <div className="bg-white rounded-xl border border-slate-200 p-12 text-center">
          <div className="text-4xl mb-3">🎁</div>
          <p className="text-slate-500 text-sm">No rewards yet. Answer training emails correctly to earn points!</p>
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100">
            <h2 className="text-sm font-semibold text-slate-700">Reward History</h2>
          </div>
          <ul className="divide-y divide-slate-50">
            {sorted.map((reward) => (
              <li key={reward.id} className="flex items-center justify-between px-6 py-4">
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 bg-amber-100 rounded-full flex items-center justify-center text-amber-600">
                    ⭐
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-700">Email Training Reward</p>
                    <p className="text-xs text-slate-400">
                      {new Date(reward.createdAt).toLocaleDateString('en-US', {
                        month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit',
                      })}
                    </p>
                  </div>
                </div>
                <span className="text-amber-600 font-bold text-sm">+{reward.points} pts</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
