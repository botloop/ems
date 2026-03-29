package com.ems.ui.screen.pcr

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.ui.components.EmsTopBar
import com.ems.ui.screen.pcr.tabs.*
import com.ems.ui.theme.ClinicalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PcrDetailScreen(
    onBack: () -> Unit,
    viewModel: PcrViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            EmsTopBar(
                title = if (state.pcr.patientLastName.isNotBlank())
                    "${state.pcr.patientLastName}, ${state.pcr.patientFirstName}"
                else "New PCR",
                onBack = onBack,
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp).padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.savePcr() }) {
                            Icon(Icons.Filled.Save, "Save PCR", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tab row
            val tabs = listOf("Response", "Patient", "Clinical", "Vitals", "Disposition")
            ScrollableTabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ClinicalBlue,
                edgePadding = 8.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Saved banner
            if (state.isSaved) {
                Surface(color = ClinicalBlue.copy(alpha = 0.1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ClinicalBlue)
                        Text("PCR saved successfully", color = ClinicalBlue, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Error banner
            state.errorMessage?.let { err ->
                Surface(color = com.ems.ui.theme.EmergencyRedContainer) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = com.ems.ui.theme.EmergencyRed)
                        Spacer(Modifier.width(8.dp))
                        Text(err, color = com.ems.ui.theme.EmergencyRed)
                    }
                }
            }

            // Tab content
            when (state.selectedTab) {
                0 -> ResponseTab(state = state, viewModel = viewModel)
                1 -> PatientTab(state = state, viewModel = viewModel)
                2 -> ClinicalTab(state = state, viewModel = viewModel)
                3 -> VitalsTab(state = state, viewModel = viewModel)
                4 -> DispositionTab(state = state, viewModel = viewModel)
            }
        }
    }
}
