# 🌿 Save Addicts — Android Application

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java%2017-orange.svg)](https://www.oracle.com/java/)
[![SDK](https://img.shields.io/badge/Min%20SDK-28-blue.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blueviolet.svg)](https://developer.android.com)
[![Backend](https://img.shields.io/badge/Backend-Firebase%20Auth%20%26%20RTDB-amber.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-Academic%20%2F%20Open%20Source-teal.svg)](#)

---

## 📌 نبذة عن المشروع (Project Overview)

**Save Addicts** هو تطبيق أندرويد متكامل للرعاية الصحية النفسية والطبية، صُمم خصيصاً للتغلب على الوصمة الاجتماعية (Social Stigma) المرتبطة بطلب العلاج من الإدمان، مع الحفاظ الصارم على سرية وهوية المستخدمين دون الحاجة إلى بيانات شخصية أو بطاقات مدنية.

**Save Addicts** is a confidential, privacy-first mobile healthcare and addiction recovery platform designed to overcome social stigma and provide accessible psychological and medical care. The platform connects individuals seeking recovery with licensed psychiatrists and physicians while strictly preserving user anonymity.

---

## 🎓 1. الخلفية الأكاديمية والمصدر المرجعي (Academic Background)

تم تطوير هذا التطبيق بناءً على المواصفات والتقارير الفنية المعتمدة لمشروع التخرج:
- **المشروع:** TM471 Final Year Project (FYP)
- **الجامعة:** الجامعة العربية المفتوحة - الكويت (Arab Open University - Kuwait)
- **إعداد الطالبة:** غالية مذرم المطيري (Ghalia Madhram Almutairi - 211857)
- **إشراف:** د. لؤي شلبي (Supervised by Dr. Loai Shalaby)

### الركائز الأساسية للمشروع (Core Mission Pillars)
1. **كسر الوصمة الاجتماعية (Dismantle Stigma):** تيسير طلب المساعدة الطبية والنفسية بسرية تامة دون خوف من التشهير أو الأحكام المسبقة.
2. **الاسم المستعار وحماية الخصوصية (Absolute Pseudonymity):** التسجيل يتم بكنية/اسم مستعار فقط، دون طلب أرقام هواتف أو بطاقات هوية وطنية.
3. **منظومة رعاية متكاملة (Integrated Care Ecosystem):** محادثات سرية مع أطباء نفسيين، حجز مواعيد سريرية مع أطباء عامين، متابعة تعافي عبر رسم بياني تفاعلي (Optimism Graph)، ومكتبة توعوية مخصصة بحسب نوع المادة.

---

## 🛠️ 2. الحزمة التقنية (Tech Stack & Architecture)

| المعيار (Parameter) | المواصفات (Specification) |
|---|---|
| **اسم التطبيق (App Name)** | Save Addicts |
| **اسم الحزمة (Package)** | `com.saveaddicts.app` |
| **لغة البرمجة (Language)** | Java (JDK 17) |
| **واجهة المستخدم (UI/UX)** | Native Android XML & Material Components (Material 3 DayNight) |
| **أدنى إصدار أندرويد (Min SDK)** | API 28 (Android 9.0 Pie) |
| **إصدار الاستهداف (Target SDK)** | API 36 |
| **نظام البناء (Build Tool)** | Gradle 8.13 مع Kotlin DSL (`build.gradle.kts`) |
| **إصدار AGP** | Android Gradle Plugin 8.13.2 |
| **خدمات المصادقة (Auth)** | Firebase Authentication (مع دعم النمط التجريبي المحلي Mock Repository) |
| **قاعدة البيانات (Database)** | Firebase Realtime Database (متصلة بقواعد أمان صارمة ومفهرسة) |

---

## 🌟 3. المتطلبات الوظيفية المنفذة (Implemented Features FR1 – FR10)

| المعرف (ID) | الدور (Role) | الخاصية (Feature) | الحالة (Status) | التفاصيل (Details) |
|:---:|:---:|---|:---:|---|
| **FR1** | مريض (Patient) | التسجيل والمصادقة بالاسم المستعار | **منجز 100%** | إنشاء حساب باسم مستعار وكلمة مرور فقط، وتوجيه تلقائي بحسب الصلاحيات. |
| **FR2** | مريض (Patient) | اختيار المادة وتصفية المحتوى | **منجز 100%** | تحديد نوع المادة (كحول، أفيونات، حشيش، أخرى، الكل) لعرض موارد مخصصة. |
| **FR3** | مريض (Patient) | محادثات مشفرة مع الطبيب النفسي | **منجز 100%** | جلسات واستشارات فورية مع تشفير آمن وإفصاح شفاف عن الحماية. |
| **FR4** | مريض (Patient) | حجز المواعيد الطبية الحضورية | **منجز 100%** | تقويم تفاعلي لاختيار اليوم والوقت، ومنع التضارب في المواعيد وإمكانية الإلغاء. |
| **FR5** | مريض (Patient) | لوحة التعافي ومؤشر التفاؤل (Optimism Graph) | **منجز 100%** | رسم بياني مخصص (`OptimismGraphView`) يُرسم عبر الـ Canvas لمتابعة تقدم العلاج بنسب مئوية. |
| **FR6** | مريض (Patient) | المكتبة التوعوية والتثقيفية | **منجز 100%** | استعراض مقالات وكتب وفيديوهات مصنفة بحسب نوع المادة والفئة العمرية. |
| **FR7** | طبيب نفسي (Psychiatrist) | إدارة طلبات الاستشارات النفسية | **منجز 100%** | قائمة انتظار الاستشارات، قبول الطلبات وبدء جلسات المحادثة المباشرة. |
| **FR8** | طبيب بشري (Physician) | تحديث ملفات ومؤشرات التعافي | **منجز 100%** | معاينة ملف المريض وسجل تفاؤله وتحديث النقاط والتقييم السريري دورياً. |
| **FR9** | طبيب بشري (Physician) | إدارة وتأكيد المواعيد السريرية | **منجز 100%** | استعراض حجوزات المرضى، تأكيد المواعيد أو الاعتذار مع ذكر الأسباب. |
| **FR10** | مدير النظام (Admin) | إدارة محتوى المكتبة التثقيفية | **منجز 100%** | لوحة تحكم كاملة لإضافة، تعديل وحذف الموارد التوعوية ووسائط الفيديو والكتب. |

---

## 📂 4. الهيكل البرمجي للمشروع (Project Structure)

```
app/src/main/
├── AndroidManifest.xml
├── java/com/saveaddicts/app/
│   ├── MainActivity.java                       # Router to SplashActivity
│   ├── SaveAddictsApplication.java             # Base Application & repository initialization
│   ├── models/                                 # Core data models
│   │   ├── UserProfile.java                    # Pseudonymous profile linked to UID
│   │   ├── UserRole.java                       # PATIENT, PSYCHIATRIST, PHYSICIAN, ADMIN
│   │   ├── SubstanceType.java                  # ALCOHOL, OPIOIDS, CANNABIS, OTHER, ALL
│   │   ├── HealthStatus.java                   # IMPROVING, STABLE, MONITORING, CRITICAL
│   │   ├── Appointment.java                    # Physician consultation booking
│   │   ├── AppointmentStatus.java              # PENDING, CONFIRMED, CANCELLED, COMPLETED
│   │   ├── ChatMessage.java                    # Message payload with cryptographic fields
│   │   ├── Conversation.java                   # Thread metadata between patient & psychiatrist
│   │   ├── ConsultationSession.java            # Session request in psychiatrist queue
│   │   ├── RecoveryRecord.java                 # Clinician-updated score for Optimism Graph
│   │   ├── EducationalResource.java            # Video, Book, Article media model
│   │   └── ResourceType.java, AgeGroup.java, SessionStatus.java
│   ├── repositories/                           # Data Abstraction & Clean Architecture
│   │   ├── AuthRepository.java                 # Authentication contract
│   │   ├── AppointmentRepository.java          # Booking contract
│   │   ├── MessagingRepository.java            # Conversations & messaging contract
│   │   ├── RecoveryRepository.java             # Recovery scores contract
│   │   ├── EducationalRepository.java          # Library resources contract
│   │   ├── UserRepository.java                 # User accounts directory contract
│   │   ├── SessionRepository.java              # Consultation queue contract
│   │   ├── RepositoryLocator.java              # Dependency injector & mode switcher
│   │   ├── firebase/                           # Live Firebase Realtime Database & Auth
│   │   │   ├── FirebaseAuthRepository.java
│   │   │   └── FirebaseRealtimeRepository.java
│   │   └── mock/                               # Thread-safe in-memory sandbox repository
│   │       └── MockDataRepository.java
│   ├── security/                               # Access control & encryption
│   │   ├── AccessControl.java                  # Strict role authorization guards
│   │   ├── EncryptionService.java              # Cryptography contract
│   │   └── DemoEncryptionService.java          # Transparent secure envelope
│   ├── adapters/                               # RecyclerView Adapters
│   │   ├── AppointmentAdapter.java
│   │   ├── ChatMessageAdapter.java
│   │   ├── ConsultationSessionAdapter.java
│   │   ├── ConversationAdapter.java
│   │   ├── EducationalResourceAdapter.java
│   │   └── PatientListAdapter.java
│   └── ui/                                     # Activities and Custom Views
│       ├── SplashActivity.java
│       ├── LoginActivity.java                  # 1-Click fast role switcher
│       ├── RegisterActivity.java               # Anonymous sign-up
│       ├── SubstanceSelectionActivity.java     # Substance category picker
│       ├── PatientMainActivity.java            # 4-tab Patient dashboard
│       ├── PsychiatristMainActivity.java       # Consultation queue & chats
│       ├── PhysicianMainActivity.java          # Clinical records & appointment manager
│       ├── AdminMainActivity.java              # Content manager
│       ├── ChatActivity.java                   # Real-time messaging screen
│       ├── BookAppointmentActivity.java        # Calendar appointment scheduler
│       ├── UpdateRecoveryActivity.java         # Optimism score updater
│       ├── ResourceDetailActivity.java         # Educational material reader
│       ├── AddEditResourceActivity.java        # Admin resource form
│       └── components/
│           └── OptimismGraphView.java          # Custom Canvas View for Optimism Timeline
└── res/                                        # Layouts, Drawables, Themes, Strings
```

---

## 🔒 5. الأمان وحماية البيانات (Security & Privacy)

- **قواعد أمان Firebase Realtime Database (`FIREBASE_SECURITY_RULES.json`):**
  - لا يمكن لأي مستخدم قراءة بيانات مستخدم آخر دون تصريح.
  - المحادثات محمية بصلاحيات مقتصرة حصرياً على طرفي المحادثة (`patientId` و `psychiatristId`).
  - تحديث سجلات التعافي محصور فقط على الأطباء المصرح لهم (`physician`).
  - إدارة المكتبة التوعوية محصورة على مدراء النظام (`admin`).
- **حماية الهوية:** لا يتم تخزين الاسم الحقيقي، الرقم المدني، رقم الهاتف أو الموقع الجغرافي.
- **التشفير:** تجهيز المعمارية لدعم التشفير التام بين الطرفين (End-to-End Encryption).

---

## 👥 6. حسابات تجريبية سريعة (Pre-Seeded Demo Accounts)

لتسهيل المعاينة الفورية أثناء التقييم والمناقشة، توفر شاشة الدخول أزرار **دخول سريع بنقرة واحدة (1-Click Login)** للحسابات الجاهزة:

| الدور (Role) | البريد الإلكتروني (Email) | كلمة المرور (Password) | الاسم الظاهر (Name / Pseudonym) | البيانات المجهزة مسبقاً |
|---|---|---|---|---|
| **مريض (Patient)** | `patient@saveaddicts.org` | `password123` | `HopeSeeker42` | سجل تعافي متكامل بنسب تصاعدية (30% -> 82%)، موعدين طبيين، ومحادثة مع طبيب. |
| **طبيب نفسي (Psychiatrist)** | `psychiatrist@saveaddicts.org` | `password123` | `Dr. Sarah Al-Sabah` | طلب استشارة في الانتظار، ومحادثة فعالة مع المريض `HopeSeeker42`. |
| **طبيب بشري (Physician)** | `physician@saveaddicts.org` | `password123` | `Dr. Ahmad Mansour` | 3 مواعيد استشارة للإدارة والتأكيد، وقائمة مرضى لتحديث سجلاتهم الطبية. |
| **مدير النظام (Admin)** | `admin@saveaddicts.org` | `password123` | `System Administrator` | 6 موارد تعليمية وتثقيفية متنوعة (مقالات، كتب، فيديوهات) جاهزة للإدارة. |

---

## 🚀 7. طريقة البناء والتشغيل (How to Build & Run)

### المتطلبات الأساسية
- **Android Studio** (نسخة Ladybug أو أحدث)
- **Java Development Kit (JDK 17)**
- **Android SDK** مثبت عليه حزمة Platform API 36 (الحد الأدنى المدعوم API 28)

### خطوات التشغيل عبر السطر البرمجي:
```bash
# استنساخ المستودع
git clone https://github.com/Ibronux7bro/SaveAddicts.git

# الانتقال للمجلد
cd SaveAddicts

# بناء ملف APK التجريبي
./gradlew assembleDebug

# تشغيل الاختبارات الآلية
./gradlew test
```

### موقع حزمة التطبيق المبنية (Debug APK):
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📚 8. الفهرس والوثائق الملحقة (Documentation Index)

- 📄 [`IMPLEMENTATION_STATUS.md`](IMPLEMENTATION_STATUS.md) — تقرير تفصيلي بحالة المتطلبات والاختبارات الفنية.
- 🔥 [`FIREBASE_INTEGRATION.md`](FIREBASE_INTEGRATION.md) — دليل ربط وإعداد خدمات Firebase السحابية.
- 🗄️ [`FIREBASE_SCHEMA.md`](FIREBASE_SCHEMA.md) — مخطط هيكلية قاعدة البيانات مع أمثلة JSON.
- 🛡️ [`FIREBASE_SECURITY_RULES.md`](FIREBASE_SECURITY_RULES.md) — شرح كامل لقواعد الأمان وقواعد الفهرسة.
- 🧪 [`TEST_PLAN.md`](TEST_PLAN.md) — خطة الاختبارات الشاملة (الوظيفية، الأمنية، واجهة الاستخدام).

---

## ⚖️ 9. الترخيص (License)

هذا المشروع مخصص للأغراض الأكاديمية والبحثية كجزء من مشروع تخرج بالجامعة العربية المفتوحة - الكويت.
جميع الحقوق محفوظة للمطور والمشرفين الأكاديميين © 2026.