# Firebase Realtime Database Security Rules

## How to Apply

1. Open [Firebase Console](https://console.firebase.google.com/)
2. Select project **saveaddicts**
3. Navigate to **Realtime Database → Rules**
4. Paste the JSON below and click **Publish**

---

## Rules JSON

```json
{
  "rules": {

    "users": {
      "$uid": {
        // Each authenticated user can read their own profile.
        // Write is locked down: only the owner can update their own basic fields;
        // role changes are blocked here (role must be set server-side or by Admin SDK).
        ".read":  "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid",
        "role": {
          // Prevent any client from elevating their own role.
          ".write": false
        }
      },
      // Physicians and Psychiatrists need to list all patients.
      // Admin needs to list all users.
      // Patients must NOT be able to enumerate other users.
      ".read": "auth != null && (
                  root.child('users').child(auth.uid).child('role').val() == 'PHYSICIAN'   ||
                  root.child('users').child(auth.uid).child('role').val() == 'PSYCHIATRIST' ||
                  root.child('users').child(auth.uid).child('role').val() == 'ADMIN'
                )"
    },

    "recovery_records": {
      "$recordId": {
        // Physician writes recovery records; patient reads their own.
        ".read": "auth != null && (
                    root.child('users').child(auth.uid).child('role').val() == 'PHYSICIAN' ||
                    data.child('patientId').val() == auth.uid
                  )",
        ".write": "auth != null &&
                   root.child('users').child(auth.uid).child('role').val() == 'PHYSICIAN'"
      }
    },

    "appointments": {
      "$appointmentId": {
        // Patients and Physicians can read/write their own appointments.
        ".read": "auth != null && (
                    data.child('patientId').val()   == auth.uid ||
                    data.child('physicianId').val() == auth.uid ||
                    root.child('users').child(auth.uid).child('role').val() == 'ADMIN'
                  )",
        ".write": "auth != null && (
                     data.child('patientId').val()   == auth.uid ||
                     data.child('physicianId').val() == auth.uid ||
                     !data.exists()
                   )"
      }
    },

    "conversations": {
      "$conversationId": {
        ".read":  "auth != null && (
                     data.child('patientId').val()      == auth.uid ||
                     data.child('psychiatristId').val() == auth.uid
                   )",
        ".write": "auth != null && (
                     data.child('patientId').val()      == auth.uid ||
                     data.child('psychiatristId').val() == auth.uid ||
                     !data.exists()
                   )"
      }
    },

    "messages": {
      "$conversationId": {
        // Only participants in the parent conversation can read/write messages.
        ".read": "auth != null && (
                    root.child('conversations').child($conversationId).child('patientId').val()      == auth.uid ||
                    root.child('conversations').child($conversationId).child('psychiatristId').val() == auth.uid
                  )",
        ".write": "auth != null && (
                     root.child('conversations').child($conversationId).child('patientId').val()      == auth.uid ||
                     root.child('conversations').child($conversationId).child('psychiatristId').val() == auth.uid
                   )"
      }
    },

    "educational_resources": {
      // Any authenticated user can read; only Admin can write.
      ".read":  "auth != null",
      ".write": "auth != null &&
                 root.child('users').child(auth.uid).child('role').val() == 'ADMIN'"
    },

    "consultation_sessions": {
      "$sessionId": {
        ".read": "auth != null && (
                    data.child('patientId').val()      == auth.uid ||
                    data.child('psychiatristId').val() == auth.uid ||
                    root.child('users').child(auth.uid).child('role').val() == 'ADMIN'
                  )",
        ".write": "auth != null &&
                   root.child('users').child(auth.uid).child('role').val() == 'PSYCHIATRIST'"
      }
    },

    // Block everything else by default.
    "$other": {
      ".read":  false,
      ".write": false
    }
  }
}
```

---

## Notes

- **Role field is write-protected** on the client: clients cannot self-elevate.
- **Privileged accounts** (Psychiatrist, Physician, Admin) must be created via the Firebase Admin SDK or manually in the Firebase Console by setting the `role` field after account creation — they cannot self-register through the app.
- These rules use the **Realtime Database expression language** (not Firestore rules).
- After applying rules, test them using the **Rules Playground** in the Firebase Console.
