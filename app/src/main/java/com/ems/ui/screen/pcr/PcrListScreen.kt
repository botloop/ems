package com.ems.ui.screen.pcr

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.domain.model.Pcr
import com.ems.domain.model.PcrStatus
import com.ems.ui.components.EmsTopBar
import com.ems.ui.theme.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PcrListScreen(
    onNavigateToPcr: (String) -> Unit,
    onCreateNewPcr: () -> Unit,
    viewModel: PcrListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { EmsTopBar(title = "Patient Care Reports") },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateNewPcr,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("New PCR") },
                containerColor = ClinicalBlue,
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearch,
                placeholder = { Text("Search by patient, incident, complaint…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearch("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ClinicalBlue)
                }
            } else if (state.pcrs.isEmpty()) {
                EmptyPcrState(onCreateNewPcr = onCreateNewPcr)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.pcrs, key = { it.id }) { pcr ->
                        PcrListItem(
                            pcr = pcr,
                            onClick = { onNavigateToPcr(pcr.id) },
                            onDelete = { viewModel.deletePcr(pcr.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PcrListItem(pcr: Pcr, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete PCR?") },
            text = { Text("This will permanently delete the PCR for ${pcr.patientFirstName} ${pcr.patientLastName}.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = EmergencyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    val formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm").withZone(ZoneId.systemDefault())
    val statusColor = when (pcr.status) {
        PcrStatus.DRAFT -> WarningAmber
        PcrStatus.COMPLETE -> ClinicalBlue
        PcrStatus.SUBMITTED -> SuccessGreen
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    modifier = Modifier.fillMaxHeight().width(4.dp),
                    color = statusColor,
                    thickness = 4.dp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = buildString {
                            if (pcr.patientFirstName.isNotBlank() || pcr.patientLastName.isNotBlank()) {
                                append("${pcr.patientLastName}, ${pcr.patientFirstName}")
                            } else {
                                append("Unknown Patient")
                            }
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = pcr.status.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                if (pcr.chiefComplaint.isNotBlank()) {
                    Text(
                        text = pcr.chiefComplaint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${formatter.format(pcr.createdAt)}${if (pcr.incidentNumber.isNotBlank()) " • Inc# ${pcr.incidentNumber}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubtleText
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = SubtleText)
            }
        }
    }
}

@Composable
private fun EmptyPcrState(onCreateNewPcr: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Description, contentDescription = null, modifier = Modifier.size(72.dp), tint = DividerGray)
        Spacer(Modifier.height(16.dp))
        Text("No PCRs Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Tap the + button to create your first Patient Care Report",
            style = MaterialTheme.typography.bodyMedium,
            color = SubtleText,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onCreateNewPcr,
            colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("New PCR")
        }
    }
}
