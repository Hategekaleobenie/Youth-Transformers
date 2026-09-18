import React, { useState, useEffect } from 'react';
import { ShieldAlert, Search, Filter, Clock, CheckCircle, AlertTriangle, XCircle, ArrowUpDown } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { fetchActivityLogs } from '../services/activityLogService';
import { ActivityLogItem, ModuleName } from '../types';

export const ActivityLogPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [logs, setLogs] = useState<ActivityLogItem[]>([]);
  const [search, setSearch] = useState('');
  const [moduleFilter, setModuleFilter] = useState<string>('all');
  const [resultFilter, setResultFilter] = useState<string>('all');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadLogs();
  }, []);

  const loadLogs = async () => {
    setLoading(true);
    const data = await fetchActivityLogs(100);
    setLogs(data);
    setLoading(false);
  };

  const filtered = logs.filter((log) => {
    const matchesSearch =
      log.action.toLowerCase().includes(search.toLowerCase()) ||
      log.actorName.toLowerCase().includes(search.toLowerCase()) ||
      (log.details && log.details.toLowerCase().includes(search.toLowerCase()));
    const matchesModule = moduleFilter === 'all' || log.module === moduleFilter;
    const matchesResult = resultFilter === 'all' || log.result === resultFilter;
    return matchesSearch && matchesModule && matchesResult;
  });

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
    'committee'
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Security & Operational Activity Audit Log
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Immutable audit record of all authentication events, administrative changes, and ministry operations
          </p>
        </div>

        <div className="text-xs font-bold text-slate-700 bg-white px-3 py-1.5 rounded-xl border border-slate-200">
          Total Entries: {logs.length}
        </div>
      </div>

      {/* Filters & Search */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search audit actions, users, or details..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <select
            value={moduleFilter}
            onChange={(e) => setModuleFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
          >
            <option value="all">All Modules</option>
            {modules.map((m) => (
              <option key={m} value={m}>{m.toUpperCase()}</option>
            ))}
          </select>

          <select
            value={resultFilter}
            onChange={(e) => setResultFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs text-slate-700 font-medium focus:outline-none"
          >
            <option value="all">All Results</option>
            <option value="success">Success</option>
            <option value="denied">Access Denied</option>
            <option value="failed">Failed</option>
          </select>
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
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-8 text-center text-slate-400 text-xs">
                    No matching audit records found.
                  </td>
                </tr>
              ) : (
                filtered.map((l) => (
                  <tr key={l.id} className="hover:bg-slate-50 transition">
                    <td className="px-5 py-3.5 font-mono text-[11px] text-slate-500 whitespace-nowrap">
                      {new Date(l.timestamp).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5">
                      <div className="font-bold text-slate-900">{l.actorName}</div>
                      <div className="text-[10px] text-slate-400 font-mono">{l.actorRole}</div>
                    </td>
                    <td className="px-4 py-3.5 font-semibold text-slate-800">{l.action}</td>
                    <td className="px-4 py-3.5">
                      <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-semibold">
                        {l.module}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 text-slate-600 max-w-xs truncate">{l.details || '—'}</td>
                    <td className="px-5 py-3.5 text-right">
                      <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        l.result === 'success' ? 'bg-emerald-100 text-emerald-800' :
                        l.result === 'denied' ? 'bg-amber-100 text-amber-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {l.result.toUpperCase()}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
