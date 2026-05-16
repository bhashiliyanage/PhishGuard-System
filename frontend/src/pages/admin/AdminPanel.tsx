import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from '../../lib/api';
import type { AdminCreateEmailRequest, AdminCreatedEmailResponse, AdminLeaderboardResponse } from '../../types';
import { Avatar } from '../../components/ui/Avatar';
import { LoadingSpinner } from '../../components/ui/LoadingSpinner';

type Tab = 'create-email' | 'leaderboard';

export function AdminPanel() {
  const [tab, setTab] = useState<Tab>('create-email');

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <h1 className="text-2xl font-bold text-slate-800 mb-6">Admin Panel</h1>

      <div className="flex gap-1 bg-white rounded-lg border border-slate-200 p-1 w-fit mb-6">
        {(['create-email', 'leaderboard'] as Tab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${
              tab === t ? 'bg-[#1a2744] text-white' : 'text-slate-500 hover:text-slate-700'
            }`}
          >
            {t === 'create-email' ? 'Create Email' : 'Leaderboard'}
          </button>
        ))}
      </div>

      {tab === 'create-email' ? <CreateEmailTab /> : <AdminLeaderboardTab />}
    </div>
  );
}

function CreateEmailTab() {
  const qc = useQueryClient();
  const [form, setForm] = useState<Omit<AdminCreateEmailRequest, 'createdAt'>>({
    emailType: 'PHISHING',
    senderAddress: '',
    emailTitle: '',
    emailBody: '',
    link: '',
  });
  const [success, setSuccess] = useState<AdminCreatedEmailResponse | null>(null);

  const mutation = useMutation({
    mutationFn: () =>
      api.post<AdminCreatedEmailResponse>('/v1/email/send/admin', {
        ...form,
        createdAt: new Date().toISOString(),
      }).then((r) => r.data),
    onSuccess: (data) => {
      setSuccess(data);
      qc.invalidateQueries({ queryKey: ['emails'] });
      setForm({ emailType: 'PHISHING', senderAddress: '', emailTitle: '', emailBody: '', link: '' });
    },
  });

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 max-w-2xl">
      <h2 className="text-base font-semibold text-slate-700 mb-4">Broadcast Email to All Users</h2>

      {success && (
        <div className="bg-green-50 border border-green-200 rounded-lg px-4 py-3 mb-4 text-green-700 text-sm">
          ✅ Email sent to all users! ID: <span className="font-mono text-xs">{success.emailId}</span>
          <button className="ml-2 underline text-xs" onClick={() => setSuccess(null)}>Dismiss</button>
        </div>
      )}

      {mutation.isError && (
        <div className="bg-red-50 border border-red-200 rounded-lg px-4 py-3 mb-4 text-red-700 text-sm">
          ❌ Failed to send email. Please try again.
        </div>
      )}

      <div className="space-y-4">
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Email Type</label>
          <div className="flex gap-3">
            {(['PHISHING', 'NORMAL'] as const).map((t) => (
              <label key={t} className="flex items-center gap-2 cursor-pointer">
                <input
                  type="radio"
                  checked={form.emailType === t}
                  onChange={() => setForm((f) => ({ ...f, emailType: t }))}
                  className="accent-amber-500"
                />
                <span className="text-sm text-slate-600">{t === 'PHISHING' ? '🎣 Phishing' : '✅ Legitimate'}</span>
              </label>
            ))}
          </div>
        </div>

        <Field label="Sender Address" type="email" placeholder="security@company.com"
          value={form.senderAddress} onChange={(v) => setForm((f) => ({ ...f, senderAddress: v }))} />
        <Field label="Subject Line" type="text" placeholder="URGENT: Verify your account"
          value={form.emailTitle} onChange={(v) => setForm((f) => ({ ...f, emailTitle: v }))} />
        <div>
          <label className="block text-xs font-medium text-slate-600 mb-1">Email Body</label>
          <textarea
            rows={8}
            placeholder="Enter the email body (HTML supported)…"
            value={form.emailBody}
            onChange={(e) => setForm((f) => ({ ...f, emailBody: e.target.value }))}
            className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent resize-y"
          />
        </div>
        <Field label="Link (optional)" type="url" placeholder="https://malicious-site.com"
          value={form.link} onChange={(v) => setForm((f) => ({ ...f, link: v }))} />

        <button
          onClick={() => mutation.mutate()}
          disabled={mutation.isPending || !form.senderAddress || !form.emailTitle || !form.emailBody}
          className="w-full bg-[#1a2744] hover:opacity-90 disabled:opacity-50 text-white font-semibold py-2.5 rounded-lg text-sm transition-opacity"
        >
          {mutation.isPending ? 'Sending…' : 'Send to All Users'}
        </button>
      </div>
    </div>
  );
}

function Field({ label, type, placeholder, value, onChange }: {
  label: string; type: string; placeholder: string; value: string; onChange: (v: string) => void;
}) {
  return (
    <div>
      <label className="block text-xs font-medium text-slate-600 mb-1">{label}</label>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"
      />
    </div>
  );
}

function AdminLeaderboardTab() {
  const [period, setPeriod] = useState<'weekly' | 'monthly' | 'all-time'>('monthly');
  const qc = useQueryClient();

  const { data: entries = [], isLoading } = useQuery<AdminLeaderboardResponse[]>({
    queryKey: ['admin-leaderboard', period],
    queryFn: () => api.get(`/v1/leaderboard/admin/${period}`).then((r) => r.data),
  });

  const recalcMutation = useMutation({
    mutationFn: () => api.post('/v1/leaderboard/admin/recalculate').then((r) => r.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin-leaderboard'] });
      qc.invalidateQueries({ queryKey: ['leaderboard'] });
    },
  });

  const getScore = (e: AdminLeaderboardResponse) => {
    if (period === 'weekly') return e.weeklyScore;
    if (period === 'monthly') return e.monthlyScore;
    return e.totalScore;
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <div className="flex gap-1 bg-white rounded-lg border border-slate-200 p-1">
          {(['weekly', 'monthly', 'all-time'] as const).map((p) => (
            <button
              key={p}
              onClick={() => setPeriod(p)}
              className={`px-3 py-1 rounded-md text-sm font-medium transition-colors ${
                period === p ? 'bg-[#1a2744] text-white' : 'text-slate-500 hover:text-slate-700'
              }`}
            >
              {p === 'all-time' ? 'All Time' : p.charAt(0).toUpperCase() + p.slice(1)}
            </button>
          ))}
        </div>
        <button
          onClick={() => recalcMutation.mutate()}
          disabled={recalcMutation.isPending}
          className="bg-amber-500 hover:bg-amber-600 disabled:opacity-60 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
        >
          {recalcMutation.isPending ? 'Recalculating…' : 'Recalculate Scores'}
        </button>
      </div>

      {recalcMutation.isSuccess && (
        <div className="bg-green-50 border border-green-200 rounded-lg px-4 py-2 mb-4 text-green-700 text-sm">
          ✅ Scores recalculated successfully.
        </div>
      )}

      {isLoading ? <LoadingSpinner /> : (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <table className="w-full">
            <thead>
              <tr className="border-b border-slate-100">
                {['Rank', 'Employee', 'Username', 'Points'].map((h) => (
                  <th key={h} className="px-5 py-3 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {entries.map((entry) => (
                <tr key={entry.userId} className="border-b border-slate-50 hover:bg-slate-50">
                  <td className="px-5 py-3 text-sm font-semibold text-slate-600">{entry.rank}</td>
                  <td className="px-5 py-3">
                    <div className="flex items-center gap-2">
                      <Avatar name={entry.fullName} size="sm" />
                      <div>
                        <p className="text-sm font-medium text-slate-700">{entry.fullName}</p>
                        <p className="text-xs text-slate-400">{entry.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-5 py-3 text-sm text-slate-500">{entry.username}</td>
                  <td className="px-5 py-3 text-sm font-bold text-slate-800">{getScore(entry).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {entries.length === 0 && (
            <div className="text-center py-10 text-slate-400 text-sm">No data available.</div>
          )}
        </div>
      )}
    </div>
  );
}
