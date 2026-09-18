import React, { useState, useEffect } from 'react';
import { CalendarCheck, Plus, CheckCircle2, Clock, AlertTriangle, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getCommitteeTasks, saveCommitteeTask } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { CommitteeTask, CommitteeTaskStatus } from '../types';

export const CommitteePage: React.FC = () => {
  const { currentUser } = useAuth();
  const [tasks, setTasks] = useState<CommitteeTask[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    title: '',
    department: 'Executive Committee',
    assignedToName: '',
    dueDate: new Date(Date.now() + 1000 * 60 * 60 * 24 * 7).toISOString().split('T')[0],
    priority: 'medium' as 'low' | 'medium' | 'high',
    deliverable: '',
    status: 'in_progress' as CommitteeTaskStatus
  });

  const isLeader = currentUser?.role === 'leader';
  const isCoordinator = currentUser?.role === 'committee_coordinator';

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    const data = await getCommitteeTasks();
    setTasks(data);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const newTask: CommitteeTask = {
      id: `task_${Date.now()}`,
      title: formData.title,
      department: formData.department,
      assignedToUid: currentUser.uid,
      assignedToName: formData.assignedToName || currentUser.displayName,
      dueDate: formData.dueDate,
      priority: formData.priority,
      deliverable: formData.deliverable,
      status: formData.status,
      createdAt: Date.now()
    };

    await saveCommitteeTask(newTask);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Committee Task Created',
      module: 'committee',
      resourceType: 'task',
      resourceId: newTask.id,
      details: `Created task "${newTask.title}" for ${newTask.department}`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadTasks();
  };

  const handleUpdateStatus = async (task: CommitteeTask, status: CommitteeTaskStatus) => {
    if (!currentUser) return;
    const updated = { ...task, status };
    await saveCommitteeTask(updated);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Committee Task Status Changed',
      module: 'committee',
      resourceType: 'task',
      resourceId: task.id,
      details: `Updated task "${task.title}" to ${status}`,
      result: 'success'
    });
    await loadTasks();
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Committee Coordination & Deliverables
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Coordinated by Yvette Neema • Executive assignments, deliverables, and operational accountability
          </p>
        </div>

        {(isLeader || isCoordinator) && (
          <button
            onClick={() => setIsModalOpen(true)}
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <Plus className="w-4 h-4" />
            <span>Assign Committee Task</span>
          </button>
        )}
      </div>

      {/* Tasks Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-[10px] tracking-wider">
              <tr>
                <th className="px-5 py-3.5">Task & Deliverable</th>
                <th className="px-4 py-3.5">Department</th>
                <th className="px-4 py-3.5">Assigned To</th>
                <th className="px-4 py-3.5">Due Date</th>
                <th className="px-4 py-3.5">Priority</th>
                <th className="px-4 py-3.5">Status</th>
                {(isLeader || isCoordinator) && <th className="px-5 py-3.5 text-right">Approval</th>}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {tasks.map((t) => (
                <tr key={t.id} className="hover:bg-slate-50 transition">
                  <td className="px-5 py-3.5">
                    <div className="font-bold text-slate-900">{t.title}</div>
                    <div className="text-[11px] text-slate-500">{t.deliverable}</div>
                  </td>
                  <td className="px-4 py-3.5 font-medium text-slate-700">{t.department}</td>
                  <td className="px-4 py-3.5 text-slate-700">{t.assignedToName}</td>
                  <td className="px-4 py-3.5 text-slate-500">{t.dueDate}</td>
                  <td className="px-4 py-3.5">
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                      t.priority === 'high' ? 'bg-red-100 text-red-800' : 'bg-slate-100 text-slate-700'
                    }`}>
                      {(t.priority || 'medium').toUpperCase()}
                    </span>
                  </td>
                  <td className="px-4 py-3.5">
                    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                      t.status === 'approved' ? 'bg-emerald-100 text-emerald-800' :
                      t.status === 'submitted' ? 'bg-blue-100 text-blue-800' :
                      t.status === 'in_progress' ? 'bg-amber-100 text-amber-800' :
                      'bg-slate-100 text-slate-700'
                    }`}>
                      {t.status.replace('_', ' ').toUpperCase()}
                    </span>
                  </td>
                  {(isLeader || isCoordinator) && (
                    <td className="px-5 py-3.5 text-right">
                      {t.status === 'submitted' && isLeader && (
                        <button
                          onClick={() => handleUpdateStatus(t, 'approved')}
                          className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-[10px] font-bold shadow-xs"
                        >
                          Approve
                        </button>
                      )}
                      {t.status === 'in_progress' && (
                        <button
                          onClick={() => handleUpdateStatus(t, 'submitted')}
                          className="px-2.5 py-1 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-[10px] font-bold shadow-xs"
                        >
                          Submit
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Assign Committee Task</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Task Title *
                </label>
                <input
                  type="text"
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Q3 Financial Audit Report Preparation"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

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
                  Assigned Member Name
                </label>
                <input
                  type="text"
                  required
                  value={formData.assignedToName}
                  onChange={(e) => setFormData({ ...formData, assignedToName: e.target.value })}
                  placeholder="e.g. Ebenezer Mugisha"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Due Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.dueDate}
                    onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Priority
                  </label>
                  <select
                    value={formData.priority}
                    onChange={(e) => setFormData({ ...formData, priority: e.target.value as any })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Expected Deliverable Description
                </label>
                <textarea
                  rows={2}
                  required
                  value={formData.deliverable}
                  onChange={(e) => setFormData({ ...formData, deliverable: e.target.value })}
                  placeholder="e.g. Consolidated spreadsheet and formal executive summary"
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
                  Assign Task
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
