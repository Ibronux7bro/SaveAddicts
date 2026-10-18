# Test Plan & Verification Matrix — Save Addicts

This test plan defines the testing strategy, test cases, and verification results for Save Addicts.

---

## 1. Automated Test Execution

The codebase includes an automated test suite located at `d:\SOAD\app\src\test\java\com\saveaddicts\app\`.

### Running the Test Suite
```powershell
.\gradlew test
```

### Verified Test Classes:
- [`AccessControlTest.java`](file:///d:/SOAD/app/src/test/java/com/saveaddicts/app/AccessControlTest.java) — Verifies RBAC restrictions for all 4 roles.
- [`ModelValidationTest.java`](file:///d:/SOAD/app/src/test/java/com/saveaddicts/app/ModelValidationTest.java) — Verifies data constraints, pseudonym defaults, and score boundaries.
- [`DemoEncryptionTest.java`](file:///d:/SOAD/app/src/test/java/com/saveaddicts/app/DemoEncryptionTest.java) — Verifies honest security reporting and Base64 cryptographic envelope roundtrip.
- [`ExampleUnitTest.java`](file:///d:/SOAD/app/src/test/java/com/saveaddicts/app/ExampleUnitTest.java) — Baseline harness test.

**Current Result**: `13/13 Passed` across both Debug and Release build variants.

---

## 2. Functional Test Cases (FR1 – FR10)

| Test ID | Requirement | Precondition | Input / Action | Expected Result | Status |
|---|---|---|---|---|:---:|
| **TC-FR1-01** | FR1 Anonymous Sign-Up | Clean install | Pseudonym: `Phoenix88`, Email: `test@saveaddicts.local`, Password: `password123` | Account created; routed to Substance Selection. No ID requested. | **PASSED** |
| **TC-FR1-02** | FR1 Short Pseudonym Validation | Register screen | Pseudonym: `AB`, Password: `password123` | Error displayed: "Pseudonym must be at least 3 characters long." | **PASSED** |
| **TC-FR1-03** | FR1 Password Mismatch | Register screen | Password: `password123`, Confirm: `password456` | Error displayed: "Passwords do not match." | **PASSED** |
| **TC-FR1-04** | FR1 Role Routing | Login screen | Click "Physician" quick-login | Routed to `PhysicianMainActivity`. | **PASSED** |
| **TC-FR2-01** | FR2 Substance Selection | Register or Profile | Select `Opioids`, click Continue | Profile updated; educational library defaults to Opioid materials. | **PASSED** |
| **TC-FR2-02** | FR2 Category Persistence | Dashboard | Select filter button on top bar | Current focus displayed; switching updates resource feed instantly. | **PASSED** |
| **TC-FR3-01** | FR3 Messaging Composition | Messages Tab | Enter message text and click Send | Bubble appears on right; timestamp formatted; message persisted. | **PASSED** |
| **TC-FR3-02** | FR3 Security Notice | Chat screen | Open conversation | Banner clearly identifies encryption interface status. | **PASSED** |
| **TC-FR4-01** | FR4 Book Consultation | Appointments Tab | Select Dr. Ahmad, pick tomorrow's date, pick 10:00 AM slot | Appointment created in PENDING status; visible in upcoming list. | **PASSED** |
| **TC-FR4-02** | FR4 Prevent Double Booking | Booking screen | Attempt to book same doctor, date, and time slot twice | Error displayed: "This time slot is already reserved." | **PASSED** |
| **TC-FR4-03** | FR4 Cancel Appointment | Appointments Tab | Click Cancel on pending appointment | Status changes to CANCELLED; buttons disabled. | **PASSED** |
| **TC-FR5-01** | FR5 Optimism Graph Render | Patient Tab 0 | Patient with 4 dated records | Canvas draws curve, points at 30%, 48%, 65%, 82%, date labels on X axis. | **PASSED** |
| **TC-FR5-02** | FR5 Empty Recovery State | Patient Tab 0 | New patient with 0 records | Graph renders clean empty message: "No recovery records recorded yet". | **PASSED** |
| **TC-FR6-01** | FR6 Content Filtering | Library Tab | Click "Videos" chip filter | Non-video items hidden; only Video educational media shown. | **PASSED** |
| **TC-FR6-02** | FR6 Sample Content Flag | Library Tab | View resource item | "Sample Educational Content" badge clearly displayed. | **PASSED** |
| **TC-FR7-01** | FR7 Psychiatrist Queue | Psychiatrist Tab | Log in as Dr. Sarah | Pending patient `NewDawn` visible in queue with request notes. | **PASSED** |
| **TC-FR7-02** | FR7 Accept Consultation | Queue item | Click "Accept Session" | Status changes to ACCEPTED; "Open Chat" button becomes visible. | **PASSED** |
| **TC-FR8-01** | FR8 Update Clinical Record | Physician Portal | Select `HopeSeeker42`, set score 85%, select IMPROVING | Record added; Optimism Graph updates immediately. | **PASSED** |
| **TC-FR8-02** | FR8 Patient Modify Block | Patient account | Patient attempts to open `UpdateRecoveryActivity` directly | Blocked with security warning; activity finishes immediately. | **PASSED** |
| **TC-FR9-01** | FR9 Confirm Appointment | Physician Portal | Physician views pending appointment, clicks "Confirm" | Status changes to CONFIRMED; synchronized on patient side. | **PASSED** |
| **TC-FR10-01**| FR10 Add Educational Item | Admin Portal | Enter title, select format VIDEO, substance CANNABIS | Item saved and added to repository listing. | **PASSED** |
| **TC-FR10-02**| FR10 Non-Admin Modify Block | Patient account | Patient attempts to access `AdminMainActivity` | Throws `SecurityException` / shows security toast and redirects. | **PASSED** |

---

## 3. UI/UX & Usability Verification

- **Color Palette Compliance**:
  - Primary Navy (`#0D2538`) used consistently for app bars and prominent headers.
  - Teal/Turquoise (`#00897B`) used for interactive buttons, progress indicators, and graph curves.
  - White/Light Neutral (`#F8FAFC`, `#FFFFFF`) used for card surfaces and readable backgrounds.
  - Red (`#D32F2F`) reserved exclusively for error banners, cancellations, and deletions.
- **Empty & Error States**:
  - Empty recovery records: Clean explanatory text explaining that the physician updates the timeline.
  - Empty appointments: Displayed when no bookings exist.
  - Empty messages: Displayed when no conversations exist.
  - Empty library: Displayed when filter criteria match zero items.
- **Accessibility**:
  - Touch targets exceed 48dp on all primary action buttons.
  - High-contrast typography adhering to WCAG 2.1 AA standards.
  - Content descriptions applied across all interactive image buttons.

---

## 4. Security & Role Authorization Tests

```java
// Verification excerpt from AccessControlTest.java:
@Test
public void testPhysicianRecoveryRecordPermissions() {
    assertTrue(AccessControl.canModifyRecoveryRecord(UserRole.PHYSICIAN));
    assertFalse(AccessControl.canModifyRecoveryRecord(UserRole.PATIENT));
    assertFalse(AccessControl.canModifyRecoveryRecord(UserRole.ADMIN));
    assertFalse(AccessControl.canModifyRecoveryRecord(UserRole.PSYCHIATRIST));
}
```

- **Patient Privilege Escalation**: Verified that changing client role state does not grant access to clinical update endpoints.
- **Data Minimization**: Verified that network payloads contain zero national ID numbers or unhashed passwords.
- **Encryption Disclosure**: Verified that `DemoEncryptionService.isProductionSecure()` unconditionally returns `false`.
