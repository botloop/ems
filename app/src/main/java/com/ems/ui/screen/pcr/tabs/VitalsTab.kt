package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ems.domain.model.VitalSigns
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import com.ems.ui.theme.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun VitalsTab(state: PcrFormState, viewModel: PcrViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddVitalsDialog(
            onDismiss = { showAddDialog = false },
            onSave = { vitals ->
                viewModel.addVitalSigns(vitals)
                showAddDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Serial Vital Signs")
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Set", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (state.vitalsList.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "No vital signs recorded\nTap 'Add Set' to record vitals",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtleText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            state.vitalsList.forEachIndexed { index, vitals ->
                VitalSignsRow(
                    vitals = vitals,
                    setNumber = index + 1,
                    onDelete = { viewModel.deleteVitalSigns(vitals.id) }
                )
            }
        }
    }
}

@Composable
private fun VitalSignsRow(vitals: VitalSigns, setNumber: Int, onDelete: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    val si = vitals.shockIndex
    val siColor = when (vitals.shockIndexSeverity) {
        com.ems.domain.model.VitalSeverity.CRITICAL -> EmergencyRed
        com.ems.domain.model.VitalSeverity.CAUTION -> WarningAmber
        com.ems.domain.model.VitalSeverity.NORMAL -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Set #$setNumber — ${formatter.format(vitals.timestamp)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ClinicalBlue
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    si?.let {
                        Text(
                            "SI: ${"%.2f".format(it)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = siColor
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = SubtleText, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalCell("BP", vitals.bpDisplay)
                VitalCell("HR", vitals.heartRate?.toString() ?: "--")
                VitalCell("RR", vitals.respiratoryRate?.toString() ?: "--")
                VitalCell("SpO₂", vitals.spo2?.let { "$it%" } ?: "--")
                VitalCell("GCS", vitals.gcsTotal?.toString() ?: "--")
            }
        }
    }
}

@Composable
private fun VitalCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = SubtleText)
    }
}

@Composable
private fun AddVitalsDialog(onDismiss: () -> Unit, onSave: (VitalSigns) -> Unit) {
    var sbp by remember { mutableStateOf("") }
    var dbp by remember { mutableStateOf("") }
    var hr by remember { mutableStateOf("") }
    var rr by remember { mutableStateOf("") }
    var spo2 by remember { mutableStateOf("") }
    var gcs by remember { mutableStateOf("") }
    var pain by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Vital Signs") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VitalDialogField("SBP", sbp, { sbp = it }, modifier = Modifier.weight(1f))
                    VitalDialogField("DBP", dbp, { dbp = it }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VitalDialogField("HR", hr, { hr = it }, modifier = Modifier.weight(1f))
                    VitalDialogField("RR", rr, { rr = it }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VitalDialogField("SpO₂%", spo2, { spo2 = it }, modifier = Modifier.weight(1f))
                    VitalDialogField("GCS", gcs, { gcs = it }, modifier = Modifier.weight(1f))
                }
                VitalDialogField("Pain (0–10)", pain, { pain = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(VitalSigns(
                    systolicBp = sbp.toIntOrNull(),
                    diastolicBp = dbp.toIntOrNull(),
                    heartRate = hr.toIntOrNull(),
                    respiratoryRate = rr.toIntOrNull(),
                    spo2 = spo2.toIntOrNull(),
                    gcsTotal = gcs.toIntOrNull(),
                    painScale = pain.toIntOrNull()
                ))
            }) { Text("Save", fontWeight = FontWeight.Bold, color = ClinicalBlue) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun VitalDialogField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}
