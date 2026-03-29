package com.ems.ui.screen.gcs

import androidx.lifecycle.ViewModel
import com.ems.domain.model.GcsScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class GcsUiState(
    val score: GcsScore = GcsScore(),
    val showCriticalWarning: Boolean = false,
    val savedToNarrative: Boolean = false
)

@HiltViewModel
class GcsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(GcsUiState())
    val uiState: StateFlow<GcsUiState> = _uiState.asStateFlow()

    fun setEyes(value: Int) = updateScore { it.copy(eyeOpening = value) }
    fun setVerbal(value: Int) = updateScore { it.copy(verbalResponse = value) }
    fun setMotor(value: Int) = updateScore { it.copy(motorResponse = value) }

    private fun updateScore(transform: (GcsScore) -> GcsScore) {
        _uiState.update { state ->
            val newScore = transform(state.score)
            state.copy(
                score = newScore,
                showCriticalWarning = newScore.isCritical
            )
        }
    }

    fun reset() { _uiState.value = GcsUiState() }
    fun dismissWarning() { _uiState.update { it.copy(showCriticalWarning = false) } }
}
