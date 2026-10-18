# Firebase Integration Guide — Save Addicts

This document outlines the exact, step-by-step procedure to connect the Save Addicts Android application to a live Google Firebase project once the Firebase account and credentials are ready.

---

## 1. Prerequisites in Firebase Console

1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Create or select your Firebase project (e.g. `save-addicts-prod`).
3. Add an Android Application to the project:
   - **Android package name**: `com.saveaddicts.app` (must match exactly)
   - **App nickname**: `Save Addicts`
   - **Debug signing certificate SHA-1**: Optional during setup, required if using Google Sign-in or App Check.
4. Download the generated `google-services.json` file.

---

## 2. Configuration Files & Gradle Setup

### Step A: Place `google-services.json`
Copy the downloaded `google-services.json` into the application module directory:
```
d:\SOAD\app\google-services.json
```

### Step B: Enable Google Services Plugin in `build.gradle.kts`

In `d:\SOAD\build.gradle.kts` (top-level):
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

In `d:\SOAD\app\build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    // Existing dependencies...
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}
```

---

## 3. Enable Firebase Services in Console

### 1. Firebase Authentication
1. Navigate to **Build > Authentication > Sign-in method**.
2. Enable **Email/Password**.
3. *(Optional)* If pseudonymous account creation without initial email verification is desired, email/password accounts can be created with random synthetic domains (e.g. `HopeSeeker42@anonymous.saveaddicts.local`) or using Firebase Anonymous Auth linked to custom pseudonyms.

### 2. Firebase Realtime Database
1. Navigate to **Build > Realtime Database > Create Database**.
2. Choose your nearest database location (e.g., `europe-west1` or `me-central1` if available).
3. Start in **Locked Mode**.
4. Apply the security rules defined in [Section 4](#4-realtime-database-security-rules).

---

## 4. Realtime Database Security Rules

Copy and paste these rules into the **Realtime Database > Rules** tab to enforce role authorization and prevent unauthorized reads or writes:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null",
        ".write": "auth != null && auth.uid === $uid"
      }
    },
    "recovery_records": {
      "$patientId": {
        ".read": "auth != null && (auth.uid === $patientId || root.child('users').child(auth.uid).child('role').val() === 'PHYSICIAN')",
        "$recordId": {
          ".write": "auth != null && root.child('users').child(auth.uid).child('role').val() === 'PHYSICIAN'"
        }
      }
    },
    "appointments": {
      "$appointmentId": {
        ".read": "auth != null && (data.child('patientId').val() === auth.uid || data.child('physicianId').val() === auth.uid)",
        ".write": "auth != null && (newData.child('patientId').val() === auth.uid || root.child('users').child(auth.uid).child('role').val() === 'PHYSICIAN')"
      }
    },
    "conversations": {
      "$convId": {
        ".read": "auth != null && (data.child('patientId').val() === auth.uid || data.child('psychiatristId').val() === auth.uid)",
        ".write": "auth != null && (data.child('patientId').val() === auth.uid || data.child('psychiatristId').val() === auth.uid)"
      }
    },
    "messages": {
      "$convId": {
        ".read": "auth != null && (root.child('conversations').child($convId).child('patientId').val() === auth.uid || root.child('conversations').child($convId).child('psychiatristId').val() === auth.uid)",
        "$msgId": {
          ".write": "auth != null && newData.child('senderId').val() === auth.uid"
        }
      }
    },
    "educational_resources": {
      ".read": true,
      "$resourceId": {
        ".write": "auth != null && root.child('users').child(auth.uid).child('role').val() === 'ADMIN'"
      }
    },
    "consultation_sessions": {
      ".read": "auth != null && (data.child('patientId').val() === auth.uid || root.child('users').child(auth.uid).child('role').val() === 'PSYCHIATRIST')",
      "$sessionId": {
        ".write": "auth != null"
      }
    }
  }
}
```

---

## 5. Connecting Repositories in Code

The codebase uses [`RepositoryLocator.java`](file:///d:/SOAD/app/src/main/java/com/saveaddicts/app/repositories/RepositoryLocator.java) as a clean dependency boundary.

To switch from `MockDataRepository` to live Firebase:
1. Implement the Firebase repositories (e.g. `FirebaseAuthRepository.java`, `FirebaseRecoveryRepository.java`, `FirebaseAppointmentRepository.java`, `FirebaseMessagingRepository.java`, `FirebaseEducationalRepository.java`).
2. Update `RepositoryLocator.java`:
   ```java
   public static synchronized AuthRepository getAuthRepository() {
       if (authRepository == null) {
           if (isFirebaseConnected()) {
               authRepository = new FirebaseAuthRepository();
           } else {
               authRepository = MockDataRepository.getInstance();
           }
       }
       return authRepository;
   }
   ```
3. Call `RepositoryLocator.setFirebaseConnected(true)` in an `Application` class upon startup.

Zero changes to Activity UI controllers will be required!
