package com.ems.ui.screen.assessment

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.domain.model.AssessmentStep
import com.ems.domain.model.penmanItems
import com.ems.ui.components.*
import com.ems.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    onBack: () -> Unit,
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            EmsTopBar(
                title = "Patient Assessment",
                onBack = onBack,
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.resetAssessment()
                    }) {
                        Icon(Icons.Filled.Refresh, "Reset", tint = androidx.compose.ui.graphics.Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress bar
            AssessmentProgressBar(
                currentStep = state.currentStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Step content
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.index > initialState.index) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "step_content"
            ) { step ->
                when (step) {
                    AssessmentStep.SCENE_SIZE_UP -> SceneSizeUpStep(
                        checks = state.penmanChecks,
                        onToggleCheck = { id ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.togglePenmanCheck(id)
                        },
                        allChecked = viewModel.allPenmanChecked(),
                        onProceed = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.proceedToNextStep()
                        }
                    )
                    AssessmentStep.INITIAL_ASSESSMENT -> InitialAssessmentStep(
                        onNext = { viewModel.proceedToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                    AssessmentStep.PRIMARY_SURVEY -> PrimarySurveyStep(
                        onNext = { viewModel.proceedToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                    AssessmentStep.SAMPLE_HISTORY -> SampleHistoryStep(
                        assessment = state.assessment,
                        onUpdateSample = { s, a, m, p, l, e -> viewModel.updateSample(s, a, m, p, l, e) },
                        onNext = { viewModel.proceedToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                    AssessmentStep.OPQRST -> OpqrstStep(
                        assessment = state.assessment,
                        onUpdate = { o, p, q, r, s, t -> viewModel.updateOpqrst(o, p, q, r, s, t) },
                        onNext = { viewModel.proceedToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                    AssessmentStep.SECONDARY_SURVEY -> SecondarySurveyStep(
                        onNext = { viewModel.proceedToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                    AssessmentStep.VITAL_SIGNS -> VitalSignsAssessmentStep(
                        onNext = { viewModel.proceedToNextStep() },
                        onBack = { viewModel.goToPreviousStep() }
                    )
                    AssessmentStep.TRANSPORT -> TransportStep(
                        onBack = { viewModel.goToPreviousStep() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AssessmentProgressBar(currentStep: AssessmentStep, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentStep.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ClinicalBlue
            )
            Text(
                text = "Step ${currentStep.index + 1} / ${AssessmentStep.TOTAL_STEPS}",
                style = MaterialTheme.typography.labelSmall,
                color = SubtleText
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { currentStep.progressFraction() },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = ClinicalBlue,
            trackColor = DividerGray
        )
    }
}

private fun AssessmentStep.progressFraction(): Float =
    (index + 1).toFloat() / AssessmentStep.TOTAL_STEPS.toFloat()

@Composable
private fun SceneSizeUpStep(
    checks: List<com.ems.domain.model.PenmanCheck>,
    onToggleCheck: (String) -> Unit,
    allChecked: Boolean,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("PENMAN Safety Check")
        Text(
            "Verify all safety conditions before patient contact.",
            style = MaterialTheme.typography.bodyMedium,
            color = SubtleText
        )
        Spacer(Modifier.height(4.dp))

        checks.forEach { check ->
            CheckboxRow(
                label = check.label,
                description = check.description,
                checked = check.isChecked,
                onCheckedChange = { onToggleCheck(check.id) }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (!allChecked) {
            WarningBanner("Check all PENMAN items to proceed to Initial Assessment")
        }

        Button(
            onClick = onProceed,
            enabled = allChecked,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue)
        ) {
            Icon(Icons.Filled.ArrowForward, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Proceed to Initial Assessment", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InitialAssessmentStep(onNext: () -> Unit, onBack: () -> Unit) {
    AssessmentStepLayout(
        title = "Initial Assessment",
        description = "Form a general impression of the patient's overall condition.",
        onNext = onNext, onBack = onBack
    ) {
        val items = listOf(
            "General Impression — Appears how? (sick/well, distressed?)",
            "Mental Status — AVPU (Alert, Verbal, Pain, Unresponsive)",
            "Airway — Patent, maintained, obstructed?",
            "Breathing — Rate, depth, effort adequate?",
            "Circulation — Skin color/condition, radial pulse present?",
            "Priority determination — Load-and-go vs. stay-and-play?"
        )
        items.forEach { item ->
            ChecklistItem(text = item)
        }
    }
}

@Composable
private fun PrimarySurveyStep(onNext: () -> Unit, onBack: () -> Unit) {
    AssessmentStepLayout(
        title = "Primary Survey (ABCDE)",
        description = "Rapidly identify and treat life threats.",
        onNext = onNext, onBack = onBack
    ) {
        listOf(
            "A — Airway: Assess and secure patent airway",
            "B — Breathing: Assess breath sounds, rate, effort; apply O₂ as needed",
            "C — Circulation: Control major hemorrhage, assess perfusion",
            "D — Disability: GCS, pupils, blood glucose if altered LOC",
            "E — Exposure/Environment: Expose injury areas; prevent hypothermia"
        ).forEach { ChecklistItem(text = it) }
    }
}

@Composable
private fun SampleHistoryStep(
    assessment: com.ems.domain.model.PatientAssessment,
    onUpdateSample: (String?, String?, String?, String?, String?, String?) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("SAMPLE History")
        LabeledTextField("S – Signs & Symptoms", assessment.signs, { onUpdateSample(it, null, null, null, null, null) })
        LabeledTextField("A – Allergies", assessment.allergies, { onUpdateSample(null, it, null, null, null, null) })
        LabeledTextField("M – Medications", assessment.medications, { onUpdateSample(null, null, it, null, null, null) })
        LabeledTextField("P – Past Medical History", assessment.pastHistory, { onUpdateSample(null, null, null, it, null, null) })
        LabeledTextField("L – Last Oral Intake", assessment.lastOralIntake, { onUpdateSample(null, null, null, null, it, null) })
        LabeledTextField("E – Events Leading Up", assessment.events, { onUpdateSample(null, null, null, null, null, it) }, singleLine = false, maxLines = 3)
        StepNavigationRow(onBack = onBack, onNext = onNext)
    }
}

@Composable
private fun OpqrstStep(
    assessment: com.ems.domain.model.PatientAssessment,
    onUpdate: (String?, String?, String?, String?, Int?, String?) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("OPQRST — Chief Complaint")
        LabeledTextField("O – Onset", assessment.onset, { onUpdate(it, null, null, null, null, null) })
        LabeledTextField("P – Provocation / Palliation", assessment.provocation, { onUpdate(null, it, null, null, null, null) })
        LabeledTextField("Q – Quality", assessment.quality, { onUpdate(null, null, it, null, null, null) })
        LabeledTextField("R – Region / Radiation", assessment.region, { onUpdate(null, null, null, it, null, null) })

        // Severity Slider
        Column {
            Text("S – Severity: ${assessment.severity}/10", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Slider(
                value = assessment.severity.toFloat(),
                onValueChange = { onUpdate(null, null, null, null, it.toInt(), null) },
                valueRange = 0f..10f,
                steps = 9,
                colors = SliderDefaults.colors(thumbColor = EmergencyRed, activeTrackColor = EmergencyRed)
            )
        }

        LabeledTextField("T – Timing", assessment.timing, { onUpdate(null, null, null, null, null, it) })
        StepNavigationRow(onBack = onBack, onNext = onNext)
    }
}

@Composable
private fun SecondarySurveyStep(onNext: () -> Unit, onBack: () -> Unit) {
    AssessmentStepLayout(
        title = "Secondary Survey",
        description = "Head-to-toe assessment using DCAP-BTLS.",
        onNext = onNext, onBack = onBack
    ) {
        val zones = listOf("Head/Skull", "Face", "Neck/C-spine", "Chest/Thorax", "Abdomen", "Pelvis", "Extremities – Upper", "Extremities – Lower", "Back/Posterior")
        zones.forEach { zone ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Circle, contentDescription = null, tint = DividerGray, modifier = Modifier.size(8.dp))
                Text(zone, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                Text("DCAP-BTLS", style = MaterialTheme.typography.labelSmall, color = SubtleText)
            }
            Divider(color = DividerGray, thickness = 0.5.dp)
        }
    }
}

@Composable
private fun VitalSignsAssessmentStep(onNext: () -> Unit, onBack: () -> Unit) {
    AssessmentStepLayout(
        title = "Vital Signs",
        description = "Obtain a full set of baseline vitals.",
        onNext = onNext, onBack = onBack
    ) {
        listOf("BP (systolic / diastolic)", "Heart Rate + rhythm", "Respiratory Rate + quality", "SpO₂ on room air", "Temperature", "Blood glucose if indicated", "Pupils (PERRL)", "GCS (E+V+M)", "Pain scale 0–10").forEach {
            ChecklistItem(text = it)
        }
    }
}

@Composable
private fun TransportStep(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Transport Decision")
        Card(
            colors = CardDefaults.cardColors(containerColor = SuccessGreenContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Assessment Complete", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SuccessGreen)
                listOf(
                    "Confirm receiving facility and notify medical control",
                    "Reassess vital signs en route",
                    "Document all findings in PCR",
                    "Provide verbal report to receiving staff"
                ).forEach { ChecklistItem(text = it) }
            }
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Back to Vital Signs")
        }
    }
}

@Composable
private fun AssessmentStepLayout(
    title: String,
    description: String,
    onNext: () -> Unit,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionHeader(title)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = SubtleText)
        Spacer(Modifier.height(4.dp))
        content()
        Spacer(Modifier.height(8.dp))
        StepNavigationRow(onBack = onBack, onNext = onNext)
    }
}

@Composable
private fun ChecklistItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Filled.CheckBoxOutlineBlank, contentDescription = null, tint = ClinicalBlue, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StepNavigationRow(onBack: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Back")
        }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue)
        ) {
            Text("Continue")
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Filled.ArrowForward, contentDescription = null)
        }
    }
}
