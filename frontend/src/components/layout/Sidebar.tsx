import { NavLink } from 'react-router-dom';
import { Avatar } from '../ui/Avatar';

interface SidebarProps {
  user: { firstName: string; lastName: string } | null;
  isAdmin: boolean;
  pendingCount: number;
  onLogout: () => void;
}

interface NavItem {
  to: string;
  label: string;
  icon: React.ReactNode;
  badge?: number;
}

const LayoutIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <rect x="3" y="3" width="7" height="7" rx="1" /><rect x="14" y="3" width="7" height="7" rx="1" />
    <rect x="3" y="14" width="7" height="7" rx="1" /><rect x="14" y="14" width="7" height="7" rx="1" />
  </svg>
);
const MailIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
  </svg>
);
const TrophyIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path d="M8 21h8m-4-4v4M5 3H3v5a5 5 0 005 5h.5M19 3h2v5a5 5 0 01-5 5h-.5M12 13V3M7 3h10" />
  </svg>
);
const ChartIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
  </svg>
);
const GiftIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path d="M20 12v10H4V12M2 7h20v5H2zM12 22V7m0 0a3 3 0 01-3-3 3 3 0 016 0 3 3 0 01-3 3z" />
  </svg>
);
const ShieldIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
  </svg>
);

export function Sidebar({ user, isAdmin, pendingCount, onLogout }: SidebarProps) {
  const fullName = user ? `${user.firstName} ${user.lastName}`.trim() : 'User';

  const navItems: NavItem[] = [
    { to: '/dashboard', label: 'Dashboard', icon: <LayoutIcon /> },
    { to: '/inbox', label: 'Training Inbox', icon: <MailIcon />, badge: pendingCount || undefined },
    { to: '/leaderboard', label: 'Leaderboard', icon: <TrophyIcon /> },
    { to: '/stats', label: 'My Stats', icon: <ChartIcon /> },
    { to: '/rewards', label: 'Rewards', icon: <GiftIcon /> },
  ];

  if (isAdmin) {
    navItems.push({ to: '/admin', label: 'Admin Panel', icon: <ShieldIcon /> });
  }

  return (
    <aside className="w-52 min-h-screen flex flex-col" style={{ backgroundColor: '#1a2744' }}>
      {/* Logo */}
      <div className="px-4 py-5 flex items-center gap-2 border-b border-white/10">
        <div className="w-7 h-7 bg-amber-500 rounded-md flex items-center justify-center flex-shrink-0">
          <svg className="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          </svg>
        </div>
        <span className="text-white font-bold text-base tracking-tight">PhishGuard</span>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors ${
                isActive
                  ? 'bg-white/15 text-white font-medium'
                  : 'text-white/65 hover:text-white hover:bg-white/10'
              }`
            }
          >
            {item.icon}
            <span className="flex-1">{item.label}</span>
            {item.badge != null && item.badge > 0 && (
              <span className="bg-amber-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                {item.badge > 9 ? '9+' : item.badge}
              </span>
            )}
          </NavLink>
        ))}
      </nav>

      {/* User */}
      <div className="px-3 pb-3 space-y-1 border-t border-white/10 pt-3">
        <div className="flex items-center gap-3 px-3 py-2 rounded-lg bg-white/10">
          <Avatar name={fullName} size="sm" />
          <div className="min-w-0">
            <p className="text-white text-xs font-medium truncate">{fullName}</p>
            <p className="text-white/50 text-xs">{isAdmin ? 'Admin' : 'Employee'}</p>
          </div>
        </div>
        <button
          onClick={onLogout}
          className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-white/65 hover:text-white hover:bg-white/10 text-sm transition-colors"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Logout
        </button>
      </div>
    </aside>
  );
}
