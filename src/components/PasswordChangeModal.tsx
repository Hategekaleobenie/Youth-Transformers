import React, { useState } from 'react';
import { KeyRound, X, CheckCircle, AlertCircle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  forced?: boolean;
}

export const PasswordChangeModal: React.FC<Props> = ({ isOpen, onClose, forced = false }) => {
  const { changeOwnPassword } = useAuth();
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (newPassword.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('New password and confirmation do not match.');
      return;
    }

    setLoading(true);
    const ok = await changeOwnPassword(newPassword);
    setLoading(false);

    if (ok) {
      setSuccess(true);
      setTimeout(() => {
        setSuccess(false);
        setNewPassword('');
        setConfirmPassword('');
        onClose();
      }, 1500);
    } else {
      setError('Failed to update password. Please check your session.');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden border border-slate-200">
        <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-emerald-700/50 rounded-lg">
              <KeyRound className="w-5 h-5 text-ministry-gold" />
            </div>
            <div>
              <h3 className="font-semibold text-base">
                {forced ? 'Set New Security Password' : 'Change Your Password'}
              </h3>
              <p className="text-xs text-emerald-100">
                {forced ? 'Mandatory update for temporary access credentials' : 'Keep your ministry account safe'}
              </p>
            </div>
          </div>
          {!forced && (
            <button
              onClick={onClose}
              className="p-1 text-slate-300 hover:text-white rounded-lg hover:bg-white/10 transition"
            >
              <X className="w-5 h-5" />
            </button>
          )}
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-xl flex items-start gap-2 text-sm text-red-700">
              <AlertCircle className="w-5 h-5 shrink-0 text-red-600 mt-0.5" />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl flex items-center gap-2 text-sm text-emerald-800">
              <CheckCircle className="w-5 h-5 text-emerald-600" />
              <span>Password updated successfully!</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              New Password
            </label>
            <input
              type="password"
              required
              minLength={6}
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="Minimum 6 characters"
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald focus:border-transparent text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1.5">
              Confirm New Password
            </label>
            <input
              type="password"
              required
              minLength={6}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Re-type your password"
              className="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald focus:border-transparent text-sm"
            />
          </div>

          <div className="pt-2 flex items-center justify-end gap-3">
            {!forced && (
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 rounded-xl transition"
              >
                Cancel
              </button>
            )}
            <button
              type="submit"
              disabled={loading || success}
              className="px-5 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-medium text-sm rounded-xl shadow-sm transition flex items-center gap-2 disabled:opacity-50"
            >
              {loading ? 'Saving...' : 'Update Password'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
