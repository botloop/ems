package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import com.ems.ui.theme.ClinicalBlue
import com.ems.ui.theme.SubtleText

// ── Intervention chip catalogue ───────────────────────────────────────────────

private val interventionGroups = mapOf(
    "Airway" to listOf("O₂ via NRB mask", "O₂ via NC", "BVM ventilation", "OPA inserted", "NPA inserted", "Intubation", "Supraglottic airway", "Needle decompression"),
    "Circulation" to listOf("IV access established", "IO access established", "Normal saline bolus", "Hemorrhage control", "Tourniquet applied", "Wound packing", "Pressure dressing"),
    "Monitoring" to listOf("12-Lead ECG", "SpO₂ monitoring", "ETCO₂/Capnography", "Blood glucose check", "Cardiac monitoring"),
    "Immobilization" to listOf("C-spine precautions", "Cervical collar", "Long backboard", "Splinting", "Pelvic binder", "Traction splint"),
    "Other" to listOf("CPR in progress", "AED applied", "Defibrillation", "Burn care", "Eye irrigation", "Glucose administration", "Activated charcoal")
)

@Composable
fun TreatmentTab(state: PcrFormState, viewModel: PcrViewModel) {
    var showMedDialog by remember { mutableStateOf(false) }
    // Initialise from saved PCR state once; sync to VM on every mutation
    val selectedInterventions = remember(state.pcr.id) {
        mutableStateListOf<String>().also { list ->
            list.addAll(state.pcr.treatmentNotes.split("|").filter { it.isNotBlank() })
        }
    }

    fun toggleIntervention(item: String) {
        if (selectedInterventions.contains(item)) selectedInterventions.remove(item)
        else selectedInterventions.add(item)
        viewModel.updateTreatmentNotes(selectedInterventions.joinToString("|"))
    }

    if (showMedDialog) {
        AddMedicationDialog(
            existing = state.pcr.medicationsGiven,
            onSave = { entry ->
                val updated = if (state.pcr.medicationsGiven.isBlank()) entry
                             else "${state.pcr.medicationsGiven}||$entry"
                viewModel.updateMedicationsGiven(updated)
                showMedDialog = false
            },
            onDismiss = { showMedDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Interventions ──────────────────────────────────────────────────
        SectionHeader("Interventions Performed")

        interventionGroups.forEach { (groupName, items) ->
            Text(
                groupName,
                style = MaterialTheme.typography.labelSmall,
                color = SubtleText,
                modifier = Modifier.padding(top = 4.dp)
            )
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEach { item ->
                    val selected = selectedInterventions.contains(item)
                    InterventionChip(
                        label = item,
                        selected = selected,
                        onClick = { toggleIntervention(item) }
                    )
                }
            }
        }

        if (selectedInterventions.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0984E3).copy(alpha = 0.06f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Selected (${selectedInterventions.size})", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0984E3), fontWeight = FontWeight.SemiBold)
                    selectedInterventions.forEach { item ->
                        Text("• $item", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2D3436))
                    }
                }
            }
        }

        HorizontalDivider()

        // ── Medications ────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Medications Administered")
            Button(
                onClick = { showMedDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984E3)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Med", style = MaterialTheme.typography.labelMedium)
            }
        }

        val medEntries = state.pcr.medicationsGiven
            .split("||")
            .filter { it.isNotBlank() }

        if (medEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F7F7))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No medications recorded", style = MaterialTheme.typography.bodySmall, color = SubtleText)
            }
        } else {
            medEntries.forEachIndexed { idx, entry ->
                val parts = entry.split("|")
                MedicationCard(
                    drug = parts.getOrNull(0) ?: "",
                    dose = parts.getOrNull(1) ?: "",
                    route = parts.getOrNull(2) ?: "",
                    time = parts.getOrNull(3) ?: "",
                    onDelete = {
                        val updated = medEntries.toMutableList().also { it.removeAt(idx) }.joinToString("||")
                        viewModel.updateMedicationsGiven(updated)
                    }
                )
            }
        }

        HorizontalDivider()

        // ── Patient Response ───────────────────────────────────────────────
        SectionHeader("Patient Response to Treatment")
        OutlinedTextField(
            value = state.pcr.narrativeNotes.lines().firstOrNull { it.startsWith("Response:") }
                ?.removePrefix("Response:") ?.trim() ?: "",
            onValueChange = {},
            label = { Text("Response / Notes") },
            placeholder = { Text("Patient tolerated O₂ well, HR improved to…") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
            maxLines = 5,
            enabled = false,
            shape = RoundedCornerShape(10.dp)
        )
        Text(
            "Document patient response in Narrative (Clinical tab)",
            style = MaterialTheme.typography.labelSmall,
            color = SubtleText
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun InterventionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Color(0xFF0984E3) else Color(0xFFF0F0F0)
    val textColor = if (selected) Color.White else Color(0xFF2D3436)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(label, fontSize = 12.sp, color = textColor, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun MedicationCard(drug: String, dose: String, route: String, time: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(drug, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF2D3436))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (dose.isNotBlank()) MedPill("Dose: $dose")
                    if (route.isNotBlank()) MedPill(route)
                    if (time.isNotBlank()) MedPill(time)
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Remove", tint = SubtleText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun MedPill(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(label, fontSize = 11.sp, color = SubtleText)
    }
}

@Composable
private fun AddMedicationDialog(existing: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var drug by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    val routes = listOf("IV", "IO", "IM", "SQ", "SL", "PO", "IN", "ETT", "Topical")
    var routeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(drug, { drug = it }, label = { Text("Drug Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(dose, { dose = it }, label = { Text("Dose") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    // Route dropdown
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(expanded = routeExpanded, onExpandedChange = { routeExpanded = !routeExpanded }, modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = route.ifBlank { "Route" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Route") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = routeExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        ExposedDropdownMenu(expanded = routeExpanded, onDismissRequest = { routeExpanded = false }) {
                            routes.forEach { r ->
                                DropdownMenuItem(text = { Text(r) }, onClick = { route = r; routeExpanded = false })
                            }
                        }
                    }
                }
                OutlinedTextField(time, { time = it }, label = { Text("Time (HH:MM)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(
                onClick = { if (drug.isNotBlank()) onSave("$drug|$dose|$route|$time") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984E3)),
                shape = RoundedCornerShape(10.dp),
                enabled = drug.isNotBlank()
            ) { Text("Add", fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
