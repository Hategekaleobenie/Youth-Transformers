import React, { useState, useEffect, useMemo } from 'react';
import {
  ShieldAlert,
  Search,
  Filter,
  Clock,
  CheckCircle,
  AlertTriangle,
  XCircle,
  Calendar,
  User,
  ChevronLeft,
  ChevronRight,
  RotateCcw
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { fetchActivityLogs } from '../services/activityLogService';
import { ActivityLogItem, ModuleName, MinistryRole } from '../types';

export const ActivityLogPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [logs, setLogs] = useState<ActivityLogItem[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [search, setSearch] = useState('');
  const [userFilter, setUserFilter] = useState<string>('all');
  const [roleFilter, setRoleFilter] = useState<string>('all');
  const [moduleFilter, setModuleFilter] = useState<string>('all');
  const [actionFilter, setActionFilter] = useState<string>('all');
  const [resultFilter, setResultFilter] = useState<string>('all');
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(15);

  useEffect(() => {
    loadLogs();
  }, []);

  const loadLogs = async () => {
    setLoading(true);
    const data = await fetchActivityLogs(500);
    // Sort strictly newest first
    const sorted = [...data].sort((a, b) => b.timestamp - a.timestamp);
    setLogs(sorted);
    setLoading(false);
  };

  // Extract unique filter options from real data
  const uniqueUsers = useMemo(() => {
    const set = new Set<string>();
    logs.forEach(l => { if (l.actorName) set.add(l.actorName); });
    return Array.from(set).sort();
  }, [logs]);

  const uniqueRoles = useMemo(() => {
    const set = new Set<string>();
    logs.forEach(l => { if (l.actorRole) set.add(l.actorRole); });
    return Array.from(set).sort();
  }, [logs]);

  const uniqueActions = useMemo(() => {
    const set = new Set<string>();
    logs.forEach(l => { if (l.action) set.add(l.action); });
    return Array.from(set).sort();
  }, [logs]);

  const modules: ModuleName[] = [
    'users',
    'members',
    'bible_study',
    'attendance',
    'member_care',
    'finance',
    'projects',
    'equipment',
    'social_media',
    'events',
    'reports',
    'announcements',
    'committee',
    'activity_log'
  ];

  // Filtering logic
  const filtered = useMemo(() => {
    return logs.filter((log) => {
      const matchesSearch =
        !search ||
        log.action.toLowerCase().includes(search.toLowerCase()) ||
        log.actorName.toLowerCase().includes(search.toLowerCase()) ||
        (log.details && log.details.toLowerCase().includes(search.toLowerCase())) ||
        log.module.toLowerCase().includes(search.toLowerCase());

      const matchesUser = userFilter === 'all' || log.actorName === userFilter;
      const matchesRole = roleFilter === 'all' || log.actorRole === roleFilter;
      const matchesModule = moduleFilter === 'all' || log.module === moduleFilter;
      const matchesAction = actionFilter === 'all' || log.action === actionFilter;
      const matchesResult = resultFilter === 'all' || log.result === resultFilter;

      let matchesDate = true;
      if (startDate) {
        const startMs = new Date(startDate).getTime();
        if (log.timestamp < startMs) matchesDate = false;
      }
      if (endDate) {
        const endMs = new Date(endDate).getTime() + (24 * 60 * 60 * 1000 - 1);
        if (log.timestamp > endMs) matchesDate = false;
      }

      return (
        matchesSearch &&
        matchesUser &&
        matchesRole &&
        matchesModule &&
        matchesAction &&
        matchesResult &&
        matchesDate
      );
    });
  }, [logs, search, userFilter, roleFilter, moduleFilter, actionFilter, resultFilter, startDate, endDate]);

  // Reset pagination when filter changes
  useEffect(() => {
    setCurrentPage(1);
  }, [search, userFilter, roleFilter, moduleFilter, actionFilter, resultFilter, startDate, endDate, pageSize]);

  // Pagination calculation
  const totalPages = Math.ceil(filtered.length / pageSize) || 1;
  const paginatedLogs = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return filtered.slice(startIndex, startIndex + pageSize);
  }, [filtered, currentPage, pageSize]);

  const handleResetFilters = () => {
    setSearch('');
    setUserFilter('all');
    setRoleFilter('all');
    setModuleFilter('all');
    setActionFilter('all');
    setResultFilter('all');
    setStartDate('');
    setEndDate('');
    setCurrentPage(1);
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Security & Operational Activity Audit Log
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Immutable audit trail of authentications, administrative changes, data actions, and security access decisions
          </p>
        </div>

        <div className="flex items-center gap-2">
          <div className="text-xs font-bold text-slate-700 bg-white px-3 py-1.5 rounded-xl border border-slate-200">
            Total Filtered: {filtered.length} / {logs.length}
          </div>
          <button
            onClick={loadLogs}
            disabled={loading}
            className="p-1.5 rounded-xl bg-white border border-slate-200 hover:bg-slate-50 text-slate-600 transition"
            title="Refresh logs"
          >
            <RotateCcw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Filter Control Center */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs space-y-4">
        {/* Search Bar */}
        <div className="relative w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search action name, actor, module, or details..."
            className="w-full pl-10 pr-4 py-2.5 bg-slate-50 rounded-xl border border-slate-200 text-xs focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
          />
        </div>

        {/* Filter Dropdowns Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-2.5">
          {/* User Filter */}
          <div>
            <label className="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">
              Actor
            </label>
            <select
              value={userFilter}
              onChange={(e) => setUserFilter(e.target.value)}
              className="w-full px-2.5 py-1.5 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
            >
              <option value="all">All Users</option>
              {uniqueUsers.map((u) => (
                <option key={u} value={u}>{u}</option>
              ))}
            </select>
          </div>

          {/* Role Filter */}
          <div>
            <label className="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">
              Role
            </label>
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="w-full px-2.5 py-1.5 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
            >
              <option value="all">All Roles</option>
              {uniqueRoles.map((r) => (
                <option key={r} value={r}>{r}</option>
              ))}
            </select>
          </div>

          {/* Module Filter */}
          <div>
            <label className="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">
              Module
            </label>
            <select
              value={moduleFilter}
              onChange={(e) => setModuleFilter(e.target.value)}
              className="w-full px-2.5 py-1.5 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
            >
              <option value="all">All Modules</option>
              {modules.map((m) => (
                <option key={m} value={m}>{m.toUpperCase()}</option>
              ))}
            </select>
          </div>

          {/* Action Filter */}
          <div>
            <label className="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">
              Action
            </label>
            <select
              value={actionFilter}
              onChange={(e) => setActionFilter(e.target.value)}
              className="w-full px-2.5 py-1.5 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
            >
              <option value="all">All Actions</option>
              {uniqueActions.map((a) => (
                <option key={a} value={a}>{a}</option>
              ))}
            </select>
          </div>

          {/* Result Filter */}
          <div>
            <label className="block text-[10px] font-bold text-slate-500 uppercase tracking-wider mb-1">
              Result
            </label>
            <select
              value={resultFilter}
              onChange={(e) => setResultFilter(e.target.value)}
              className="w-full px-2.5 py-1.5 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
            >
              <option value="all">All Results</option>
              <option value="success">Success</option>
              <option value="denied">Access Denied</option>
              <option value="failed">Failed</option>
            </select>
          </div>

          {/* Reset Filters */}
          <div className="flex items-end">
            <button
              onClick={handleResetFilters}
              className="w-full px-2.5 py-1.5 text-xs text-slate-600 hover:text-slate-900 font-semibold bg-slate-100 hover:bg-slate-200 rounded-xl transition flex items-center justify-center gap-1"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              <span>Reset</span>
            </button>
          </div>
        </div>

        {/* Date Range Row */}
        <div className="flex flex-wrap items-center gap-3 pt-2 border-t border-slate-100 text-xs">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1">
            <Calendar className="w-3.5 h-3.5" />
            Date Range:
          </span>
          <div className="flex items-center gap-2">
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="px-2.5 py-1 bg-slate-50 rounded-lg border border-slate-200 text-xs text-slate-700 focus:outline-none"
            />
            <span className="text-slate-400">to</span>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="px-2.5 py-1 bg-slate-50 rounded-lg border border-slate-200 text-xs text-slate-700 focus:outline-none"
            />
          </div>

          {(startDate || endDate) && (
            <button
              onClick={() => { setStartDate(''); setEndDate(''); }}
              className="text-[11px] text-red-600 hover:underline"
            >
              Clear dates
            </button>
          )}
        </div>
      </div>

      {/* Audit Log Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-[10px] tracking-wider">
              <tr>
                <th className="px-5 py-3.5">Timestamp</th>
                <th className="px-4 py-3.5">Actor</th>
                <th className="px-4 py-3.5">Action</th>
                <th className="px-4 py-3.5">Module</th>
                <th className="px-4 py-3.5">Details</th>
                <th className="px-5 py-3.5 text-right">Result</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {paginatedLogs.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-10 text-center text-slate-400 text-xs">
                    No matching audit records found for current filters.
                  </td>
                </tr>
              ) : (
                paginatedLogs.map((l) => (
                  <tr key={l.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5 font-mono text-[11px] text-slate-500 whitespace-nowrap">
                      {new Date(l.timestamp).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="font-bold text-slate-900">{l.actorName}</div>
                      <div className="text-[10px] text-slate-400 font-mono">{l.actorRole}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-800">{l.action}</td>
                    <td className="px-4 py-3.5">
                      <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-semibold uppercase">
                        {l.module}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-600 max-w-xs truncate" title={l.details || ''}>
                      {l.details || '—'}
                    </td>
                    <td className="px-5 py-3.5 text-right whitespace-nowrap">
                      <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        l.result === 'success' ? 'bg-emerald-100 text-emerald-800' :
                        l.result === 'denied' ? 'bg-amber-100 text-amber-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {l.result === 'success' ? <CheckCircle className="w-3 h-3" /> :
                         l.result === 'denied' ? <AlertTriangle className="w-3 h-3" /> :
                         <XCircle className="w-3 h-3" />}
                        {l.result.toUpperCase()}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        <div className="p-4 bg-slate-50/70 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-600">
          <div className="flex items-center gap-2">
            <span>Rows per page:</span>
            <select
              value={pageSize}
              onChange={(e) => setPageSize(Number(e.target.value))}
              className="px-2 py-1 bg-white rounded-lg border border-slate-200 text-xs text-slate-700 focus:outline-none"
            >
              <option value={10}>10</option>
              <option value={15}>15</option>
              <option value={25}>25</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
            <span className="text-slate-400">
              Showing {filtered.length === 0 ? 0 : (currentPage - 1) * pageSize + 1} - {Math.min(currentPage * pageSize, filtered.length)} of {filtered.length}
            </span>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
              disabled={currentPage === 1}
              className="p-1.5 rounded-lg border border-slate-200 bg-white hover:bg-slate-100 disabled:opacity-40 transition"
              title="Previous Page"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="px-3 py-1 font-semibold text-slate-700 text-xs">
              Page {currentPage} of {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages || filtered.length === 0}
              className="p-1.5 rounded-lg border border-slate-200 bg-white hover:bg-slate-100 disabled:opacity-40 transition"
              title="Next Page"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
