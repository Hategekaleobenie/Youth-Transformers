import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import { getAuth, Auth } from 'firebase/auth';
import { getFirestore, Firestore } from 'firebase/firestore';
import { getStorage, FirebaseStorage } from 'firebase/storage';

// Firebase Web App configuration.
// Web Firebase config values are intended to be included in client-side builds.
// Environment variables can still override these values when supplied.
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyDPoE4myxgYGL89rgJpwqyDYuGAZRUO6Y0",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "youth-transformers-database.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "youth-transformers-database",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "youth-transformers-database.firebasestorage.app",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "738366925914",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:738366925914:web:684d7c1f786180a791bc63"
};

export const isFirebaseConfigured = Boolean(
  firebaseConfig.apiKey &&
  firebaseConfig.authDomain &&
  firebaseConfig.projectId &&
  firebaseConfig.appId
);

let app: FirebaseApp;
if (getApps().length) {
  app = getApp();
} else {
  app = initializeApp(firebaseConfig);
}

export const auth: Auth = getAuth(app);
export const db: Firestore = getFirestore(app);
export const storage: FirebaseStorage = getStorage(app);
export default app;
