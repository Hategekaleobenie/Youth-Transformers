# Youth Transformers — Ministry Management System

> Production Deployment & Operations Guide for GitHub & Firebase

Youth Transformers is an enterprise-grade discipleship, ministry operations, and governance management platform. Designed specifically for church leadership, pastoral care, financial stewardship, Bible study tracking, digital evangelism, infrastructure projects, and executive committee oversight, the system enforces strict Role-Based Access Control (RBAC) backed by Firebase Authentication and server-enforced Cloud Firestore Security Rules.

---

## 1. What Youth Transformers Is

Youth Transformers is a unified operational platform built to empower ministry leadership and departments with real-time operational workflows:
- **Executive Leadership**: Unrestricted oversight of church health, department performance, member growth, audit logs, and administrative user management led by **Leader Leo Benie Hategeka**.
- **Pastoral Member Care & Welfare**: Confidential tracking of disciple welfare, home visitations, spiritual follow-up cases, and support requests.
- **Finance & Treasury**: Real-time tithes, offerings, equipment expense tracking, voucher authorizations, and official treasury ledger audits.
- **Bible Study & Spiritual Growth**: Weekly curriculum tracking, scripture outlines, lesson plans, and disciple attendance rosters.
- **Level 1 Discipleship**: New believer follow-up, foundational Christian doctrine progression, and spiritual maturity milestone tracking.
- **Executive Committee Governance**: Cross-departmental task assignment, quarterly review submissions, event planning, and ministry announcements.
- **Projects & Equipment**: Infrastructure development tracking, budget allocations, and sound/media asset inventory custody logs.
- **Social Media & Digital Evangelism**: Multi-platform content calendars, online outreach engagement metrics, and campaign analytics.

---

## 2. Technology Stack

- **Frontend Core**: React 18 with TypeScript 5
- **Build Tool**: Vite 5
- **Styling & Design System**: Tailwind CSS with custom Ministry theme (`ministry-deepGreen`, `ministry-gold`, `ministry-forestDark`, `ministry-lightGreen`)
- **Icons**: Lucide React
- **Backend & Cloud Infrastructure**:
  - **Firebase Authentication**: Email and Password authentication with session token validation and password reset flows
  - **Cloud Firestore**: Real-time NoSQL database with granular security rules
  - **Firebase Storage**: Cloud storage for ministry documents, receipts, media assets, and avatar images
  - **Firebase Hosting**: Worldwide CDN with HTTPS, SSL, HTTP/2, and Single Page Application (SPA) routing rewrites
- **Reporting Engine**: jsPDF with jspdf-autotable for cryptographic-stamped PDF exports across all 7 departments
- **PWA Capabilities**: W3C Web Manifest, standalone display, and service worker shell caching (`sw.js`)

---

## 3. Local Development Setup

### Prerequisites
- Node.js 20.x or higher
- npm 10.x or higher
- Git

### Installation Steps
```bash
# 1. Clone the repository
git clone https://github.com/your-organization/youth-transformers.git
cd youth-transformers

# 2. Install dependencies
npm install

# 3. Create your local environment configuration
cp .env.example .env.local

# 4. Fill in your Firebase configuration values in .env.local

# 5. Start development server
npm run dev
```

The application will be available at `http://localhost:5173`.

---

## 4. Firebase Setup

1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Click **Create a project** and name it `youth-transformers` (or your preferred project ID).
3. Disable Google Analytics (or enable if your organization requires it).
4. Wait for project provisioning to complete.

---

## 5. Authentication Setup

1. In the Firebase Console sidebar, navigate to **Build > Authentication**.
2. Click **Get Started**.
3. Under the **Sign-in method** tab, select **Email/Password**.
4. Enable the **Email/Password** toggle (do **not** enable Email Link / Passwordless unless required).
5. Click **Save**.
6. Under **Settings > Authorized domains**, ensure your Firebase Hosting domain (e.g. `youth-transformers.web.app` and custom domain) is present.

### Creating Initial Canonical Ministry Accounts
In Firebase Console > Authentication > Users, create the initial email accounts with strong temporary passwords:
- `leo@youthtransformers.org` (Leader)
- `cedrick@youthtransformers.org` (Pastoral Member Care)
- `ebenezer@youthtransformers.org` (Finance & Treasury)
- `liona@youthtransformers.org` (Bible Study)
- `bonheur@youthtransformers.org` (Level 1 Discipleship)
- `green@youthtransformers.org` (Executive Committee Coordinator)
- `rene@youthtransformers.org` (Projects & Equipment Manager)
- `livia@youthtransformers.org` (Social Media & Digital Evangelism)

Users will be prompted to change temporary passwords upon initial sign-in, or can use the **Forgot Password / Reset Link** workflow.

---

## 6. Firestore Database Setup

1. Navigate to **Build > Firestore Database** in the Firebase Console.
2. Click **Create database**.
3. Choose your database location (e.g. `europe-west1` or `us-central1` close to your congregation).
4. Select **Start in production mode** (all reads and writes are denied until rules are deployed).
5. Click **Create**.

### Required Firestore Collections Schema
The application requires the following 26 collections:
- `users`: Authenticated user profiles, roles, and assigned granular permissions.
- `members`: Complete directory of church members and disciples.
- `roles`: Canonical role definitions and permission hierarchies.
- `permissions`: Granular action capabilities (`module:view`, `module:create`, `module:manage`).
- `committee_assignments`: Departmental project and task delegations.
- `tasks`: Active ministry assignments with deadlines and statuses.
- `reports`: Department progress and operational period submissions.
- `bibleStudies`: Lesson outlines, scheduled teaching sessions, and curriculum modules.
- `bibleStudyAttendance`: Per-session attendance records for Bible study groups.
- `attendance`: General Sunday and weekly meeting attendance rosters.
- `followUps`: Pastoral follow-up cases and intervention records.
- `supportCases`: Member welfare and benevolence assistance requests.
- `socialPosts`: Scheduled evangelism posts across digital platforms.
- `socialInteractions`: Online responses and prayer request inquiries.
- `evangelismEvents`: City outreaches, campus missions, and youth crusade logs.
- `projects`: Church infrastructure and developmental capital projects.
- `projectTasks`: Subtasks and timelines for physical projects.
- `equipment`: Musical instruments, sound equipment, cameras, and asset custody.
- `equipmentTransactions`: Equipment check-in, check-out, and maintenance logs.
- `financialTransactions`: Tithes, offerings, pledges, and approved operational disbursements.
- `budgets`: Fiscal department budgets and allocation ceilings.
- `events`: Ministry conferences, retreats, and worship night schedules.
- `announcements`: Congregational announcements and urgent ministry notices.
- `activityLogs`: Immutable append-only audit trail of system events.
- `notifications`: User-directed system alerts and task updates.
- `settings`: System-wide settings and ministry parameters.
- `memberAssignments`: Pastoral assignment of disciples to specific cell leaders.

---

## 7. Firebase Storage Setup

1. In the Firebase Console sidebar, select **Build > Storage**.
2. Click **Get Started**.
3. Select **Start in production mode**.
4. Choose the cloud storage bucket location and click **Done**.
5. Deploy `storage.rules` (see Section 9).

---

## 8. Environment Variables

Create `.env` in the root of the project (or configure secrets in your hosting/CI provider).

```bash
# Application Environment
VITE_APP_ENV=production

# Firebase Web App SDK Configuration
# (Obtain from: Firebase Console > Project Settings > General > Your apps > Web app)
VITE_FIREBASE_API_KEY=your_actual_api_key
VITE_FIREBASE_AUTH_DOMAIN=your-project-id.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your-project-id
VITE_FIREBASE_STORAGE_BUCKET=your-project-id.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_messaging_sender_id
VITE_FIREBASE_APP_ID=your_firebase_web_app_id
```

> **SECURITY MANDATE**: Never commit `.env` or any file containing private keys, service account JSON files, or database credentials to GitHub. The `.gitignore` file is pre-configured to strictly prevent committing credentials.

---

## 9. Security Rules Deployment

Deploy the battle-tested Security Rules directly to Firebase using the Firebase CLI:

```bash
# Install Firebase CLI globally if not installed
npm install -g firebase-tools

# Login to your Google account
firebase login

# Associate project
firebase use --add your-firebase-project-id

# Deploy Firestore Security Rules
firebase deploy --only firestore:rules

# Deploy Storage Security Rules
firebase deploy --only storage:rules
```

### Security Policy Enforcements:
- **Zero Self-Escalation**: Users cannot modify their own `role`, `status`, or `assignedPermissions`.
- **Administrative Protection**: Only users with `role: 'leader'` can create, modify, or deactivate user accounts.
- **Account Status Gating**: Disabled user accounts (`status == 'disabled'`) are blocked from all reads and writes.
- **Financial Privacy**: Only accountants (`role: 'accountant'`) and the General Leader can read or record financial transactions and budgets.
- **Pastoral Care Confidentiality**: Follow-up cases and welfare support records are accessible only to Member Care coordinators and the Leader.
- **Immutable Activity Audit Log**: Activity logs can only be created by authenticated sessions; update and delete operations are strictly set to `if false;`.

---

## 10. Production Build

Verify the production build locally before deployment:

```bash
# Run strict TypeScript validation and Vite build
npm run build
```

This compiles all TypeScript files, bundles assets, performs Tree-Shaking minification, and outputs static production assets to the `dist/` directory.

---

## 11. Firebase Hosting Deployment

Deploy the application shell and static assets to Firebase Hosting:

```bash
# Deploy hosting and rules simultaneously
firebase deploy
```

Or deploy hosting only:
```bash
firebase deploy --only hosting
```

---

## 12. PWA Installation & Offline Behavior

- **PWA Name**: Youth Transformers
- **Branding**: Official deep green (`#0B5D3B`) and gold palette with maskable high-resolution icons (`icon-192.png`, `icon-512.png`).
- **Offline Shell Caching**: The service worker (`sw.js`) caches the core application shell (HTML, styles, scripts, and fonts) for instant page loads.
- **Database Clarification**: To ensure data integrity, live database queries (Firestore) and authentication transactions are always verified against real Firebase servers and are not cached locally in an offline state.

---

## 13. Troubleshooting

| Issue | Root Cause | Solution |
|---|---|---|
| White screen on deployment | Missing base path or bad SPA redirect | Verify `firebase.json` has `rewrites: [{ "source": "**", "destination": "/index.html" }]` |
| `FirebaseError: Missing or insufficient permissions` | User has not been assigned the correct Firestore role | Check user's document in `users/{uid}` and ensure `role` matches departmental responsibility and `status: "active"` |
| PDF download fails or logs Access Denied | User does not have departmental authority | Verify the user's role matches the export department (e.g. accountant for Finance, bible_study for Bible Study) |
| Password reset email not received | Email/Password provider not enabled or spam folder | Verify Email/Password is enabled in Firebase Console and verify domain SPF/DKIM records |

---

## 18-Step Production Deployment Sequence

Follow these 18 consecutive steps to take Youth Transformers live to production:

- **STEP 1**: Create a new private or organization GitHub repository (e.g. `youth-transformers-app`).
- **STEP 2**: Commit and push the project to GitHub (`git add .`, `git commit -m "feat: production release"`, `git push -u origin main`).
- **STEP 3**: Open [Firebase Console](https://console.firebase.google.com/) and create a new project.
- **STEP 4**: Register a new Web App under the project and note down your Firebase configuration object.
- **STEP 5**: Navigate to **Authentication > Sign-in method** and enable **Email/Password**.
- **STEP 6**: Navigate to **Firestore Database** and create the database in production mode.
- **STEP 7**: Navigate to **Storage** and initialize cloud storage for the project.
- **STEP 8**: Configure your environment variables in `.env` (and add GitHub Secrets for CI/CD).
- **STEP 9**: Deploy Firestore Security Rules using `firebase deploy --only firestore:rules`.
- **STEP 10**: Deploy Cloud Functions if privileged custom admin SDK workflows are utilized.
- **STEP 11**: Run `npm install` and `npm run build` to confirm zero compilation errors.
- **STEP 12**: Deploy the built distribution to Firebase Hosting with `firebase deploy --only hosting`.
- **STEP 13**: Open the live production HTTPS URL assigned by Firebase (e.g. `https://your-project.web.app`).
- **STEP 14**: Test real authentication with sign-in, sign-out, and password reset.
- **STEP 15**: Test role-based permissions across department dashboards and ensure Access Denied restricts unauthorized routes.
- **STEP 16**: Test real-time PDF generation across all departments and verify valid verification codes.
- **STEP 17**: Inspect the Activity Audit Log to ensure all logins, modifications, and PDF downloads are recorded newest-first.
- **STEP 18**: Verify PWA installation prompt on Android Chrome and iOS Safari.
