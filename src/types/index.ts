export type MinistryRole =
  | 'leader'
  | 'committee_coordinator'
  | 'level1_leader'
  | 'social_media'
  | 'member_care'
  | 'bible_study'
  | 'accountant'
  | 'projects_manager'
  | 'member';

export type AccountStatus = 'active' | 'disabled';

export type PermissionAction =
  | 'view'
  | 'create'
  | 'edit'
  | 'delete'
  | 'approve'
  | 'export'
  | 'manage';

export type ModuleName =
  | 'members'
  | 'bible_study'
  | 'attendance'
  | 'member_care'
  | 'social_media'
  | 'finance'
  | 'projects'
  | 'equipment'
  | 'events'
  | 'reports'
  | 'users'
  | 'committee'
  | 'activity_log'
  | 'announcements';

export interface UserProfile {
  uid: string;
  email: string;
  displayName: string;
  phone: string;
  role: MinistryRole;
  department: string;
  status: AccountStatus;
  mustChangePassword?: boolean;
  assignedPermissions?: string[];
  createdAt: number;
  lastLoginAt: number;
  photoUrl?: string;
}

export type MemberStatus = 'active' | 'inactive' | 'follow_up_required' | 'deactivated';
export type MinistryLevel = 'Level 1' | 'Level 2' | 'Level 3' | 'Youth Leader' | 'General Member';

export interface Member {
  id: string;
  fullName: string;
  profilePhoto?: string;
  phone: string;
  email?: string;
  dateJoined: string;
  currentResidence: string;
  permanentResidence: string;
  employmentStatus: string;
  educationStatus: string;
  familyInfo: string;
  ministryLevel: MinistryLevel;
  assignedLeader: string;
  attendanceCount: number;
  bibleStudyParticipation: number;
  evangelismParticipation: number;
  status: MemberStatus;
  notes?: string;
  createdAt: number;
  updatedAt: number;
}

export type FollowUpStatus = 'open' | 'in_progress' | 'waiting' | 'resolved' | 'closed';
export type FollowUpPriority = 'low' | 'medium' | 'high' | 'urgent';
export type FollowUpReason =
  | 'Attendance'
  | 'Academic'
  | 'Employment'
  | 'Financial'
  | 'Family'
  | 'Spiritual'
  | 'General Support'
  | 'Prefer not to say';

export interface FollowUpCase {
  id: string;
  memberId: string;
  memberName: string;
  assignedToUid: string;
  assignedToName: string;
  reason: FollowUpReason;
  dateOpened: string;
  priority: FollowUpPriority;
  status: FollowUpStatus;
  notes: string;
  nextFollowUpDate: string;
  resolutionNotes?: string;
  createdAt: number;
  updatedAt: number;
}

export interface BibleStudy {
  id: string;
  title: string;
  scriptureReferences: string;
  dateStr: string;
  timeStr: string;
  location: string;
  preacher: string;
  host: string;
  curriculum: string;
  expectedAttendance: number;
  actualAttendance?: number;
  recurrence: 'once' | 'daily' | 'weekly' | 'monthly';
  notes?: string;
  createdAt: number;
}

export type AttendanceStatus = 'present' | 'absent' | 'excused';
export type AttendanceType = 'Bible Study' | 'Event' | 'Evangelism' | 'Committee' | 'Other';

export interface AttendanceRecord {
  id: string;
  memberId: string;
  memberName: string;
  eventId: string;
  eventName: string;
  eventType: AttendanceType;
  dateStr: string;
  status: AttendanceStatus;
  excuseReason?: string;
  recordedBy: string;
  createdAt: number;
}

export type TransactionType = 'income' | 'expense';
export type TransactionCategory =
  | 'Donations'
  | 'Fundraising'
  | 'Events'
  | 'Equipment'
  | 'Transport'
  | 'Welfare'
  | 'Other';

export interface FinancialTransaction {
  id: string;
  dateStr: string;
  description: string;
  category: TransactionCategory;
  amount: number;
  type: TransactionType;
  recordedBy: string;
  notes?: string;
  createdAt: number;
}

export type ProjectStatus = 'planning' | 'active' | 'delayed' | 'completed' | 'cancelled';

export interface Project {
  id: string;
  name: string;
  title?: string;
  description: string;
  leader: string;
  startDate: string;
  endDate: string;
  budget: number;
  currentSpending?: number;
  status: ProjectStatus;
  progressPercent: number;
  progressPercentage?: number;
  tasksCount?: number;
  completedTasksCount?: number;
  tasksTotal?: number;
  tasksCompleted?: number;
  objectives?: string;
  team: string[];
  assignedTeam?: string[];
  notes?: string;
  createdAt: number;
  updatedAt: number;
}

export type EquipmentCondition = 'Excellent' | 'Good' | 'Fair' | 'Needs Repair';
export type EquipmentStatus =
  | 'available'
  | 'in_use'
  | 'borrowed'
  | 'maintenance'
  | 'missing'
  | 'damaged';

export interface EquipmentItem {
  id: string;
  name: string;
  assetTag?: string;
  category: string;
  quantity: number;
  condition: EquipmentCondition;
  location: string;
  responsiblePerson: string;
  status: EquipmentStatus;
  checkoutStatus?: 'Available' | 'Checked Out' | 'Maintenance' | EquipmentStatus | string;
  assignedTo?: string;
  purchaseDate: string;
  lastInspectedDate?: string;
  lastMaintenanceDate?: string;
  notes?: string;
  createdAt: number;
}

export type SocialPlatform = 'Instagram' | 'Facebook' | 'TikTok' | 'YouTube' | 'WhatsApp' | 'X' | 'Website' | 'Other';
export type SocialPostStatus = 'draft' | 'scheduled' | 'published' | 'archived';
export type PostStatus = SocialPostStatus;

export interface SocialPost {
  id: string;
  title: string;
  content?: string;
  bibleVerse?: string;
  platform: SocialPlatform;
  postDate?: string;
  scheduledDate?: string;
  targetAudience?: string;
  status: SocialPostStatus;
  link?: string;
  responsiblePerson?: string;
  approvalStatus?: 'pending' | 'approved' | 'rejected';
  notes?: string;
  views?: number;
  likes?: number;
  shares?: number;
  comments?: number;
  createdAt: number;
}

export interface EvangelismRecord {
  id: string;
  eventName?: string;
  location: string;
  dateStr: string;
  leaderName?: string;
  teamMembers: string[];
  peopleReached: number;
  decisionsForChrist?: number;
  newContacts?: number;
  followUpsRequired?: number;
  followUpsScheduled?: number;
  testimonies: string;
  notes?: string;
  createdAt: number;
}

export type EventType = 'Fellowship' | 'Retreat' | 'Conference' | 'Prayer Night' | 'Outreach' | string;

export interface MinistryEvent {
  id: string;
  title: string;
  type?: EventType;
  description: string;
  dateStr: string;
  timeStr: string;
  location: string;
  coordinator?: string;
  organizer?: string;
  budget: number;
  expectedAttendance?: number;
  rsvpCount?: number;
  actualAttendance?: number;
  status?: 'upcoming' | 'in_progress' | 'completed' | 'cancelled';
  createdAt: number;
}

export type ReportType =
  | 'Daily'
  | 'Weekly'
  | 'Monthly'
  | 'Department'
  | 'Project'
  | 'Finance'
  | 'Bible Study'
  | 'Member Care'
  | 'Social Media'
  | 'Attendance'
  | 'Committee';

export interface CommitteeReport {
  id: string;
  title?: string;
  reportType?: ReportType;
  department: string;
  authorUid?: string;
  authorName?: string;
  authorRole?: string;
  submittedByUid?: string;
  submittedByName?: string;
  reportingPeriod: string;
  dateStr?: string;
  dateSubmitted?: string;
  executiveSummary?: string;
  activitiesCompleted?: string;
  accomplished?: string;
  currentWork?: string;
  challenges?: string;
  supportNeeded?: string;
  nextSteps?: string;
  prayerNotes?: string;
  status: 'draft' | 'submitted' | 'reviewed' | 'approved' | 'needs_revision';
  createdAt: number;
  updatedAt?: number;
}

export type CommitteeTaskStatus = 'pending' | 'in_progress' | 'submitted' | 'completed' | 'approved' | 'needs_revision';

export interface CommitteeTask {
  id: string;
  title: string;
  description?: string;
  assignedToUid: string;
  assignedToName: string;
  department: string;
  dueDate: string;
  priority?: 'low' | 'medium' | 'high';
  deliverable?: string;
  status: CommitteeTaskStatus;
  createdAt: number;
}

export interface Announcement {
  id: string;
  title: string;
  content: string;
  priority: 'normal' | 'important' | 'urgent';
  targetAudience: string;
  authorUid?: string;
  authorName: string;
  publishDate?: string;
  activeUntil?: string;
  createdAt: number;
}

export interface ActivityLogItem {
  id: string;
  actorUid: string;
  actorName: string;
  actorRole: string;
  action: string;
  module: ModuleName | string;
  resourceType: string;
  resourceId: string;
  details?: string;
  timestamp: number;
  result: 'success' | 'failed' | 'denied';
}

export interface NotificationItem {
  id: string;
  recipientUid: string;
  title: string;
  message: string;
  type: 'assignment' | 'deadline' | 'report_request' | 'reminder' | 'announcement';
  read: boolean;
  link?: string;
  createdAt: number;
}
