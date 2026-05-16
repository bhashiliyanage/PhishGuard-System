import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../lib/api';
import type { AppUser, ResponseEmailDto, SubmitAnswerResponse } from '../types';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';

interface EmailReviewProps {
  user: AppUser | null;
}

const REVIEW_SECONDS = 6 * 60; // 6 minutes

export function EmailReview({ user }: EmailReviewProps) {
  const { emailId } = useParams<{ emailId: string }>();
  const navigate = useNavigate();
  const qc = useQueryClient();
  const [result, setResult] = useState<SubmitAnswerResponse | null>(null);
  const [timeLeft, setTimeLeft] = useState(REVIEW_SECONDS);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const { data: emails = [], isLoading } = useQuery<ResponseEmailDto[]>({
    queryKey: ['emails'],
    queryFn: () => api.get('/v1/email').then((r) => r.data),
    enabled: !!user,
  });

  const email = emails.find((e) => e.emailId === emailId);

  useEffect(() => {
    if (!email || email.submitted) return;
    timerRef.current = setInterval(() => setTimeLeft((t) => Math.max(0, t - 1)), 1000);
    return () => { if (timerRef.current) clearInterval(timerRef.current); };
  }, [email?.emailId, email?.submitted]);

  const submitMutation = useMutation({
    mutationFn: (userChoice: 'PHISHING' | 'NORMAL') =>
      api.put<SubmitAnswerResponse>('/v1/email/answer', {
        emailId,
        userId: user?.id,
        userChoice,
      }).then((r) => r.data),
    onSuccess: (data) => {
      setResult(data);
      if (timerRef.current) clearInterval(timerRef.current);
      qc.invalidateQueries({ queryKey: ['emails'] });
      qc.invalidateQueries({ queryKey: ['my-stats'] });
      qc.invalidateQueries({ queryKey: ['my-rewards', user?.id] });
    },
  });

  if (isLoading) return <LoadingSpinner />;
  if (!email) {
    return (
      <div className="p-6 text-center text-slate-500">
        Email not found. <button className="text-amber-600 underline" onClick={() => navigate('/inbox')}>Back to Inbox</button>
      </div>
    );
  }

  const mins = String(Math.floor(timeLeft / 60)).padStart(2, '0');
  const secs = String(timeLeft % 60).padStart(2, '0');

  const isAlreadySubmitted = email.submitted;
  const wasCorrect = isAlreadySubmitted && email.userChoice === email.emailType;

  return (
    <div className="min-h-screen bg-slate-100">
      {/* Top bar */}
      <div className="bg-white border-b border-slate-200 px-6 py-3 flex items-center justify-between sticky top-0 z-10">
        <button
          onClick={() => navigate('/inbox')}
          className="flex items-center gap-2 text-slate-500 hover:text-slate-700 text-sm transition-colors"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Inbox
        </button>
        <h1 className="text-sm font-semibold text-slate-700">Email Review</h1>
        <div className="flex items-center gap-4">
          {!isAlreadySubmitted && (
            <span className="flex items-center gap-1 text-amber-600 text-sm font-medium">
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <circle cx="12" cy="12" r="10" /><path d="M12 6v6l4 2" />
              </svg>
              {mins}:{secs} remaining
            </span>
          )}
        </div>
      </div>

      {/* Email content */}
      <div className="max-w-3xl mx-auto px-4 py-6">
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden mb-4">
          {/* Email header */}
          <div className="px-6 py-4 border-b border-slate-100 space-y-1 bg-slate-50">
            <MetaRow label="FROM" value={email.senderAddress} />
            <MetaRow label="TO" value={user?.email ?? ''} />
            <MetaRow label="DATE" value={new Date(email.createdAt).toLocaleString()} />
            <p className="font-bold text-slate-800 mt-2 text-sm">{email.emailTitle}</p>
          </div>

          {/* Training banner */}
          {!isAlreadySubmitted && (
            <div className="mx-6 mt-4 flex items-center gap-2 bg-red-50 border border-red-200 rounded-lg px-4 py-2">
              <svg className="w-4 h-4 text-red-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
              </svg>
              <p className="text-red-600 text-xs font-medium">Training Email — Determine if this is phishing or legitimate</p>
            </div>
          )}

          {/* Result banner */}
          {result && (
            <div className={`mx-6 mt-4 flex items-start gap-3 rounded-lg px-4 py-3 ${
              result.isCorrect ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'
            }`}>
              <span className="text-lg">{result.isCorrect ? '✅' : '❌'}</span>
              <div>
                <p className={`text-sm font-semibold ${result.isCorrect ? 'text-green-700' : 'text-red-700'}`}>
                  {result.isCorrect ? 'Correct!' : 'Incorrect'}
                </p>
                <p className="text-xs text-slate-600 mt-0.5">{result.response}</p>
              </div>
            </div>
          )}

          {isAlreadySubmitted && !result && (
            <div className={`mx-6 mt-4 flex items-center gap-2 rounded-lg px-4 py-3 ${
              wasCorrect ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'
            }`}>
              <span>{wasCorrect ? '✅' : '❌'}</span>
              <p className={`text-sm font-medium ${wasCorrect ? 'text-green-700' : 'text-red-700'}`}>
                Already answered — {wasCorrect ? 'Correct' : 'Incorrect'}
              </p>
            </div>
          )}

          {/* Body */}
          <div
            className="px-6 py-5 text-sm text-slate-700 leading-relaxed"
            dangerouslySetInnerHTML={{ __html: email.emailBody }}
          />
        </div>

        {/* Action buttons */}
        {!isAlreadySubmitted && !result && (
          <div className="flex gap-4">
            <button
              onClick={() => submitMutation.mutate('PHISHING')}
              disabled={submitMutation.isPending}
              className="flex-1 flex items-center justify-center gap-2 bg-red-600 hover:bg-red-700 disabled:opacity-60 text-white font-semibold py-4 rounded-xl text-sm transition-colors"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
              </svg>
              Mark as SPAM / Phishing
            </button>
            <button
              onClick={() => submitMutation.mutate('NORMAL')}
              disabled={submitMutation.isPending}
              className="flex-1 flex items-center justify-center gap-2 bg-green-600 hover:bg-green-700 disabled:opacity-60 text-white font-semibold py-4 rounded-xl text-sm transition-colors"
            >
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path d="M5 13l4 4L19 7" />
              </svg>
              Mark as Legitimate
            </button>
          </div>
        )}

        {(isAlreadySubmitted || result) && (
          <div className="text-center mt-2">
            <button
              onClick={() => navigate('/inbox')}
              className="bg-[#1a2744] text-white font-medium px-6 py-2 rounded-lg text-sm hover:opacity-90 transition-opacity"
            >
              Back to Inbox
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

function MetaRow({ label, value }: { label: string; value: string }) {
  return (
    <p className="text-xs text-slate-500">
      <span className="font-semibold text-slate-600 uppercase tracking-wide mr-2">{label}</span>
      {value}
    </p>
  );
}
