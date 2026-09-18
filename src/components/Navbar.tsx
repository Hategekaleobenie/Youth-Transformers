import React, { useState } from 'react';
import { Menu, Globe, LogOut, KeyRound, User as UserIcon } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { NotificationBell } from './NotificationBell';
import { PasswordChangeModal } from './PasswordChangeModal';

interface Props {
  onToggleSidebar: () => void;
}

export const Navbar: React.FC<Props> = ({ onToggleSidebar }) => {
  const { currentUser, logout, lang, setLang, t } = useAuth();
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  const getRoleBadge = (role?: string) => {
    switch (role) {
      case 'leader': return 'bg-amber-100 text-amber-900 border-amber-300';
      case 'committee_coordinator': return 'bg-emerald-100 text-emerald-900 border-emerald-300';
      case 'accountant': return 'bg-blue-100 text-blue-900 border-blue-300';
      default: return 'bg-slate-100 text-slate-800 border-slate-200';
    }
  };

  const formatRoleLabel = (r?: string) => {
    if (!r) return '';
    return r.split('_').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ');
  };

  return (
    <>
      <header className="sticky top-0 z-30 bg-white/95 backdrop-blur border-b border-slate-200 shadow-sm transition-all">
        <div className="flex items-center justify-between px-4 lg:px-6 h-16">
          {/* Left: Mobile hamburger & title */}
          <div className="flex items-center gap-3">
            <button
              onClick={onToggleSidebar}
              className="lg:hidden p-2 rounded-xl text-slate-600 hover:text-ministry-deepGreen hover:bg-slate-100 transition focus:outline-none"
              aria-label="Toggle navigation menu"
            >
              <Menu className="w-5 h-5" />
            </button>

            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-ministry-deepGreen flex items-center justify-center text-ministry-gold font-black text-sm shadow-sm">
                YT
              </div>
              <div className="hidden sm:block">
                <h1 className="text-sm font-bold text-slate-900 leading-tight">
                  {t('app.title')}
                </h1>
                <p className="text-[11px] text-slate-500 font-medium">
                  {t('app.subtitle')}
                </p>
              </div>
            </div>
          </div>

          {/* Right: Actions, Language, Notifications, User */}
          <div className="flex items-center gap-2 sm:gap-3">
            {/* Language Switcher */}
            <div className="flex items-center bg-slate-100 rounded-xl p-1 text-xs">
              <button
                onClick={() => setLang('en')}
                className={`px-2.5 py-1 rounded-lg font-medium transition ${
                  lang === 'en'
                    ? 'bg-white text-ministry-deepGreen shadow-xs font-bold'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                EN
              </button>
              <button
                onClick={() => setLang('rw')}
                className={`px-2.5 py-1 rounded-lg font-medium transition ${
                  lang === 'rw'
                    ? 'bg-white text-ministry-deepGreen shadow-xs font-bold'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                RW
              </button>
            </div>

            {/* Notification Bell */}
            <NotificationBell />

            {/* User Profile Info */}
            {currentUser && (
              <div className="flex items-center gap-3 pl-2 sm:pl-3 border-l border-slate-200">
                <div className="hidden md:block text-right">
                  <div className="text-xs font-bold text-slate-900 leading-tight">
                    {currentUser.displayName}
                  </div>
                  <span className={`inline-block text-[10px] font-semibold px-2 py-0.5 rounded-full border mt-0.5 ${getRoleBadge(currentUser.role)}`}>
                    {formatRoleLabel(currentUser.role)}
                  </span>
                </div>

                <div className="flex items-center gap-1">
                  <button
                    onClick={() => setShowPasswordModal(true)}
                    className="p-2 rounded-xl text-slate-500 hover:text-ministry-deepGreen hover:bg-slate-100 transition"
                    title={t('app.changePassword')}
                  >
                    <KeyRound className="w-4 h-4" />
                  </button>

                  <button
                    onClick={logout}
                    className="p-2 rounded-xl text-slate-500 hover:text-red-600 hover:bg-red-50 transition"
                    title={t('app.logout')}
                  >
                    <LogOut className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Password change dialog */}
      <PasswordChangeModal
        isOpen={showPasswordModal}
        onClose={() => setShowPasswordModal(false)}
        forced={currentUser?.mustChangePassword}
      />
    </>
  );
};
