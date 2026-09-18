import React, { useState, useEffect } from 'react';
import { Wallet, TrendingUp, TrendingDown, Plus, Download, Filter, Search, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getFinanceTransactions, saveFinanceTransaction } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { FinancialTransaction, TransactionCategory, TransactionType } from '../types';

export const FinancePage: React.FC = () => {
  const { currentUser } = useAuth();
  const [transactions, setTransactions] = useState<FinancialTransaction[]>([]);
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState<string>('all');
  const [categoryFilter, setCategoryFilter] = useState<string>('all');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    description: '',
    category: 'Donations' as TransactionCategory,
    amount: '',
    type: 'income' as TransactionType,
    dateStr: new Date().toISOString().split('T')[0],
    notes: ''
  });

  useEffect(() => {
    loadFinance();
  }, []);

  const loadFinance = async () => {
    const data = await getFinanceTransactions();
    setTransactions(data);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const val = parseFloat(formData.amount);
    if (isNaN(val) || val <= 0) return;

    const tx: FinancialTransaction = {
      id: `tx_${Date.now()}`,
      description: formData.description,
      category: formData.category,
      amount: val,
      type: formData.type,
      dateStr: formData.dateStr,
      recordedBy: currentUser.displayName,
      notes: formData.notes || undefined,
      createdAt: Date.now()
    };

    await saveFinanceTransaction(tx);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Financial Transaction Recorded',
      module: 'finance',
      resourceType: 'transaction',
      resourceId: tx.id,
      details: `${tx.type.toUpperCase()}: ${tx.amount.toLocaleString()} RWF for ${tx.description}`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadFinance();
  };

  const totalIncome = transactions.filter(t => t.type === 'income').reduce((s, t) => s + t.amount, 0);
  const totalExpense = transactions.filter(t => t.type === 'expense').reduce((s, t) => s + t.amount, 0);
  const balance = totalIncome - totalExpense;

  const filtered = transactions.filter(t => {
    const matchesSearch = t.description.toLowerCase().includes(search.toLowerCase()) ||
      t.category.toLowerCase().includes(search.toLowerCase());
    const matchesType = typeFilter === 'all' || t.type === typeFilter;
    const matchesCat = categoryFilter === 'all' || t.category === categoryFilter;
    return matchesSearch && matchesType && matchesCat;
  });

  const categories: TransactionCategory[] = [
    'Donations',
    'Fundraising',
    'Events',
    'Equipment',
    'Transport',
    'Welfare',
    'Other'
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Treasury & Ministry Finances
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Department managed by Ebenezer Mugisha • Consecrated accountability and financial stewardship
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <Plus className="w-4 h-4" />
          <span>Record Transaction</span>
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs">
          <div className="flex items-center gap-2 text-emerald-600 mb-1">
            <TrendingUp className="w-4 h-4" />
            <span className="text-xs font-bold">Total Inflow / Tithes</span>
          </div>
          <div className="text-xl font-black text-slate-900">
            {totalIncome.toLocaleString()} RWF
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs">
          <div className="flex items-center gap-2 text-red-600 mb-1">
            <TrendingDown className="w-4 h-4" />
            <span className="text-xs font-bold">Total Operational Expenses</span>
          </div>
          <div className="text-xl font-black text-slate-900">
            {totalExpense.toLocaleString()} RWF
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs">
          <div className="flex items-center gap-2 text-blue-600 mb-1">
            <Wallet className="w-4 h-4" />
            <span className="text-xs font-bold">Net Treasury Balance</span>
          </div>
          <div className="text-xl font-black text-slate-900">
            {balance.toLocaleString()} RWF
          </div>
        </div>
      </div>

      {/* Filter & Search */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search descriptions, tithes, requisitions..."
            className="w-full pl-10 pr-4 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs font-medium text-slate-700 focus:outline-none"
          >
            <option value="all">All Types</option>
            <option value="income">Income Only</option>
            <option value="expense">Expense Only</option>
          </select>

          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 rounded-xl border border-slate-200 text-xs font-medium text-slate-700 focus:outline-none"
          >
            <option value="all">All Categories</option>
            {categories.map((c) => (
              <option key={c} value={c}>{c}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 text-slate-700 font-bold border-b border-slate-200 uppercase text-[10px] tracking-wider">
              <tr>
                <th className="px-5 py-3.5">Date</th>
                <th className="px-4 py-3.5">Description</th>
                <th className="px-4 py-3.5">Category</th>
                <th className="px-4 py-3.5">Amount</th>
                <th className="px-4 py-3.5">Type</th>
                <th className="px-5 py-3.5 text-right">Recorded By</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-5 py-8 text-center text-slate-400 text-xs">
                    No transactions found.
                  </td>
                </tr>
              ) : (
                filtered.map((t) => (
                  <tr key={t.id} className="hover:bg-slate-50 transition">
                    <td className="px-5 py-3.5 font-medium text-slate-500">{t.dateStr}</td>
                    <td className="px-4 py-3.5 font-bold text-slate-900">{t.description}</td>
                    <td className="px-4 py-3.5">
                      <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-semibold">
                        {t.category}
                      </span>
                    </td>
                    <td className="px-4 py-3.5 font-black text-slate-900">
                      {t.amount.toLocaleString()} RWF
                    </td>
                    <td className="px-4 py-3.5">
                      <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                        t.type === 'income' ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {t.type.toUpperCase()}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-right text-slate-500">{t.recordedBy}</td>
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
              <h3 className="font-bold text-sm">Record Financial Transaction</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Description *
                </label>
                <input
                  type="text"
                  required
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="e.g. Sunday Youth Tithes & Offerings"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Amount (RWF) *
                  </label>
                  <input
                    type="number"
                    required
                    min="100"
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                    placeholder="e.g. 50000"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Transaction Type
                  </label>
                  <select
                    value={formData.type}
                    onChange={(e) => setFormData({ ...formData, type: e.target.value as TransactionType })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="income">Income (Deposit)</option>
                    <option value="expense">Expense (Disbursement)</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Category
                  </label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value as TransactionCategory })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    {categories.map((c) => (
                      <option key={c} value={c}>{c}</option>
                    ))}
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
                  Accounting Notes
                </label>
                <textarea
                  rows={2}
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  placeholder="Receipt number, bank transfer details..."
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
                  Save Transaction
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
