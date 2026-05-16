import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import api from '../lib/api';
import type { AppUser, ResponseEmailDto } from '../types';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

interface TrainingInboxProps {
  user: AppUser | null;
}

export function TrainingInbox({ user }: TrainingInboxProps) {
  const navigate = useNavigate();
  const qc = useQueryClient();
  const [requesting, setRequesting] = useState(false);

  const { data: emails = [], isLoading } = useQuery<ResponseEmailDto[]>({
    queryKey: ['emails'],
    queryFn: () => api.get('/v1/email').then((r) => r.data),
    enabled: !!user,
  });

  const myEmails = emails
    .filter((e) => e.userId === user?.id)
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

  const requestMutation = useMutation({
    mutationFn: () => api.post('/v1/email/request/user').then((r) => r.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['emails'] });
      setRequesting(false);
    },
    onError: () => setRequesting(false),
  });

  const handleRequest = () => {
    setRequesting(true);
    requestMutation.mutate();
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Training Inbox</h1>
          <p className="text-slate-500 text-sm mt-1">Review emails and test your phishing detection skills.</p>
        </div>
        <button
          onClick={handleRequest}
          disabled={requesting || requestMutation.isPending}
          className="bg-amber-500 hover:bg-amber-600 disabled:opacity-60 text-white font-medium px-4 py-2 rounded-lg text-sm transition-colors flex items-center gap-2"
        >
          {requestMutation.isPending ? (
            <>
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              Generating…
            </>
          ) : (
            <>
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path d="M12 4v16m8-8H4" />
              </svg>
              Request New Email
            </>
          )}
        </button>
      </div>

      {myEmails.length === 0 ? (
        <div className="bg-white rounded-xl border border-slate-200 p-12 text-center">
          <div className="w-12 h-12 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <svg className="w-6 h-6 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          <p className="text-slate-500 text-sm">No emails yet. Click "Request New Email" to start training.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {myEmails.map((email) => (
            <EmailCard key={email.emailId} email={email} onClick={() => navigate(`/inbox/${email.emailId}`)} />
          ))}
        </div>
      )}
    </div>
  );
}

function EmailCard({ email, onClick }: { email: ResponseEmailDto; onClick: () => void }) {
  const date = new Date(email.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

  return (
    <button
      onClick={onClick}
      className="w-full bg-white rounded-xl border border-slate-200 p-4 text-left hover:border-amber-400 hover:shadow-sm transition-all"
    >
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            {!email.submitted && (
              <span className="w-2 h-2 bg-amber-500 rounded-full flex-shrink-0" />
            )}
            <p className="text-sm font-semibold text-slate-800 truncate">{email.emailTitle}</p>
          </div>
          <p className="text-xs text-slate-500 mb-1">From: {email.senderAddress}</p>
          <p className="text-xs text-slate-400 line-clamp-2">{email.emailBody.replace(/<[^>]+>/g, '')}</p>
        </div>
        <div className="flex flex-col items-end gap-2 flex-shrink-0">
          <span className="text-xs text-slate-400">{date}</span>
          {email.submitted ? (
            <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${
              email.userChoice === email.emailType
                ? 'bg-green-100 text-green-700'
                : 'bg-red-100 text-red-700'
            }`}>
              {email.userChoice === email.emailType ? 'Correct' : 'Incorrect'}
            </span>
          ) : (
            <span className="text-xs font-medium px-2 py-0.5 rounded-full bg-amber-100 text-amber-700">
              Pending
            </span>
          )}
          <span className="text-xs px-2 py-0.5 rounded-full bg-slate-100 text-slate-500">
            {email.generateBy === 'AI' ? 'AI Generated' : 'Admin'}
          </span>
        </div>
      </div>
    </button>
  );
}
