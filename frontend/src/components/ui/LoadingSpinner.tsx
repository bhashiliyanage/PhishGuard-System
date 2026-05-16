export function LoadingSpinner({ fullScreen = false }: { fullScreen?: boolean }) {
  const spinner = (
    <div className="flex items-center justify-center gap-3">
      <div className="w-8 h-8 border-3 border-amber-500 border-t-transparent rounded-full animate-spin" />
      <span className="text-slate-500 text-sm">Loading…</span>
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-[#f1f5f9]">
        {spinner}
      </div>
    );
  }
  return <div className="flex items-center justify-center py-16">{spinner}</div>;
}
