import React, { useState, useEffect } from 'react';
import {
  Users,
  UserCheck,
  HeartHandshake,
  Wallet,
  FolderGit2,
  Share2,
  BookOpen,
  FileText,
  Clock,
  ArrowUpRight,
  ShieldCheck,
  CalendarCheck
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import {
  getMembers,
  getFollowUpCases,
  getFinanceTransactions,
  getProjects,
  getSocialPosts,
  getBibleStudies,
  getReports
} from '../services/firestoreService';
import { fetchActivityLogs } from '../services/activityLogService';
import { Member, FollowUpCase, FinancialTransaction, Project, SocialPost, BibleStudy, CommitteeReport, ActivityLogItem } from '../types';
import { NavTab } from '../components/Sidebar';

interface Props {
  onNavigate: (tab: NavTab) => void;
}

export const LeaderDashboardPage: React.FC<Props> = ({ onNavigate }) => {
  const { currentUser, t } = useAuth();

  const [members, setMembers] = useState<Member[]>([]);
  const [cases, setCases] = useState<FollowUpCase[]>([]);
  const [finance, setFinance] = useState<FinancialTransaction[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [social, setSocial] = useState<SocialPost[]>([]);
  const [studies, setStudies] = useState<BibleStudy[]>([]);
  const [reports, setReports] = useState<CommitteeReport[]>([]);
  const [logs, setLogs] = useState<ActivityLogItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      setLoading(true);
      const [m, c, f, p, s, b, r, l] = await Promise.all([
        getMembers(),
        getFollowUpCases(),
        getFinanceTransactions(),
        getProjects(),
        getSocialPosts(),
        getBibleStudies(),
        getReports(),
        fetchActivityLogs(10)
      ]);
      setMembers(m);
      setCases(c);
      setFinance(f);
      setProjects(p);
      setSocial(s);
      setStudies(b);
      setReports(r);
      setLogs(l);
      setLoading(false);
    }
    loadData();
  }, []);

  // Compute actual database statistics
  const totalMembers = members.length;
  const activeMembers = members.filter(m => m.status === 'active').length;
  const followUpRequired = cases.filter(c => c.status === 'open' || c.status === 'in_progress').length;

  const totalIncome = finance.filter(tx => tx.type === 'income').reduce((sum, tx) => sum + tx.amount, 0);
  const totalExpenses = finance.filter(tx => tx.type === 'expense').reduce((sum, tx) => sum + tx.amount, 0);
  const treasuryBalance = totalIncome - totalExpenses;

  const activeProjects = projects.filter(p => p.status === 'active' || p.status === 'planning').length;
  const gospelPublishedPosts = social.filter(s => s.status === 'published').length;
  const pendingReports = reports.filter(r => r.status === 'submitted' || r.status === 'draft').length;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Top Welcome Banner */}
      <div className="bg-gradient-to-r from-ministry-deepGreen via-emerald-800 to-ministry-forestDark rounded-3xl p-6 sm:p-8 text-white shadow-lg relative overflow-hidden">
        <div className="relative z-10 max-w-2xl">
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-ministry-gold text-ministry-forestDark mb-3">
            <ShieldCheck className="w-3.5 h-3.5" />
            Executive Ministry Administration
          </span>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">
            Welcome, {currentUser?.displayName}
          </h2>
          <p className="text-xs sm:text-sm text-emerald-100 mt-1.5 leading-relaxed">
            Leading and equipping the youth to live transformed lives in Christ Jesus. Real-time ministry health, pastoral care, and departmental stewardship.
          </p>
        </div>
      </div>

      {/* Primary KPI Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-2 lg:grid-cols-4 gap-3.5 sm:gap-4">
        {/* Total Members */}
        <div
          onClick={() => onNavigate('members')}
          className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-xs hover:shadow-md transition cursor-pointer group"
        >
          <div className="flex items-center justify-between mb-2">
            <div className="p-2.5 rounded-xl bg-emerald-50 text-ministry-emerald group-hover:bg-ministry-emerald group-hover:text-white transition">
              <Users className="w-5 h-5" />
            </div>
            <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-ministry-emerald transition" />
          </div>
          <div className="text-2xl font-black text-slate-900">{totalMembers}</div>
          <div className="text-xs font-semibold text-slate-500 mt-0.5">{t('dashboard.kpiTotalMembers')}</div>
        </div>

        {/* Active Disciples */}
        <div
          onClick={() => onNavigate('members')}
          className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-xs hover:shadow-md transition cursor-pointer group"
        >
          <div className="flex items-center justify-between mb-2">
            <div className="p-2.5 rounded-xl bg-emerald-50 text-ministry-emerald group-hover:bg-ministry-emerald group-hover:text-white transition">
              <UserCheck className="w-5 h-5" />
            </div>
            <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-ministry-emerald transition" />
          </div>
          <div className="text-2xl font-black text-slate-900">{activeMembers}</div>
          <div className="text-xs font-semibold text-slate-500 mt-0.5">{t('dashboard.kpiActiveMembers')}</div>
        </div>

        {/* Follow Up Required */}
        <div
          onClick={() => onNavigate('member_care')}
          className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-xs hover:shadow-md transition cursor-pointer group"
        >
          <div className="flex items-center justify-between mb-2">
            <div className="p-2.5 rounded-xl bg-amber-50 text-amber-600 group-hover:bg-amber-600 group-hover:text-white transition">
              <HeartHandshake className="w-5 h-5" />
            </div>
            <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-amber-600 transition" />
          </div>
          <div className="text-2xl font-black text-slate-900">{followUpRequired}</div>
          <div className="text-xs font-semibold text-slate-500 mt-0.5">{t('dashboard.kpiFollowUp')}</div>
        </div>

        {/* Treasury Balance */}
        <div
          onClick={() => onNavigate('finance')}
          className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-xs hover:shadow-md transition cursor-pointer group"
        >
          <div className="flex items-center justify-between mb-2">
            <div className="p-2.5 rounded-xl bg-blue-50 text-blue-600 group-hover:bg-blue-600 group-hover:text-white transition">
              <Wallet className="w-5 h-5" />
            </div>
            <ArrowUpRight className="w-4 h-4 text-slate-400 group-hover:text-blue-600 transition" />
          </div>
          <div className="text-xl font-black text-slate-900 truncate">
            {treasuryBalance.toLocaleString()} RWF
          </div>
          <div className="text-xs font-semibold text-slate-500 mt-0.5">{t('dashboard.kpiTreasury')}</div>
        </div>
      </div>

      {/* Secondary Departmental Metrics */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5 sm:gap-4">
        <div
          onClick={() => onNavigate('projects')}
          className="bg-white p-4 rounded-2xl border border-slate-200 cursor-pointer hover:border-slate-300 transition"
        >
          <div className="flex items-center gap-2 text-slate-500 mb-1">
            <FolderGit2 className="w-4 h-4 text-ministry-emerald" />
            <span className="text-xs font-medium">Projects</span>
          </div>
          <div className="text-lg font-bold text-slate-900">{activeProjects} Active</div>
        </div>

        <div
          onClick={() => onNavigate('social_media')}
          className="bg-white p-4 rounded-2xl border border-slate-200 cursor-pointer hover:border-slate-300 transition"
        >
          <div className="flex items-center gap-2 text-slate-500 mb-1">
            <Share2 className="w-4 h-4 text-purple-600" />
            <span className="text-xs font-medium">Gospel Content</span>
          </div>
          <div className="text-lg font-bold text-slate-900">{gospelPublishedPosts} Published</div>
        </div>

        <div
          onClick={() => onNavigate('bible_study')}
          className="bg-white p-4 rounded-2xl border border-slate-200 cursor-pointer hover:border-slate-300 transition"
        >
          <div className="flex items-center gap-2 text-slate-500 mb-1">
            <BookOpen className="w-4 h-4 text-amber-600" />
            <span className="text-xs font-medium">Bible Studies</span>
          </div>
          <div className="text-lg font-bold text-slate-900">{studies.length} Sessions</div>
        </div>

        <div
          onClick={() => onNavigate('reports')}
          className="bg-white p-4 rounded-2xl border border-slate-200 cursor-pointer hover:border-slate-300 transition"
        >
          <div className="flex items-center gap-2 text-slate-500 mb-1">
            <FileText className="w-4 h-4 text-emerald-600" />
            <span className="text-xs font-medium">Reports</span>
          </div>
          <div className="text-lg font-bold text-slate-900">{pendingReports} Pending</div>
        </div>
      </div>

      {/* Recent Activity Audit Feed (Newest First) */}
      <div className="bg-white rounded-2xl border border-slate-200 p-5 sm:p-6 shadow-xs">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-slate-100 text-slate-700">
              <Clock className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-slate-900">
                {t('dashboard.recentActivity')}
              </h3>
              <p className="text-[11px] text-slate-500">
                Sorted chronological audit trail (Newest first)
              </p>
            </div>
          </div>
          {currentUser?.role === 'leader' && (
            <button
              onClick={() => onNavigate('activity_log')}
              className="text-xs text-ministry-emerald hover:text-ministry-deepGreen font-bold flex items-center gap-1"
            >
              <span>View Full Audit Log</span>
              <ArrowUpRight className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        <div className="divide-y divide-slate-100">
          {logs.slice(0, 6).map((log) => (
            <div key={log.id} className="py-3 flex items-start justify-between gap-3 text-xs">
              <div>
                <div className="font-semibold text-slate-800 flex items-center gap-2">
                  <span>{log.action}</span>
                  <span className="text-[10px] px-2 py-0.5 rounded-full bg-slate-100 text-slate-600 font-medium">
                    {log.module}
                  </span>
                </div>
                <p className="text-slate-500 text-[11px] mt-0.5">
                  By <strong className="text-slate-700">{log.actorName}</strong> ({log.actorRole})
                  {log.details && ` • ${log.details}`}
                </p>
              </div>
              <div className="text-right shrink-0 text-slate-400 text-[11px]">
                {new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
