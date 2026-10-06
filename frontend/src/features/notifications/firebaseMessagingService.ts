import { getApp, getApps, initializeApp } from "firebase/app";
import { getMessaging, getToken, isSupported } from "firebase/messaging";
import messagingWorkerUrl from "../../firebase-messaging-sw.js?worker&url";
import { deviceTokenService } from "./deviceTokenService";

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

const vapidKey = import.meta.env.VITE_FIREBASE_VAPID_KEY;

export const isPushMessagingConfigured =
  Object.values(firebaseConfig).every(Boolean) && Boolean(vapidKey);

export async function registerCurrentDeviceForPush(
  requestPermission = true,
): Promise<void> {
  if (!isPushMessagingConfigured) {
    throw new Error("Push notifications are not configured for this app.");
  }

  if (!("Notification" in window) || !("serviceWorker" in navigator)) {
    throw new Error("This browser does not support web push notifications.");
  }

  if (!(await isSupported())) {
    throw new Error("Firebase messaging is not supported in this browser.");
  }

  if (Notification.permission === "denied") {
    throw new Error(
      "Notifications are blocked by your browser. Allow them in site settings, then try again.",
    );
  }

  const permission = Notification.permission === "granted"
    ? "granted"
    : requestPermission
      ? await Notification.requestPermission()
      : Notification.permission;
  if (permission !== "granted") {
    throw new Error("Notification permission was not granted.");
  }

  const serviceWorkerRegistration = await navigator.serviceWorker.register(
    messagingWorkerUrl,
    { type: "module" },
  );
  const app = getApps().length ? getApp() : initializeApp(firebaseConfig);
  const token = await getToken(getMessaging(app), {
    vapidKey,
    serviceWorkerRegistration,
  });

  if (!token) {
    throw new Error("Firebase did not return a device token.");
  }

  await deviceTokenService.register({ token, platform: "WEB" });
}
