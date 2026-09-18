import React, { useState, useEffect } from 'react';
import { GraduationCap, CheckCircle2, Clock, UserCheck, BookOpen, ChevronRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getMembers, saveMember } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { Member } from '../types';

export const Level1Page: React.FC = () => {
  const { currentUser } = useAuth();
  const [candidates, setCandidates] = useState<Member[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadLevel1();
  }, []);

  const loadLevel1 = async () => {
    setLoading(true);
    const data = await getMembers();
    // Filter level 1 disciples
    const l1 = data.filter(m => m.ministryLevel === 'Level 1');
    setCandidates(l1);
    setLoading(false);
  };

  const handlePromote = async (member: Member) => {
    if (!currentUser) return;
    const updated: Member = {
      ...member,
      ministryLevel: 'Level 2',
      updatedAt: Date.now()
    };
    await saveMember(updated);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Discipleship Level Advancement',
      module: 'members',
      resourceType: 'member',
      resourceId: member.id,
      details: `Advanced ${member.fullName} from Level 1 to Level 2 based on curriculum completion`,
      result: 'success'
    });
    await loadLevel1();
  };

  const curriculumModules = [
    { title: 'Module 1: The New Birth & Salvation Assurance', completedLessons: 4, totalLessons: 4 },
    { title: 'Module 2: The Holy Spirit & Personal Prayer Life', completedLessons: 4, totalLessons: 4 },
    { title: 'Module 3: Daily Bible Study & Spiritual Growth', completedLessons: 3, totalLessons: 4 },
    { title: 'Module 4: Consecrated Christian Living & Testimony', completedLessons: 2, totalLessons: 4 }
  ];

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Level 1 Discipleship Academy
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Department led by Shema Prince • Foundational doctrinal training and spiritual nurture
          </p>
        </div>

        <div className="text-xs font-bold text-emerald-800 bg-emerald-50 px-3.5 py-2 rounded-xl border border-emerald-200">
          Enrolled Disciples: {candidates.length}
        </div>
      </div>

      {/* Curriculum Outline */}
      <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs">
        <h3 className="text-sm font-bold text-slate-900 mb-3 flex items-center gap-2">
          <BookOpen className="w-4 h-4 text-ministry-emerald" />
          <span>Foundational Discipleship Curriculum Syllabus</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {curriculumModules.map((m, idx) => (
            <div key={idx} className="p-3.5 bg-slate-50 rounded-xl border border-slate-200">
              <span className="text-[10px] font-bold text-ministry-deepGreen uppercase tracking-wider block">
                Part {idx + 1}
              </span>
              <p className="text-xs font-bold text-slate-900 mt-1 mb-2 leading-tight">
                {m.title}
              </p>
              <div className="flex items-center justify-between text-[11px] text-slate-500">
                <span>Lessons: {m.completedLessons}/{m.totalLessons}</span>
                <span className="text-emerald-700 font-bold">Active</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Candidates List */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="px-5 py-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between">
          <span className="font-bold text-xs text-slate-800 uppercase tracking-wider">
            Level 1 Candidate Cohort
          </span>
          <span className="text-xs text-slate-500">
            Attendance & Advancement Recommendations
          </span>
        </div>

        <div className="divide-y divide-slate-100">
          {candidates.length === 0 ? (
            <div className="p-8 text-center text-xs text-slate-400">
              No active candidates currently in Level 1.
            </div>
          ) : (
            candidates.map((cand) => (
              <div key={cand.id} className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-slate-50/80 transition">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold text-sm">
                    {cand.fullName.charAt(0)}
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-slate-900">{cand.fullName}</h4>
                    <p className="text-xs text-slate-500">
                      {cand.phone} • Residence: {cand.currentResidence}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-6 text-xs">
                  <div>
                    <span className="text-slate-400 block text-[10px]">Fellowships</span>
                    <strong className="text-slate-800 font-bold">{cand.attendanceCount} sessions</strong>
                  </div>

                  <div>
                    <span className="text-slate-400 block text-[10px]">Bible Study</span>
                    <strong className="text-slate-800 font-bold">{cand.bibleStudyParticipation} studies</strong>
                  </div>

                  <button
                    onClick={() => handlePromote(cand)}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl text-xs shadow-xs transition"
                  >
                    <span>Promote to Level 2</span>
                    <ChevronRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
