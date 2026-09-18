import React, { useState, useEffect } from 'react';
import { BookOpen, Calendar, Clock, MapPin, User, Plus, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getBibleStudies, saveBibleStudy } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { BibleStudy } from '../types';

export const BibleStudyPage: React.FC = () => {
  const { currentUser, hasPermission } = useAuth();
  const [studies, setStudies] = useState<BibleStudy[]>([]);
  const [viewFilter, setViewFilter] = useState<'all' | 'weekly' | 'monthly'>('all');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    scriptureReferences: '',
    dateStr: new Date().toISOString().split('T')[0],
    timeStr: '17:30 - 19:00',
    location: 'Youth Main Sanctuary',
    preacher: currentUser?.displayName || '',
    host: '',
    curriculum: 'Spiritual Consecration & Foundations',
    expectedAttendance: 40,
    recurrence: 'weekly' as 'once' | 'daily' | 'weekly' | 'monthly',
    notes: ''
  });

  const canManage = currentUser?.role === 'leader' || hasPermission('bible_study', 'manage') || hasPermission('bible_study', 'create');

  useEffect(() => {
    loadStudies();
  }, []);

  const loadStudies = async () => {
    const data = await getBibleStudies();
    setStudies(data);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const newStudy: BibleStudy = {
      id: `bs_${Date.now()}`,
      ...formData,
      createdAt: Date.now()
    };

    await saveBibleStudy(newStudy);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Bible Study Session Scheduled',
      module: 'bible_study',
      resourceType: 'bible_study',
      resourceId: newStudy.id,
      details: `Scheduled "${newStudy.title}" (${newStudy.scriptureReferences})`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadStudies();
  };

  const filtered = studies.filter(s => {
    if (viewFilter === 'all') return true;
    return s.recurrence === viewFilter;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Bible Study & Scripture Curriculum
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Department led by Liona Akaliza • Deepening scripture foundations and discipleship doctrine
          </p>
        </div>

        {canManage && (
          <button
            onClick={() => setIsModalOpen(true)}
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            <span>Schedule Bible Study</span>
          </button>
        )}
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={() => setViewFilter('all')}
          className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition ${
            viewFilter === 'all' ? 'bg-ministry-deepGreen text-white' : 'bg-white text-slate-600 border border-slate-200'
          }`}
        >
          All Sessions ({studies.length})
        </button>
        <button
          onClick={() => setViewFilter('weekly')}
          className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition ${
            viewFilter === 'weekly' ? 'bg-ministry-deepGreen text-white' : 'bg-white text-slate-600 border border-slate-200'
          }`}
        >
          Weekly Recurring
        </button>
        <button
          onClick={() => setViewFilter('monthly')}
          className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition ${
            viewFilter === 'monthly' ? 'bg-ministry-deepGreen text-white' : 'bg-white text-slate-600 border border-slate-200'
          }`}
        >
          Monthly Intensives
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filtered.map((s) => (
          <div key={s.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
            <div>
              <div className="flex items-start justify-between gap-2 mb-2">
                <span className="text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-900 border border-amber-200">
                  {s.curriculum}
                </span>
                <span className="text-[10px] text-slate-400 capitalize font-medium">
                  {s.recurrence}
                </span>
              </div>

              <h3 className="font-bold text-sm text-slate-900 mb-1">{s.title}</h3>
              <p className="text-xs font-semibold text-ministry-emerald mb-3">
                {s.scriptureReferences}
              </p>

              <div className="space-y-1.5 text-xs text-slate-600 mb-4">
                <div className="flex items-center gap-2">
                  <Calendar className="w-3.5 h-3.5 text-slate-400" />
                  <span>{s.dateStr}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Clock className="w-3.5 h-3.5 text-slate-400" />
                  <span>{s.timeStr}</span>
                </div>
                <div className="flex items-center gap-2">
                  <MapPin className="w-3.5 h-3.5 text-slate-400" />
                  <span>{s.location}</span>
                </div>
                <div className="flex items-center gap-2">
                  <User className="w-3.5 h-3.5 text-slate-400" />
                  <span>Teacher: <strong>{s.preacher}</strong> {s.host && `• Host: ${s.host}`}</span>
                </div>
              </div>

              {s.notes && (
                <div className="p-3 bg-slate-50 rounded-xl text-slate-600 text-xs italic border border-slate-100">
                  "{s.notes}"
                </div>
              )}
            </div>

            <div className="pt-4 mt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span>Expected: <strong>{s.expectedAttendance} youth</strong></span>
              {s.actualAttendance !== undefined && (
                <span className="text-emerald-700 font-semibold">
                  Attended: {s.actualAttendance}
                </span>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Schedule Bible Study Gathering</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Topic / Lesson Title *
                </label>
                <input
                  type="text"
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Walking in Holiness: 1 Peter 1"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Scripture References *
                </label>
                <input
                  type="text"
                  required
                  value={formData.scriptureReferences}
                  onChange={(e) => setFormData({ ...formData, scriptureReferences: e.target.value })}
                  placeholder="e.g. Romans 12:1-2; Ephesians 4:22-24"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Date *
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.dateStr}
                    onChange={(e) => setFormData({ ...formData, dateStr: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Time *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.timeStr}
                    onChange={(e) => setFormData({ ...formData, timeStr: e.target.value })}
                    placeholder="17:00 - 19:00"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Preacher / Teacher
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.preacher}
                    onChange={(e) => setFormData({ ...formData, preacher: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Location
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.location}
                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Recurrence
                  </label>
                  <select
                    value={formData.recurrence}
                    onChange={(e) => setFormData({ ...formData, recurrence: e.target.value as any })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="once">One-time Event</option>
                    <option value="daily">Daily</option>
                    <option value="weekly">Weekly Gathering</option>
                    <option value="monthly">Monthly Intensive</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Expected Attendance
                  </label>
                  <input
                    type="number"
                    value={formData.expectedAttendance}
                    onChange={(e) => setFormData({ ...formData, expectedAttendance: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
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
                  Schedule Session
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
