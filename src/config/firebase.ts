import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import { getAuth, Auth } from 'firebase/auth';
import { getFirestore, Firestore } from 'firebase/firestore';
import { getStorage, FirebaseStorage } from 'firebase/storage';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || ""
};

export const isFirebaseConfigured = Boolean(
  import.meta.env.VITE_FIREBASE_API_KEY &&
  import.meta.env.VITE_FIREBASE_PROJECT_ID
);

let app: FirebaseApp;
if (getApps().length) {
  app = getApp();
} else if (isFirebaseConfigured) {
  app = initializeApp(firebaseConfig);
} else {
  // Standalone fallback initialization to avoid runtime crash when .env is pending
  app = initializeApp({
    apiKey: "pending_env_configuration",
    authDomain: "pending_env_configuration.firebaseapp.com",
    projectId: "pending_env_configuration",
    storageBucket: "pending_env_configuration.appspot.com",
    messagingSenderId: "000000000000",
    appId: "1:000000000000:web:000000000000"
  });
}

export const auth: Auth = getAuth(app);
export const db: Firestore = getFirestore(app);
export const storage: FirebaseStorage = getStorage(app);
export default app;
