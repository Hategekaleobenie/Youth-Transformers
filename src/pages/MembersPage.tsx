import React, { useState, useEffect } from 'react';
import {
  Search,
  UserPlus,
  Filter,
  Phone,
  Mail,
  MapPin,
  HeartHandshake,
  CheckCircle2,
  Clock,
  Edit2,
  X,
  User
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getMembers, saveMember } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { Member, MemberStatus, MinistryLevel } from '../types';

export const MembersPage: React.FC = () => {
  const { currentUser, hasPermission } = useAuth();
  const [members, setMembers] = useState<Member[]>([]);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [levelFilter, setLevelFilter] = useState<string>('all');
  const [loading, setLoading] = useState(true);

  // Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingMember, setEditingMember] = useState<Member | null>(null);
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    email: '',
    dateJoined: new Date().toISOString().split('T')[0],
    currentResidence: '',
    permanentResidence: '',
    employmentStatus: 'Prefer not to say',
    educationStatus: 'Prefer not to say',
    familyInfo: 'Prefer not to say',
    ministryLevel: 'Level 1' as MinistryLevel,
    assignedLeader: '',
    status: 'active' as MemberStatus,
    notes: ''
  });

  const canEdit = currentUser?.role === 'leader' || hasPermission('members', 'edit') || hasPermission('members', 'create');

  useEffect(() => {
    loadMembers();
  }, []);

  const loadMembers = async () => {
    setLoading(true);
    const data = await getMembers();
    setMembers(data);
    setLoading(false);
  };

  const handleOpenAdd = () => {
    setEditingMember(null);
    setFormData({
      fullName: '',
      phone: '',
      email: '',
      dateJoined: new Date().toISOString().split('T')[0],
      currentResidence: '',
      permanentResidence: '',
      employmentStatus: 'Prefer not to say',
      educationStatus: 'Prefer not to say',
      familyInfo: 'Prefer not to say',
      ministryLevel: 'Level 1',
      assignedLeader: currentUser?.displayName || '',
      status: 'active',
      notes: ''
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (m: Member) => {
    setEditingMember(m);
    setFormData({
      fullName: m.fullName,
      phone: m.phone,
      email: m.email || '',
      dateJoined: m.dateJoined,
      currentResidence: m.currentResidence,
      permanentResidence: m.permanentResidence,
      employmentStatus: m.employmentStatus,
      educationStatus: m.educationStatus,
      familyInfo: m.familyInfo,
      ministryLevel: m.ministryLevel,
      assignedLeader: m.assignedLeader,
      status: m.status,
      notes: m.notes || ''
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    if (editingMember) {
      const updated: Member = {
        ...editingMember,
        ...formData,
        updatedAt: Date.now()
      };
      await saveMember(updated);
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Member Profile Updated',
        module: 'members',
        resourceType: 'member',
        resourceId: updated.id,
        details: `Updated info for ${updated.fullName}`,
        result: 'success'
      });
    } else {
      const newId = `mem_${Date.now()}`;
      const newMember: Member = {
        id: newId,
        ...formData,
        attendanceCount: 1,
        bibleStudyParticipation: 1,
        evangelismParticipation: 0,
        createdAt: Date.now(),
        updatedAt: Date.now()
      };
      await saveMember(newMember);
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'New Member Registered',
        module: 'members',
        resourceType: 'member',
        resourceId: newId,
        details: `Registered ${newMember.fullName} into ${newMember.ministryLevel}`,
        result: 'success'
      });
    }

    setIsModalOpen(false);
    await loadMembers();
  };

  const filtered = members.filter((m) => {
    const matchesSearch =
      m.fullName.toLowerCase().includes(search.toLowerCase()) ||
      m.phone.includes(search) ||
      m.currentResidence.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === 'all' || m.status === statusFilter;
    const matchesLevel = levelFilter === 'all' || m.ministryLevel === levelFilter;
    return matchesSearch && matchesStatus && matchesLevel;
  });

  const getStatusBadge = (s: MemberStatus) => {
    switch (s) {
      case 'active': return 'bg-emerald-100 text-emerald-800 border-emerald-200';
      case 'follow_up_required': return 'bg-amber-100 text-amber-800 border-amber-200';
      case 'inactive': return 'bg-slate-100 text-slate-700 border-slate-200';
      case 'deactivated': return 'bg-red-100 text-red-800 border-red-200';
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Members & Discipleship Directory
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Official congregation records, ministry progression levels, and pastoral oversight
          </p>
        </div>

        {canEdit && (
          <button
            onClick={handleOpenAdd}
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
          >
            <UserPlus className="w-4 h-4" />
            <span>Register New Member</span>
          </button>
        )}
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by full name, phone number, residence..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
          >
            <option value="all">All Statuses</option>
            <option value="active">Active</option>
            <option value="follow_up_required">Follow-up Required</option>
            <option value="inactive">Inactive</option>
            <option value="deactivated">Deactivated</option>
          </select>

          <select
            value={levelFilter}
            onChange={(e) => setLevelFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
          >
            <option value="all">All Ministry Levels</option>
            <option value="Level 1">Level 1</option>
            <option value="Level 2">Level 2</option>
            <option value="Level 3">Level 3</option>
            <option value="Youth Leader">Youth Leader</option>
            <option value="General Member">General Member</option>
          </select>
        </div>
      </div>

      {/* Member Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filtered.length === 0 ? (
          <div className="col-span-full py-12 text-center bg-white rounded-2xl border border-slate-200 text-xs text-slate-400">
            No members found matching criteria.
          </div>
        ) : (
          filtered.map((m) => (
            <div
              key={m.id}
              className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs hover:shadow-md transition flex flex-col justify-between"
            >
              <div>
                <div className="flex items-start justify-between gap-2 mb-3">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold text-sm">
                      {m.fullName.charAt(0)}
                    </div>
                    <div>
                      <h3 className="font-bold text-sm text-slate-900 leading-tight">
                        {m.fullName}
                      </h3>
                      <span className="text-[11px] font-semibold text-ministry-emerald">
                        {m.ministryLevel}
                      </span>
                    </div>
                  </div>

                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${getStatusBadge(m.status)}`}>
                    {m.status.replace('_', ' ').toUpperCase()}
                  </span>
                </div>

                <div className="space-y-1.5 text-xs text-slate-600 mb-4">
                  <div className="flex items-center gap-2">
                    <Phone className="w-3.5 h-3.5 text-slate-400" />
                    <span>{m.phone}</span>
                  </div>
                  {m.email && (
                    <div className="flex items-center gap-2">
                      <Mail className="w-3.5 h-3.5 text-slate-400" />
                      <span className="truncate">{m.email}</span>
                    </div>
                  )}
                  <div className="flex items-center gap-2">
                    <MapPin className="w-3.5 h-3.5 text-slate-400" />
                    <span className="truncate">{m.currentResidence}</span>
                  </div>
                </div>

                <div className="p-3 bg-slate-50 rounded-xl border border-slate-100 grid grid-cols-3 text-center text-[10px] mb-4">
                  <div>
                    <span className="text-slate-400 block font-medium">Fellowships</span>
                    <strong className="text-xs text-slate-800">{m.attendanceCount}</strong>
                  </div>
                  <div className="border-x border-slate-200">
                    <span className="text-slate-400 block font-medium">Bible Study</span>
                    <strong className="text-xs text-slate-800">{m.bibleStudyParticipation}</strong>
                  </div>
                  <div>
                    <span className="text-slate-400 block font-medium">Evangelism</span>
                    <strong className="text-xs text-slate-800">{m.evangelismParticipation}</strong>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-100 text-xs">
                <span className="text-[11px] text-slate-400">
                  Joined: {m.dateJoined}
                </span>

                {canEdit && (
                  <button
                    onClick={() => handleOpenEdit(m)}
                    className="p-1.5 text-slate-500 hover:text-ministry-deepGreen hover:bg-slate-100 rounded-lg transition"
                    title="Edit Member Details"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Add / Edit Member Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-xl max-h-[90vh] overflow-y-auto border border-slate-200">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white sticky top-0 z-10">
              <h3 className="font-bold text-sm">
                {editingMember ? `Edit Record: ${editingMember.fullName}` : 'Register New Congregation Member'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Full Name *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.fullName}
                    onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Phone Number *
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    placeholder="+250 788 000 000"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Email Address
                  </label>
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Date Joined
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.dateJoined}
                    onChange={(e) => setFormData({ ...formData, dateJoined: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Current Residence
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.currentResidence}
                    onChange={(e) => setFormData({ ...formData, currentResidence: e.target.value })}
                    placeholder="e.g. Kigali - Kimironko"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Permanent Residence
                  </label>
                  <input
                    type="text"
                    value={formData.permanentResidence}
                    onChange={(e) => setFormData({ ...formData, permanentResidence: e.target.value })}
                    placeholder="Home district / province"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              {/* Sensitive Demographics with "Prefer not to say" support */}
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 space-y-3">
                <span className="font-bold text-slate-800 text-[11px] block uppercase tracking-wider">
                  Demographics & Pastoral Care Information
                </span>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5">
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 mb-1">
                      Employment
                    </label>
                    <input
                      type="text"
                      value={formData.employmentStatus}
                      onChange={(e) => setFormData({ ...formData, employmentStatus: e.target.value })}
                      className="w-full px-2.5 py-1.5 bg-white rounded-lg border border-slate-300 focus:outline-none focus:ring-1 focus:ring-ministry-emerald text-xs"
                    />
                  </div>

                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 mb-1">
                      Education
                    </label>
                    <input
                      type="text"
                      value={formData.educationStatus}
                      onChange={(e) => setFormData({ ...formData, educationStatus: e.target.value })}
                      className="w-full px-2.5 py-1.5 bg-white rounded-lg border border-slate-300 focus:outline-none focus:ring-1 focus:ring-ministry-emerald text-xs"
                    />
                  </div>

                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 mb-1">
                      Family Info
                    </label>
                    <input
                      type="text"
                      value={formData.familyInfo}
                      onChange={(e) => setFormData({ ...formData, familyInfo: e.target.value })}
                      className="w-full px-2.5 py-1.5 bg-white rounded-lg border border-slate-300 focus:outline-none focus:ring-1 focus:ring-ministry-emerald text-xs"
                    />
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Ministry Level
                  </label>
                  <select
                    value={formData.ministryLevel}
                    onChange={(e) => setFormData({ ...formData, ministryLevel: e.target.value as MinistryLevel })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="Level 1">Level 1</option>
                    <option value="Level 2">Level 2</option>
                    <option value="Level 3">Level 3</option>
                    <option value="Youth Leader">Youth Leader</option>
                    <option value="General Member">General Member</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Assigned Leader
                  </label>
                  <input
                    type="text"
                    value={formData.assignedLeader}
                    onChange={(e) => setFormData({ ...formData, assignedLeader: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Status
                  </label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value as MemberStatus })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="active">Active</option>
                    <option value="follow_up_required">Follow-up Required</option>
                    <option value="inactive">Inactive</option>
                    <option value="deactivated">Deactivated</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Pastoral Notes
                </label>
                <textarea
                  rows={2}
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  placeholder="Notes for prayer and discipleship mentorship..."
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
                  {editingMember ? 'Save Member' : 'Register Member'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
