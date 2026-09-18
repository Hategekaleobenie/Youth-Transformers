import React from 'react';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  CalendarCheck,
  GraduationCap,
  BookOpen,
  CheckSquare,
  Flame,
  HeartHandshake,
  Share2,
  Wallet,
  FolderGit2,
  FileText,
  Megaphone,
  ShieldAlert,
  Settings,
  X
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { ModuleName } from '../types';

export type NavTab =
  | 'dashboard'
  | 'users'
  | 'members'
  | 'committee'
  | 'level1'
  | 'bible_study'
  | 'attendance'
  | 'evangelism'
  | 'member_care'
  | 'social_media'
  | 'finance'
  | 'projects'
  | 'reports'
  | 'announcements'
  | 'activity_log'
  | 'settings';

interface Props {
  activeTab: NavTab;
  onSelectTab: (tab: NavTab) => void;
  isOpen: boolean;
  onClose: () => void;
}

interface NavItem {
  id: NavTab;
  module?: ModuleName;
  labelKey: string;
  defaultLabel: string;
  icon: React.ElementType;
  leaderOnly?: boolean;
}

export const Sidebar: React.FC<Props> = ({ activeTab, onSelectTab, isOpen, onClose }) => {
  const { currentUser, hasPermission, t } = useAuth();
  const isLeader = currentUser?.role === 'leader';

  const navItems: NavItem[] = [
    { id: 'dashboard', labelKey: 'nav.dashboard', defaultLabel: 'Dashboard', icon: LayoutDashboard },
    { id: 'users', module: 'users', labelKey: 'nav.userManagement', defaultLabel: 'User Management', icon: Users, leaderOnly: true },
    { id: 'members', module: 'members', labelKey: 'nav.members', defaultLabel: 'Members Directory', icon: UserCheck },
    { id: 'committee', module: 'committee', labelKey: 'nav.committee', defaultLabel: 'Committee & Tasks', icon: CalendarCheck },
    { id: 'level1', module: 'members', labelKey: 'nav.level1', defaultLabel: 'Level 1 Discipleship', icon: GraduationCap },
    { id: 'bible_study', module: 'bible_study', labelKey: 'nav.bibleStudy', defaultLabel: 'Bible Study', icon: BookOpen },
    { id: 'attendance', module: 'attendance', labelKey: 'nav.attendance', defaultLabel: 'Attendance', icon: CheckSquare },
    { id: 'evangelism', module: 'events', labelKey: 'nav.evangelism', defaultLabel: 'Evangelism & Events', icon: Flame },
    { id: 'member_care', module: 'member_care', labelKey: 'nav.memberCare', defaultLabel: 'Pastoral Member Care', icon: HeartHandshake },
    { id: 'social_media', module: 'social_media', labelKey: 'nav.socialMedia', defaultLabel: 'Social Media Gospel', icon: Share2 },
    { id: 'finance', module: 'finance', labelKey: 'nav.finance', defaultLabel: 'Finance & Treasury', icon: Wallet },
    { id: 'projects', module: 'projects', labelKey: 'nav.projects', defaultLabel: 'Projects & Equipment', icon: FolderGit2 },
    { id: 'reports', module: 'reports', labelKey: 'nav.reports', defaultLabel: 'Department Reports', icon: FileText },
    { id: 'announcements', module: 'announcements', labelKey: 'nav.announcements', defaultLabel: 'Bulletins & Notices', icon: Megaphone },
    { id: 'activity_log', module: 'activity_log', labelKey: 'nav.activityLog', defaultLabel: 'Security Activity Log', icon: ShieldAlert, leaderOnly: true },
    { id: 'settings', labelKey: 'nav.settings', defaultLabel: 'Settings & Security', icon: Settings },
  ];

  // Filter items by role permissions
  const visibleItems = navItems.filter((item) => {
    if (item.leaderOnly) return isLeader;
    if (item.id === 'dashboard' || item.id === 'settings') return true;
    if (!item.module) return true;
    return hasPermission(item.module);
  });

  const handleSelect = (tab: NavTab) => {
    onSelectTab(tab);
    onClose();
  };

  return (
    <>
      {/* Mobile backdrop overlay */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 z-40 bg-black/60 backdrop-blur-xs lg:hidden transition-opacity"
          aria-hidden="true"
        />
      )}

      {/* Sidebar container */}
      <aside
        className={`fixed lg:static top-0 bottom-0 left-0 z-40 w-72 bg-ministry-deepGreen text-white flex flex-col shadow-xl transition-transform duration-300 ease-in-out ${
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        {/* Brand Header */}
        <div className="px-5 py-5 border-b border-emerald-800/60 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-ministry-emerald to-ministry-deepGreen p-0.5 shadow-md flex items-center justify-center">
              <span className="text-ministry-gold font-black text-lg tracking-wider">YT</span>
            </div>
            <div>
              <h2 className="text-sm font-extrabold tracking-wide text-white uppercase">
                Youth Transformers
              </h2>
              <span className="text-[10px] font-semibold text-ministry-gold tracking-wider uppercase block">
                Ministry Platform
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="lg:hidden p-1.5 text-emerald-300 hover:text-white rounded-lg hover:bg-white/10"
            aria-label="Close sidebar"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* User Context Banner */}
        {currentUser && (
          <div className="mx-4 my-3 p-3 bg-emerald-900/50 rounded-xl border border-emerald-700/40">
            <p className="text-xs font-bold text-emerald-100 truncate">{currentUser.displayName}</p>
            <p className="text-[11px] text-ministry-gold font-medium truncate mt-0.5">
              {currentUser.department}
            </p>
          </div>
        )}

        {/* Navigation Items List */}
        <div className="flex-1 overflow-y-auto px-3 py-2 space-y-1">
          {visibleItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => handleSelect(item.id)}
                className={`w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl font-medium text-xs transition-all text-left group ${
                  isActive
                    ? 'bg-ministry-emerald text-white shadow-md font-semibold'
                    : 'text-emerald-100 hover:bg-emerald-800/50 hover:text-white'
                }`}
              >
                <Icon className={`w-4 h-4 shrink-0 transition ${
                  isActive ? 'text-ministry-gold' : 'text-emerald-300 group-hover:text-white'
                }`} />
                <span className="truncate">{t(item.labelKey, item.defaultLabel)}</span>
                {isActive && (
                  <span className="ml-auto w-1.5 h-1.5 rounded-full bg-ministry-gold" />
                )}
              </button>
            );
          })}
        </div>

        {/* Footer Brand Info */}
        <div className="p-4 border-t border-emerald-800/60 text-center">
          <p className="text-[10px] text-emerald-300/80 font-medium">
            Leader Leo Benie Hategeka
          </p>
          <p className="text-[9px] text-emerald-400/60 mt-0.5">
            Youth Transformers Ministry • 2026
          </p>
        </div>
      </aside>
    </>
  );
};
