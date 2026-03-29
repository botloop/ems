package com.ems.ui.screen.pcr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ems.domain.model.Pcr
import com.ems.domain.model.PcrStatus
import com.ems.domain.model.VitalSigns
import com.ems.domain.repository.PcrRepository
import com.ems.domain.repository.VitalSignsRepository
import com.ems.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class PcrFormState(
    val pcr: Pcr = Pcr(id = UUID.randomUUID().toString()),
    val vitalsList: List<VitalSigns> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: Int = 0,
    val showFloatingGcs: Boolean = false
)

@HiltViewModel
class PcrViewModel @Inject constructor(
    private val pcrRepository: PcrRepository,
    private val vitalSignsRepository: VitalSignsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pcrId: String = savedStateHandle[Screen.PcrDetail.ARG_PCR_ID] ?: "new"

    private val _uiState = MutableStateFlow(PcrFormState())
    val uiState: StateFlow<PcrFormState> = _uiState.asStateFlow()

    init {
        if (pcrId != "new") loadPcr(pcrId)
        observeVitals()
    }

    private fun loadPcr(id: String) {
        viewModelScope.launch {
            pcrRepository.getPcrById(id)?.let { pcr ->
                _uiState.update { it.copy(pcr = pcr) }
            }
        }
    }

    private fun observeVitals() {
        viewModelScope.launch {
            vitalSignsRepository.getVitalSignsForPcr(_uiState.value.pcr.id).collect { vitals ->
                _uiState.update { it.copy(vitalsList = vitals) }
            }
        }
    }

    fun selectTab(index: Int) = _uiState.update { it.copy(selectedTab = index) }
    fun toggleFloatingGcs() = _uiState.update { it.copy(showFloatingGcs = !it.showFloatingGcs) }

    // Response tab
    fun updateUnitNumber(v: String) = updatePcr { it.copy(unitNumber = v) }
    fun updateIncidentNumber(v: String) = updatePcr { it.copy(incidentNumber = v) }
    fun updateCallType(v: String) = updatePcr { it.copy(callType = v) }
    fun updateIncidentAddress(v: String) = updatePcr { it.copy(incidentAddress = v) }
    fun setDispatchNow() = setTimedField(java.time.Instant.now()) { t -> updatePcr { it.copy(dispatchTime = t) } }
    fun setOnSceneNow() = setTimedField(java.time.Instant.now()) { t -> updatePcr { it.copy(onSceneTime = t) } }
    fun setTransportNow() = setTimedField(java.time.Instant.now()) { t -> updatePcr { it.copy(transportTime = t) } }
    fun setDispatchTime(hour: Int, minute: Int) = setTimedField(instantFromTime(hour, minute)) { t -> updatePcr { it.copy(dispatchTime = t) } }
    fun setOnSceneTime(hour: Int, minute: Int) = setTimedField(instantFromTime(hour, minute)) { t -> updatePcr { it.copy(onSceneTime = t) } }
    fun setTransportTime(hour: Int, minute: Int) = setTimedField(instantFromTime(hour, minute)) { t -> updatePcr { it.copy(transportTime = t) } }

    private fun instantFromTime(hour: Int, minute: Int): Instant =
        java.time.LocalDate.now().atTime(hour, minute)
            .atZone(java.time.ZoneId.systemDefault()).toInstant()

    private fun setTimedField(instant: Instant, block: (Instant) -> Unit) = block(instant)

    // Patient tab
    fun updateFirstName(v: String) = updatePcr { it.copy(patientFirstName = v) }
    fun updateLastName(v: String) = updatePcr { it.copy(patientLastName = v) }
    fun updateDob(v: String) {
        val digits = v.filter { it.isDigit() }.take(8)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                if (i == 2 || i == 4) append('/')
                append(c)
            }
        }
        val age = computeAge(formatted)
        updatePcr { it.copy(patientDob = formatted, patientAge = age ?: if (formatted.isBlank()) null else it.patientAge) }
    }
    fun updateAge(v: String) = updatePcr { it.copy(patientAge = v.toIntOrNull()) }

    private fun computeAge(dob: String): Int? = try {
        val date = java.time.LocalDate.parse(dob, java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        java.time.Period.between(date, java.time.LocalDate.now()).years.takeIf { it in 0..130 }
    } catch (_: Exception) { null }

    fun updateGender(v: String) = updatePcr { it.copy(patientGender = v) }
    fun updatePatientAddress(v: String) = updatePcr { it.copy(patientAddress = v) }
    fun updatePhone(v: String) = updatePcr { it.copy(patientPhone = v) }
    fun updateInsuranceProvider(v: String) = updatePcr { it.copy(insuranceProvider = v) }
    fun updateInsuranceId(v: String) = updatePcr { it.copy(insuranceId = v) }

    // Clinical tab
    fun updateChiefComplaint(v: String) = updatePcr { it.copy(chiefComplaint = v) }
    fun updateMechanism(v: String) = updatePcr { it.copy(mechanismOfInjury = v) }
    fun updateLoc(v: String) = updatePcr { it.copy(levelOfConsciousness = v) }
    fun updatePertinentNegatives(v: String) = updatePcr { it.copy(pertinentNegatives = v) }
    fun appendToNarrative(text: String) = updatePcr {
        val existing = it.narrativeNotes
        it.copy(narrativeNotes = if (existing.isBlank()) text else "$existing\n\n$text")
    }
    fun updateNarrative(v: String) = updatePcr { it.copy(narrativeNotes = v) }

    // Vitals tab
    fun addVitalSigns(vitals: VitalSigns) {
        viewModelScope.launch {
            pcrRepository.savePcr(_uiState.value.pcr)
            val newVitals = vitals.copy(pcrId = _uiState.value.pcr.id, id = UUID.randomUUID().toString())
            vitalSignsRepository.saveVitalSigns(newVitals)
        }
    }
    fun deleteVitalSigns(id: String) {
        viewModelScope.launch { vitalSignsRepository.deleteVitalSigns(id) }
    }

    // Treatment tab
    fun updateTreatmentNotes(v: String) = updatePcr { it.copy(treatmentNotes = v) }
    fun updateMedicationsGiven(v: String) = updatePcr { it.copy(medicationsGiven = v) }

    // Disposition tab
    fun updateTransportDestination(v: String) = updatePcr { it.copy(transportDestination = v) }
    fun updateConditionOnArrival(v: String) = updatePcr { it.copy(patientConditionOnArrival = v) }
    fun updateRefusalSigned(v: Boolean) = updatePcr { it.copy(refusalSigned = v) }

    fun savePcr() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val pcr = _uiState.value.pcr.copy(updatedAt = Instant.now(), status = PcrStatus.COMPLETE)
                pcrRepository.savePcr(pcr)
                _uiState.update { it.copy(isSaving = false, isSaved = true, pcr = pcr) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    private fun updatePcr(transform: (Pcr) -> Pcr) {
        _uiState.update { state -> state.copy(pcr = transform(state.pcr)) }
    }
}
