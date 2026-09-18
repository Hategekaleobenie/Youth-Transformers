import React, { useState, useEffect } from 'react';
import { HeartHandshake, Plus, Clock, AlertTriangle, CheckCircle2, User, X, Edit3 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getFollowUpCases, saveFollowUpCase, getMembers } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { FollowUpCase, FollowUpPriority, FollowUpReason, FollowUpStatus, Member } from '../types';

export const MemberCarePage: React.FC = () => {
  const { currentUser } = useAuth();
  const [cases, setCases] = useState<FollowUpCase[]>([]);
  const [members, setMembers] = useState<Member[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCase, setEditingCase] = useState<FollowUpCase | null>(null);

  const [formData, setFormData] = useState({
    memberId: '',
    reason: 'Attendance' as FollowUpReason,
    priority: 'medium' as FollowUpPriority,
    status: 'open' as FollowUpStatus,
    notes: '',
    nextFollowUpDate: new Date(Date.now() + 1000 * 60 * 60 * 24 * 7).toISOString().split('T')[0],
    resolutionNotes: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [c, m] = await Promise.all([getFollowUpCases(), getMembers()]);
    setCases(c);
    setMembers(m);
    if (m.length > 0) {
      setFormData(prev => ({ ...prev, memberId: m[0].id }));
    }
  };

  const handleOpenAdd = () => {
    setEditingCase(null);
    setFormData({
      memberId: members[0]?.id || '',
      reason: 'Attendance',
      priority: 'medium',
      status: 'open',
      notes: '',
      nextFollowUpDate: new Date(Date.now() + 1000 * 60 * 60 * 24 * 7).toISOString().split('T')[0],
      resolutionNotes: ''
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (c: FollowUpCase) => {
    setEditingCase(c);
    setFormData({
      memberId: c.memberId,
      reason: c.reason,
      priority: c.priority,
      status: c.status,
      notes: c.notes,
      nextFollowUpDate: c.nextFollowUpDate,
      resolutionNotes: c.resolutionNotes || ''
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const chosenMember = members.find(m => m.id === formData.memberId);
    if (!chosenMember && !editingCase) return;

    if (editingCase) {
      const updated: FollowUpCase = {
        ...editingCase,
        ...formData,
        updatedAt: Date.now()
      };
      await saveFollowUpCase(updated);
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Follow-Up Case Updated',
        module: 'member_care',
        resourceType: 'case',
        resourceId: updated.id,
        details: `Updated case for ${updated.memberName} - Status: ${updated.status}`,
        result: 'success'
      });
    } else {
      const newCase: FollowUpCase = {
        id: `case_${Date.now()}`,
        memberId: chosenMember!.id,
        memberName: chosenMember!.fullName,
        assignedToUid: currentUser.uid,
        assignedToName: currentUser.displayName,
        reason: formData.reason,
        dateOpened: new Date().toISOString().split('T')[0],
        priority: formData.priority,
        status: formData.status,
        notes: formData.notes,
        nextFollowUpDate: formData.nextFollowUpDate,
        resolutionNotes: formData.resolutionNotes || undefined,
        createdAt: Date.now(),
        updatedAt: Date.now()
      };
      await saveFollowUpCase(newCase);
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Follow-Up Case Opened',
        module: 'member_care',
        resourceType: 'case',
        resourceId: newCase.id,
        details: `Opened care case for ${newCase.memberName} (${newCase.reason})`,
        result: 'success'
      });
    }

    setIsModalOpen(false);
    await loadData();
  };

  const getPriorityBadge = (p: FollowUpPriority) => {
    switch (p) {
      case 'urgent': return 'bg-red-100 text-red-800 border-red-200';
      case 'high': return 'bg-amber-100 text-amber-800 border-amber-200';
      case 'medium': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'low': return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  const reasons: FollowUpReason[] = [
    'Attendance',
    'Academic',
    'Employment',
    'Financial',
    'Family',
    'Spiritual',
    'General Support',
    'Prefer not to say'
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Pastoral Member Care & Follow-Up
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Department led by Cedrick Gisubizo • Confidential pastoral support, counseling, and prayer visits
          </p>
        </div>

        <button
          onClick={handleOpenAdd}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <Plus className="w-4 h-4" />
          <span>Open Follow-Up Case</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {cases.map((c) => (
          <div key={c.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
            <div>
              <div className="flex items-start justify-between gap-2 mb-2">
                <span className={`text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full border ${getPriorityBadge(c.priority)}`}>
                  {c.priority} priority
                </span>
                <span className="text-[10px] text-slate-500 font-semibold uppercase">
                  {c.status.replace('_', ' ')}
                </span>
              </div>

              <h3 className="font-bold text-sm text-slate-900 mb-1">{c.memberName}</h3>
              <p className="text-xs font-medium text-amber-700 mb-3">
                Reason: <strong>{c.reason}</strong>
              </p>

              <div className="p-3 bg-slate-50 rounded-xl text-slate-700 text-xs border border-slate-100 mb-3">
                <p className="leading-relaxed">{c.notes}</p>
                {c.resolutionNotes && (
                  <p className="mt-2 text-emerald-800 font-medium text-[11px] border-t border-slate-200 pt-1.5">
                    Resolution: {c.resolutionNotes}
                  </p>
                )}
              </div>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span>Next Check-In: <strong>{c.nextFollowUpDate}</strong></span>
              <button
                onClick={() => handleOpenEdit(c)}
                className="p-1.5 text-slate-500 hover:text-ministry-deepGreen hover:bg-slate-100 rounded-lg transition"
                title="Update Case"
              >
                <Edit3 className="w-4 h-4" />
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">
                {editingCase ? `Update Case: ${editingCase.memberName}` : 'Open Pastoral Support Case'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              {!editingCase && (
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Select Member *
                  </label>
                  <select
                    required
                    value={formData.memberId}
                    onChange={(e) => setFormData({ ...formData, memberId: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    {members.map((m) => (
                      <option key={m.id} value={m.id}>
                        {m.fullName} ({m.phone})
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Care Reason
                  </label>
                  <select
                    value={formData.reason}
                    onChange={(e) => setFormData({ ...formData, reason: e.target.value as FollowUpReason })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    {reasons.map((r) => (
                      <option key={r} value={r}>{r}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Priority Level
                  </label>
                  <select
                    value={formData.priority}
                    onChange={(e) => setFormData({ ...formData, priority: e.target.value as FollowUpPriority })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                    <option value="urgent">Urgent</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Case Status
                  </label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value as FollowUpStatus })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="open">Open</option>
                    <option value="in_progress">In Progress</option>
                    <option value="waiting">Waiting on Member</option>
                    <option value="resolved">Resolved</option>
                    <option value="closed">Closed</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Next Follow-up Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.nextFollowUpDate}
                    onChange={(e) => setFormData({ ...formData, nextFollowUpDate: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Confidential Pastoral Notes
                </label>
                <textarea
                  rows={3}
                  required
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  placeholder="Record visit summary, spiritual status, and prayer requests..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Resolution Notes (Optional)
                </label>
                <input
                  type="text"
                  value={formData.resolutionNotes}
                  onChange={(e) => setFormData({ ...formData, resolutionNotes: e.target.value })}
                  placeholder="Outcome of prayer and counseling follow-up..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs"
                >
                  Save Case
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
