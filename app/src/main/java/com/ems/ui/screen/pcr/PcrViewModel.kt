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
    val selectedTab: Int = 0
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

    // Response tab
    fun updateUnitNumber(v: String) = updatePcr { it.copy(unitNumber = v) }
    fun updateIncidentNumber(v: String) = updatePcr { it.copy(incidentNumber = v) }
    fun updateCallType(v: String) = updatePcr { it.copy(callType = v) }
    fun updateIncidentAddress(v: String) = updatePcr { it.copy(incidentAddress = v) }
    fun setDispatchNow() = updatePcr { it.copy(dispatchTime = Instant.now()) }
    fun setOnSceneNow() = updatePcr { it.copy(onSceneTime = Instant.now()) }
    fun setTransportNow() = updatePcr { it.copy(transportTime = Instant.now()) }

    // Patient tab
    fun updateFirstName(v: String) = updatePcr { it.copy(patientFirstName = v) }
    fun updateLastName(v: String) = updatePcr { it.copy(patientLastName = v) }
    fun updateDob(v: String) = updatePcr { it.copy(patientDob = v) }
    fun updateAge(v: String) = updatePcr { it.copy(patientAge = v.toIntOrNull()) }
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
            val newVitals = vitals.copy(pcrId = _uiState.value.pcr.id, id = UUID.randomUUID().toString())
            vitalSignsRepository.saveVitalSigns(newVitals)
        }
    }
    fun deleteVitalSigns(id: String) {
        viewModelScope.launch { vitalSignsRepository.deleteVitalSigns(id) }
    }

    // Disposition tab
    fun updateTransportDestination(v: String) = updatePcr { it.copy(transportDestination = v) }
    fun updateConditionOnArrival(v: String) = updatePcr { it.copy(patientConditionOnArrival = v) }
    fun updateRefusalSigned(v: Boolean) = updatePcr { it.copy(refusalSigned = v) }

    fun savePcr() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val pcr = _uiState.value.pcr.copy(
                    updatedAt = Instant.now(),
                    status = PcrStatus.COMPLETE
                )
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
