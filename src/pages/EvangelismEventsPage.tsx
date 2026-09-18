import React, { useState, useEffect } from 'react';
import { Flame, Calendar, Plus, MapPin, Users, Heart, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getEvangelismRecords, saveEvangelismRecord, getMinistryEvents, saveMinistryEvent } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { EvangelismRecord, MinistryEvent, EventType } from '../types';

export const EvangelismEventsPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [activeTab, setActiveTab] = useState<'evangelism' | 'events'>('evangelism');
  const [evangelism, setEvangelism] = useState<EvangelismRecord[]>([]);
  const [events, setEvents] = useState<MinistryEvent[]>([]);
  const [isEvanModalOpen, setIsEvanModalOpen] = useState(false);
  const [isEventModalOpen, setIsEventModalOpen] = useState(false);

  // Evangelism Form
  const [evanForm, setEvanForm] = useState({
    location: '',
    dateStr: new Date().toISOString().split('T')[0],
    teamMembers: currentUser?.displayName || '',
    peopleReached: 20,
    decisionsForChrist: 3,
    followUpsRequired: 2,
    testimonies: '',
    notes: ''
  });

  // Event Form
  const [eventForm, setEventForm] = useState({
    title: '',
    type: 'Fellowship' as EventType,
    dateStr: new Date().toISOString().split('T')[0],
    timeStr: '18:00 - 21:00',
    location: '',
    description: '',
    budget: '50000',
    coordinator: currentUser?.displayName || '',
    rsvpCount: 50
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    const [ev, evts] = await Promise.all([getEvangelismRecords(), getMinistryEvents()]);
    setEvangelism(ev);
    setEvents(evts);
  };

  const handleSaveEvan = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const record: EvangelismRecord = {
      id: `evan_${Date.now()}`,
      location: evanForm.location,
      dateStr: evanForm.dateStr,
      teamMembers: evanForm.teamMembers.split(',').map(s => s.trim()),
      peopleReached: evanForm.peopleReached,
      decisionsForChrist: evanForm.decisionsForChrist,
      followUpsRequired: evanForm.followUpsRequired,
      testimonies: evanForm.testimonies,
      notes: evanForm.notes,
      createdAt: Date.now()
    };

    await saveEvangelismRecord(record);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Evangelism Outreach Recorded',
      module: 'events',
      resourceType: 'evangelism',
      resourceId: record.id,
      details: `Outreach in ${record.location}: ${record.decisionsForChrist} souls gave lives to Christ`,
      result: 'success'
    });

    setIsEvanModalOpen(false);
    await loadData();
  };

  const handleSaveEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const evt: MinistryEvent = {
      id: `evt_${Date.now()}`,
      title: eventForm.title,
      type: eventForm.type,
      dateStr: eventForm.dateStr,
      timeStr: eventForm.timeStr,
      location: eventForm.location,
      description: eventForm.description,
      budget: parseFloat(eventForm.budget) || 0,
      coordinator: eventForm.coordinator,
      rsvpCount: eventForm.rsvpCount,
      createdAt: Date.now()
    };

    await saveMinistryEvent(evt);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Ministry Event Scheduled',
      module: 'events',
      resourceType: 'event',
      resourceId: evt.id,
      details: `Scheduled ${evt.type}: "${evt.title}"`,
      result: 'success'
    });

    setIsEventModalOpen(false);
    await loadData();
  };

  const totalSoulsReached = evangelism.reduce((s, r) => s + (r.peopleReached || 0), 0);
  const totalDecisions = evangelism.reduce((s, r) => s + (r.decisionsForChrist || 0), 0);

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Evangelism & Ministry Events
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Gospel crusades, campus outreach, prayer vigils, and youth fellowships
          </p>
        </div>

        <div>
          {activeTab === 'evangelism' ? (
            <button
              onClick={() => setIsEvanModalOpen(true)}
              className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
            >
              <Plus className="w-4 h-4" />
              <span>Record Outreach Mission</span>
            </button>
          ) : (
            <button
              onClick={() => setIsEventModalOpen(true)}
              className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
            >
              <Plus className="w-4 h-4" />
              <span>Schedule Event</span>
            </button>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center bg-slate-100 p-1 rounded-2xl w-fit">
        <button
          onClick={() => setActiveTab('evangelism')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${
            activeTab === 'evangelism'
              ? 'bg-white text-ministry-deepGreen shadow-xs'
              : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <Flame className="w-4 h-4" />
          <span>Evangelism Missions ({evangelism.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('events')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${
            activeTab === 'events'
              ? 'bg-white text-ministry-deepGreen shadow-xs'
              : 'text-slate-600 hover:text-slate-900'
          }`}
        >
          <Calendar className="w-4 h-4" />
          <span>Events & Retreats ({events.length})</span>
        </button>
      </div>

      {activeTab === 'evangelism' ? (
        <div className="space-y-4">
          {/* Metrics summary */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="bg-white p-4 rounded-2xl border border-slate-200">
              <span className="text-[10px] text-slate-400 font-bold block uppercase">Total Reached</span>
              <strong className="text-xl font-black text-slate-900">{totalSoulsReached}</strong>
            </div>
            <div className="bg-white p-4 rounded-2xl border border-slate-200">
              <span className="text-[10px] text-emerald-600 font-bold block uppercase">Decisions for Christ</span>
              <strong className="text-xl font-black text-emerald-700">{totalDecisions}</strong>
            </div>
            <div className="bg-white p-4 rounded-2xl border border-slate-200">
              <span className="text-[10px] text-amber-600 font-bold block uppercase">Follow-ups Needed</span>
              <strong className="text-xl font-black text-amber-700">
                {evangelism.reduce((s, r) => s + (r.followUpsRequired || 0), 0)}
              </strong>
            </div>
            <div className="bg-white p-4 rounded-2xl border border-slate-200">
              <span className="text-[10px] text-slate-400 font-bold block uppercase">Missions Deployed</span>
              <strong className="text-xl font-black text-slate-900">{evangelism.length}</strong>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {evangelism.map((r) => (
              <div key={r.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <span className="text-[10px] font-bold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-800">
                      {r.dateStr}
                    </span>
                    <span className="text-xs font-black text-emerald-700">
                      {r.decisionsForChrist} Decisions for Christ
                    </span>
                  </div>

                  <h3 className="font-bold text-sm text-slate-900 mb-1 flex items-center gap-1.5">
                    <MapPin className="w-3.5 h-3.5 text-slate-400" />
                    {r.location}
                  </h3>

                  <p className="text-xs text-slate-500 mb-3">
                    Missionaries: {r.teamMembers.join(', ')}
                  </p>

                  {r.testimonies && (
                    <div className="p-3 bg-emerald-50/50 rounded-xl text-xs text-emerald-950 border border-emerald-100 mb-3">
                      <strong>Testimony:</strong> {r.testimonies}
                    </div>
                  )}
                </div>

                <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
                  <span>People Engaged: {r.peopleReached}</span>
                  <span>Follow-ups: {r.followUpsRequired}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {events.map((evt) => (
            <div key={evt.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between gap-2 mb-2">
                  <span className="text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-800">
                    {evt.type}
                  </span>
                  <span className="text-xs font-semibold text-slate-500">
                    {evt.rsvpCount} RSVPs
                  </span>
                </div>

                <h3 className="font-bold text-sm text-slate-900 mb-1">{evt.title}</h3>
                <p className="text-xs text-slate-500 mb-3">{evt.description}</p>

                <div className="space-y-1 text-xs text-slate-600 mb-3">
                  <div className="flex items-center gap-2">
                    <Calendar className="w-3.5 h-3.5 text-slate-400" />
                    <span>{evt.dateStr} • {evt.timeStr}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <MapPin className="w-3.5 h-3.5 text-slate-400" />
                    <span>{evt.location}</span>
                  </div>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
                <span>Coordinator: {evt.coordinator}</span>
                <span>Budget: {evt.budget.toLocaleString()} RWF</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Evangelism Modal */}
      {isEvanModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Record Evangelism Mission</h3>
              <button onClick={() => setIsEvanModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEvan} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Location / Community Reached *
                </label>
                <input
                  type="text"
                  required
                  value={evanForm.location}
                  onChange={(e) => setEvanForm({ ...evanForm, location: e.target.value })}
                  placeholder="e.g. Nyabugogo Bus Station Outreach"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Date of Outreach
                  </label>
                  <input
                    type="date"
                    required
                    value={evanForm.dateStr}
                    onChange={(e) => setEvanForm({ ...evanForm, dateStr: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Team Members
                  </label>
                  <input
                    type="text"
                    required
                    value={evanForm.teamMembers}
                    onChange={(e) => setEvanForm({ ...evanForm, teamMembers: e.target.value })}
                    placeholder="Comma separated names"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    People Reached
                  </label>
                  <input
                    type="number"
                    value={evanForm.peopleReached}
                    onChange={(e) => setEvanForm({ ...evanForm, peopleReached: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Decisions for Christ
                  </label>
                  <input
                    type="number"
                    value={evanForm.decisionsForChrist}
                    onChange={(e) => setEvanForm({ ...evanForm, decisionsForChrist: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Follow-ups
                  </label>
                  <input
                    type="number"
                    value={evanForm.followUpsRequired}
                    onChange={(e) => setEvanForm({ ...evanForm, followUpsRequired: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Testimonies & Salvation Encounters
                </label>
                <textarea
                  rows={2}
                  value={evanForm.testimonies}
                  onChange={(e) => setEvanForm({ ...evanForm, testimonies: e.target.value })}
                  placeholder="Record miraculous transformations, deliverances, and prayer outcomes..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsEvanModalOpen(false)}
                  className="px-4 py-2 font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs"
                >
                  Save Outreach
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Event Modal */}
      {isEventModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Schedule Ministry Event</h3>
              <button onClick={() => setIsEventModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEvent} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Event Title *
                </label>
                <input
                  type="text"
                  required
                  value={eventForm.title}
                  onChange={(e) => setEventForm({ ...eventForm, title: e.target.value })}
                  placeholder="e.g. Annual Youth Fire Overnight Prayer Vigil"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Event Type
                  </label>
                  <select
                    value={eventForm.type}
                    onChange={(e) => setEventForm({ ...eventForm, type: e.target.value as EventType })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="Fellowship">Fellowship</option>
                    <option value="Retreat">Retreat</option>
                    <option value="Conference">Conference</option>
                    <option value="Prayer Night">Prayer Night</option>
                    <option value="Outreach">Community Outreach</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Location
                  </label>
                  <input
                    type="text"
                    required
                    value={eventForm.location}
                    onChange={(e) => setEventForm({ ...eventForm, location: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Date
                  </label>
                  <input
                    type="date"
                    required
                    value={eventForm.dateStr}
                    onChange={(e) => setEventForm({ ...eventForm, dateStr: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Time
                  </label>
                  <input
                    type="text"
                    required
                    value={eventForm.timeStr}
                    onChange={(e) => setEventForm({ ...eventForm, timeStr: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsEventModalOpen(false)}
                  className="px-4 py-2 font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs"
                >
                  Schedule Event
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
