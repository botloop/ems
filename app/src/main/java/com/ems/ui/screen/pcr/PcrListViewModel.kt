package com.ems.ui.screen.pcr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ems.domain.model.Pcr
import com.ems.domain.repository.PcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PcrListUiState(
    val pcrs: List<Pcr> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

@HiltViewModel
class PcrListViewModel @Inject constructor(
    private val pcrRepository: PcrRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _pcrs = pcrRepository.getAllPcrs()

    val uiState: StateFlow<PcrListUiState> = combine(_pcrs, _searchQuery) { pcrs, query ->
        val filtered = if (query.isBlank()) pcrs else pcrs.filter { pcr ->
            pcr.incidentNumber.contains(query, ignoreCase = true) ||
            pcr.patientFirstName.contains(query, ignoreCase = true) ||
            pcr.patientLastName.contains(query, ignoreCase = true) ||
            pcr.chiefComplaint.contains(query, ignoreCase = true)
        }
        PcrListUiState(pcrs = filtered, isLoading = false, searchQuery = query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PcrListUiState())

    fun updateSearch(query: String) { _searchQuery.value = query }

    fun deletePcr(id: String) {
        viewModelScope.launch { pcrRepository.deletePcr(id) }
    }
}
