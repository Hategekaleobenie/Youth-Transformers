import React, { useState } from 'react';
import { Mail, Lock, LogIn, AlertCircle, CheckCircle2, ShieldCheck, KeyRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const LoginPage: React.FC = () => {
  const { login, sendResetEmail, error, clearError, t } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [resetSent, setResetSent] = useState(false);
  const [isForgotPasswordMode, setIsForgotPasswordMode] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (loading) return;
    clearError();
    setLoading(true);

    if (isForgotPasswordMode) {
      const ok = await sendResetEmail(email.trim());
      setLoading(false);
      if (ok) {
        setResetSent(true);
      }
    } else {
      await login(email.trim(), password);
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-ministry-forestDark via-ministry-deepGreen to-emerald-950 flex items-center justify-center p-4">
      {/* Container */}
      <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl overflow-hidden border border-emerald-800/30 animate-fade-in">
        {/* Header Branding */}
        <div className="bg-gradient-to-b from-ministry-deepGreen to-ministry-forestDark p-8 text-center text-white relative">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-tr from-ministry-emerald to-ministry-gold p-1 shadow-lg mb-3">
            <div className="w-full h-full bg-ministry-forestDark rounded-xl flex items-center justify-center text-ministry-gold font-extrabold text-2xl tracking-wider">
              YT
            </div>
          </div>

          <h2 className="text-xl font-black tracking-wide text-white">
            {t('app.title')}
          </h2>
          <p className="text-xs text-ministry-gold font-semibold tracking-wider uppercase mt-1">
            {t('auth.loginTitle')}
          </p>
          <p className="text-xs text-emerald-200 mt-2 max-w-xs mx-auto">
            {isForgotPasswordMode
              ? 'Enter your registered ministry email to receive a password reset link'
              : t('auth.loginSubtitle')}
          </p>
        </div>

        {/* Form Body */}
        <div className="p-8">
          {error && (
            <div className="mb-5 p-3.5 bg-red-50 border border-red-200 rounded-xl flex items-start gap-2.5 text-xs text-red-700">
              <AlertCircle className="w-4 h-4 shrink-0 text-red-600 mt-0.5" />
              <span>{error}</span>
            </div>
          )}

          {resetSent && (
            <div className="mb-5 p-3.5 bg-emerald-50 border border-emerald-200 rounded-xl flex items-start gap-2.5 text-xs text-emerald-800">
              <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600 mt-0.5" />
              <span>{t('auth.resetSent')}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                {t('auth.email')}
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@youthtransformers.org"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald focus:border-transparent text-sm"
                />
              </div>
            </div>

            {!isForgotPasswordMode && (
              <div>
                <div className="flex items-center justify-between mb-1.5">
                  <label className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                    {t('auth.password')}
                  </label>
                  <button
                    type="button"
                    onClick={() => {
                      clearError();
                      setIsForgotPasswordMode(true);
                    }}
                    className="text-xs text-ministry-emerald hover:text-ministry-deepGreen font-semibold"
                  >
                    {t('auth.forgotPassword')}
                  </button>
                </div>
                <div className="relative">
                  <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
                  <input
                    type="password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald focus:border-transparent text-sm"
                  />
                </div>
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-3 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold text-sm rounded-xl shadow-md hover:shadow-lg transition flex items-center justify-center gap-2 disabled:opacity-60"
            >
              {loading ? (
                <span>Verifying credentials...</span>
              ) : isForgotPasswordMode ? (
                <>
                  <KeyRound className="w-4 h-4" />
                  <span>Send Reset Email</span>
                </>
              ) : (
                <>
                  <LogIn className="w-4 h-4" />
                  <span>{t('auth.signIn')}</span>
                </>
              )}
            </button>
          </form>

          {isForgotPasswordMode && (
            <div className="mt-4 text-center">
              <button
                type="button"
                onClick={() => {
                  clearError();
                  setIsForgotPasswordMode(false);
                  setResetSent(false);
                }}
                className="text-xs text-slate-600 hover:text-slate-900 font-semibold"
              >
                Back to Sign In
              </button>
            </div>
          )}

          {/* Security Governance Notice */}
          <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-center gap-2 text-xs text-slate-500">
            <ShieldCheck className="w-4 h-4 text-ministry-emerald" />
            <span>Protected by Role-Based Access Control</span>
          </div>
        </div>
      </div>
    </div>
  );
};
