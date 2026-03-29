package com.ems.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.ui.components.DashboardModuleCard
import com.ems.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateToAssessment: () -> Unit,
    onNavigateToPcrList: () -> Unit,
    onNavigateToGcs: () -> Unit,
    onNavigateToVitals: () -> Unit,
    onNavigateToMnemonics: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val pcrCount by viewModel.pcrCount.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp
        val columns = if (isTablet) 3 else 2

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ClinicalBlue, ClinicalBlueDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EMS PCR",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                            Text(
                                text = "Patient Care Reporting System",
                                style = MaterialTheme.typography.bodySmall,
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            Icons.Filled.LocalHospital,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Stats row
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatChip(label = "PCRs Saved", value = pcrCount.toString())
                        StatChip(label = "Status", value = "Offline Ready")
                    }
                }
            }

            // Bento Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Feature: New Assessment — full width
                item(span = { GridItemSpan(columns) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalBlue),
                        shape = MaterialTheme.shapes.large,
                        onClick = onNavigateToAssessment
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Start Assessment",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                                Text(
                                    "Scene Size-Up → Transport",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                item {
                    DashboardModuleCard(
                        title = "PCR Reports",
                        subtitle = "$pcrCount saved",
                        icon = Icons.Filled.Description,
                        accentColor = ClinicalBlue,
                        onClick = onNavigateToPcrList,
                        badge = if (pcrCount > 0) "$pcrCount" else null
                    )
                }

                item {
                    DashboardModuleCard(
                        title = "GCS Score",
                        subtitle = "Glasgow Coma Scale",
                        icon = Icons.Filled.Psychology,
                        accentColor = WarningAmber,
                        onClick = onNavigateToGcs
                    )
                }

                item {
                    DashboardModuleCard(
                        title = "Vital Signs",
                        subtitle = "Shock index & trends",
                        icon = Icons.Filled.MonitorHeart,
                        accentColor = EmergencyRed,
                        onClick = onNavigateToVitals
                    )
                }

                item {
                    DashboardModuleCard(
                        title = "Mnemonics",
                        subtitle = "SAMPLE, OPQRST +",
                        icon = Icons.Filled.MenuBook,
                        accentColor = SuccessGreen,
                        onClick = onNavigateToMnemonics,
                        badge = "8"
                    )
                }

                // Reference card — protocols
                item(span = { GridItemSpan(columns) }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = SubtleText)
                            Column {
                                Text(
                                    "Quick Reference",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "PENMAN → Primary → Secondary → Transport",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SubtleText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
