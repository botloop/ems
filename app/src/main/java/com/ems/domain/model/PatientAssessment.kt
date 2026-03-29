package com.ems.domain.model

import java.time.Instant

enum class AssessmentStep(val label: String, val index: Int) {
    SCENE_SIZE_UP("Scene Size-Up", 0),
    INITIAL_ASSESSMENT("Initial Assessment", 1),
    PRIMARY_SURVEY("Primary Survey (ABCDE)", 2),
    SAMPLE_HISTORY("SAMPLE History", 3),
    OPQRST("OPQRST — Chief Complaint", 4),
    SECONDARY_SURVEY("Secondary Survey", 5),
    VITAL_SIGNS("Vital Signs", 6),
    TRANSPORT("Transport Decision", 7);

    companion object {
        val TOTAL_STEPS = entries.size
    }
}

data class PenmanCheck(
    val id: String,
    val label: String,
    val description: String,
    var isChecked: Boolean = false
)

val penmanItems = listOf(
    PenmanCheck("P", "P – Personal Protective Equipment", "Ensure PPE is donned appropriately for scene hazards"),
    PenmanCheck("E", "E – Environmental Hazards", "Survey for fire, traffic, chemical, electrical, or structural hazards"),
    PenmanCheck("N", "N – Number of Patients", "Determine total patient count and request resources if needed"),
    PenmanCheck("M", "M – Mechanism of Injury / Illness", "Identify MOI or NOI to guide assessment focus"),
    PenmanCheck("A", "A – Additional Resources Needed", "Call for ALS, fire, police, extrication, or HAZMAT as needed"),
    PenmanCheck("N2", "N – Navigate Route / Destination", "Confirm hospital destination and ETD for medical control")
)

data class PatientAssessment(
    val id: String = "",
    val pcrId: String = "",
    val timestamp: Instant = Instant.now(),
    val currentStep: AssessmentStep = AssessmentStep.SCENE_SIZE_UP,
    val penmanChecked: Map<String, Boolean> = emptyMap(),
    // SAMPLE
    val signs: String = "",
    val allergies: String = "",
    val medications: String = "",
    val pastHistory: String = "",
    val lastOralIntake: String = "",
    val events: String = "",
    // OPQRST
    val onset: String = "",
    val provocation: String = "",
    val quality: String = "",
    val region: String = "",
    val severity: Int = 0,
    val timing: String = "",
    // Primary ABCDE
    val airwayPatent: Boolean? = null,
    val breathingAdequate: Boolean? = null,
    val circulationIntact: Boolean? = null,
    val disabilityGcs: Int? = null,
    val exposureFindings: String = "",
    // Secondary
    val dcapBtlsFindings: Map<String, String> = emptyMap(),
    val additionalNotes: String = ""
) {
    val allPenmanChecked: Boolean
        get() = penmanItems.all { penmanChecked[it.id] == true }

    val progressFraction: Float
        get() = (currentStep.index + 1).toFloat() / AssessmentStep.TOTAL_STEPS.toFloat()
}
