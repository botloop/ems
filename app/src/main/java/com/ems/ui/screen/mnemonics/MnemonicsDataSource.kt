package com.ems.ui.screen.mnemonics

import com.ems.domain.model.Mnemonic
import com.ems.domain.model.MnemonicCategory
import com.ems.domain.model.MnemonicItem

object MnemonicsDataSource {
    val all: List<Mnemonic> = listOf(
        Mnemonic(
            acronym = "SAMPLE",
            title = "Patient History",
            category = MnemonicCategory.HISTORY,
            items = listOf(
                MnemonicItem("S", "Signs & Symptoms", "Current signs and symptoms the patient is experiencing"),
                MnemonicItem("A", "Allergies", "Allergies to medications, foods, or environmental factors"),
                MnemonicItem("M", "Medications", "Current prescription and OTC medications, supplements"),
                MnemonicItem("P", "Pertinent Past History", "Relevant past medical/surgical history, hospitalizations"),
                MnemonicItem("L", "Last Oral Intake", "When and what the patient last ate or drank"),
                MnemonicItem("E", "Events Leading Up", "What was the patient doing when this occurred?")
            )
        ),
        Mnemonic(
            acronym = "OPQRST",
            title = "Pain Assessment",
            category = MnemonicCategory.HISTORY,
            items = listOf(
                MnemonicItem("O", "Onset", "When did this start? Was it sudden or gradual?"),
                MnemonicItem("P", "Provocation / Palliation", "What makes it better or worse?"),
                MnemonicItem("Q", "Quality", "Describe the pain: sharp, dull, crushing, burning, tearing?"),
                MnemonicItem("R", "Region / Radiation", "Where is it? Does it travel anywhere?"),
                MnemonicItem("S", "Severity", "Rate on a scale of 0-10 (0 = no pain, 10 = worst possible)"),
                MnemonicItem("T", "Timing", "Is it constant or intermittent? How long has it lasted?")
            )
        ),
        Mnemonic(
            acronym = "DCAP-BTLS",
            title = "Trauma Assessment",
            category = MnemonicCategory.TRAUMA,
            items = listOf(
                MnemonicItem("D", "Deformities", "Obvious deformities, fractures, angulation"),
                MnemonicItem("C", "Contusions", "Bruising or discoloration of the skin"),
                MnemonicItem("A", "Abrasions", "Scrapes or superficial skin injuries"),
                MnemonicItem("P", "Punctures / Penetrations", "Puncture wounds, impaled objects, penetrating injuries"),
                MnemonicItem("B", "Burns", "Thermal, chemical, electrical, or radiation burns"),
                MnemonicItem("T", "Tenderness", "Point tenderness or guarding on palpation"),
                MnemonicItem("L", "Lacerations", "Cuts or tears in the skin"),
                MnemonicItem("S", "Swelling", "Edema, swelling, or hematoma formation")
            )
        ),
        Mnemonic(
            acronym = "PENMAN",
            title = "Scene Size-Up",
            category = MnemonicCategory.SCENE,
            items = listOf(
                MnemonicItem("P", "Personal Protective Equipment", "Don appropriate PPE before approaching scene"),
                MnemonicItem("E", "Environmental Hazards", "Survey for fire, traffic, chemical, electrical hazards"),
                MnemonicItem("N", "Number of Patients", "Determine total number; request additional resources if needed"),
                MnemonicItem("M", "Mechanism of Injury / Nature of Illness", "MOI for trauma; NOI for medical emergencies"),
                MnemonicItem("A", "Additional Resources Needed", "ALS, fire, police, extrication, HAZMAT, air transport?"),
                MnemonicItem("N", "Navigate Route / Hospital", "Confirm destination and notify receiving facility")
            )
        ),
        Mnemonic(
            acronym = "AVPU",
            title = "Level of Consciousness",
            category = MnemonicCategory.ASSESSMENT,
            items = listOf(
                MnemonicItem("A", "Alert", "Patient is awake, alert, and oriented"),
                MnemonicItem("V", "Verbal", "Patient responds to verbal stimuli only"),
                MnemonicItem("P", "Pain", "Patient responds to painful stimuli only"),
                MnemonicItem("U", "Unresponsive", "Patient does not respond to any stimuli")
            )
        ),
        Mnemonic(
            acronym = "AEIOU-TIPS",
            title = "Altered Mental Status",
            category = MnemonicCategory.ASSESSMENT,
            items = listOf(
                MnemonicItem("A", "Alcohol / Acidosis", "Intoxication or metabolic acidosis"),
                MnemonicItem("E", "Epilepsy / Electrolytes", "Seizure disorder or electrolyte imbalance"),
                MnemonicItem("I", "Insulin / Infection", "Hypoglycemia, DKA, or CNS infection"),
                MnemonicItem("O", "Overdose / Oxygen", "Drug overdose or hypoxia"),
                MnemonicItem("U", "Uremia", "Kidney failure causing toxic build-up"),
                MnemonicItem("T", "Trauma", "Head injury, stroke, or intracranial bleeding"),
                MnemonicItem("I", "Infection", "Sepsis, meningitis, encephalitis"),
                MnemonicItem("P", "Psychiatric / Poison", "Psychiatric emergency or poisoning"),
                MnemonicItem("S", "Stroke / Shock", "CVA or hemodynamic compromise")
            )
        ),
        Mnemonic(
            acronym = "Cincinnati",
            title = "Stroke Assessment",
            category = MnemonicCategory.ASSESSMENT,
            items = listOf(
                MnemonicItem("1", "Facial Droop", "Ask patient to show teeth or smile; one side drooping = abnormal"),
                MnemonicItem("2", "Arm Drift", "Both arms extended, eyes closed for 10 sec; one drifts = abnormal"),
                MnemonicItem("3", "Speech", "Repeat 'You can't teach an old dog new tricks'; slurred/wrong = abnormal")
            )
        ),
        Mnemonic(
            acronym = "6 Rs",
            title = "Medication Administration",
            category = MnemonicCategory.ASSESSMENT,
            items = listOf(
                MnemonicItem("1", "Right Patient", "Confirm patient identity before administering"),
                MnemonicItem("2", "Right Drug", "Verify medication name, not just packaging"),
                MnemonicItem("3", "Right Dose", "Check concentration and calculate correct dose"),
                MnemonicItem("4", "Right Route", "Confirm IV, IO, IM, SQ, or other route is appropriate"),
                MnemonicItem("5", "Right Time", "Confirm timing and frequency are appropriate"),
                MnemonicItem("6", "Right Documentation", "Document drug, dose, route, time, and patient response")
            )
        )
    )

    fun search(query: String): List<Mnemonic> {
        if (query.isBlank()) return all
        val q = query.lowercase()
        return all.filter { m ->
            m.acronym.lowercase().contains(q) ||
            m.title.lowercase().contains(q) ||
            m.category.displayName.lowercase().contains(q) ||
            m.items.any { it.term.lowercase().contains(q) || it.description.lowercase().contains(q) }
        }
    }
}
