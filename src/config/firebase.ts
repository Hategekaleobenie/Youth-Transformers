import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import { getAuth, Auth } from 'firebase/auth';
import { initializeFirestore, getFirestore, Firestore } from 'firebase/firestore';
import { getStorage, FirebaseStorage } from 'firebase/storage';

// Firebase Web App configuration.
// These values are safe to ship in a browser app; Firebase Security Rules
// and Authentication control access to the data.
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

// Some networks/proxies have trouble with Firestore's default WebChannel
// transport and report "Failed to get document because the client is offline"
// even while normal internet access is working. Long polling is more tolerant
// of those environments.
let db: Firestore;
try {
  db = initializeFirestore(app, {
    experimentalForceLongPolling: true
  });
} catch {
  // Reuse the existing instance during hot reload or if another module
  // initialized Firestore first.
  db = getFirestore(app);
}
export { db };

export const storage: FirebaseStorage = getStorage(app);
export default app;
