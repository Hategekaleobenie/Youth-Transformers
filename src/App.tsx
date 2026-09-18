import React, { useState } from 'react';
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

export const App: React.FC = () => {
  const { currentUser, loading, hasPermission } = useAuth();
  const [activeTab, setActiveTab] = useState<NavTab>('dashboard');
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

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

  // Render view with RBAC verification
  const renderActiveView = () => {
    switch (activeTab) {
      case 'dashboard':
        return <LeaderDashboardPage onNavigate={setActiveTab} />;

      case 'users':
        if (currentUser.role !== 'leader') {
          return <AccessDeniedPage moduleName="users" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <UserManagementPage />;

      case 'members':
        if (!hasPermission('members')) {
          return <AccessDeniedPage moduleName="members" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <MembersPage />;

      case 'committee':
        if (!hasPermission('committee')) {
          return <AccessDeniedPage moduleName="committee" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <CommitteePage />;

      case 'level1':
        return <Level1Page />;

      case 'bible_study':
        if (!hasPermission('bible_study')) {
          return <AccessDeniedPage moduleName="bible_study" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <BibleStudyPage />;

      case 'attendance':
        if (!hasPermission('attendance')) {
          return <AccessDeniedPage moduleName="attendance" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <AttendancePage />;

      case 'member_care':
        if (!hasPermission('member_care')) {
          return <AccessDeniedPage moduleName="member_care" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <MemberCarePage />;

      case 'finance':
        if (!hasPermission('finance')) {
          return <AccessDeniedPage moduleName="finance" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <FinancePage />;

      case 'projects':
        if (!hasPermission('projects')) {
          return <AccessDeniedPage moduleName="projects" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <ProjectsEquipmentPage />;

      case 'social_media':
        if (!hasPermission('social_media')) {
          return <AccessDeniedPage moduleName="social_media" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <SocialMediaPage />;

      case 'evangelism':
        if (!hasPermission('events')) {
          return <AccessDeniedPage moduleName="events" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <EvangelismEventsPage />;

      case 'reports':
        if (!hasPermission('reports')) {
          return <AccessDeniedPage moduleName="reports" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <ReportsPage />;

      case 'announcements':
        return <AnnouncementsPage />;

      case 'activity_log':
        if (currentUser.role !== 'leader') {
          return <AccessDeniedPage moduleName="activity_log" onGoHome={() => setActiveTab('dashboard')} />;
        }
        return <ActivityLogPage />;

      case 'settings':
        return <SettingsPage />;

      default:
        return <LeaderDashboardPage onNavigate={setActiveTab} />;
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
          onSelectTab={setActiveTab}
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
