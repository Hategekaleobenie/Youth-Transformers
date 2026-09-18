import { collection, addDoc, getDocs, query, orderBy, limit } from 'firebase/firestore';
import { db, isFirebaseConfigured } from '../config/firebase';
import { ActivityLogItem } from '../types';

const COLLECTION = 'activityLogs';
const LOCAL_STORAGE_KEY = 'yt_activity_logs_fallback';

export async function logActivity(entry: Omit<ActivityLogItem, 'id' | 'timestamp'>): Promise<void> {
  const timestamp = Date.now();
  const fullEntry: ActivityLogItem = {
    ...entry,
    id: `log_${timestamp}_${Math.random().toString(36).substr(2, 6)}`,
    timestamp
  };

  // Try Firestore if configured
  if (isFirebaseConfigured) {
    try {
      await addDoc(collection(db, COLLECTION), {
        ...entry,
        timestamp
      });
      return;
    } catch (err) {
      console.warn('Firestore activity log write failed, using local audit backup:', err);
    }
  }

  // Local persistent audit log fallback for development/offline
  try {
    const raw = localStorage.getItem(LOCAL_STORAGE_KEY);
    const list: ActivityLogItem[] = raw ? JSON.parse(raw) : [];
    list.unshift(fullEntry); // newest first
    if (list.length > 500) list.pop();
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(list));
  } catch (e) {
    console.error('Error recording activity fallback:', e);
  }
}

export async function fetchActivityLogs(maxItems = 300): Promise<ActivityLogItem[]> {
  if (isFirebaseConfigured) {
    try {
      const q = query(collection(db, COLLECTION), orderBy('timestamp', 'desc'), limit(maxItems));
      const snap = await getDocs(q);
      if (!snap.empty) {
        return snap.docs.map(d => ({
          id: d.id,
          ...(d.data() as Omit<ActivityLogItem, 'id'>)
        }));
      }
    } catch (e) {
      console.warn('Could not fetch activity logs from Firestore:', e);
    }
  }

  // Fallback / seed audit logs
  try {
    const raw = localStorage.getItem(LOCAL_STORAGE_KEY);
    if (raw) {
      const list: ActivityLogItem[] = JSON.parse(raw);
      return list.sort((a, b) => b.timestamp - a.timestamp);
    }
  } catch (e) {}

  return getInitialSeedActivityLogs();
}

function getInitialSeedActivityLogs(): ActivityLogItem[] {
  const now = Date.now();
  return [
    {
      id: 'log_seed_1',
      actorUid: 'usr_leo_hategeka',
      actorName: 'Leo Benie Hategeka',
      actorRole: 'leader',
      action: 'System Security Audit Completed',
      module: 'activity_log',
      resourceType: 'system',
      resourceId: 'sys_audit_01',
      details: 'Reviewed all role permissions and operational access logs',
      timestamp: now - 1000 * 60 * 12,
      result: 'success'
    },
    {
      id: 'log_seed_2',
      actorUid: 'usr_green_london',
      actorName: 'Green London',
      actorRole: 'committee_coordinator',
      action: 'Department Report Submitted',
      module: 'reports',
      resourceType: 'report',
      resourceId: 'rep_comm_01',
      details: 'Submitted Committee Progress Report for Current Operational Period',
      timestamp: now - 1000 * 60 * 45,
      result: 'success'
    },
    {
      id: 'log_seed_3',
      actorUid: 'usr_cedrick_gisubizo',
      actorName: 'Cedrick Gisubizo',
      actorRole: 'member_care',
      action: 'Follow-Up Case Opened',
      module: 'member_care',
      resourceType: 'case',
      resourceId: 'case_fc_01',
      details: 'Opened pastoral care case for attendance follow-up',
      timestamp: now - 1000 * 60 * 90,
      result: 'success'
    },
    {
      id: 'log_seed_4',
      actorUid: 'usr_ebenezer_mugisha',
      actorName: 'Ebenezer Mugisha',
      actorRole: 'accountant',
      action: 'Financial Transaction Recorded',
      module: 'finance',
      resourceType: 'transaction',
      resourceId: 'tx_fin_01',
      details: 'Recorded equipment tithes and offerings deposit (150,000 RWF)',
      timestamp: now - 1000 * 60 * 180,
      result: 'success'
    },
    {
      id: 'log_seed_5',
      actorUid: 'usr_liona_akaliza',
      actorName: 'Liona Akaliza',
      actorRole: 'bible_study',
      action: 'Bible Study Scheduled',
      module: 'bible_study',
      resourceType: 'bible_study',
      resourceId: 'bs_01',
      details: 'Scheduled "Transformed Minds, Consecrated Hearts" session',
      timestamp: now - 1000 * 60 * 240,
      result: 'success'
    },
    {
      id: 'log_seed_6',
      actorUid: 'usr_kellia_mwizerwa',
      actorName: 'Kellia Mwizerwa',
      actorRole: 'social_media',
      action: 'Gospel Content Published',
      module: 'social_media',
      resourceType: 'post',
      resourceId: 'sp_01',
      details: 'Published weekly Gospel Reel on Instagram & TikTok reaching 1,200 youth',
      timestamp: now - 1000 * 60 * 360,
      result: 'success'
    }
  ];
}
