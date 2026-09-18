import React, { useState } from 'react';
import { Settings, User, KeyRound, Globe, Smartphone, ShieldCheck, CheckCircle2, Server } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { PasswordChangeModal } from '../components/PasswordChangeModal';
import { isFirebaseConfigured } from '../config/firebase';

export const SettingsPage: React.FC = () => {
  const { currentUser, lang, setLang, t } = useAuth();
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);

  return (
    <div className="space-y-6 max-w-4xl animate-fade-in">
      <div>
        <h2 className="text-xl font-black text-slate-900 tracking-tight">
          Account Settings & System Configuration
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Manage your personal credentials, localization, and inspect system deployment environment
        </p>
      </div>

      {/* Profile Card */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-ministry-deepGreen to-ministry-emerald text-white flex items-center justify-center font-black text-xl shadow-md">
            {currentUser?.displayName?.charAt(0) || 'U'}
          </div>
          <div>
            <h3 className="font-bold text-base text-slate-900">{currentUser?.displayName}</h3>
            <p className="text-xs text-slate-500">{currentUser?.email}</p>
            <div className="flex items-center gap-2 mt-1">
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-200 uppercase">
                {currentUser?.role?.replace('_', ' ')}
              </span>
              <span className="text-xs text-slate-400">• {currentUser?.department}</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 border-t border-slate-100 pt-5">
          <div>
            <span className="text-xs text-slate-400 block mb-1">Security & Password</span>
            <button
              onClick={() => setIsPasswordModalOpen(true)}
              className="inline-flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-bold rounded-xl transition"
            >
              <KeyRound className="w-4 h-4 text-ministry-emerald" />
              <span>Change Password</span>
            </button>
          </div>

          <div>
            <span className="text-xs text-slate-400 block mb-1">Language Preference</span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setLang('en')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition ${
                  lang === 'en' ? 'bg-ministry-deepGreen text-white' : 'bg-slate-100 text-slate-600'
                }`}
              >
                English (EN)
              </button>
              <button
                onClick={() => setLang('rw')}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition ${
                  lang === 'rw' ? 'bg-ministry-deepGreen text-white' : 'bg-slate-100 text-slate-600'
                }`}
              >
                Ikinyarwanda (RW)
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* PWA & Mobile Installation */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs">
        <div className="flex items-start gap-4">
          <div className="p-3 bg-emerald-50 text-ministry-emerald rounded-2xl">
            <Smartphone className="w-6 h-6" />
          </div>
          <div>
            <h3 className="font-bold text-sm text-slate-900">
              Install on Mobile Devices (iPhone & Android)
            </h3>
            <p className="text-xs text-slate-600 mt-1 leading-relaxed">
              This application is a certified Progressive Web App (PWA) that installs as a native-feeling standalone app:
            </p>
            <ul className="text-xs text-slate-500 mt-2 space-y-1 list-disc list-inside">
              <li><strong>iOS (iPhone / iPad):</strong> Tap Safari Share icon → Select <em>"Add to Home Screen"</em>.</li>
              <li><strong>Android (Chrome):</strong> Tap 3-dots menu → Select <em>"Install App"</em> or tap the install banner.</li>
              <li><strong>Desktop (Chrome / Edge / Mac):</strong> Click the install icon in the URL bar.</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Deployment & Backend Infrastructure Status */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-xs">
        <div className="flex items-start gap-4">
          <div className="p-3 bg-blue-50 text-blue-600 rounded-2xl">
            <Server className="w-6 h-6" />
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <h3 className="font-bold text-sm text-slate-900">
                Firebase Backend Connectivity
              </h3>
              <span className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full ${
                isFirebaseConfigured
                  ? 'bg-emerald-100 text-emerald-800'
                  : 'bg-amber-100 text-amber-800'
              }`}>
                {isFirebaseConfigured ? 'LIVE FIREBASE ACTIVE' : 'DEV MODE (LOCAL PERSISTENCE)'}
              </span>
            </div>
            <p className="text-xs text-slate-600 mt-2 leading-relaxed">
              {isFirebaseConfigured
                ? 'Connected to Firebase Authentication and Cloud Firestore. Role-based security rules actively enforced.'
                : 'Running in development mode with seeded ministry records and local persistence. To deploy live to production, set your Firebase credentials in environment variables or hosting configuration.'}
            </p>
          </div>
        </div>
      </div>

      {/* Password Modal */}
      <PasswordChangeModal
        isOpen={isPasswordModalOpen}
        onClose={() => setIsPasswordModalOpen(false)}
      />
    </div>
  );
};
