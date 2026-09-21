import React, { useState, useEffect } from 'react';
import {
  Users,
  Search,
  Filter,
  UserPlus,
  Shield,
  Edit2,
  Lock,
  UserX,
  UserCheck,
  Check,
  X,
  AlertTriangle,
  KeyRound,
  CheckCircle2
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getUsers, saveUser, updateUserStatus } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { UserProfile, MinistryRole, AccountStatus } from '../types';

export const UserManagementPage: React.FC = () => {
  const { currentUser, sendResetEmail } = useAuth();
  const [users, setUsers] = useState<UserProfile[]>([]);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState<string>('all');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [loading, setLoading] = useState(true);
  const [resetFeedback, setResetFeedback] = useState<{ email: string; message: string } | null>(null);

  // Edit / Add Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserProfile | null>(null);
  const [formData, setFormData] = useState({
    firebaseUid: '',
    displayName: '',
    email: '',
    phone: '',
    role: 'member' as MinistryRole,
    department: 'General Youth Ministry',
    status: 'active' as AccountStatus,
    assignedPermissions: [] as string[]
  });

  // Deactivate confirmation modal
  const [confirmDeactivateUser, setConfirmDeactivateUser] = useState<UserProfile | null>(null);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    setLoading(true);
    const data = await getUsers();
    setUsers(data);
    setLoading(false);
  };

  const handleOpenAdd = () => {
    setEditingUser(null);
    setFormData({
      firebaseUid: '',
      displayName: '',
      email: '',
      phone: '',
      role: 'member',
      department: 'Youth Ministry',
      status: 'active',
      assignedPermissions: []
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (user: UserProfile) => {
    setEditingUser(user);
    setFormData({
      firebaseUid: user.uid,
      displayName: user.displayName,
      email: user.email,
      phone: user.phone || '',
      role: user.role,
      department: user.department,
      status: user.status,
      assignedPermissions: user.assignedPermissions || []
    });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    if (editingUser) {
      // Update
      const updated: UserProfile = {
        ...editingUser,
        ...formData
      };
      await saveUser(updated);
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'User Profile Updated',
        module: 'users',
        resourceType: 'user',
        resourceId: updated.uid,
        details: `Updated ${updated.displayName} (${updated.role})`,
        result: 'success'
      });
    } else {
      // A Firestore ministry profile must use the real Firebase Authentication UID.
      // Never invent a local UID here: Firestore rules bind access to request.auth.uid.
      const newUid = formData.firebaseUid.trim();
      if (!newUid) {
        throw new Error('Enter the real Firebase Authentication UID for this account.');
      }

      const newUser: UserProfile = {
        uid: newUid,
        displayName: formData.displayName,
        email: formData.email,
        phone: formData.phone,
        role: formData.role,
        department: formData.department,
        status: formData.status,
        assignedPermissions: formData.assignedPermissions,
        createdAt: Date.now(),
        lastLoginAt: Date.now(),
        mustChangePassword: true
      };
      await saveUser(newUser);
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'New User Created',
        module: 'users',
        resourceType: 'user',
        resourceId: newUid,
        details: `Created user account for ${newUser.displayName} as ${newUser.role}`,
        result: 'success'
      });
    }

    setIsModalOpen(false);
    await loadUsers();
  };

  const handleToggleStatus = async (user: UserProfile) => {
    if (!currentUser) return;
    const newStatus: AccountStatus = user.status === 'active' ? 'disabled' : 'active';

    await updateUserStatus(user.uid, newStatus);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: newStatus === 'active' ? 'User Activated' : 'User Deactivated',
      module: 'users',
      resourceType: 'user',
      resourceId: user.uid,
      details: `${user.displayName} status changed to ${newStatus}`,
      result: 'success'
    });

    setConfirmDeactivateUser(null);
    await loadUsers();
  };

  const handleInitiatePasswordReset = async (user: UserProfile) => {
    if (!currentUser) return;
    const ok = await sendResetEmail(user.email);
    if (ok) {
      setResetFeedback({
        email: user.email,
        message: `Password reset email sent to ${user.email}.`
      });
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Password Reset Initiated',
        module: 'users',
        resourceType: 'user',
        resourceId: user.uid,
        details: `Administrator initiated password reset email for ${user.displayName} (${user.email})`,
        result: 'success'
      });
      setTimeout(() => setResetFeedback(null), 5000);
    }
  };

  const filteredUsers = users.filter((u) => {
    const matchesSearch =
      u.displayName.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase()) ||
      u.department.toLowerCase().includes(search.toLowerCase());
    const matchesRole = roleFilter === 'all' || u.role === roleFilter;
    const matchesStatus = statusFilter === 'all' || u.status === statusFilter;
    return matchesSearch && matchesRole && matchesStatus;
  });

  const allRoles: MinistryRole[] = [
    'leader',
    'committee_coordinator',
    'level1_leader',
    'social_media',
    'member_care',
    'bible_study',
    'accountant',
    'projects_manager',
    'member'
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            User Administration & RBAC
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Manage authenticated accounts, departmental roles, and security permissions
          </p>
        </div>

        <button
          onClick={handleOpenAdd}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <UserPlus className="w-4 h-4" />
          <span>Add Ministry User</span>
        </button>
      </div>

      {/* Filters & Search */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name, email, or department..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
          >
            <option value="all">All Roles</option>
            {allRoles.map((r) => (
              <option key={r} value={r}>
                {r.replace('_', ' ').toUpperCase()}
              </option>
            ))}
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
          >
            <option value="all">All Statuses</option>
            <option value="active">Active</option>
            <option value="disabled">Disabled</option>
          </select>
        </div>
      </div>

      {/* Reset Feedback Notification */}
      {resetFeedback && (
        <div className="p-3.5 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-center justify-between gap-2 text-xs text-emerald-800">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
            <span>{resetFeedback.message}</span>
          </div>
          <button onClick={() => setResetFeedback(null)} className="text-emerald-700 hover:text-emerald-950 font-bold text-xs">
            Dismiss
          </button>
        </div>
      )}

      {/* Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-[10px] tracking-wider">
              <tr>
                <th className="px-5 py-3.5">User</th>
                <th className="px-4 py-3.5">Role</th>
                <th className="px-4 py-3.5">Department</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5">Last Login</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-8 text-center text-slate-400 text-xs">
                    No users matching criteria.
                  </td>
                </tr>
              ) : (
                filteredUsers.map((u) => (
                  <tr key={u.uid} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5">
                      <div className="font-bold text-slate-900">{u.displayName}</div>
                      <div className="text-[11px] text-slate-400">{u.email}</div>
                    </td>
                    <td className="px-4 py-3.5">
                      <span className={`inline-block px-2.5 py-1 rounded-full text-[10px] font-bold border ${
                        u.role === 'leader' ? 'bg-amber-50 text-amber-900 border-amber-200' :
                        u.role === 'committee_coordinator' ? 'bg-emerald-50 text-emerald-900 border-emerald-200' :
                        'bg-slate-100 text-slate-800 border-slate-200'
                      }`}>
                        {u.role.replace('_', ' ').toUpperCase()}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-700 font-medium">{u.department}</td>
                    <td className="px-4 py-3.5">
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        u.status === 'active'
                          ? 'bg-emerald-100 text-emerald-800'
                          : 'bg-red-100 text-red-800'
                      }`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${
                          u.status === 'active' ? 'bg-emerald-600' : 'bg-red-600'
                        }`} />
                        {u.status.toUpperCase()}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-500 text-[11px]">
                      {new Date(u.lastLoginAt).toLocaleDateString()}
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => handleInitiatePasswordReset(u)}
                          className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-amber-50 transition"
                          title="Send Password Reset Email"
                        >
                          <KeyRound className="w-4 h-4" />
                        </button>

                        <button
                          onClick={() => handleOpenEdit(u)}
                          className="p-1.5 rounded-lg text-slate-600 hover:text-ministry-deepGreen hover:bg-slate-100 transition"
                          title="Edit User & Permissions"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>

                        {u.status === 'active' ? (
                          <button
                            onClick={() => setConfirmDeactivateUser(u)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition"
                            title="Deactivate Account"
                            disabled={u.role === 'leader'}
                          >
                            <UserX className="w-4 h-4" />
                          </button>
                        ) : (
                          <button
                            onClick={() => handleToggleStatus(u)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 transition"
                            title="Activate Account"
                          >
                            <UserCheck className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add / Edit User Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg overflow-hidden border border-slate-200">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">
                {editingUser ? `Edit ${editingUser.displayName}` : 'Create New Ministry Account'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Firebase Authentication UID
                </label>
                <input
                  type="text"
                  required={!editingUser}
                  disabled={Boolean(editingUser)}
                  value={formData.firebaseUid}
                  onChange={(e) => setFormData({ ...formData, firebaseUid: e.target.value.trim() })}
                  placeholder="Paste the UID from Firebase Authentication"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs disabled:bg-slate-100 font-mono"
                />
                {!editingUser && (
                  <p className="mt-1.5 text-[10px] text-slate-500">
                    This must be the real Firebase Auth UID. The app no longer creates fake local user IDs.
                  </p>
                )}
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Full Name
                </label>
                <input
                  type="text"
                  required
                  value={formData.displayName}
                  onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Email Address
                </label>
                <input
                  type="email"
                  required
                  disabled={Boolean(editingUser)}
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs disabled:bg-slate-100"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Phone
                  </label>
                  <input
                    type="text"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    placeholder="+250 788 000 000"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Ministry Role
                  </label>
                  <select
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value as MinistryRole })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs"
                  >
                    {allRoles.map((r) => (
                      <option key={r} value={r}>
                        {r.replace('_', ' ').toUpperCase()}
                      </option>
                    ))}
                  </select>
                </div>
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
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Account Status
                </label>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value as AccountStatus })}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald text-xs"
                >
                  <option value="active">Active (Access Allowed)</option>
                  <option value="disabled">Disabled (Access Denied)</option>
                </select>
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
                  {editingUser ? 'Save Updates' : 'Create Ministry Profile'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Confirmation Dialog for Deactivation */}
      {confirmDeactivateUser && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm overflow-hidden border border-slate-200 p-6 text-center">
            <div className="w-12 h-12 rounded-full bg-red-100 text-red-600 mx-auto flex items-center justify-center mb-3">
              <AlertTriangle className="w-6 h-6" />
            </div>
            <h4 className="font-bold text-base text-slate-900 mb-1">
              Deactivate User Account?
            </h4>
            <p className="text-xs text-slate-500 mb-5 leading-relaxed">
              Are you sure you want to deactivate <strong className="text-slate-800">{confirmDeactivateUser.displayName}</strong>?
              This will immediately deny login access while preserving all ministry history and records.
            </p>
            <div className="flex items-center justify-center gap-2">
              <button
                onClick={() => setConfirmDeactivateUser(null)}
                className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={() => handleToggleStatus(confirmDeactivateUser)}
                className="px-4 py-2 text-xs font-bold bg-red-600 hover:bg-red-700 text-white rounded-xl shadow-xs"
              >
                Confirm Deactivate
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
