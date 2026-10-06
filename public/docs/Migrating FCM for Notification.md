How to Configure Firebase Cloud Messaging (FCM) for Web

1. **Create the Firebase project**
   - Open [Firebase Console](https://console.firebase.google.com/?utm_source=chatgpt.com)
   - Click **Create a project**
   - Enter your project name, e.g. `my-notification-app`
   - Google Analytics is optional; you can disable it if you don't need it.
   - Click **Create project**. [Firebase](https://firebase.google.com/docs/web/setup?utm_source=chatgpt.com)

2. **Register your Web App**
   - Open your newly created project.
   - On the project overview page, click the **Web (`</>`) icon**.
   - Give it an app nickname.
   - Click **Register app**.
   - Firebase will show you a `firebaseConfig` object. **You'll need this for your frontend.** [Firebase](https://firebase.google.com/docs/web/setup?utm_source=chatgpt.com)

   Official guide: [Add Firebase to your JavaScript project](https://firebase.google.com/docs/web/setup?utm_source=chatgpt.com)

3. **Create the backend service-account JSON**
   - Go to **Project settings** (gear icon).
   - Select **Service accounts**.
   - Click **Generate new private key** → **Generate key**.
   - Download the JSON file.
   - **Do not put this file in Git/GitHub or your frontend.** [Firebase](https://firebase.google.com/docs/admin/setup?utm_source=chatgpt.com)

   Official guide: [Firebase Admin SDK setup](https://firebase.google.com/docs/admin/setup?utm_source=chatgpt.com)

4. **Set up Web Push / VAPID**
   - Go to **Project settings → Cloud Messaging**.
   - Find **Web configuration → Web Push certificates**.
   - Click **Generate key pair**.
   - Copy the **public VAPID key** for your frontend. [Firebase](https://firebase.google.com/docs/cloud-messaging/web/get-started?utm_source=chatgpt.com)

   Official guide: [Firebase Cloud Messaging for Web](https://firebase.google.com/docs/cloud-messaging/web/get-started?utm_source=chatgpt.com)

5. **FCM HTTP v1**
   - Your backend will use Firebase's HTTP v1/API credentials to send notifications.
   - The service-account JSON you generated is the important backend credential. [Firebase](https://firebase.google.com/docs/cloud-messaging/send/v1-api?authuser=44\&utm_source=chatgpt.com)
