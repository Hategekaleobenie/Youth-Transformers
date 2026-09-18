import React, { useState, useEffect } from 'react';
import { Megaphone, Plus, Calendar, AlertCircle, X, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getAnnouncements, saveAnnouncement } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { Announcement } from '../types';

export const AnnouncementsPage: React.FC = () => {
  const { currentUser, hasPermission } = useAuth();
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    title: '',
    content: '',
    targetAudience: 'All Youth Transformers',
    priority: 'normal' as 'normal' | 'important' | 'urgent',
    activeUntil: new Date(Date.now() + 1000 * 60 * 60 * 24 * 14).toISOString().split('T')[0]
  });

  const canPost = currentUser?.role === 'leader' || hasPermission('announcements', 'create');

  useEffect(() => {
    loadAnnouncements();
  }, []);

  const loadAnnouncements = async () => {
    const data = await getAnnouncements();
    setAnnouncements(data);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const newAnn: Announcement = {
      id: `ann_${Date.now()}`,
      title: formData.title,
      content: formData.content,
      authorUid: currentUser.uid,
      authorName: currentUser.displayName,
      targetAudience: formData.targetAudience,
      priority: formData.priority,
      activeUntil: formData.activeUntil,
      createdAt: Date.now()
    };

    await saveAnnouncement(newAnn);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Ministry Bulletin Announcement Published',
      module: 'announcements',
      resourceType: 'announcement',
      resourceId: newAnn.id,
      details: `Published "${newAnn.title}" (${newAnn.priority})`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadAnnouncements();
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Ministry Bulletins & Announcements
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Official communications, ministry reminders, and spiritual directives
          </p>
        </div>

        {canPost && (
          <button
            onClick={() => setIsModalOpen(true)}
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            <span>Post Bulletin</span>
          </button>
        )}
      </div>

      <div className="space-y-4">
        {announcements.map((a) => (
          <div
            key={a.id}
            className={`p-6 rounded-2xl border shadow-xs transition ${
              a.priority === 'urgent'
                ? 'bg-red-50/50 border-red-200'
                : a.priority === 'important'
                ? 'bg-amber-50/50 border-amber-200'
                : 'bg-white border-slate-200'
            }`}
          >
            <div className="flex items-start justify-between gap-2 mb-2">
              <div className="flex items-center gap-2">
                <span className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full uppercase tracking-wider ${
                  a.priority === 'urgent' ? 'bg-red-100 text-red-800' :
                  a.priority === 'important' ? 'bg-amber-100 text-amber-800' :
                  'bg-emerald-50 text-emerald-800'
                }`}>
                  {a.priority} Priority
                </span>
                <span className="text-xs text-slate-400">
                  Target: <strong>{a.targetAudience}</strong>
                </span>
              </div>
              <span className="text-xs text-slate-400">
                {new Date(a.createdAt).toLocaleDateString()}
              </span>
            </div>

            <h3 className="font-bold text-base text-slate-900 mb-2">{a.title}</h3>
            <p className="text-xs text-slate-700 leading-relaxed mb-4 whitespace-pre-line">{a.content}</p>

            <div className="pt-3 border-t border-slate-200/60 flex items-center justify-between text-xs text-slate-500">
              <span>Published by: <strong>{a.authorName}</strong></span>
              <span>Valid through: {a.activeUntil}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Post Ministry Bulletin</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Bulletin Title *
                </label>
                <input
                  type="text"
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Mandatory Leaders Consecration Fast & Vigil"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Priority Level
                  </label>
                  <select
                    value={formData.priority}
                    onChange={(e) => setFormData({ ...formData, priority: e.target.value as any })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="normal">Normal Bulletin</option>
                    <option value="important">Important Notice</option>
                    <option value="urgent">Urgent Directive</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Target Audience
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.targetAudience}
                    onChange={(e) => setFormData({ ...formData, targetAudience: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Announcement Body *
                </label>
                <textarea
                  rows={4}
                  required
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="Type official communication details..."
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
                  Publish Announcement
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
