import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  signInWithEmailAndPassword,
  signOut as firebaseSignOut,
  sendPasswordResetEmail,
  updatePassword,
  onAuthStateChanged,
  User as FirebaseUser
} from 'firebase/auth';
import { auth, isFirebaseConfigured } from '../config/firebase';
import { getUserById, getUsers, saveUser } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { UserProfile, MinistryRole, ModuleName, PermissionAction } from '../types';
import { LanguageCode, getTranslation } from '../locales/i18n';

interface AuthContextType {
  currentUser: UserProfile | null;
  firebaseUser: FirebaseUser | null;
  loading: boolean;
  error: string | null;
  lang: LanguageCode;
  setLang: (l: LanguageCode) => void;
  t: (key: string, fallback?: string) => string;
  login: (email: string, pass: string) => Promise<boolean>;
  logout: () => Promise<void>;
  sendResetEmail: (email: string) => Promise<boolean>;
  changeOwnPassword: (newPass: string) => Promise<boolean>;
  hasPermission: (module: ModuleName, action?: PermissionAction) => boolean;
  clearError: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<UserProfile | null>(null);
  const [firebaseUser, setFirebaseUser] = useState<FirebaseUser | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [lang, setLang] = useState<LanguageCode>('en');

  // Translation helper
  const t = (key: string, fallback?: string) => getTranslation(lang, key, fallback);

  useEffect(() => {
    // Check local saved language
    const savedLang = localStorage.getItem('yt_app_lang') as LanguageCode;
    if (savedLang === 'en' || savedLang === 'rw') {
      setLang(savedLang);
    }

    if (isFirebaseConfigured) {
      const unsubscribe = onAuthStateChanged(auth, async (fbUser) => {
        setFirebaseUser(fbUser);
        if (fbUser) {
          try {
            const profile = await getUserById(fbUser.uid, await fbUser.getIdToken());
            if (profile) {
              if (profile.status === 'disabled') {
                await firebaseSignOut(auth);
                setCurrentUser(null);
                setError(t('auth.disabledAccount'));
                await logActivity({
                  actorUid: fbUser.uid,
                  actorName: profile.displayName,
                  actorRole: profile.role,
                  action: 'Login Attempt on Disabled Account',
                  module: 'users',
                  resourceType: 'auth',
                  resourceId: fbUser.uid,
                  details: 'Access rejected due to disabled status',
                  result: 'denied'
                });
              } else {
                setCurrentUser(profile);
                setError(null);
              }
            } else {
              // Never auto-provision an authenticated user into the ministry.
              // A Firebase Auth account must have an explicitly created Firestore
              // ministry profile before it can access the application.
              await firebaseSignOut(auth);
              setCurrentUser(null);
              setError('Your Firebase account is valid, but no Youth Transformers ministry profile is assigned to it. Ask the leader to create/assign your ministry profile.');
            }
          } catch (err: any) {
            console.error('Error fetching user profile:', err);
            setError(err.message || 'Failed to load user profile');
          }
        } else {
          setCurrentUser(null);
        }
        setLoading(false);
      });

      return () => unsubscribe();
    } else {
      // Offline / Development mode with session persistence
      const savedUid = localStorage.getItem('yt_active_uid');
      if (savedUid) {
        getUserById(savedUid).then((profile) => {
          if (profile && profile.status === 'active') {
            setCurrentUser(profile);
          } else {
            localStorage.removeItem('yt_active_uid');
            setCurrentUser(null);
          }
          setLoading(false);
        });
      } else {
        setLoading(false);
      }
    }
  }, [lang]);

  const handleSetLang = (newLang: LanguageCode) => {
    setLang(newLang);
    localStorage.setItem('yt_app_lang', newLang);
  };

  const login = async (email: string, pass: string): Promise<boolean> => {
    setError(null);
    setLoading(true);

    if (isFirebaseConfigured) {
      try {
        const cred = await signInWithEmailAndPassword(auth, email, pass);
        const profile = await getUserById(cred.user.uid, await cred.user.getIdToken());
        if (profile && profile.status === 'disabled') {
          await firebaseSignOut(auth);
          setCurrentUser(null);
          setError(t('auth.disabledAccount'));
          setLoading(false);
          return false;
        }

        if (!profile) {
          await firebaseSignOut(auth);
          setCurrentUser(null);
          setError('Your Firebase account exists, but your Youth Transformers ministry profile is missing. Ask the leader to create/assign your profile.');
          setLoading(false);
          return false;
        }

        if (profile) {
          // Do not write lastLoginAt during sign-in. Firestore rules intentionally
          // prevent ordinary users from modifying their own profile document.
          // Authentication and profile loading are sufficient to establish the session.
          setCurrentUser(profile);
          void logActivity({
            actorUid: profile.uid,
            actorName: profile.displayName,
            actorRole: profile.role,
            action: 'User Signed In',
            module: 'users',
            resourceType: 'auth',
            resourceId: profile.uid,
            details: `Logged into ${profile.department}`,
            result: 'success'
          });
        }
        setLoading(false);
        return true;
      } catch (err: any) {
        setLoading(false);
        const code = err.code || '';
        if (code === 'auth/wrong-password' || code === 'auth/user-not-found' || code === 'auth/invalid-credential') {
          setError(t('auth.invalidCredentials'));
        } else {
          setError(err.message || t('auth.invalidCredentials'));
        }
        return false;
      }
    } else {
      // Local development authentication check using seeded database records
      const users = await getUsers();
      const match = users.find(u => u.email.toLowerCase() === email.trim().toLowerCase());

      if (!match) {
        setError(t('auth.invalidCredentials'));
        setLoading(false);
        return false;
      }

      if (match.status === 'disabled') {
        setError(t('auth.disabledAccount'));
        setLoading(false);
        await logActivity({
          actorUid: match.uid,
          actorName: match.displayName,
          actorRole: match.role,
          action: 'Login Attempt on Disabled Account',
          module: 'users',
          resourceType: 'auth',
          resourceId: match.uid,
          details: 'Access rejected due to disabled status',
          result: 'denied'
        });
        return false;
      }

      // Valid credentials match
      match.lastLoginAt = Date.now();
      await saveUser(match);
      setCurrentUser(match);
      localStorage.setItem('yt_active_uid', match.uid);

      await logActivity({
        actorUid: match.uid,
        actorName: match.displayName,
        actorRole: match.role,
        action: 'User Signed In',
        module: 'users',
        resourceType: 'auth',
        resourceId: match.uid,
        details: `Logged in as ${match.displayName} (${match.role})`,
        result: 'success'
      });

      setLoading(false);
      return true;
    }
  };

  const logout = async (): Promise<void> => {
    if (currentUser) {
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'User Signed Out',
        module: 'users',
        resourceType: 'auth',
        resourceId: currentUser.uid,
        result: 'success'
      });
    }

    if (isFirebaseConfigured) {
      await firebaseSignOut(auth);
    }
    localStorage.removeItem('yt_active_uid');
    setCurrentUser(null);
    setFirebaseUser(null);
  };

  const sendResetEmail = async (email: string): Promise<boolean> => {
    setError(null);
    try {
      if (isFirebaseConfigured) {
        await sendPasswordResetEmail(auth, email);
      }
      await logActivity({
        actorUid: currentUser?.uid || 'anonymous',
        actorName: currentUser?.displayName || email,
        actorRole: currentUser?.role || 'user',
        action: 'Password Reset Requested',
        module: 'users',
        resourceType: 'auth',
        resourceId: email,
        details: `Reset link requested for email: ${email}`,
        result: 'success'
      });
      return true;
    } catch (err: any) {
      setError(err.message || 'Failed to request password reset.');
      return false;
    }
  };

  const changeOwnPassword = async (newPass: string): Promise<boolean> => {
    if (!currentUser) return false;
    try {
      if (isFirebaseConfigured && auth.currentUser) {
        await updatePassword(auth.currentUser, newPass);
      }
      // If user had mustChangePassword flag, clear it
      if (currentUser.mustChangePassword) {
        const updated = { ...currentUser, mustChangePassword: false };
        await saveUser(updated);
        setCurrentUser(updated);
      }
      await logActivity({
        actorUid: currentUser.uid,
        actorName: currentUser.displayName,
        actorRole: currentUser.role,
        action: 'Password Changed Successfully',
        module: 'users',
        resourceType: 'auth',
        resourceId: currentUser.uid,
        result: 'success'
      });
      return true;
    } catch (err: any) {
      setError(err.message || 'Failed to change password. You may need to sign in again.');
      return false;
    }
  };

  const hasPermission = (module: ModuleName, action?: PermissionAction): boolean => {
    if (!currentUser) return false;
    if (currentUser.status === 'disabled') return false;
    if (currentUser.role === 'leader') return true; // Leader Leo Benie Hategeka has full access

    // Check specific assigned permission e.g. "finance:manage"
    const key = action ? `${module}:${action}` : `${module}:view`;
    if (currentUser.assignedPermissions?.includes(key) || currentUser.assignedPermissions?.includes(`${module}:*`)) {
      return true;
    }

    // Role-based defaults
    switch (currentUser.role) {
      case 'committee_coordinator':
        return ['committee', 'reports', 'announcements', 'events', 'members'].includes(module);
      case 'level1_leader':
        return ['members', 'reports', 'attendance', 'events'].includes(module);
      case 'social_media':
        return ['social_media', 'announcements', 'reports'].includes(module);
      case 'member_care':
        return ['member_care', 'members', 'reports'].includes(module);
      case 'bible_study':
        return ['bible_study', 'attendance', 'reports'].includes(module);
      case 'accountant':
        return ['finance', 'reports'].includes(module);
      case 'projects_manager':
        return ['projects', 'equipment', 'reports'].includes(module);
      case 'member':
        return ['announcements'].includes(module);
      default:
        return false;
    }
  };

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        firebaseUser,
        loading,
        error,
        lang,
        setLang: handleSetLang,
        t,
        login,
        logout,
        sendResetEmail,
        changeOwnPassword,
        hasPermission,
        clearError: () => setError(null)
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
  return ctx;
};
