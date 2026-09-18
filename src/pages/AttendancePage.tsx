import React, { useState, useEffect } from 'react';
import { CheckSquare, Calendar, Users, Plus, X, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getAttendance, saveAttendance, getMembers } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { AttendanceRecord, AttendanceStatus, AttendanceType, Member } from '../types';

export const AttendancePage: React.FC = () => {
  const { currentUser } = useAuth();
  const [records, setRecords] = useState<AttendanceRecord[]>([]);
  const [members, setMembers] = useState<Member[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [typeFilter, setTypeFilter] = useState<string>('all');

  const [formData, setFormData] = useState({
    memberId: '',
    eventName: 'Weekly Bible Study Fellowship',
    eventType: 'Bible Study' as AttendanceType,
    dateStr: new Date().toISOString().split('T')[0],
    status: 'present' as AttendanceStatus,
    excuseReason: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [att, mem] = await Promise.all([getAttendance(), getMembers()]);
    setRecords(att);
    setMembers(mem);
    if (mem.length > 0) {
      setFormData(prev => ({ ...prev, memberId: mem[0].id }));
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const chosenMember = members.find(m => m.id === formData.memberId);
    if (!chosenMember) return;

    const rec: AttendanceRecord = {
      id: `att_${Date.now()}`,
      memberId: chosenMember.id,
      memberName: chosenMember.fullName,
      eventId: `event_${Date.now()}`,
      eventName: formData.eventName,
      eventType: formData.eventType,
      dateStr: formData.dateStr,
      status: formData.status,
      excuseReason: formData.excuseReason || undefined,
      recordedBy: currentUser.displayName,
      createdAt: Date.now()
    };

    await saveAttendance(rec);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Attendance Recorded',
      module: 'attendance',
      resourceType: 'attendance',
      resourceId: rec.id,
      details: `Marked ${rec.memberName} as ${rec.status} for ${rec.eventName}`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadData();
  };

  const filtered = records.filter(r => typeFilter === 'all' || r.eventType === typeFilter);

  const presentCount = records.filter(r => r.status === 'present').length;
  const excusedCount = records.filter(r => r.status === 'excused').length;
  const absentCount = records.filter(r => r.status === 'absent').length;

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Attendance Records & Pastoral Accountability
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Tracking discipleship presence to identify members needing pastoral follow-up
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <Plus className="w-4 h-4" />
          <span>Record Attendance</span>
        </button>
      </div>

      {/* Attendance Stats Cards */}
      <div className="grid grid-cols-3 gap-3">
        <div className="bg-white p-4 rounded-2xl border border-slate-200 text-center">
          <span className="text-xs text-emerald-600 font-bold block">Present</span>
          <strong className="text-xl font-black text-slate-900">{presentCount}</strong>
        </div>
        <div className="bg-white p-4 rounded-2xl border border-slate-200 text-center">
          <span className="text-xs text-amber-600 font-bold block">Excused</span>
          <strong className="text-xl font-black text-slate-900">{excusedCount}</strong>
        </div>
        <div className="bg-white p-4 rounded-2xl border border-slate-200 text-center">
          <span className="text-xs text-red-600 font-bold block">Absent</span>
          <strong className="text-xl font-black text-slate-900">{absentCount}</strong>
        </div>
      </div>

      {/* Filters */}
      <div className="flex items-center gap-2">
        <select
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.target.value)}
          className="px-3 py-2 bg-white rounded-xl border border-slate-200 text-xs font-medium text-slate-700 focus:outline-none"
        >
          <option value="all">All Gathering Types</option>
          <option value="Bible Study">Bible Study</option>
          <option value="Event">Event</option>
          <option value="Evangelism">Evangelism</option>
          <option value="Committee">Committee</option>
          <option value="Other">Other</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-[10px] tracking-wider">
              <tr>
                <th className="px-5 py-3.5">Member</th>
                <th className="px-4 py-3.5">Gathering</th>
                <th className="px-4 py-3.5">Type</th>
                <th className="px-4 py-3.5">Date</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-5 py-3.5">Recorded By</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-8 text-center text-slate-400 text-xs">
                    No attendance records for this period.
                  </td>
                </tr>
              ) : (
                filtered.map((r) => (
                  <tr key={r.id} className="hover:bg-slate-50 transition">
                    <td className="px-5 py-3.5 font-bold text-slate-900">{r.memberName}</td>
                    <td className="px-4 py-3.5 text-slate-800">{r.eventName}</td>
                    <td className="px-4 py-3.5">
                      <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-semibold">
                        {r.eventType}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-500">{r.dateStr}</td>
                    <td className="px-4 py-3.5">
                      <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        r.status === 'present' ? 'bg-emerald-100 text-emerald-800' :
                        r.status === 'excused' ? 'bg-amber-100 text-amber-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {r.status.toUpperCase()}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-slate-500">{r.recordedBy}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Mark Member Attendance</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
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
                      {m.fullName} ({m.ministryLevel})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Gathering / Event Name *
                </label>
                <input
                  type="text"
                  required
                  value={formData.eventName}
                  onChange={(e) => setFormData({ ...formData, eventName: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Gathering Type
                  </label>
                  <select
                    value={formData.eventType}
                    onChange={(e) => setFormData({ ...formData, eventType: e.target.value as AttendanceType })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="Bible Study">Bible Study</option>
                    <option value="Event">Event</option>
                    <option value="Evangelism">Evangelism</option>
                    <option value="Committee">Committee</option>
                    <option value="Other">Other</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.dateStr}
                    onChange={(e) => setFormData({ ...formData, dateStr: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Attendance Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value as AttendanceStatus })}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                >
                  <option value="present">Present</option>
                  <option value="excused">Excused (Prior Notice)</option>
                  <option value="absent">Absent (Requires Follow-up)</option>
                </select>
              </div>

              {formData.status === 'excused' && (
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Excuse Reason
                  </label>
                  <input
                    type="text"
                    value={formData.excuseReason}
                    onChange={(e) => setFormData({ ...formData, excuseReason: e.target.value })}
                    placeholder="e.g. Travel, exam preparation, illness"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              )}

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
                  Record Entry
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
