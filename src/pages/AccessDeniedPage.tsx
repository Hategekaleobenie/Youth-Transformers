import React, { useEffect } from 'react';
import { ShieldAlert, ArrowLeft } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { logActivity } from '../services/activityLogService';

interface Props {
  moduleName: string;
  onGoHome: () => void;
}

export const AccessDeniedPage: React.FC<Props> = ({ moduleName, onGoHome }) => {
  const { currentUser, t } = useAuth();

  useEffect(() => {
    if (currentUser) {
      logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Unauthorized Module Access Attempted',
        module: moduleName as any,
        resourceType: 'screen',
        resourceId: moduleName,
        details: `Access to ${moduleName} blocked for ${currentUser.displayName} (${currentUser.role})`,
        result: 'denied'
      });
    }
  }, [moduleName, currentUser]);

  return (
    <div className="min-h-[60vh] flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl p-8 max-w-md w-full text-center border border-slate-200 shadow-lg">
        <div className="w-16 h-16 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center mx-auto mb-4">
          <ShieldAlert className="w-8 h-8" />
        </div>

        <h3 className="text-lg font-black text-slate-900 mb-1">
          {t('auth.accessDenied')}
        </h3>

        <p className="text-xs text-slate-500 mb-5 leading-relaxed">
          {t('auth.accessDeniedSubtitle')}
        </p>

        {currentUser && (
          <div className="p-3.5 bg-slate-50 rounded-2xl border border-slate-100 text-xs text-slate-600 mb-6 text-left space-y-1">
            <div className="flex justify-between">
              <span className="text-slate-400">Authenticated:</span>
              <strong className="text-slate-800">{currentUser.displayName}</strong>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Department:</span>
              <strong className="text-slate-800">{currentUser.department}</strong>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Assigned Role:</span>
              <span className="px-2 py-0.5 rounded-full bg-slate-200 text-slate-800 font-bold text-[10px] uppercase">
                {currentUser.role.replace('_', ' ')}
              </span>
            </div>
          </div>
        )}

        <button
          onClick={onGoHome}
          className="w-full py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold text-xs rounded-xl shadow-xs transition flex items-center justify-center gap-2"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Return to Dashboard</span>
        </button>
      </div>
    </div>
  );
};
