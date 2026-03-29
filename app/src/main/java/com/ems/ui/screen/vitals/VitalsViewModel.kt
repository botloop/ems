package com.ems.ui.screen.vitals

import androidx.lifecycle.ViewModel
import com.ems.domain.model.VitalSeverity
import com.ems.domain.model.VitalSigns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class VitalsUiState(
    val vitals: VitalSigns = VitalSigns(),
    val sbpText: String = "",
    val dbpText: String = "",
    val hrText: String = "",
    val rrText: String = "",
    val spo2Text: String = "",
    val tempText: String = "",
    val bpSeverity: VitalSeverity = VitalSeverity.NORMAL,
    val hrSeverity: VitalSeverity = VitalSeverity.NORMAL,
    val rrSeverity: VitalSeverity = VitalSeverity.NORMAL,
    val spo2Severity: VitalSeverity = VitalSeverity.NORMAL,
    val shockIndexSeverity: VitalSeverity = VitalSeverity.NORMAL
)

@HiltViewModel
class VitalsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VitalsUiState())
    val uiState: StateFlow<VitalsUiState> = _uiState.asStateFlow()

    fun updateSbp(value: String) {
        _uiState.update { it.copy(sbpText = value) }
        recalculate()
    }
    fun updateDbp(value: String) {
        _uiState.update { it.copy(dbpText = value) }
        recalculate()
    }
    fun updateHr(value: String) {
        _uiState.update { it.copy(hrText = value) }
        recalculate()
    }
    fun updateRr(value: String) {
        _uiState.update { it.copy(rrText = value) }
        recalculate()
    }
    fun updateSpo2(value: String) {
        _uiState.update { it.copy(spo2Text = value) }
        recalculate()
    }
    fun updateTemp(value: String) {
        _uiState.update { it.copy(tempText = value) }
        recalculate()
    }

    private fun recalculate() {
        val s = _uiState.value
        val sbp = s.sbpText.toIntOrNull()
        val dbp = s.dbpText.toIntOrNull()
        val hr = s.hrText.toIntOrNull()
        val rr = s.rrText.toIntOrNull()
        val spo2 = s.spo2Text.toIntOrNull()
        val temp = s.tempText.toFloatOrNull()

        val vitals = VitalSigns(
            systolicBp = sbp, diastolicBp = dbp, heartRate = hr,
            respiratoryRate = rr, spo2 = spo2, temperature = temp
        )

        _uiState.update { state ->
            state.copy(
                vitals = vitals,
                bpSeverity = classifyBp(sbp, dbp),
                hrSeverity = classifyHr(hr),
                rrSeverity = classifyRr(rr),
                spo2Severity = classifySpo2(spo2),
                shockIndexSeverity = vitals.shockIndexSeverity
            )
        }
    }

    private fun classifyBp(sbp: Int?, dbp: Int?): VitalSeverity = when {
        sbp == null -> VitalSeverity.NORMAL
        sbp < 90 || sbp > 180 -> VitalSeverity.CRITICAL
        sbp < 100 || sbp > 160 -> VitalSeverity.CAUTION
        else -> VitalSeverity.NORMAL
    }

    private fun classifyHr(hr: Int?): VitalSeverity = when {
        hr == null -> VitalSeverity.NORMAL
        hr < 50 || hr > 130 -> VitalSeverity.CRITICAL
        hr < 60 || hr > 100 -> VitalSeverity.CAUTION
        else -> VitalSeverity.NORMAL
    }

    private fun classifyRr(rr: Int?): VitalSeverity = when {
        rr == null -> VitalSeverity.NORMAL
        rr < 8 || rr > 30 -> VitalSeverity.CRITICAL
        rr < 12 || rr > 20 -> VitalSeverity.CAUTION
        else -> VitalSeverity.NORMAL
    }

    private fun classifySpo2(spo2: Int?): VitalSeverity = when {
        spo2 == null -> VitalSeverity.NORMAL
        spo2 < 90 -> VitalSeverity.CRITICAL
        spo2 < 94 -> VitalSeverity.CAUTION
        else -> VitalSeverity.NORMAL
    }

    fun reset() { _uiState.value = VitalsUiState() }
}
