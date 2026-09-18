import React, { useState, useEffect, useRef } from 'react';
import { Bell, Check, Clock, AlertTriangle } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getNotifications, markNotificationRead } from '../services/firestoreService';
import { NotificationItem } from '../types';

export const NotificationBell: React.FC = () => {
  const { currentUser } = useAuth();
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (currentUser) {
      getNotifications(currentUser.uid).then(setNotifications);
    }
  }, [currentUser]);

  // Close on outside click
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const unreadCount = notifications.filter(n => !n.read).length;

  const handleMarkAsRead = async (id: string) => {
    await markNotificationRead(id);
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 rounded-xl text-slate-600 hover:text-ministry-deepGreen hover:bg-slate-100 transition focus:outline-none"
        title="Ministry Notifications"
      >
        <Bell className="w-5 h-5" />
        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 w-4 h-4 bg-red-600 text-white text-[10px] font-bold rounded-full flex items-center justify-center border-2 border-white animate-pulse">
            {unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-white rounded-2xl shadow-xl border border-slate-200 z-50 overflow-hidden animate-fade-in">
          <div className="bg-slate-50 px-4 py-3 border-b border-slate-200 flex items-center justify-between">
            <span className="font-semibold text-sm text-slate-800">Ministry Notifications</span>
            <span className="text-xs bg-emerald-100 text-emerald-800 font-medium px-2 py-0.5 rounded-full">
              {unreadCount} new
            </span>
          </div>

          <div className="max-h-80 overflow-y-auto divide-y divide-slate-100">
            {notifications.length === 0 ? (
              <div className="p-6 text-center text-xs text-slate-500">
                No notifications right now.
              </div>
            ) : (
              notifications.map((item) => (
                <div
                  key={item.id}
                  className={`p-3.5 hover:bg-slate-50 transition flex items-start gap-3 ${
                    !item.read ? 'bg-emerald-50/40' : ''
                  }`}
                >
                  <div className={`p-2 rounded-lg shrink-0 ${
                    item.type === 'report_request' ? 'bg-amber-100 text-amber-700' :
                    item.type === 'deadline' ? 'bg-red-100 text-red-700' :
                    'bg-emerald-100 text-emerald-700'
                  }`}>
                    {item.type === 'deadline' ? (
                      <AlertTriangle className="w-4 h-4" />
                    ) : (
                      <Clock className="w-4 h-4" />
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-xs font-semibold text-slate-800 leading-tight">{item.title}</p>
                    <p className="text-xs text-slate-600 mt-1 leading-snug">{item.message}</p>
                    <span className="text-[10px] text-slate-400 mt-1 block">
                      {new Date(item.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                  {!item.read && (
                    <button
                      onClick={() => handleMarkAsRead(item.id)}
                      className="p-1 hover:bg-emerald-200 rounded-md text-emerald-700 transition"
                      title="Mark as read"
                    >
                      <Check className="w-3.5 h-3.5" />
                    </button>
                  )}
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};
