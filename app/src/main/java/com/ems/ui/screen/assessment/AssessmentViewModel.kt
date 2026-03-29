package com.ems.ui.screen.assessment

import androidx.lifecycle.ViewModel
import com.ems.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class CaseType { MEDICAL, TRAUMA }

data class AssessmentUiState(
    val assessment: PatientAssessment = PatientAssessment(),
    val penmanChecks: List<PenmanCheck> = penmanItems.map { it.copy() },
    val currentStep: AssessmentStep = AssessmentStep.SCENE_SIZE_UP,
    val isStepComplete: Boolean = false,
    val caseType: CaseType = CaseType.MEDICAL
)

@HiltViewModel
class AssessmentViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    fun togglePenmanCheck(id: String) {
        _uiState.update { state ->
            val updated = state.penmanChecks.map {
                if (it.id == id) it.copy(isChecked = !it.isChecked) else it
            }
            state.copy(
                penmanChecks = updated,
                assessment = state.assessment.copy(
                    penmanChecked = updated.associate { it.id to it.isChecked }
                )
            )
        }
    }

    fun allPenmanChecked(): Boolean =
        _uiState.value.penmanChecks.all { it.isChecked }

    fun proceedToNextStep() {
        _uiState.update { state ->
            val next = AssessmentStep.entries.getOrNull(state.currentStep.index + 1)
                ?: state.currentStep
            state.copy(
                currentStep = next,
                assessment = state.assessment.copy(currentStep = next)
            )
        }
    }

    fun goToPreviousStep() {
        _uiState.update { state ->
            val prev = AssessmentStep.entries.getOrNull(state.currentStep.index - 1)
                ?: state.currentStep
            state.copy(
                currentStep = prev,
                assessment = state.assessment.copy(currentStep = prev)
            )
        }
    }

    fun updateSample(
        signs: String? = null, allergies: String? = null, medications: String? = null,
        pastHistory: String? = null, lastOralIntake: String? = null, events: String? = null
    ) {
        _uiState.update { state ->
            state.copy(
                assessment = state.assessment.copy(
                    signs = signs ?: state.assessment.signs,
                    allergies = allergies ?: state.assessment.allergies,
                    medications = medications ?: state.assessment.medications,
                    pastHistory = pastHistory ?: state.assessment.pastHistory,
                    lastOralIntake = lastOralIntake ?: state.assessment.lastOralIntake,
                    events = events ?: state.assessment.events
                )
            )
        }
    }

    fun updateOpqrst(
        onset: String? = null, provocation: String? = null, quality: String? = null,
        region: String? = null, severity: Int? = null, timing: String? = null
    ) {
        _uiState.update { state ->
            state.copy(
                assessment = state.assessment.copy(
                    onset = onset ?: state.assessment.onset,
                    provocation = provocation ?: state.assessment.provocation,
                    quality = quality ?: state.assessment.quality,
                    region = region ?: state.assessment.region,
                    severity = severity ?: state.assessment.severity,
                    timing = timing ?: state.assessment.timing
                )
            )
        }
    }

    fun setCaseType(type: CaseType) {
        _uiState.update { it.copy(caseType = type) }
    }

    fun resetAssessment() {
        _uiState.value = AssessmentUiState()
    }
}
