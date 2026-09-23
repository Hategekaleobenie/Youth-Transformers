import {
  collection,
  doc,
  getDocs,
  getDoc,
  setDoc,
  updateDoc,
  addDoc,
  deleteDoc,
  query,
  where,
  orderBy
} from 'firebase/firestore';
import { db, isFirebaseConfigured, firebaseConfig } from '../config/firebase';

function fromFirestoreValue(value: any): any {
  if (!value || typeof value !== 'object') return value;
  if ('stringValue' in value) return value.stringValue;
  if ('integerValue' in value) return Number(value.integerValue);
  if ('doubleValue' in value) return Number(value.doubleValue);
  if ('booleanValue' in value) return value.booleanValue;
  if ('nullValue' in value) return null;
  if ('timestampValue' in value) return value.timestampValue;
  if ('arrayValue' in value) return (value.arrayValue.values || []).map(fromFirestoreValue);
  if ('mapValue' in value) {
    const out: Record<string, any> = {};
    for (const [key, val] of Object.entries(value.mapValue.fields || {})) out[key] = fromFirestoreValue(val);
    return out;
  }
  return value;
}

async function getUserByIdViaRest(uid: string, idToken: string): Promise<T.UserProfile | null> {
  const url =
    `https://firestore.googleapis.com/v1/projects/${encodeURIComponent(firebaseConfig.projectId)}/databases/(default)/documents/users/${encodeURIComponent(uid)}?key=${encodeURIComponent(firebaseConfig.apiKey)}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${idToken}`,
      Accept: 'application/json'
    },
    cache: 'no-store'
  });

  if (response.status === 404) return null;

  const body = await response.text();
  if (!response.ok) {
    throw new Error(`Firestore REST request failed (${response.status}): ${body.slice(0, 300)}`);
  }

  const parsed = JSON.parse(body);
  return {
    uid,
    ...(Object.fromEntries(
      Object.entries(parsed.fields || {}).map(([key, value]) => [key, fromFirestoreValue(value)])
    ) as T.UserProfile)
  };
}
import * as T from '../types';

// Storage keys for offline/dev fallback
const K_USERS = 'yt_users_data';
const K_MEMBERS = 'yt_members_data';
const K_CASES = 'yt_cases_data';
const K_BIBLE = 'yt_bible_data';
const K_ATTENDANCE = 'yt_attendance_data';
const K_FINANCE = 'yt_finance_data';
const K_PROJECTS = 'yt_projects_data';
const K_EQUIPMENT = 'yt_equipment_data';
const K_SOCIAL = 'yt_social_data';
const K_EVANGELISM = 'yt_evangelism_data';
const K_EVENTS = 'yt_events_data';
const K_REPORTS = 'yt_reports_data';
const K_COMMITTEE_TASKS = 'yt_committee_tasks_data';
const K_ANNOUNCEMENTS = 'yt_announcements_data';
const K_NOTIFS = 'yt_notifs_data';

function getLocal<I>(key: string, initial: I[]): I[] {
  try {
    const raw = localStorage.getItem(key);
    if (raw) {
      const parsed = JSON.parse(raw);
      if (key === K_USERS && Array.isArray(parsed)) {
        // Guarantee all canonical seed users exist
        let changed = false;
        for (const seedItem of initial as any[]) {
          const exists = parsed.some((p: any) => p.email?.toLowerCase() === seedItem.email?.toLowerCase() || p.uid === seedItem.uid);
          if (!exists) {
            parsed.push(seedItem);
            changed = true;
          }
        }
        if (changed) {
          localStorage.setItem(key, JSON.stringify(parsed));
        }
      }
      return parsed;
    }
  } catch (e) {}
  localStorage.setItem(key, JSON.stringify(initial));
  return initial;
}

function setLocal<I>(key: string, data: I[]): void {
  try {
    localStorage.setItem(key, JSON.stringify(data));
  } catch (e) {}
}

// ================= USERS =================
export async function getUsers(): Promise<T.UserProfile[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'users'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ uid: d.id, ...d.data() } as T.UserProfile));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.UserProfile>(K_USERS, SEED_USERS);
}

export async function getUserById(uid: string, idToken?: string): Promise<T.UserProfile | null> {
  if (isFirebaseConfigured) {
    // Authentication already gives us an ID token. Use the authenticated
    // Firestore REST endpoint first so profile loading does not depend on the
    // browser's Firestore WebChannel transport.
    if (idToken) {
      try {
        return await getUserByIdViaRest(uid, idToken);
      } catch (restError) {
        console.warn('Firestore REST profile read failed; falling back to SDK:', restError);
      }
    }

    try {
      const snap = await getDoc(doc(db, 'users', uid));
      if (snap.exists()) {
        return { uid: snap.id, ...snap.data() } as T.UserProfile;
      }
      return null;
    } catch (e: any) {
      throw e;
    }
  }

  const all = await getUsers();
  return all.find(u => u.uid === uid) || null;
}

export async function saveUser(user: T.UserProfile): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'users', user.uid), user);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getUsers();
  const idx = all.findIndex(u => u.uid === user.uid);
  if (idx >= 0) all[idx] = user;
  else all.push(user);
  setLocal(K_USERS, all);
}

export async function updateUserStatus(uid: string, status: T.AccountStatus): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await updateDoc(doc(db, 'users', uid), { status });
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getUsers();
  const found = all.find(u => u.uid === uid);
  if (found) {
    found.status = status;
    setLocal(K_USERS, all);
  }
}

// ================= MEMBERS =================
export async function getMembers(): Promise<T.Member[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'members'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.Member));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.Member>(K_MEMBERS, SEED_MEMBERS);
}

export async function saveMember(member: T.Member): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'members', member.id), member);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getMembers();
  const idx = all.findIndex(m => m.id === member.id);
  if (idx >= 0) all[idx] = member;
  else all.unshift(member);
  setLocal(K_MEMBERS, all);
}

// ================= FOLLOW UP CASES =================
export async function getFollowUpCases(): Promise<T.FollowUpCase[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'followUpCases'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.FollowUpCase));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.FollowUpCase>(K_CASES, SEED_CASES);
}

export async function saveFollowUpCase(item: T.FollowUpCase): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'followUpCases', item.id), item);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getFollowUpCases();
  const idx = all.findIndex(c => c.id === item.id);
  if (idx >= 0) all[idx] = item;
  else all.unshift(item);
  setLocal(K_CASES, all);
}

// ================= BIBLE STUDIES =================
export async function getBibleStudies(): Promise<T.BibleStudy[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'bibleStudies'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.BibleStudy));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.BibleStudy>(K_BIBLE, SEED_BIBLE);
}

export async function saveBibleStudy(study: T.BibleStudy): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'bibleStudies', study.id), study);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getBibleStudies();
  const idx = all.findIndex(b => b.id === study.id);
  if (idx >= 0) all[idx] = study;
  else all.unshift(study);
  setLocal(K_BIBLE, all);
}

// ================= ATTENDANCE =================
export async function getAttendance(): Promise<T.AttendanceRecord[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'attendanceRecords'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.AttendanceRecord));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.AttendanceRecord>(K_ATTENDANCE, SEED_ATTENDANCE);
}

export async function saveAttendance(rec: T.AttendanceRecord): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'attendanceRecords', rec.id), rec);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getAttendance();
  all.unshift(rec);
  setLocal(K_ATTENDANCE, all);
}

// ================= FINANCE =================
export async function getFinanceTransactions(): Promise<T.FinancialTransaction[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'financialTransactions'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.FinancialTransaction));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.FinancialTransaction>(K_FINANCE, SEED_FINANCE);
}

export async function saveFinanceTransaction(tx: T.FinancialTransaction): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'financialTransactions', tx.id), tx);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getFinanceTransactions();
  all.unshift(tx);
  setLocal(K_FINANCE, all);
}

// ================= PROJECTS & EQUIPMENT =================
export async function getProjects(): Promise<T.Project[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'projects'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.Project));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.Project>(K_PROJECTS, SEED_PROJECTS);
}

export async function saveProject(proj: T.Project): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'projects', proj.id), proj);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getProjects();
  const idx = all.findIndex(p => p.id === proj.id);
  if (idx >= 0) all[idx] = proj;
  else all.unshift(proj);
  setLocal(K_PROJECTS, all);
}

export async function getEquipment(): Promise<T.EquipmentItem[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'equipment'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.EquipmentItem));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.EquipmentItem>(K_EQUIPMENT, SEED_EQUIPMENT);
}

export async function saveEquipment(item: T.EquipmentItem): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'equipment', item.id), item);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getEquipment();
  const idx = all.findIndex(e => e.id === item.id);
  if (idx >= 0) all[idx] = item;
  else all.unshift(item);
  setLocal(K_EQUIPMENT, all);
}

// ================= SOCIAL MEDIA =================
export async function getSocialPosts(): Promise<T.SocialPost[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'socialPosts'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.SocialPost));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.SocialPost>(K_SOCIAL, SEED_SOCIAL);
}

export async function saveSocialPost(post: T.SocialPost): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'socialPosts', post.id), post);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getSocialPosts();
  const idx = all.findIndex(s => s.id === post.id);
  if (idx >= 0) all[idx] = post;
  else all.unshift(post);
  setLocal(K_SOCIAL, all);
}

// ================= EVANGELISM & EVENTS =================
export async function getEvangelismRecords(): Promise<T.EvangelismRecord[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'evangelismRecords'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.EvangelismRecord));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.EvangelismRecord>(K_EVANGELISM, SEED_EVANGELISM);
}

export async function saveEvangelismRecord(rec: T.EvangelismRecord): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'evangelismRecords', rec.id), rec);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getEvangelismRecords();
  all.unshift(rec);
  setLocal(K_EVANGELISM, all);
}

export async function getMinistryEvents(): Promise<T.MinistryEvent[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'ministryEvents'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.MinistryEvent));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.MinistryEvent>(K_EVENTS, SEED_EVENTS);
}

export async function saveMinistryEvent(ev: T.MinistryEvent): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'ministryEvents', ev.id), ev);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getMinistryEvents();
  const idx = all.findIndex(e => e.id === ev.id);
  if (idx >= 0) all[idx] = ev;
  else all.unshift(ev);
  setLocal(K_EVENTS, all);
}

// ================= COMMITTEE REPORTS =================
export async function getReports(): Promise<T.CommitteeReport[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'committeeReports'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.CommitteeReport));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.CommitteeReport>(K_REPORTS, SEED_REPORTS);
}

export async function saveReport(report: T.CommitteeReport): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'committeeReports', report.id), report);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getReports();
  const idx = all.findIndex(r => r.id === report.id);
  if (idx >= 0) all[idx] = report;
  else all.unshift(report);
  setLocal(K_REPORTS, all);
}

// ================= COMMITTEE TASKS =================
export async function getCommitteeTasks(): Promise<T.CommitteeTask[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'committeeTasks'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.CommitteeTask));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.CommitteeTask>(K_COMMITTEE_TASKS, SEED_COMMITTEE_TASKS);
}

export async function saveCommitteeTask(task: T.CommitteeTask): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'committeeTasks', task.id), task);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getCommitteeTasks();
  const idx = all.findIndex(t => t.id === task.id);
  if (idx >= 0) all[idx] = task;
  else all.unshift(task);
  setLocal(K_COMMITTEE_TASKS, all);
}

// ================= ANNOUNCEMENTS =================
export async function getAnnouncements(): Promise<T.Announcement[]> {
  if (isFirebaseConfigured) {
    try {
      const snap = await getDocs(collection(db, 'announcements'));
      if (!snap.empty) {
        return snap.docs.map(d => ({ id: d.id, ...d.data() } as T.Announcement));
      }
    } catch (e) {
      throw e;
    }
  }
  return getLocal<T.Announcement>(K_ANNOUNCEMENTS, SEED_ANNOUNCEMENTS);
}

export async function saveAnnouncement(item: T.Announcement): Promise<void> {
  if (isFirebaseConfigured) {
    try {
      await setDoc(doc(db, 'announcements', item.id), item);
      return;
    } catch (e) {
      throw e;
    }
  }
  const all = await getAnnouncements();
  const idx = all.findIndex(a => a.id === item.id);
  if (idx >= 0) all[idx] = item;
  else all.unshift(item);
  setLocal(K_ANNOUNCEMENTS, all);
}

// ================= NOTIFICATIONS =================
export async function getNotifications(userUid: string): Promise<T.NotificationItem[]> {
  const all = getLocal<T.NotificationItem>(K_NOTIFS, SEED_NOTIFS);
  return all.filter(n => n.recipientUid === userUid || n.recipientUid === 'all');
}

export async function markNotificationRead(id: string): Promise<void> {
  const all = getLocal<T.NotificationItem>(K_NOTIFS, SEED_NOTIFS);
  const found = all.find(n => n.id === id);
  if (found) {
    found.read = true;
    setLocal(K_NOTIFS, all);
  }
}

// ================= AUTHENTIC INITIAL SEED DATA (DEVELOPMENT ONLY) =================
const now = Date.now();

export const SEED_USERS: T.UserProfile[] = [
  {
    uid: 'usr_leo_hategeka',
    email: 'leo@youthtransformers.org',
    displayName: 'Leo Benie Hategeka',
    phone: '+250 788 000 001',
    role: 'leader',
    department: 'General Leadership & Oversight',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 180,
    lastLoginAt: now - 1000 * 60 * 15,
    assignedPermissions: ['*']
  },
  {
    uid: 'usr_green_london',
    email: 'green@youthtransformers.org',
    displayName: 'Green London',
    phone: '+250 788 000 002',
    role: 'committee_coordinator',
    department: 'Committee Coordination',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 170,
    lastLoginAt: now - 1000 * 60 * 45,
    assignedPermissions: ['committee:manage', 'reports:create', 'announcements:create']
  },
  {
    uid: 'usr_bonheur_ndinzwe',
    email: 'bonheur@youthtransformers.org',
    displayName: 'Bonheur Ndinzwe',
    phone: '+250 788 000 003',
    role: 'level1_leader',
    department: 'Level 1 Discipleship',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 165,
    lastLoginAt: now - 1000 * 60 * 120,
    assignedPermissions: ['level1:manage', 'members:view']
  },
  {
    uid: 'usr_livia_kirezi',
    email: 'livia@youthtransformers.org',
    displayName: 'Livia Kirezi',
    phone: '+250 788 000 004',
    role: 'social_media',
    department: 'Social Media & Digital Gospel',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 150,
    lastLoginAt: now - 1000 * 60 * 180,
    assignedPermissions: ['social_media:manage', 'announcements:create']
  },
  {
    uid: 'usr_kellia_mwizerwa',
    email: 'kellia@youthtransformers.org',
    displayName: 'Kellia Mwizerwa',
    phone: '+250 788 000 009',
    role: 'social_media',
    department: 'Social Media & Digital Gospel',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 150,
    lastLoginAt: now - 1000 * 60 * 180,
    assignedPermissions: ['social_media:manage', 'announcements:create']
  },
  {
    uid: 'usr_cedrick_gisubizo',
    email: 'cedrick@youthtransformers.org',
    displayName: 'Cedrick Gisubizo',
    phone: '+250 788 000 005',
    role: 'member_care',
    department: 'Pastoral Member Care & Welfare',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 160,
    lastLoginAt: now - 1000 * 60 * 200,
    assignedPermissions: ['member_care:manage', 'members:view', 'members:create', 'members:edit']
  },
  {
    uid: 'usr_liona_akaliza',
    email: 'liona@youthtransformers.org',
    displayName: 'Liona Akaliza',
    phone: '+250 788 000 006',
    role: 'bible_study',
    department: 'Bible Study & Spiritual Growth',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 155,
    lastLoginAt: now - 1000 * 60 * 240,
    assignedPermissions: ['bible_study:manage', 'attendance:create', 'attendance:view']
  },
  {
    uid: 'usr_ebenezer_mugisha',
    email: 'ebenezer@youthtransformers.org',
    displayName: 'Ebenezer Mugisha',
    phone: '+250 788 000 007',
    role: 'accountant',
    department: 'Finance & Treasury',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 175,
    lastLoginAt: now - 1000 * 60 * 300,
    assignedPermissions: ['finance:manage', 'finance:export']
  },
  {
    uid: 'usr_rene_cyubahiro',
    email: 'rene@youthtransformers.org',
    displayName: 'Rene Cyubahiro',
    phone: '+250 788 000 008',
    role: 'projects_manager',
    department: 'Projects & Equipment Maintenance',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 140,
    lastLoginAt: now - 1000 * 60 * 400,
    assignedPermissions: ['projects:manage', 'equipment:manage']
  },
  {
    uid: 'usr_member_sample',
    email: 'member@youthtransformers.org',
    displayName: 'Aline Umutoni',
    phone: '+250 788 123 456',
    role: 'member',
    department: 'General Youth Ministry',
    status: 'active',
    createdAt: now - 1000 * 60 * 60 * 24 * 60,
    lastLoginAt: now - 1000 * 60 * 60 * 24,
    assignedPermissions: []
  }
];

export const SEED_MEMBERS: T.Member[] = [
  {
    id: 'mem_001',
    fullName: 'David Niyonkuru',
    phone: '+250 788 111 222',
    email: 'david.niyonkuru@example.com',
    dateJoined: '2025-01-15',
    currentResidence: 'Kigali - Gasabo, Kimironko',
    permanentResidence: 'Huye, Southern Province',
    employmentStatus: 'Employed (Software)',
    educationStatus: 'Bachelor Degree Completed',
    familyInfo: 'Oldest sibling, Christian family',
    ministryLevel: 'Level 1',
    assignedLeader: 'Bonheur Ndinzwe',
    attendanceCount: 28,
    bibleStudyParticipation: 14,
    evangelismParticipation: 4,
    status: 'active',
    notes: 'Very attentive during Friday prayer meetings; committed disciple.',
    createdAt: now - 1000 * 60 * 60 * 24 * 200,
    updatedAt: now - 1000 * 60 * 60 * 24 * 5
  },
  {
    id: 'mem_002',
    fullName: 'Clarisse Uwase',
    phone: '+250 789 222 333',
    email: 'clarisse.u@example.com',
    dateJoined: '2025-02-10',
    currentResidence: 'Kigali - Kicukiro, Niboye',
    permanentResidence: 'Rwamagana, Eastern Province',
    employmentStatus: 'Student (University of Rwanda)',
    educationStatus: 'Undergraduate Year 3',
    familyInfo: 'Prefer not to say',
    ministryLevel: 'Level 1',
    assignedLeader: 'Cedrick Gisubizo',
    attendanceCount: 16,
    bibleStudyParticipation: 8,
    evangelismParticipation: 2,
    status: 'follow_up_required',
    notes: 'Missed recent two fellowship gatherings due to exam pressure.',
    createdAt: now - 1000 * 60 * 60 * 24 * 180,
    updatedAt: now - 1000 * 60 * 60 * 24 * 2
  },
  {
    id: 'mem_003',
    fullName: 'Patrick Mugabo',
    phone: '+250 783 333 444',
    dateJoined: '2024-11-05',
    currentResidence: 'Kigali - Nyarugenge, Nyamirambo',
    permanentResidence: 'Rubavu, Western Province',
    employmentStatus: 'Self-employed (Crafts)',
    educationStatus: 'High School Diploma',
    familyInfo: 'Single, supporting mother',
    ministryLevel: 'Level 2',
    assignedLeader: 'Leo Benie Hategeka',
    attendanceCount: 42,
    bibleStudyParticipation: 22,
    evangelismParticipation: 9,
    status: 'active',
    notes: 'Serving in acoustic setup and praise mobilization.',
    createdAt: now - 1000 * 60 * 60 * 24 * 250,
    updatedAt: now - 1000 * 60 * 60 * 24 * 10
  }
];

export const SEED_CASES: T.FollowUpCase[] = [
  {
    id: 'case_fc_01',
    memberId: 'mem_002',
    memberName: 'Clarisse Uwase',
    assignedToUid: 'usr_cedrick_gisubizo',
    assignedToName: 'Cedrick Gisubizo',
    reason: 'Academic',
    dateOpened: '2026-09-12',
    priority: 'medium',
    status: 'open',
    notes: 'Clarisse requested academic prayers during her upcoming midterms.',
    nextFollowUpDate: '2026-09-22',
    createdAt: now - 1000 * 60 * 60 * 24 * 5,
    updatedAt: now - 1000 * 60 * 60 * 24 * 1
  }
];

export const SEED_BIBLE: T.BibleStudy[] = [
  {
    id: 'bs_01',
    title: 'Transformed by the Word: Romans 12:1-2',
    scriptureReferences: 'Romans 12:1-2; Ephesians 4:22-24',
    dateStr: '2026-09-20',
    timeStr: '17:00 - 19:00',
    location: 'Main Youth Sanctuary, Hall B',
    preacher: 'Liona Akaliza',
    host: 'Cedrick Gisubizo',
    curriculum: 'Spiritual Foundations & Consecration',
    expectedAttendance: 45,
    actualAttendance: 41,
    recurrence: 'weekly',
    notes: 'Bring journals and scripture notebooks. Discussion on renewing our mindset.',
    createdAt: now - 1000 * 60 * 60 * 24 * 3
  }
];

export const SEED_ATTENDANCE: T.AttendanceRecord[] = [
  {
    id: 'att_01',
    memberId: 'mem_001',
    memberName: 'David Niyonkuru',
    eventId: 'bs_01',
    eventName: 'Romans 12 Study',
    eventType: 'Bible Study',
    dateStr: '2026-09-13',
    status: 'present',
    recordedBy: 'Liona Akaliza',
    createdAt: now - 1000 * 60 * 60 * 24 * 4
  }
];

export const SEED_FINANCE: T.FinancialTransaction[] = [
  {
    id: 'tx_01',
    dateStr: '2026-09-15',
    description: 'Youth Monthly Tithes and Pledges',
    category: 'Donations',
    amount: 320000,
    type: 'income',
    recordedBy: 'Ebenezer Mugisha',
    notes: 'Collected during Sunday Youth Service and online bank transfers',
    createdAt: now - 1000 * 60 * 60 * 24 * 2
  },
  {
    id: 'tx_02',
    dateStr: '2026-09-16',
    description: 'Sound System Wireless Microphone Batteries & Cables',
    category: 'Equipment',
    amount: 45000,
    type: 'expense',
    recordedBy: 'Ebenezer Mugisha',
    notes: 'Requisition approved by Leader Leo Hategeka for Bible study sound system',
    createdAt: now - 1000 * 60 * 60 * 24 * 1
  }
];

export const SEED_PROJECTS: T.Project[] = [
  {
    id: 'proj_01',
    name: 'Youth Media Production & Podcast Setup',
    description: 'Procuring acoustic foam, lighting, and recording microphones for weekly Gospel videos.',
    leader: 'Rene Cyubahiro',
    startDate: '2026-09-01',
    endDate: '2026-10-15',
    budget: 600000,
    status: 'active',
    progressPercent: 65,
    tasksCount: 6,
    completedTasksCount: 4,
    team: ['Rene Cyubahiro', 'Kellia Mwizerwa', 'Ebenezer Mugisha'],
    notes: 'Acoustic panels installed. Remaining: video camera tripod adjustments.',
    createdAt: now - 1000 * 60 * 60 * 24 * 16,
    updatedAt: now - 1000 * 60 * 60 * 24 * 1
  }
];

export const SEED_EQUIPMENT: T.EquipmentItem[] = [
  {
    id: 'eq_01',
    name: 'Yamaha EMX5 Powered Mixer (12 Channels)',
    category: 'Audio / PA',
    quantity: 1,
    condition: 'Excellent',
    location: 'Youth Hall Sound Booth',
    responsiblePerson: 'Rene Cyubahiro',
    status: 'available',
    purchaseDate: '2025-06-10',
    lastMaintenanceDate: '2026-08-15',
    notes: 'Used for Sunday youth services, Friday vigils, and outreach rallies.',
    createdAt: now - 1000 * 60 * 60 * 24 * 90
  },
  {
    id: 'eq_02',
    name: 'Shure SM58 Vocal Microphone Pair',
    category: 'Audio / Vocal',
    quantity: 2,
    condition: 'Good',
    location: 'Sound Booth Drawer 1',
    responsiblePerson: 'Rene Cyubahiro',
    status: 'in_use',
    purchaseDate: '2025-08-20',
    lastMaintenanceDate: '2026-09-01',
    createdAt: now - 1000 * 60 * 60 * 24 * 80
  }
];

export const SEED_SOCIAL: T.SocialPost[] = [
  {
    id: 'sp_01',
    title: 'Transforming Your Mindset - Short Gospel Video',
    platform: 'Instagram',
    postDate: '2026-09-17',
    status: 'published',
    link: 'https://instagram.com/reel/youthtransformers',
    responsiblePerson: 'Kellia Mwizerwa',
    approvalStatus: 'approved',
    views: 2450,
    likes: 380,
    notes: 'Targeting youth in universities and colleges with Gospel hope.',
    createdAt: now - 1000 * 60 * 60 * 20
  },
  {
    id: 'sp_02',
    title: 'Weekly Scripture Reflection: Philippians 4:13',
    platform: 'TikTok',
    postDate: '2026-09-16',
    status: 'published',
    responsiblePerson: 'Kellia Mwizerwa',
    approvalStatus: 'approved',
    views: 4100,
    likes: 620,
    createdAt: now - 1000 * 60 * 60 * 44
  },
  {
    id: 'sp_03',
    title: 'Youth Transformers Fellowship Invitation Graphic',
    platform: 'WhatsApp',
    postDate: '2026-09-15',
    status: 'published',
    responsiblePerson: 'Kellia Mwizerwa',
    approvalStatus: 'approved',
    views: 520,
    likes: 98,
    createdAt: now - 1000 * 60 * 60 * 68
  }
];

export const SEED_EVANGELISM: T.EvangelismRecord[] = [
  {
    id: 'ev_01',
    eventName: 'Kigali Campuses Gospel Outreach Drive',
    dateStr: '2026-09-12',
    location: 'College of Science and Technology (CST) Perimeter',
    leaderName: 'Bonheur Ndinzwe',
    teamMembers: ['Bonheur Ndinzwe', 'David Niyonkuru', 'Cedrick Gisubizo'],
    peopleReached: 120,
    newContacts: 18,
    followUpsScheduled: 9,
    testimonies: 'Three university students accepted Christ and committed to joining Level 1 discipleship.',
    notes: 'Printed 200 gospel booklets; distributed all with prayer cards.',
    createdAt: now - 1000 * 60 * 60 * 24 * 5
  }
];

export const SEED_EVENTS: T.MinistryEvent[] = [
  {
    id: 'event_01',
    title: 'Transformers Annual Youth Revival Night',
    description: 'An all-night prayer, worship, and spiritual empowerment vigil for young believers.',
    dateStr: '2026-10-02',
    timeStr: '20:00 - 05:00',
    location: 'Main Worship Auditorium',
    organizer: 'Green London',
    budget: 450000,
    expectedAttendance: 250,
    status: 'upcoming',
    createdAt: now - 1000 * 60 * 60 * 24 * 10
  }
];

export const SEED_REPORTS: T.CommitteeReport[] = [
  {
    id: 'rep_01',
    reportType: 'Department',
    department: 'Level 1 Discipleship',
    authorUid: 'usr_bonheur_ndinzwe',
    authorName: 'Bonheur Ndinzwe',
    authorRole: 'level1_leader',
    reportingPeriod: 'September 2026 - Week 2',
    dateStr: '2026-09-14',
    accomplished: 'Completed Module 2 on Personal Prayer Life. 16 candidates submitted their weekly reflective scripture journals. Conducted 3 personal discipleship check-ins.',
    currentWork: 'Preparing study materials for Module 3 (The Person and Work of the Holy Spirit). Mentoring 4 newly reached university students from the campus outreach.',
    challenges: 'Two students have timetable conflicts with the Thursday evening session; scheduling a makeup Saturday morning session.',
    supportNeeded: 'Requisition for 20 additional printed copies of the Level 1 Discipleship Handbook.',
    nextSteps: 'Conduct Saturday review and host the upcoming baptismal foundations discussion.',
    prayerNotes: 'Pray for clarity of heart and spiritual steadfastness among all Level 1 disciples.',
    status: 'approved',
    createdAt: now - 1000 * 60 * 60 * 24 * 3,
    updatedAt: now - 1000 * 60 * 60 * 24 * 2
  },
  {
    id: 'rep_02',
    reportType: 'Finance',
    department: 'Finance & Treasury',
    authorUid: 'usr_ebenezer_mugisha',
    authorName: 'Ebenezer Mugisha',
    authorRole: 'accountant',
    reportingPeriod: 'September 2026',
    dateStr: '2026-09-16',
    accomplished: 'Reconciled all weekly offerings, tithes, and donor pledges. Disbursed audio cable equipment purchase. Balance sheet is fully balanced.',
    currentWork: 'Preparing Q3 budgetary expenditure summary for Leader Leo Hategeka and executive committee.',
    challenges: 'Ensuring all departmental expense receipts are submitted within 48 hours of disbursement.',
    supportNeeded: 'Digital receipt submission reminder to committee heads.',
    nextSteps: 'Publish monthly treasury report and present audit summary at the committee meeting.',
    prayerNotes: 'Thank God for faithfulness in ministry resources and generous youth hearts.',
    status: 'submitted',
    createdAt: now - 1000 * 60 * 60 * 24 * 1,
    updatedAt: now - 1000 * 60 * 60 * 24 * 1
  }
];

export const SEED_ANNOUNCEMENTS: T.Announcement[] = [
  {
    id: 'ann_01',
    title: 'Transformers Weekly Fellowship & Prayer Gathering',
    content: 'Join us this Friday at 17:30 in the Main Youth Sanctuary. Topic: "Living Uncompromised in a Changing World". All youth and seekers welcome!',
    priority: 'important',
    targetAudience: 'All',
    authorName: 'Green London',
    publishDate: '2026-09-15',
    createdAt: now - 1000 * 60 * 60 * 24 * 2
  }
];

export const SEED_COMMITTEE_TASKS: T.CommitteeTask[] = [
  {
    id: 'ctask_01',
    title: 'Q3 Financial Audit & Requisition Consolidation',
    description: 'Compile verified receipts and prepare audit file for executive signoff.',
    assignedToUid: 'usr_ebenezer_mugisha',
    assignedToName: 'Ebenezer Mugisha',
    department: 'Finance & Treasury',
    dueDate: '2026-09-28',
    priority: 'high',
    deliverable: 'Audited balance sheet and bank statement reconciliations',
    status: 'submitted',
    createdAt: now - 1000 * 60 * 60 * 24 * 4
  },
  {
    id: 'ctask_02',
    title: 'Level 1 Discipleship Graduation Ceremony Planning',
    description: 'Draft schedule and coordinate certificates for disciples graduating to Level 2.',
    assignedToUid: 'usr_shema_prince',
    assignedToName: 'Shema Prince',
    department: 'Level 1 Discipleship',
    dueDate: '2026-10-05',
    priority: 'medium',
    deliverable: 'Program agenda and candidate graduation verification list',
    status: 'in_progress',
    createdAt: now - 1000 * 60 * 60 * 24 * 3
  }
];

export const SEED_NOTIFS: T.NotificationItem[] = [
  {
    id: 'notif_01',
    recipientUid: 'all',
    title: 'Committee Progress Report Due',
    message: 'All department leaders are requested to submit weekly operational reports by Friday 18:00.',
    type: 'report_request',
    read: false,
    createdAt: now - 1000 * 60 * 60 * 5
  },
  {
    id: 'notif_02',
    recipientUid: 'usr_cedrick_gisubizo',
    title: 'Follow-up Case Reminder',
    message: 'Check-in scheduled for Clarisse Uwase regarding academic support.',
    type: 'reminder',
    read: false,
    createdAt: now - 1000 * 60 * 60 * 12
  }
];
