import React, { useState, useEffect } from 'react';
import { useAuth } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { Navbar } from './components/Navbar';
import { Sidebar, NavTab } from './components/Sidebar';
import { LeaderDashboardPage } from './pages/LeaderDashboardPage';
import { UserManagementPage } from './pages/UserManagementPage';
import { MembersPage } from './pages/MembersPage';
import { CommitteePage } from './pages/CommitteePage';
import { Level1Page } from './pages/Level1Page';
import { BibleStudyPage } from './pages/BibleStudyPage';
import { AttendancePage } from './pages/AttendancePage';
import { MemberCarePage } from './pages/MemberCarePage';
import { FinancePage } from './pages/FinancePage';
import { ProjectsEquipmentPage } from './pages/ProjectsEquipmentPage';
import { SocialMediaPage } from './pages/SocialMediaPage';
import { EvangelismEventsPage } from './pages/EvangelismEventsPage';
import { ReportsPage } from './pages/ReportsPage';
import { AnnouncementsPage } from './pages/AnnouncementsPage';
import { ActivityLogPage } from './pages/ActivityLogPage';
import { SettingsPage } from './pages/SettingsPage';
import { AccessDeniedPage } from './pages/AccessDeniedPage';
import { MinistryRole } from './types';

export function getDefaultTabForRole(role: MinistryRole): NavTab {
  switch (role) {
    case 'leader':
      return 'dashboard';
    case 'member_care':
      return 'member_care';
    case 'level1_leader':
      return 'level1';
    case 'bible_study':
      return 'bible_study';
    case 'accountant':
      return 'finance';
    case 'projects_manager':
      return 'projects';
    case 'social_media':
      return 'social_media';
    case 'committee_coordinator':
      return 'committee';
    case 'member':
    default:
      return 'announcements';
  }
}

const ROUTE_MAP: Record<string, NavTab> = {
  leader: 'dashboard',
  dashboard: 'dashboard',
  users: 'users',
  members: 'members',
  committee: 'committee',
  level1: 'level1',
  'bible-study': 'bible_study',
  bible_study: 'bible_study',
  attendance: 'attendance',
  'member-care': 'member_care',
  member_care: 'member_care',
  finance: 'finance',
  projects: 'projects',
  'social-media': 'social_media',
  social_media: 'social_media',
  events: 'evangelism',
  evangelism: 'evangelism',
  reports: 'reports',
  announcements: 'announcements',
  'activity-log': 'activity_log',
  activity_log: 'activity_log',
  settings: 'settings'
};

const TAB_TO_ROUTE: Record<NavTab, string> = {
  dashboard: 'leader',
  users: 'users',
  members: 'members',
  committee: 'committee',
  level1: 'level1',
  bible_study: 'bible-study',
  attendance: 'attendance',
  member_care: 'member-care',
  finance: 'finance',
  projects: 'projects',
  social_media: 'social-media',
  evangelism: 'events',
  reports: 'reports',
  announcements: 'announcements',
  activity_log: 'activity-log',
  settings: 'settings'
};

export const App: React.FC = () => {
  const { currentUser, loading, hasPermission } = useAuth();
  const [activeTab, setActiveTab] = useState<NavTab>('dashboard');
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  // Sync activeTab from URL hash or path, or default role landing
  useEffect(() => {
    if (!currentUser) return;

    const parseRouteFromLocation = (): NavTab | null => {
      // 1. Check hash e.g. #/leader or #leader
      const hash = window.location.hash.replace(/^#\/?/, '').trim().toLowerCase();
      if (hash && ROUTE_MAP[hash]) {
        return ROUTE_MAP[hash];
      }

      // 2. Check pathname e.g. /leader
      const path = window.location.pathname.replace(/^\//, '').trim().toLowerCase();
      if (path && ROUTE_MAP[path]) {
        return ROUTE_MAP[path];
      }

      return null;
    };

    const targetRoute = parseRouteFromLocation();
    if (targetRoute) {
      setActiveTab(targetRoute);
    } else {
      const defaultTab = getDefaultTabForRole(currentUser.role);
      setActiveTab(defaultTab);
      window.location.hash = `#${TAB_TO_ROUTE[defaultTab]}`;
    }

    const handleHashChange = () => {
      const matched = parseRouteFromLocation();
      if (matched) {
        setActiveTab(matched);
      }
    };

    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, [currentUser]);

  const handleSelectTab = (tab: NavTab) => {
    setActiveTab(tab);
    window.location.hash = `#${TAB_TO_ROUTE[tab]}`;
  };

  // Loading state
  if (loading) {
    return (
      <div className="min-h-screen bg-ministry-forestDark flex flex-col items-center justify-center p-4 text-white">
        <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-ministry-emerald to-ministry-gold p-1 shadow-xl animate-pulse mb-4 flex items-center justify-center">
          <div className="w-full h-full bg-ministry-forestDark rounded-xl flex items-center justify-center text-ministry-gold font-extrabold text-xl">
            YT
          </div>
        </div>
        <h2 className="text-base font-bold tracking-wide">Youth Transformers Ministry</h2>
        <p className="text-xs text-emerald-200 mt-1">Securing connection & loading permissions...</p>
      </div>
    );
  }

  // Not logged in -> show real login
  if (!currentUser) {
    return <LoginPage />;
  }

  const defaultHome = getDefaultTabForRole(currentUser.role);

  // Render view with strict RBAC verification
  const renderActiveView = () => {
    switch (activeTab) {
      case 'dashboard':
        if (currentUser.role !== 'leader') {
          return <AccessDeniedPage moduleName="leader" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <LeaderDashboardPage onNavigate={handleSelectTab} />;

      case 'users':
        if (currentUser.role !== 'leader') {
          return <AccessDeniedPage moduleName="users" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <UserManagementPage />;

      case 'activity_log':
        if (currentUser.role !== 'leader') {
          return <AccessDeniedPage moduleName="activity_log" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <ActivityLogPage />;

      case 'members':
        if (!hasPermission('members')) {
          return <AccessDeniedPage moduleName="members" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <MembersPage />;

      case 'committee':
        if (!hasPermission('committee') && currentUser.role !== 'committee_coordinator') {
          return <AccessDeniedPage moduleName="committee" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <CommitteePage />;

      case 'level1':
        if (currentUser.role !== 'level1_leader' && currentUser.role !== 'leader' && !hasPermission('members')) {
          return <AccessDeniedPage moduleName="level1" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <Level1Page />;

      case 'bible_study':
        if (!hasPermission('bible_study') && currentUser.role !== 'bible_study') {
          return <AccessDeniedPage moduleName="bible_study" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <BibleStudyPage />;

      case 'attendance':
        if (!hasPermission('attendance')) {
          return <AccessDeniedPage moduleName="attendance" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <AttendancePage />;

      case 'member_care':
        if (!hasPermission('member_care') && currentUser.role !== 'member_care') {
          return <AccessDeniedPage moduleName="member_care" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <MemberCarePage />;

      case 'finance':
        if (!hasPermission('finance') && currentUser.role !== 'accountant') {
          return <AccessDeniedPage moduleName="finance" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <FinancePage />;

      case 'projects':
        if (!hasPermission('projects') && currentUser.role !== 'projects_manager') {
          return <AccessDeniedPage moduleName="projects" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <ProjectsEquipmentPage />;

      case 'social_media':
        if (!hasPermission('social_media') && currentUser.role !== 'social_media') {
          return <AccessDeniedPage moduleName="social_media" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <SocialMediaPage />;

      case 'evangelism':
        if (!hasPermission('events')) {
          return <AccessDeniedPage moduleName="events" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <EvangelismEventsPage />;

      case 'reports':
        if (!hasPermission('reports')) {
          return <AccessDeniedPage moduleName="reports" onGoHome={() => handleSelectTab(defaultHome)} />;
        }
        return <ReportsPage />;

      case 'announcements':
        return <AnnouncementsPage />;

      case 'settings':
        return <SettingsPage />;

      default:
        return <LeaderDashboardPage onNavigate={handleSelectTab} />;
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col antialiased selection:bg-ministry-emerald selection:text-white">
      {/* Top Navigation */}
      <Navbar onToggleSidebar={() => setIsSidebarOpen(prev => !prev)} />

      {/* Main workspace with Sidebar & Content */}
      <div className="flex-1 flex overflow-hidden">
        <Sidebar
          activeTab={activeTab}
          onSelectTab={handleSelectTab}
          isOpen={isSidebarOpen}
          onClose={() => setIsSidebarOpen(false)}
        />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto w-full">
          {renderActiveView()}
        </main>
      </div>
    </div>
  );
};
