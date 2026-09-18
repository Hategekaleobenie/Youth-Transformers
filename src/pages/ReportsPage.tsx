import React, { useState, useEffect } from 'react';
import {
  FileText,
  Download,
  Plus,
  CheckCircle2,
  Clock,
  X,
  AlertCircle,
  ShieldCheck,
  BookOpen,
  DollarSign,
  HeartHandshake,
  Share2,
  Briefcase,
  Users,
  ClipboardList
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getReports, saveReport, getMembers, getFinanceTransactions, getProjects } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import {
  generateOfficialMinistryPDF,
  downloadBibleStudyReportPDF,
  downloadFinanceReportPDF,
  downloadMemberCareReportPDF,
  downloadSocialMediaReportPDF,
  downloadProjectReportPDF,
  downloadAttendanceReportPDF,
  downloadCommitteeReportPDF
} from '../services/pdfService';
import { CommitteeReport } from '../types';

export const ReportsPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [reports, setReports] = useState<CommitteeReport[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [generatingPdfId, setGeneratingPdfId] = useState<string | null>(null);
  const [statusMessage, setStatusMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const [formData, setFormData] = useState({
    title: '',
    department: currentUser?.department || 'Executive Leadership',
    reportingPeriod: 'September 2026',
    executiveSummary: '',
    activitiesCompleted: '',
    challenges: '',
    nextSteps: '',
    status: 'submitted' as 'draft' | 'submitted' | 'approved' | 'needs_revision'
  });

  const isLeader = currentUser?.role === 'leader';

  useEffect(() => {
    loadReports();
  }, []);

  const loadReports = async () => {
    const data = await getReports();
    setReports(data);
  };

  const handleDownloadDepartmentPDF = async (
    deptKey: string,
    deptName: string,
    generator: () => Promise<void>
  ) => {
    if (!currentUser) return;
    setStatusMessage(null);
    setGeneratingPdfId(deptKey);

    try {
      await generator();
      setStatusMessage({
        type: 'success',
        text: `Official ${deptName} PDF Report generated from live database records and downloaded successfully. Verification record logged.`
      });
    } catch (err: any) {
      setStatusMessage({
        type: 'error',
        text: err.message || `Access Denied: You do not have authorization to download the ${deptName} Report.`
      });
    } finally {
      setGeneratingPdfId(null);
    }
  };

  const DEPARTMENT_REPORTS = [
    {
      id: 'bible_study_pdf',
      name: 'Bible Study Ministry Report',
      dept: 'Bible Study & Spiritual Growth',
      icon: BookOpen,
      authorizedRoles: ['bible_study', 'leader'],
      desc: 'Doctrinal syllabus, weekly participation numbers, topic highlights, and spiritual growth audits.',
      action: () => downloadBibleStudyReportPDF(currentUser!)
    },
    {
      id: 'finance_pdf',
      name: 'Finance & Treasury Audit Report',
      dept: 'Finance & Treasury',
      icon: DollarSign,
      authorizedRoles: ['accountant', 'leader'],
      desc: 'Verified inflows, expenditures, category balances, net treasury assets, and certified transaction ledger.',
      action: () => downloadFinanceReportPDF(currentUser!)
    },
    {
      id: 'member_care_pdf',
      name: 'Member Care Pastoral Report',
      dept: 'Pastoral Member Care & Welfare',
      icon: HeartHandshake,
      authorizedRoles: ['member_care', 'leader'],
      desc: 'Pastoral follow-up cases, prayer counseling requests, urgent visitation records, and welfare initiatives.',
      action: () => downloadMemberCareReportPDF(currentUser!)
    },
    {
      id: 'social_media_pdf',
      name: 'Social Media Outreach Report',
      dept: 'Digital Evangelism & Social Media',
      icon: Share2,
      authorizedRoles: ['social_media', 'leader'],
      desc: 'Digital gospel broadcasts, platform reach (Instagram, TikTok, YouTube), engagement statistics, and publication status.',
      action: () => downloadSocialMediaReportPDF(currentUser!)
    },
    {
      id: 'projects_pdf',
      name: 'Capital Projects & Asset Report',
      dept: 'Projects & Equipment Maintenance',
      icon: Briefcase,
      authorizedRoles: ['projects_manager', 'leader'],
      desc: 'Infrastructure projects, budget vs expenditure, physical inventory registry, and equipment custodian logs.',
      action: () => downloadProjectReportPDF(currentUser!)
    },
    {
      id: 'attendance_pdf',
      name: 'Fellowship Attendance Audit Report',
      dept: 'Discipleship & Attendance Oversight',
      icon: Users,
      authorizedRoles: ['bible_study', 'level1_leader', 'leader'],
      desc: 'Weekly service attendance trends, expected vs actual disciples present, and discipleship retention metrics.',
      action: () => downloadAttendanceReportPDF(currentUser!)
    },
    {
      id: 'committee_pdf',
      name: 'Executive Committee Report',
      dept: 'Committee Coordination & Deliverables',
      icon: ClipboardList,
      authorizedRoles: ['committee_coordinator', 'leader'],
      desc: 'Executive assignment tracking, departmental deliverables, quarterly milestones, and governance accountability.',
      action: () => downloadCommitteeReportPDF(currentUser!)
    }
  ];

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const newReport: CommitteeReport = {
      id: `rep_${Date.now()}`,
      title: formData.title,
      department: formData.department,
      reportingPeriod: formData.reportingPeriod,
      submittedByUid: currentUser.uid,
      submittedByName: currentUser.displayName,
      dateSubmitted: new Date().toISOString().split('T')[0],
      executiveSummary: formData.executiveSummary,
      activitiesCompleted: formData.activitiesCompleted,
      challenges: formData.challenges,
      nextSteps: formData.nextSteps,
      status: formData.status,
      createdAt: Date.now()
    };

    await saveReport(newReport);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Department Report Submitted',
      module: 'reports',
      resourceType: 'report',
      resourceId: newReport.id,
      details: `Submitted "${newReport.title}" for ${newReport.department}`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadReports();
  };

  const handleDownloadCustomPDF = async (rep: CommitteeReport) => {
    if (!currentUser) return;
    setGeneratingPdfId(rep.id);

    try {
      const [members, txs, projs] = await Promise.all([
        getMembers(),
        getFinanceTransactions(),
        getProjects()
      ]);

      const income = txs.filter(t => t.type === 'income').reduce((s, t) => s + t.amount, 0);
      const expense = txs.filter(t => t.type === 'expense').reduce((s, t) => s + t.amount, 0);
      const balance = income - expense;

      await generateOfficialMinistryPDF({
        report: rep,
        generatedBy: currentUser.displayName,
        totalMembers: members.length,
        activeMembers: members.filter(m => m.status === 'active').length,
        treasuryBalance: balance,
        activeProjects: projs.filter(p => p.status === 'active').length
      });

      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Official PDF Report Downloaded',
        module: 'reports',
        resourceType: 'pdf',
        resourceId: rep.id,
        details: `Generated official document for: ${rep.title}`,
        result: 'success'
      });
      setStatusMessage({
        type: 'success',
        text: `Official PDF for "${rep.title}" successfully generated and downloaded.`
      });
    } catch (err: any) {
      console.error('PDF error:', err);
      setStatusMessage({
        type: 'error',
        text: err.message || 'Failed to generate PDF.'
      });
    } finally {
      setGeneratingPdfId(null);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Official Ministry Reports & Publications
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Real-time departmental PDF generation, official audit trails, and executive governance summaries
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <Plus className="w-4 h-4" />
          <span>Draft Department Report</span>
        </button>
      </div>

      {/* Status Notifications */}
      {statusMessage && (
        <div
          className={`p-4 rounded-2xl flex items-start gap-3 text-xs border animate-fade-in ${
            statusMessage.type === 'success'
              ? 'bg-emerald-50 border-emerald-200 text-emerald-900'
              : 'bg-rose-50 border-rose-200 text-rose-900'
          }`}
        >
          {statusMessage.type === 'success' ? (
            <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
          ) : (
            <AlertCircle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />
          )}
          <div className="flex-1">
            <span className="font-bold block mb-0.5">
              {statusMessage.type === 'success' ? 'Report Export Successful' : 'Access Verification Failure'}
            </span>
            <span>{statusMessage.text}</span>
          </div>
          <button
            onClick={() => setStatusMessage(null)}
            className="text-slate-400 hover:text-slate-600 text-xs font-bold"
          >
            Dismiss
          </button>
        </div>
      )}

      {/* 7 Official Real-Time Department Reports Station */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 shadow-xs">
        <div className="flex items-center gap-2 mb-2">
          <ShieldCheck className="w-5 h-5 text-ministry-emerald" />
          <h3 className="text-sm font-black text-slate-900 uppercase tracking-wide">
            Real-Time Authorized Department PDF Reports
          </h3>
        </div>
        <p className="text-xs text-slate-500 mb-5">
          Each report queries current database collections and generates an official, verified ministry document.
          Access is enforced by Role-Based Access Control and all attempts are logged in the Security Audit Log.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {DEPARTMENT_REPORTS.map((r) => {
            const Icon = r.icon;
            const isAuthorized = currentUser ? r.authorizedRoles.includes(currentUser.role) : false;
            const isBusy = generatingPdfId === r.id;

            return (
              <div
                key={r.id}
                className="bg-slate-50 rounded-2xl border border-slate-200 p-4.5 flex flex-col justify-between hover:border-slate-300 transition"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <div className="w-9 h-9 rounded-xl bg-white border border-slate-200 flex items-center justify-center text-ministry-deepGreen shadow-2xs">
                      <Icon className="w-4 h-4" />
                    </div>
                    <span
                      className={`text-[9px] font-bold px-2 py-0.5 rounded-full uppercase ${
                        isAuthorized
                          ? 'bg-emerald-100 text-emerald-800'
                          : 'bg-slate-200 text-slate-600'
                      }`}
                    >
                      {isAuthorized ? 'Authorized' : 'Restricted'}
                    </span>
                  </div>

                  <h4 className="font-bold text-xs text-slate-900 mb-1">{r.name}</h4>
                  <span className="block text-[10px] text-ministry-emerald font-semibold mb-2">
                    {r.dept}
                  </span>
                  <p className="text-[11px] text-slate-500 leading-relaxed mb-4">
                    {r.desc}
                  </p>
                </div>

                <div className="pt-3 border-t border-slate-200/60">
                  <button
                    onClick={() => handleDownloadDepartmentPDF(r.id, r.name, r.action)}
                    disabled={isBusy}
                    className={`w-full py-2 px-3 rounded-xl text-xs font-bold transition flex items-center justify-center gap-2 shadow-2xs ${
                      isAuthorized
                        ? 'bg-ministry-deepGreen hover:bg-ministry-forestDark text-white'
                        : 'bg-slate-200 hover:bg-rose-100 hover:text-rose-800 text-slate-700'
                    }`}
                  >
                    <Download className="w-3.5 h-3.5" />
                    <span>{isBusy ? 'Compiling PDF...' : 'Download Official PDF'}</span>
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Submitted Departmental Progress Reports */}
      <div className="pt-2">
        <h3 className="text-sm font-black text-slate-900 uppercase tracking-wide mb-3">
          Submitted Departmental Progress Reports
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {reports.map((rep) => (
            <div key={rep.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span className="text-[10px] font-bold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-200 uppercase">
                    {rep.department}
                  </span>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                    rep.status === 'approved' ? 'bg-emerald-100 text-emerald-800' : 'bg-blue-100 text-blue-800'
                  }`}>
                    {rep.status.toUpperCase()}
                  </span>
                </div>

                <h3 className="font-bold text-sm text-slate-900 mb-1">{rep.title}</h3>
                <p className="text-xs text-slate-500 mb-3">
                  Period: <strong>{rep.reportingPeriod}</strong> • Submitted: {rep.dateSubmitted}
                </p>

                <div className="p-3 bg-slate-50 rounded-xl text-slate-700 text-xs border border-slate-100 mb-3">
                  <strong className="block text-slate-900 mb-1 text-[11px] uppercase tracking-wider">
                    Executive Summary
                  </strong>
                  <p className="leading-relaxed line-clamp-3">{rep.executiveSummary}</p>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                <span className="text-slate-400">By: {rep.submittedByName}</span>

                <button
                  onClick={() => handleDownloadCustomPDF(rep)}
                  disabled={generatingPdfId === rep.id}
                  className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl text-xs shadow-xs transition disabled:opacity-50"
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>{generatingPdfId === rep.id ? 'Generating...' : 'Download PDF'}</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-xl max-h-[90vh] overflow-y-auto border border-slate-200">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white sticky top-0 z-10">
              <h3 className="font-bold text-sm">Draft Ministry Department Report</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Report Title *
                </label>
                <input
                  type="text"
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Monthly Discipleship & Evangelism Progress Report"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Department
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.department}
                    onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Reporting Period
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.reportingPeriod}
                    onChange={(e) => setFormData({ ...formData, reportingPeriod: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Executive Summary *
                </label>
                <textarea
                  required
                  rows={3}
                  value={formData.executiveSummary}
                  onChange={(e) => setFormData({ ...formData, executiveSummary: e.target.value })}
                  placeholder="High-level overview of ministry impact and accomplishments..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Key Activities Completed
                </label>
                <textarea
                  rows={2}
                  value={formData.activitiesCompleted}
                  onChange={(e) => setFormData({ ...formData, activitiesCompleted: e.target.value })}
                  placeholder="Bullet points or summary of initiatives executed..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Operational Challenges
                  </label>
                  <textarea
                    rows={2}
                    value={formData.challenges}
                    onChange={(e) => setFormData({ ...formData, challenges: e.target.value })}
                    placeholder="Hindrances or areas needing prayer & resource support..."
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Strategic Next Steps
                  </label>
                  <textarea
                    rows={2}
                    value={formData.nextSteps}
                    onChange={(e) => setFormData({ ...formData, nextSteps: e.target.value })}
                    placeholder="Milestones slated for next reporting interval..."
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs transition"
                >
                  Submit Official Report
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
