import React, { useState, useEffect } from 'react';
import { Share2, Plus, Calendar, CheckCircle2, AlertCircle, TrendingUp, X } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { getSocialPosts, saveSocialPost } from '../services/firestoreService';
import { logActivity } from '../services/activityLogService';
import { SocialPost, SocialPlatform, SocialPostStatus } from '../types';

export const SocialMediaPage: React.FC = () => {
  const { currentUser } = useAuth();
  const [posts, setPosts] = useState<SocialPost[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    title: '',
    content: '',
    bibleVerse: '',
    platform: 'Instagram' as SocialPlatform,
    targetAudience: 'Youth & University Students',
    scheduledDate: new Date().toISOString().split('T')[0],
    status: 'scheduled' as SocialPostStatus
  });

  useEffect(() => {
    loadPosts();
  }, []);

  const loadPosts = async () => {
    const data = await getSocialPosts();
    setPosts(data);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUser) return;

    const newPost: SocialPost = {
      id: `post_${Date.now()}`,
      ...formData,
      views: 0,
      likes: 0,
      shares: 0,
      comments: 0,
      createdAt: Date.now()
    };

    await saveSocialPost(newPost);
    await logActivity({
      actorUid: currentUser.uid,
      actorName: currentUser.displayName,
      actorRole: currentUser.role,
      action: 'Social Media Gospel Post Created',
      module: 'social_media',
      resourceType: 'social_post',
      resourceId: newPost.id,
      details: `Scheduled ${newPost.platform} gospel post: "${newPost.title}"`,
      result: 'success'
    });

    setIsModalOpen(false);
    await loadPosts();
  };

  // Weekly compliance check: at least 3 posts in the current week
  const oneWeekAgo = Date.now() - 7 * 24 * 60 * 60 * 1000;
  const weeklyPostsCount = posts.filter(p => p.createdAt >= oneWeekAgo).length;
  const isCompliant = weeklyPostsCount >= 3;

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-black text-slate-900 tracking-tight">
            Digital Evangelism & Social Media
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Department managed by Kenny Hirwa Ruzindana • Reaching nations through Christ-centered digital media
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white text-xs font-bold rounded-xl shadow-xs transition"
        >
          <Plus className="w-4 h-4" />
          <span>New Gospel Post</span>
        </button>
      </div>

      {/* Weekly Compliance Status Card */}
      <div className={`p-5 rounded-2xl border ${
        isCompliant
          ? 'bg-emerald-50 border-emerald-200 text-emerald-900'
          : 'bg-amber-50 border-amber-200 text-amber-900'
      } flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-xs`}>
        <div className="flex items-center gap-3">
          <div className={`p-2.5 rounded-xl ${isCompliant ? 'bg-emerald-600 text-white' : 'bg-amber-600 text-white'}`}>
            <Share2 className="w-5 h-5" />
          </div>
          <div>
            <h4 className="font-bold text-sm">
              Weekly Gospel Requirement: 3 Posts Minimum
            </h4>
            <p className="text-xs opacity-90 mt-0.5">
              Current performance: <strong>{weeklyPostsCount} of 3</strong> posts scheduled/published this week.
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {isCompliant ? (
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-200 text-emerald-900 text-xs font-bold">
              <CheckCircle2 className="w-4 h-4" />
              Requirement Satisfied
            </span>
          ) : (
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-200 text-amber-900 text-xs font-bold">
              <AlertCircle className="w-4 h-4" />
              Action Required ({3 - weeklyPostsCount} more needed)
            </span>
          )}
        </div>
      </div>

      {/* Posts Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {posts.map((post) => (
          <div key={post.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs flex flex-col justify-between">
            <div>
              <div className="flex items-start justify-between gap-2 mb-2">
                <span className="text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full bg-purple-50 text-purple-800 border border-purple-200">
                  {post.platform}
                </span>
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                  post.status === 'published' ? 'bg-emerald-100 text-emerald-800' : 'bg-blue-100 text-blue-800'
                }`}>
                  {post.status.toUpperCase()}
                </span>
              </div>

              <h3 className="font-bold text-sm text-slate-900 mb-1">{post.title}</h3>
              <p className="text-xs text-ministry-emerald font-semibold mb-2">
                {post.bibleVerse}
              </p>
              <p className="text-xs text-slate-600 line-clamp-3 mb-4">{post.content}</p>

              <div className="grid grid-cols-4 gap-1 p-2 bg-slate-50 rounded-xl text-center text-[10px] border border-slate-100 mb-3">
                <div>
                  <span className="text-slate-400 block font-medium">Views</span>
                  <strong className="text-slate-800">{(post.views || 0).toLocaleString()}</strong>
                </div>
                <div>
                  <span className="text-slate-400 block font-medium">Likes</span>
                  <strong className="text-slate-800">{(post.likes || 0).toLocaleString()}</strong>
                </div>
                <div>
                  <span className="text-slate-400 block font-medium">Shares</span>
                  <strong className="text-slate-800">{(post.shares || 0).toLocaleString()}</strong>
                </div>
                <div>
                  <span className="text-slate-400 block font-medium">Comments</span>
                  <strong className="text-slate-800">{(post.comments || 0).toLocaleString()}</strong>
                </div>
              </div>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
              <span>Audience: {post.targetAudience}</span>
              <span>Date: {post.scheduledDate}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg border border-slate-200 overflow-hidden">
            <div className="bg-ministry-deepGreen px-6 py-4 flex items-center justify-between text-white">
              <h3 className="font-bold text-sm">Schedule Gospel Media Post</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-emerald-200 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="p-6 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Post Headline / Title *
                </label>
                <input
                  type="text"
                  required
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="e.g. Overcoming Anxiety Through God's Peace"
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Platform
                  </label>
                  <select
                    value={formData.platform}
                    onChange={(e) => setFormData({ ...formData, platform: e.target.value as SocialPlatform })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  >
                    <option value="Instagram">Instagram</option>
                    <option value="TikTok">TikTok</option>
                    <option value="YouTube">YouTube</option>
                    <option value="Facebook">Facebook</option>
                    <option value="X">X (Twitter)</option>
                    <option value="Website">Ministry Portal</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Bible Scripture
                  </label>
                  <input
                    type="text"
                    required
                    value={formData.bibleVerse}
                    onChange={(e) => setFormData({ ...formData, bibleVerse: e.target.value })}
                    placeholder="Philippians 4:6-7"
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Gospel Message & Copy *
                </label>
                <textarea
                  rows={3}
                  required
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="Draft caption, devotional body, and evangelistic call to action..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Scheduled Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.scheduledDate}
                    onChange={(e) => setFormData({ ...formData, scheduledDate: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Target Audience
                  </label>
                  <input
                    type="text"
                    value={formData.targetAudience}
                    onChange={(e) => setFormData({ ...formData, targetAudience: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-ministry-emerald"
                  />
                </div>
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 font-medium text-slate-600 hover:bg-slate-100 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-ministry-deepGreen hover:bg-ministry-forestDark text-white font-bold rounded-xl shadow-xs"
                >
                  Save Post
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
