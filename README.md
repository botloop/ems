# EMS PCR — Patient Assessment & Care Reporting

A high-fidelity, minimalist Android application for EMS professionals. Streamlines patient assessment workflows and Patient Care Report (PCR) documentation with clinical calculators and reference tools.

---

## Screenshots

> _Coming soon — build and run the app to see the UI._

---

## Features

### Assessment Engine
- **8-step progressive workflow** — Scene Size-Up → Initial → Primary Survey → SAMPLE → OPQRST → Secondary → Vital Signs → Transport
- **PENMAN safety checklist** — all 6 boxes must be checked before advancing to Initial Assessment
- Animated step transitions with a live progress bar

### Smart PCR System
- **5-tab Patient Care Report** scaffold
  | Tab | Contents |
  |---|---|
  | Response | Unit #, incident #, call type, timestamped response times |
  | Patient | Demographics, gender, DOB, insurance |
  | Clinical | Chief complaint, LOC (AVPU), interactive body map, DCAP-BTLS findings, narrative |
  | Vitals | Serial vital signs sets with add/delete; shock index per set |
  | Disposition | Transport destination, condition on arrival, refusal of care, transfer checklist |
- **Interactive Body Map** — Canvas-drawn human silhouette; tap any zone to document findings
- **Narrative Notes** with one-tap mnemonic paste from the Mnemonics screen

### Clinical Calculators
- **GCS Calculator** — 3-column selector (Eyes 1–4, Verbal 1–5, Motor 1–6)
  - Live total with severity label (Mild / Moderate / **Severe**)
  - `LowGCSWarning` dialog + haptic feedback when total ≤ 8
- **Vital Signs Interpreter** — real-time per-field severity (Normal / Caution / Critical)
  - **Shock Index** = HR ÷ SBP — red if > 0.9, amber if 0.6–0.9
  - Normal ranges reference card

### Clinical Mnemonics
Searchable, expandable cards with **Copy to PCR** clipboard button:

| Acronym | Purpose |
|---|---|
| SAMPLE | Patient history |
| OPQRST | Pain/chief complaint |
| DCAP-BTLS | Trauma assessment |
| PENMAN | Scene size-up |
| AVPU | Level of consciousness |
| AEIOU-TIPS | Altered mental status |
| Cincinnati | Stroke assessment |
| 6 Rs | Medication administration |

### Offline & Sync
- **Room database** — all PCRs stored locally; works without network
- **Firestore background sync** — unsynced PCRs pushed to Firebase when online

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.22 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.50 |
| Local DB | Room 2.6.1 (KSP) |
| Remote | Firebase Firestore |
| Navigation | Navigation Compose 2.7.7 |
| Build | AGP 8.2.2, Gradle 8.4 |

---

## Project Structure

```
app/src/main/java/com/ems/
├── data/
│   ├── local/          # Room entities, DAOs, EmsDatabase
│   └── repository/     # PcrRepositoryImpl, VitalSignsRepositoryImpl
├── di/                 # Hilt modules (Database, Repository, Firebase)
├── domain/
│   ├── model/          # Pcr, VitalSigns, GcsScore, PatientAssessment, Mnemonic
│   └── repository/     # Repository interfaces
├── navigation/         # NavGraph, Screen routes
└── ui/
    ├── components/     # BodyMap (Canvas), CommonComponents
    ├── theme/          # Color, Theme, Type, Shape
    └── screen/
        ├── assessment/ # 8-step assessment engine
        ├── dashboard/  # Bento Box home screen
        ├── gcs/        # GCS calculator
        ├── mnemonics/  # Mnemonic library
        ├── pcr/        # PCR list + 5-tab detail
        └── vitals/     # Vital signs interpreter
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- A Firebase project (for Firestore sync)

### Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/botloop/ems.git
   cd ems
   ```

2. Add your `google-services.json` to `app/`:
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Add an Android app with package name `com.ems`
   - Download `google-services.json` and place it at `app/google-services.json`

3. Open in Android Studio → **Sync Project with Gradle Files**

4. Run on a device or emulator (API 26+)

---

## Design System

| Token | Value | Usage |
|---|---|---|
| Clinical Blue | `#1A73E8` | Primary actions, headers |
| Emergency Red | `#D93025` | Critical alerts, GCS ≤ 8, SI > 0.9 |
| Warning Amber | `#FBBC04` | Caution states |
| Surface | `#F8F9FA` | App background |

The dashboard uses a **Bento Box** grid layout — `BoxWithConstraints` adapts to 2 columns on phones and 3 columns on tablets.

---

## Electronic PCR (E-PCR) — Pros & Cons

### Pros

| # | Benefit | Detail |
|---|---|---|
| 1 | **Legibility** | Typed entries eliminate handwriting errors, ambiguous abbreviations, and illegible drug dosages |
| 2 | **Completeness checks** | Required-field validation prevents incomplete PCRs from being submitted — reducing QA rejections |
| 3 | **Speed at billing** | Structured data feeds directly into billing software; reduces manual re-keying and claim denials |
| 4 | **Real-time access** | Receiving hospitals can view the PCR before the unit arrives, allowing earlier preparation |
| 5 | **Data analytics** | Aggregated records enable response-time analysis, protocol compliance audits, and outcomes research |
| 6 | **Reduced storage** | No physical paper files; records are searchable and backed up in the cloud |
| 7 | **NEMSIS compliance** | E-PCR systems export to the National EMS Information System standard automatically |
| 8 | **Timestamps** | GPS + system clock auto-stamp dispatch, on-scene, and transport times — removes estimation bias |

### Cons

| # | Limitation | Detail |
|---|---|---|
| 1 | **Device dependency** | A dead battery, cracked screen, or software crash can halt documentation mid-call |
| 2 | **Distraction risk** | Eyes on a screen instead of the patient during active care phases |
| 3 | **Learning curve** | New providers require training time; mistakes in dropdown selection can be as harmful as bad handwriting |
| 4 | **Data entry lag** | Complex interfaces can slow documentation compared to a paper form in high-acuity situations |
| 5 | **Connectivity** | Sync to hospital or dispatch relies on cellular/Wi-Fi — poor signal areas lose real-time sharing benefit |
| 6 | **Privacy & security** | PHI stored digitally requires HIPAA-compliant encryption, access controls, and breach response plans |
| 7 | **Cost** | Hardware procurement, software licensing, and IT support add operational overhead |
| 8 | **System downtime** | Server outages or app updates can force a temporary return to paper backup workflows |

---

## License

MIT License — see [LICENSE](LICENSE) for details.
