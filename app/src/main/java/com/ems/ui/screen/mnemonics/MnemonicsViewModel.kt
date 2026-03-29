package com.ems.ui.screen.mnemonics

import androidx.lifecycle.ViewModel
import com.ems.domain.model.Mnemonic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MnemonicsUiState(
    val mnemonics: List<Mnemonic> = MnemonicsDataSource.all,
    val searchQuery: String = "",
    val copiedMnemonicId: String? = null
)

@HiltViewModel
class MnemonicsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MnemonicsUiState())
    val uiState: StateFlow<MnemonicsUiState> = _uiState.asStateFlow()

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query, mnemonics = MnemonicsDataSource.search(query)) }
    }

    fun onCopied(acronym: String) {
        _uiState.update { it.copy(copiedMnemonicId = acronym) }
    }

    fun clearCopied() {
        _uiState.update { it.copy(copiedMnemonicId = null) }
    }
}
