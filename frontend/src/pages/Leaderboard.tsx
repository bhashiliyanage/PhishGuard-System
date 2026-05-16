import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../lib/api';
import type { UserLeaderboardResponse } from '../types';
import { Avatar } from '../components/ui/Avatar';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

type Period = 'weekly' | 'monthly' | 'all-time';

const PERIOD_LABELS: Record<Period, string> = {
  weekly: 'Weekly',
  monthly: 'Monthly',
  'all-time': 'All Time',
};

export function Leaderboard() {
  const [period, setPeriod] = useState<Period>('monthly');

  const { data: entries = [], isLoading } = useQuery<UserLeaderboardResponse[]>({
    queryKey: ['leaderboard', period],
    queryFn: () => api.get(`/v1/leaderboard/${period}`).then((r) => r.data),
  });

  const top3 = entries.slice(0, 3);

  const getScore = (e: UserLeaderboardResponse) => {
    if (period === 'weekly') return e.weeklyScore;
    if (period === 'monthly') return e.monthlyScore;
    return e.totalScore;
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Leaderboard</h1>
      </div>

      {/* Period tabs */}
      <div className="flex gap-1 bg-white rounded-lg border border-slate-200 p-1 w-fit mb-8">
        {(Object.keys(PERIOD_LABELS) as Period[]).map((p) => (
          <button
            key={p}
            onClick={() => setPeriod(p)}
            className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${
              period === p
                ? 'bg-[#1a2744] text-white'
                : 'text-slate-500 hover:text-slate-700'
            }`}
          >
            {PERIOD_LABELS[p]}
          </button>
        ))}
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : (
        <>
          {/* Podium top 3 */}
          {top3.length >= 3 && (
            <div className="flex items-end justify-center gap-4 mb-8">
              {/* 2nd */}
              <PodiumCard entry={top3[1]} score={getScore(top3[1])} place={2} />
              {/* 1st */}
              <PodiumCard entry={top3[0]} score={getScore(top3[0])} place={1} />
              {/* 3rd */}
              <PodiumCard entry={top3[2]} score={getScore(top3[2])} place={3} />
            </div>
          )}

          {/* Full rankings table */}
          <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
              <h2 className="text-sm font-semibold text-slate-700">
                Full Rankings — {new Date().toLocaleString('default', { month: 'long', year: 'numeric' })}
              </h2>
              <span className="text-xs text-slate-400">{entries.length} employees</span>
            </div>
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100">
                  {['Rank', 'Employee', 'Points'].map((h) => (
                    <th key={h} className="px-6 py-3 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {entries.map((entry) => (
                  <tr key={entry.rank} className="border-b border-slate-50 hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-3 text-sm font-semibold text-slate-600 w-16">{entry.rank}</td>
                    <td className="px-6 py-3">
                      <div className="flex items-center gap-3">
                        <Avatar name={entry.fullName} size="sm" />
                        <span className="text-sm font-medium text-slate-700">{entry.fullName}</span>
                      </div>
                    </td>
                    <td className="px-6 py-3 text-sm font-bold text-slate-800">{getScore(entry).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {entries.length === 0 && (
              <div className="text-center py-12 text-slate-400 text-sm">No data available yet.</div>
            )}
          </div>
        </>
      )}
    </div>
  );
}

function PodiumCard({ entry, score, place }: { entry: UserLeaderboardResponse; score: number; place: 1 | 2 | 3 }) {
  const configs = {
    1: {
      border: 'border-amber-400',
      bar: 'bg-amber-500',
      label: '#1',
      height: 'mt-0',
      crown: '👑',
      medal: '🥇',
      textSize: 'text-3xl',
    },
    2: {
      border: 'border-slate-300',
      bar: 'bg-slate-400',
      label: '#2',
      height: 'mt-8',
      crown: '',
      medal: '🥈',
      textSize: 'text-2xl',
    },
    3: {
      border: 'border-orange-300',
      bar: 'bg-orange-400',
      label: '#3',
      height: 'mt-8',
      crown: '',
      medal: '🥉',
      textSize: 'text-2xl',
    },
  };

  const c = configs[place];

  return (
    <div className={`flex flex-col items-center ${c.height}`}>
      {place === 1 && <div className="text-2xl mb-1">👑</div>}
      <div className={`bg-white rounded-2xl border-2 ${c.border} shadow-sm p-5 w-44 text-center`}>
        <p className="text-xs font-medium text-slate-400 mb-3">{c.medal} {place === 1 ? '1st' : place === 2 ? '2nd' : '3rd'} Place</p>
        <div className="flex justify-center mb-2">
          <Avatar name={entry.fullName} size="lg" />
        </div>
        <p className="text-sm font-semibold text-slate-800 mb-1">{entry.fullName}</p>
        <p className={`${c.textSize} font-bold text-slate-900`}>{score.toLocaleString()}</p>
        <p className="text-xs text-slate-400">points</p>
      </div>
      <div className={`w-full ${c.bar} rounded-b-lg py-1 text-white text-xs font-bold text-center mt-0`}>
        {c.label}
      </div>
    </div>
  );
}
