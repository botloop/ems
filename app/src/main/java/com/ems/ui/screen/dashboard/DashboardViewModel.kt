package com.ems.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import com.ems.domain.repository.PcrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    pcrRepository: PcrRepository
) : ViewModel() {
    val pcrCount = pcrRepository.getPcrCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
