package com.ems.ui.screen.pcr

import android.print.PrintAttributes
import android.print.PrintManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.ui.components.EmsTopBar
import com.ems.ui.screen.pcr.tabs.*
import com.ems.ui.theme.ClinicalBlue
import com.ems.util.PcrPrintAdapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PcrDetailScreen(
    onBack: () -> Unit,
    viewModel: PcrViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Floating GCS overlay
    if (state.showFloatingGcs) {
        FloatingGcsOverlay(onDismiss = { viewModel.toggleFloatingGcs() })
    }

    Scaffold(
        topBar = {
            EmsTopBar(
                title = if (state.pcr.patientLastName.isNotBlank())
                    "${state.pcr.patientLastName}, ${state.pcr.patientFirstName}"
                else "New PCR",
                onBack = onBack,
                actions = {
                    // PDF/Print button
                    IconButton(onClick = {
                        val printManager = context.getSystemService(android.content.Context.PRINT_SERVICE) as PrintManager
                        printManager.print(
                            "PCR_${state.pcr.incidentNumber.ifBlank { state.pcr.id.take(8) }}",
                            PcrPrintAdapter(context, state.pcr, state.vitalsList),
                            PrintAttributes.Builder().build()
                        )
                    }) {
                        Icon(Icons.Filled.Print, "Print / Save PDF", tint = Color.White)
                    }
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
        },
        floatingActionButton = {
            // Floating GCS calculator button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF2D3436))
                    .clickable { viewModel.toggleFloatingGcs() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Psychology, contentDescription = "GCS Calculator", tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("GCS", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tab row — 7 tabs
            val tabs = listOf("Response", "Patient", "Clinical", "Vitals", "Treatment", "MIST", "Disposition")
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
                4 -> TreatmentTab(state = state, viewModel = viewModel)
                5 -> MistTab(state = state, viewModel = viewModel)
                6 -> DispositionTab(state = state, viewModel = viewModel)
            }
        }
    }
}

// ── Floating GCS Overlay ──────────────────────────────────────────────────────

@Composable
private fun FloatingGcsOverlay(onDismiss: () -> Unit) {
    var eyes by remember { mutableStateOf(0) }
    var verbal by remember { mutableStateOf(0) }
    var motor by remember { mutableStateOf(0) }
    val total = eyes + verbal + motor
    val isCritical = total in 3..8 && total > 0

    val scoreColor = when {
        total == 0 -> Color(0xFF9CA3AF)
        isCritical -> Color(0xFFEF4444)
        total <= 12 -> Color(0xFFF59E0B)
        else -> Color(0xFF22C55E)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("GCS Quick Calc", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D3436))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF9CA3AF))
                    }
                }

                // Score display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(scoreColor.copy(alpha = 0.08f))
                        .border(1.dp, scoreColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (total > 0) total.toString() else "—",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                        Text(
                            when {
                                total == 0 -> "Select scores below"
                                isCritical -> "SEVERE — Airway risk"
                                total <= 12 -> "MODERATE"
                                else -> "MILD / NORMAL"
                            },
                            fontSize = 12.sp,
                            color = scoreColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (total > 0) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "E$eyes + V$verbal + M$motor",
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }

                // Component selectors
                GcsCompactSelector("Eye (E)", listOf(1 to "No response", 2 to "To pain", 3 to "To voice", 4 to "Spontaneous"), eyes) { eyes = it }
                GcsCompactSelector("Verbal (V)", listOf(1 to "None", 2 to "Sounds", 3 to "Words", 4 to "Confused", 5 to "Oriented"), verbal) { verbal = it }
                GcsCompactSelector("Motor (M)", listOf(1 to "None", 2 to "Extension", 3 to "Flexion", 4 to "Withdrawal", 5 to "Localizes", 6 to "Obeys"), motor) { motor = it }

                // Reset
                TextButton(
                    onClick = { eyes = 0; verbal = 0; motor = 0 },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Reset", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun GcsCompactSelector(
    label: String,
    options: List<Pair<Int, String>>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B7280))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { (value, desc) ->
                val isSelected = selected == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF0984E3) else Color(0xFFF3F4F6))
                        .clickable { onSelect(value) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            value.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isSelected) Color.White else Color(0xFF2D3436),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            desc.take(6) + if (desc.length > 6) "." else "",
                            fontSize = 8.sp,
                            color = if (isSelected) Color.White.copy(0.8f) else Color(0xFF9CA3AF),
                            textAlign = TextAlign.Center,
                            lineHeight = 9.sp
                        )
                    }
                }
            }
        }
    }
}
