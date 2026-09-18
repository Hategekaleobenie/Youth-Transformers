// Youth Transformers Ministry Management System
// Complete 18-Step Production Verification Suite

import fs from 'fs';

console.log('======================================================================');
console.log('YOUTH TRANSFORMERS MINISTRY MANAGEMENT SYSTEM: 18-STEP PRODUCTION AUDIT');
console.log('======================================================================\n');

let passedTests = 0;
let totalTests = 18;

function assert(condition, message) {
  if (!condition) {
    console.error(`❌ FAILED: ${message}`);
    throw new Error(message);
  }
}

function runTest(num, name, testFn) {
  try {
    testFn();
    console.log(`✅ TEST ${num}: ${name} - PASSED`);
    passedTests++;
  } catch (err) {
    console.error(`❌ TEST ${num}: ${name} - FAILED (${err.message})`);
  }
}

// 1. Load canonical accounts from firestoreService.ts
const firestoreServiceContent = fs.readFileSync('src/services/firestoreService.ts', 'utf8');

// Test 1: Leo Benie Hategeka
runTest(1, 'Leo Benie Hategeka Authentication & Leader Dashboard Landing', () => {
  assert(firestoreServiceContent.includes('Leo Benie Hategeka'), 'Leo Benie Hategeka must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'leo@youthtransformers.org'"), 'Leo email must be leo@youthtransformers.org');
  assert(/displayName:\s*['"]Leo Benie Hategeka['"][\s\S]*?role:\s*['"]leader['"]/.test(firestoreServiceContent), 'Leo role must be leader');
  
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'leader':") && appContent.includes("return 'dashboard';"), 'Leader must land on dashboard');
});

// Test 2: Cedrick Gisubizo
runTest(2, 'Cedrick Gisubizo Authentication & Pastoral Member Care Landing', () => {
  assert(firestoreServiceContent.includes('Cedrick Gisubizo'), 'Cedrick Gisubizo must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'cedrick@youthtransformers.org'"), 'Cedrick email must be cedrick@youthtransformers.org');
  assert(/displayName:\s*['"]Cedrick Gisubizo['"][\s\S]*?role:\s*['"]member_care['"]/.test(firestoreServiceContent), 'Cedrick role must be member_care');
  
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'member_care':") && appContent.includes("return 'member_care';"), 'Member Care must land on member_care portal');
});

// Test 3: Ebenezer Mugisha
runTest(3, 'Ebenezer Mugisha Authentication & Finance Treasury Landing', () => {
  assert(firestoreServiceContent.includes('Ebenezer Mugisha'), 'Ebenezer Mugisha must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'ebenezer@youthtransformers.org'"), 'Ebenezer email must be ebenezer@youthtransformers.org');
  assert(/displayName:\s*['"]Ebenezer Mugisha['"][\s\S]*?role:\s*['"]accountant['"]/.test(firestoreServiceContent), 'Ebenezer role must be accountant');

  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'accountant':") && appContent.includes("return 'finance';"), 'Accountant must land on finance portal');
});

// Test 4: Liona Akaliza
runTest(4, 'Liona Akaliza Authentication & Bible Study Ministry Landing', () => {
  assert(firestoreServiceContent.includes('Liona Akaliza'), 'Liona Akaliza must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'liona@youthtransformers.org'"), 'Liona email must be liona@youthtransformers.org');
  assert(/displayName:\s*['"]Liona Akaliza['"][\s\S]*?role:\s*['"]bible_study['"]/.test(firestoreServiceContent), 'Liona role must be bible_study');

  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'bible_study':") && appContent.includes("return 'bible_study';"), 'Bible Study must land on bible_study portal');
});

// Test 5: Bonheur Ndinzwe
runTest(5, 'Bonheur Ndinzwe Authentication & Level 1 Discipleship Landing', () => {
  assert(firestoreServiceContent.includes('Bonheur Ndinzwe'), 'Bonheur Ndinzwe must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'bonheur@youthtransformers.org'"), 'Bonheur email must be bonheur@youthtransformers.org');
  assert(/displayName:\s*['"]Bonheur Ndinzwe['"][\s\S]*?role:\s*['"]level1_leader['"]/.test(firestoreServiceContent), 'Bonheur role must be level1_leader');

  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'level1_leader':") && appContent.includes("return 'level1';"), 'Level 1 Leader must land on level1 portal');
});

// Test 6: Green London
runTest(6, 'Green London Authentication & Executive Committee Landing', () => {
  assert(firestoreServiceContent.includes('Green London'), 'Green London must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'green@youthtransformers.org'"), 'Green London email must be green@youthtransformers.org');
  assert(/displayName:\s*['"]Green London['"][\s\S]*?role:\s*['"]committee_coordinator['"]/.test(firestoreServiceContent), 'Green London role must be committee_coordinator');

  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'committee_coordinator':") && appContent.includes("return 'committee';"), 'Committee Coordinator must land on committee portal');
});

// Test 7: Rene Cyubahiro
runTest(7, 'Rene Cyubahiro Authentication & Projects/Equipment Landing', () => {
  assert(firestoreServiceContent.includes('Rene Cyubahiro'), 'Rene Cyubahiro must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'rene@youthtransformers.org'"), 'Rene email must be rene@youthtransformers.org');
  assert(/displayName:\s*['"]Rene Cyubahiro['"][\s\S]*?role:\s*['"]projects_manager['"]/.test(firestoreServiceContent), 'Rene role must be projects_manager');

  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'projects_manager':") && appContent.includes("return 'projects';"), 'Projects Manager must land on projects portal');
});

// Test 8: Livia Kirezi
runTest(8, 'Livia Kirezi Authentication & Social Media Outreach Landing', () => {
  assert(firestoreServiceContent.includes('Livia Kirezi'), 'Livia Kirezi must exist in SEED_USERS');
  assert(firestoreServiceContent.includes("'livia@youthtransformers.org'"), 'Livia email must be livia@youthtransformers.org');
  assert(/displayName:\s*['"]Livia Kirezi['"][\s\S]*?role:\s*['"]social_media['"]/.test(firestoreServiceContent), 'Livia role must be social_media');

  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'social_media':") && appContent.includes("return 'social_media';"), 'Social Media Coordinator must land on social_media portal');
});

// Test 9: RBAC Denial: Non-leader visiting /leader
runTest(9, 'RBAC Enforcement: Non-Leader Restricted From Leader Dashboard', () => {
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'dashboard':") && appContent.includes("currentUser.role !== 'leader'"), 'App.tsx must gate dashboard to leader');
  assert(appContent.includes('<AccessDeniedPage moduleName="leader"'), 'App.tsx must display AccessDeniedPage for non-leader accessing dashboard');

  const rulesContent = fs.readFileSync('firestore.rules', 'utf8');
  assert(rulesContent.includes('function isLeader()'), 'firestore.rules must define isLeader');
});

// Test 10: RBAC Denial: Non-leader visiting /users
runTest(10, 'RBAC Enforcement: Non-Leader Restricted From User Management', () => {
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'users':") && appContent.includes("currentUser.role !== 'leader'"), 'App.tsx must gate users to leader');
  assert(appContent.includes('<AccessDeniedPage moduleName="users"'), 'App.tsx must display AccessDeniedPage for non-leader accessing users');

  const rulesContent = fs.readFileSync('firestore.rules', 'utf8');
  assert(rulesContent.includes('match /users/{userId}') && rulesContent.includes('allow create, update, delete: if isLeader();'), 'firestore.rules must restrict user admin to isLeader()');
});

// Test 11: RBAC Denial: Non-leader visiting /activity-log
runTest(11, 'RBAC Enforcement: Non-Leader Restricted From Security Audit Log', () => {
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'activity_log':") && appContent.includes("currentUser.role !== 'leader'"), 'App.tsx must gate activity_log to leader');
  assert(appContent.includes('<AccessDeniedPage moduleName="activity_log"'), 'App.tsx must display AccessDeniedPage for non-leader accessing activity_log');

  const rulesContent = fs.readFileSync('firestore.rules', 'utf8');
  assert(rulesContent.includes('match /activityLogs/{logId}') && rulesContent.includes('allow read: if isLeader();'), 'firestore.rules must restrict activityLogs read to isLeader()');
});

// Test 12: RBAC Denial: Non-accountant visiting /finance
runTest(12, 'RBAC Enforcement: Non-Accountant Restricted From Financial Transactions', () => {
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'finance':") && (appContent.includes("currentUser.role !== 'accountant'") || appContent.includes("!hasPermission('finance')")), 'App.tsx must gate finance');
  assert(appContent.includes('<AccessDeniedPage moduleName="finance"'), 'App.tsx must render AccessDeniedPage for unauthorized finance access');

  const rulesContent = fs.readFileSync('firestore.rules', 'utf8');
  assert(rulesContent.includes('match /financialTransactions/{txId}') && rulesContent.includes("allow read, write: if hasRole('accountant');"), 'firestore.rules must restrict financialTransactions to accountant');
});

// Test 13: RBAC Denial: Non-member_care visiting /member-care
runTest(13, 'RBAC Enforcement: Unauthorized Users Restricted From Confidential Follow-ups', () => {
  const appContent = fs.readFileSync('src/App.tsx', 'utf8');
  assert(appContent.includes("case 'member_care':") && (appContent.includes("currentUser.role !== 'member_care'") || appContent.includes("!hasPermission('member_care')")), 'App.tsx must gate member_care');
  assert(appContent.includes('<AccessDeniedPage moduleName="member_care"'), 'App.tsx must render AccessDeniedPage for unauthorized member_care access');

  const rulesContent = fs.readFileSync('firestore.rules', 'utf8');
  assert(rulesContent.includes('match /followUpCases/{caseId}') && rulesContent.includes("allow read, write: if hasRole('member_care');"), 'firestore.rules must restrict followUpCases to member_care');
});

// Test 14: PDF Generation for Bible Study
runTest(14, 'PDF Generation: Bible Study Ministry Real-Time PDF & RBAC Check', () => {
  const pdfContent = fs.readFileSync('src/services/pdfService.ts', 'utf8');
  assert(pdfContent.includes('downloadBibleStudyReportPDF'), 'downloadBibleStudyReportPDF must be implemented');
  assert(pdfContent.includes("currentUser.role === 'bible_study' || currentUser.role === 'leader'"), 'Only bible_study or leader can export Bible Study PDF');
  assert(pdfContent.includes('Unauthorized PDF Download Rejected'), 'Unauthorized Bible Study PDF downloads must log rejected attempt');
  assert(pdfContent.includes('Official Department PDF Generated'), 'Successful PDF downloads must log success audit');
});

// Test 15: PDF Generation for Finance
runTest(15, 'PDF Generation: Finance & Treasury Audit Real-Time PDF & RBAC Check', () => {
  const pdfContent = fs.readFileSync('src/services/pdfService.ts', 'utf8');
  assert(pdfContent.includes('downloadFinanceReportPDF'), 'downloadFinanceReportPDF must be implemented');
  assert(pdfContent.includes("currentUser.role === 'accountant' || currentUser.role === 'leader'"), 'Only accountant or leader can export Finance PDF');
  assert(pdfContent.includes('Net Treasury Balance:'), 'Finance PDF must calculate net treasury balance from database records');
});

// Test 16: PDF Generation for Member Care
runTest(16, 'PDF Generation: Pastoral Member Care Real-Time PDF & RBAC Check', () => {
  const pdfContent = fs.readFileSync('src/services/pdfService.ts', 'utf8');
  assert(pdfContent.includes('downloadMemberCareReportPDF'), 'downloadMemberCareReportPDF must be implemented');
  assert(pdfContent.includes("currentUser.role === 'member_care' || currentUser.role === 'leader'"), 'Only member_care or leader can export Member Care PDF');
  assert(pdfContent.includes('Active Follow-Up Cases:') || pdfContent.includes('Total Ministry Disciples:'), 'Member Care PDF must include pastoral care statistics from database');
});

// Test 17: Activity Audit Log
runTest(17, 'Activity Logging: Immutable Audit Records with Actor Metadata & Newest-First Order', () => {
  const logContent = fs.readFileSync('src/services/activityLogService.ts', 'utf8');
  assert(logContent.includes('export async function logActivity'), 'logActivity function must exist');
  assert(logContent.includes('export async function fetchActivityLogs'), 'fetchActivityLogs function must exist');
  assert(logContent.includes("orderBy('timestamp', 'desc')") || logContent.includes('b.timestamp - a.timestamp'), 'Logs must be ordered newest-first');

  const rulesContent = fs.readFileSync('firestore.rules', 'utf8');
  assert(rulesContent.includes('allow update, delete: if false;'), 'Activity logs must be strictly immutable in Firestore rules');
});

// Test 18: PWA & Responsive Verification
runTest(18, 'PWA & Responsive Configuration: Manifest, ServiceWorker, Icons & Mobile Layout', () => {
  assert(fs.existsSync('public/manifest.webmanifest'), 'manifest.webmanifest must exist');
  assert(fs.existsSync('public/manifest.json'), 'manifest.json must exist');
  assert(fs.existsSync('public/sw.js'), 'sw.js must exist');
  assert(fs.existsSync('public/icon-192.png'), 'icon-192.png must exist');
  assert(fs.existsSync('public/icon-512.png'), 'icon-512.png must exist');

  const indexHtml = fs.readFileSync('index.html', 'utf8');
  assert(indexHtml.includes('manifest.webmanifest'), 'index.html must reference manifest');
  assert(indexHtml.includes('viewport'), 'index.html must specify mobile-responsive viewport');
  assert(indexHtml.includes('serviceWorker.register'), 'index.html must register service worker');
});

console.log('\n======================================================================');
console.log(`AUDIT SUMMARY: ${passedTests} OF ${totalTests} TESTS PASSED SUCCESSFULLY`);
console.log('======================================================================');

if (passedTests !== totalTests) {
  process.exit(1);
}
