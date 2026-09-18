import React, { useState, useEffect } from 'react';
import { FolderGit2, Wrench, Plus, CheckCircle2, Clock, X, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getProjects, saveProject, getEquipment, saveEquipment } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { Project, EquipmentItem, ProjectStatus, EquipmentCondition, EquipmentStatus } from '../types';

export const ProjectsEquipmentPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [activeTab, setActiveTab] = useState<'projects' | 'equipment'>('projects');
  const [projects, setProjects] = useState<Project[]>([]);
  const [equipment, setEquipment] = useState<EquipmentItem[]>([]);
  const [isProjectModalOpen, setIsProjectModalOpen] = useState(false);
  const [isEquipmentModalOpen, setIsEquipmentModalOpen] = useState(false);

  // Project form state
  const [projectForm, setProjectForm] = useState({
    title: '',
    objectives: '',
    budget: '',
    currentSpending: '0',
    assignedTeam: '',
    startDate: new Date().toISOString().split('T')[0],
    endDate: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30).toISOString().split('T')[0],
    status: 'active' as ProjectStatus,
    tasksTotal: 4,
    tasksCompleted: 1
  });

  // Equipment form state
  const [equipmentForm, setEquipmentForm] = useState({
    name: '',
    assetTag: '',
    category: 'Audio/Visual',
    condition: 'good' as EquipmentCondition,
    checkoutStatus: 'available' as EquipmentStatus,
    assignedTo: 'Media Department',
    location: 'Main Sound Booth',
    notes: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [p, e] = await Promise.all([getProjects(), getEquipment()]);
    setProjects(p);
    setEquipment(e);
  };

  const handleSaveProject = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const b = parseFloat(projectForm.budget) || 0;
    const s = parseFloat(projectForm.currentSpending) || 0;
    const progress = projectForm.tasksTotal > 0
      ? Math.round((projectForm.tasksCompleted / projectForm.tasksTotal) * 100)
      : 0;

    const newProj: Project = {
      id: `proj_${Date.now()}`,
      name: projectForm.title,
      title: projectForm.title,
      description: projectForm.objectives,
      objectives: projectForm.objectives,
      leader: currentUser.displayName,
      budget: b,
      currentSpending: s,
      team: projectForm.assignedTeam.split(',').map(name => name.trim()),
      assignedTeam: projectForm.assignedTeam.split(',').map(name => name.trim()),
      startDate: projectForm.startDate,
      endDate: projectForm.endDate,
      status: projectForm.status,
      tasksCount: projectForm.tasksTotal,
      completedTasksCount: projectForm.tasksCompleted,
      tasksTotal: projectForm.tasksTotal,
      tasksCompleted: projectForm.tasksCompleted,
      progressPercent: progress,
      progressPercentage: progress,
      createdAt: Date.now(),
      updatedAt: Date.now()
    };

    await saveProject(newProj);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Ministry Project Created',
      module: 'projects',
      resourceType: 'project',
      resourceId: newProj.id,
      details: `Launched project "${newProj.title}" with budget ${newProj.budget.toLocaleString()} RWF`,
      result: 'success'
    });

    setIsProjectModalOpen(false);
    await loadData();
  };

  const handleSaveEquipment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const newEquip: EquipmentItem = {
      id: `eq_${Date.now()}`,
      name: equipmentForm.name,
      assetTag: equipmentForm.assetTag,
      category: equipmentForm.category,
      quantity: 1,
      condition: equipmentForm.condition,
      location: equipmentForm.location,
      responsiblePerson: equipmentForm.assignedTo || currentUser.displayName,
      assignedTo: equipmentForm.assignedTo || currentUser.displayName,
      status: 'available',
      checkoutStatus: equipmentForm.checkoutStatus,
      purchaseDate: new Date().toISOString().split('T')[0],
      lastInspectedDate: new Date().toISOString().split('T')[0],
      notes: equipmentForm.notes,
      createdAt: Date.now()
    };

    await saveEquipment(newEquip);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Equipment Asset Registered',
      module: 'equipment',
      resourceType: 'equipment',
      resourceId: newEquip.id,
      details: `Registered asset ${newEquip.name} [${newEquip.assetTag}]`,
      result: 'success'
    });

    setIsEquipmentModalOpen(false);
    await loadData();
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Projects & Physical Asset Stewardship
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Department managed by Rene Cyubahiro • Infrastructure initiatives, media gear, and capital assets
          </p>
        </div>

        <div className="flex items-center gap-2">
          {activeTab === 'projects' ? (
            <button
              onClick={() => setIsProjectModalOpen(true)}
              className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
            >
              <Plus className="w-4 h-4" />
              <span>Create Project</span>
            </button>
          ) : (
            <button
              onClick={() => setIsEquipmentModalOpen(true)}
              className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
            >
              <Plus className="w-4 h-4" />
              <span>Register Asset</span>
            </button>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center bg-slate-100 p-1 rounded-2xl w-fit">
        <button
          onClick={() => setActiveTab('projects')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${
            activeTab === 'projects'
              ? 'bg-white text-ministry-deepGreen shadow-xs'
              : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <FolderGit2 className="w-4 h-4" />
          <span>Capital Projects ({projects.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('equipment')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${
            activeTab === 'equipment'
              ? 'bg-white text-ministry-deepGreen shadow-xs'
              : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <Wrench className="w-4 h-4" />
          <span>Equipment & Assets ({equipment.length})</span>
        </button>
      </div>

      {/* Content */}
      {activeTab === 'projects' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {projects.map((p) => (
            <div key={p.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <span className={`text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full ${
                    p.status === 'active' ? 'bg-emerald-100 text-emerald-800' :
                    p.status === 'completed' ? 'bg-blue-100 text-blue-800' :
                    'bg-slate-100 text-slate-700'
                  }`}>
                    {p.status}
                  </span>
                  <span className="text-xs font-black text-slate-800">
                    {p.progressPercentage}% Done
                  </span>
                </div>

                <h3 className="font-bold text-sm text-slate-900 mb-1">{p.title}</h3>
                <p className="text-xs text-slate-500 mb-4">{p.objectives}</p>

                {/* Progress bar */}
                <div className="w-full bg-slate-100 rounded-full h-2 mb-4 overflow-hidden">
                  <div
                    className="bg-ministry-emerald h-2 rounded-full transition-all duration-500"
                    style={{ width: `${p.progressPercentage}%` }}
                  />
                </div>

                <div className="grid grid-cols-2 gap-3 text-xs bg-slate-50 p-3 rounded-xl mb-3">
                  <div>
                    <span className="text-slate-400 block text-[10px]">Budget Allocated</span>
                    <strong className="text-slate-800">{p.budget.toLocaleString()} RWF</strong>
                  </div>
                  <div>
                    <span className="text-slate-400 block text-[10px]">Current Spend</span>
                    <strong className="text-slate-800">{(p.currentSpending || 0).toLocaleString()} RWF</strong>
                  </div>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
                <span>Tasks: {p.tasksCompleted} of {p.tasksTotal} completed</span>
                <span>Due: {p.endDate}</span>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-600">
              <thead className="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-[10px] tracking-wider">
                <tr>
                  <th className="px-5 py-3.5">Asset / Equipment</th>
                  <th className="px-4 py-3.5">Asset Tag</th>
                  <th className="px-4 py-3.5">Condition</th>
                  <th className="px-4 py-3.5">Status</th>
                  <th className="px-4 py-3.5">Location</th>
                  <th className="px-5 py-3.5 text-right">Custodian</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {equipment.map((e) => (
                  <tr key={e.id} className="hover:bg-slate-50 transition">
                    <td className="px-5 py-3.5 font-bold text-slate-900">{e.name}</td>
                    <td className="px-4 py-3.5 font-mono text-[11px] text-slate-500">{e.assetTag}</td>
                    <td className="px-4 py-3.5">
                      <span className="px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 text-[10px] font-bold">
                        {e.condition.toUpperCase()}
                      </span>
                    </td>
                    <td className="px-4 py-3.5">
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                        (e.checkoutStatus || e.status) === 'available' ? 'bg-blue-50 text-blue-800' : 'bg-amber-50 text-amber-800'
                      }`}>
                        {(e.checkoutStatus || e.status).replace('_', ' ').toUpperCase()}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-600">{e.location}</td>
                    <td className="px-5 py-3.5 text-right font-medium text-slate-700">{e.assignedTo}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Project Modal */}
      {isProjectModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Launch Ministry Project</h3>
              <button onClick={() => setIsProjectModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveProject} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Project Title *
                </label>
                <input
                  type="text"
                  required
                  value={projectForm.title}
                  onChange={(e) => setProjectForm({ ...projectForm, title: e.target.value })}
                  placeholder="e.g. Media Ministry Streaming Rig Upgrade"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Objectives & Purpose
                </label>
                <textarea
                  rows={2}
                  required
                  value={projectForm.objectives}
                  onChange={(e) => setProjectForm({ ...projectForm, objectives: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Budget (RWF) *
                  </label>
                  <input
                    type="number"
                    required
                    value={projectForm.budget}
                    onChange={(e) => setProjectForm({ ...projectForm, budget: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Current Spending
                  </label>
                  <input
                    type="number"
                    value={projectForm.currentSpending}
                    onChange={(e) => setProjectForm({ ...projectForm, currentSpending: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Tasks Total
                  </label>
                  <input
                    type="number"
                    value={projectForm.tasksTotal}
                    onChange={(e) => setProjectForm({ ...projectForm, tasksTotal: parseInt(e.target.value) || 1 })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Tasks Completed
                  </label>
                  <input
                    type="number"
                    value={projectForm.tasksCompleted}
                    onChange={(e) => setProjectForm({ ...projectForm, tasksCompleted: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsProjectModalOpen(false)}
                  className="px-4 py-2 font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs"
                >
                  Save Project
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Equipment Modal */}
      {isEquipmentModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Register Equipment Asset</h3>
              <button onClick={() => setIsEquipmentModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEquipment} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Item Name *
                </label>
                <input
                  type="text"
                  required
                  value={equipmentForm.name}
                  onChange={(e) => setEquipmentForm({ ...equipmentForm, name: e.target.value })}
                  placeholder="e.g. Shure Wireless Microphone Kit"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Asset Tag / Serial
                  </label>
                  <input
                    type="text"
                    required
                    value={equipmentForm.assetTag}
                    onChange={(e) => setEquipmentForm({ ...equipmentForm, assetTag: e.target.value })}
                    placeholder="YT-AV-001"
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
                    value={equipmentForm.location}
                    onChange={(e) => setEquipmentForm({ ...equipmentForm, location: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsEquipmentModalOpen(false)}
                  className="px-4 py-2 font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs"
                >
                  Register Item
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
