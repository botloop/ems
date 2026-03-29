package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ems.ui.components.BodyMap
import com.ems.ui.components.BodyZone
import com.ems.ui.components.LabeledTextField
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import com.ems.ui.theme.ClinicalBlue
import com.ems.ui.theme.SubtleText

@Composable
fun ClinicalTab(state: PcrFormState, viewModel: PcrViewModel) {
    var selectedBodyZones by remember { mutableStateOf(emptySet<BodyZone>()) }
    var showZoneDialog by remember { mutableStateOf<BodyZone?>(null) }
    var zoneText by remember { mutableStateOf("") }

    // Zone findings dialog
    showZoneDialog?.let { zone ->
        AlertDialog(
            onDismissRequest = { showZoneDialog = null },
            title = { Text(zone.label) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Document DCAP-BTLS findings for this zone:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Quick-pick chips grouped by type
                    DcapChipGroup(
                        selectedText = zoneText,
                        onChipToggle = { chip ->
                            zoneText = if (zoneText.isBlank()) {
                                chip
                            } else if (zoneText.contains(chip)) {
                                // remove chip
                                zoneText.replace(", $chip", "").replace("$chip, ", "").replace(chip, "").trim().trimEnd(',').trim()
                            } else {
                                "${zoneText.trimEnd()}, $chip"
                            }
                        }
                    )

                    OutlinedTextField(
                        value = zoneText,
                        onValueChange = { zoneText = it },
                        label = { Text("Findings") },
                        placeholder = { Text("e.g., Contusion 3cm, tenderness on palpation") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (zoneText.isNotEmpty()) {
                                IconButton(onClick = { zoneText = "" }, modifier = Modifier.size(18.dp)) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedBodyZones = selectedBodyZones + zone
                    showZoneDialog = null
                    zoneText = ""
                }) { Text("Add Finding", fontWeight = FontWeight.Bold, color = ClinicalBlue) }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedBodyZones = selectedBodyZones - zone
                    showZoneDialog = null
                    zoneText = ""
                }) { Text("Clear Zone") }
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
        SectionHeader("Chief Complaint & Mechanism")
        LabeledTextField("Chief Complaint", state.pcr.chiefComplaint, viewModel::updateChiefComplaint,
            placeholder = "Patient's primary complaint in their own words")
        LabeledTextField("Mechanism of Injury / Nature of Illness", state.pcr.mechanismOfInjury, viewModel::updateMechanism)

        SectionHeader("Level of Consciousness")
        val locOptions = listOf("Alert", "Verbal", "Pain", "Unresponsive")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            locOptions.forEach { loc ->
                FilterChip(
                    selected = state.pcr.levelOfConsciousness == loc,
                    onClick = { viewModel.updateLoc(loc) },
                    label = { Text(loc, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        SectionHeader("Interactive Body Map")
        BodyMap(
            selectedZones = selectedBodyZones,
            onZoneTapped = { zone ->
                zoneText = ""
                showZoneDialog = zone
            },
            modifier = Modifier.fillMaxWidth()
        )

        SectionHeader("Pertinent Negatives")
        LabeledTextField(
            label = "Pertinent Negatives",
            value = state.pcr.pertinentNegatives,
            onValueChange = viewModel::updatePertinentNegatives,
            placeholder = "Denies chest pain, SOB, N/V…",
            singleLine = false, maxLines = 3
        )

        SectionHeader("Narrative Notes")
        NarrativeSection(
            narrative = state.pcr.narrativeNotes,
            onUpdate = viewModel::updateNarrative,
            onAppend = viewModel::appendToNarrative
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun NarrativeSection(
    narrative: String,
    onUpdate: (String) -> Unit,
    onAppend: (String) -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = narrative,
            onValueChange = onUpdate,
            label = { Text("Narrative") },
            placeholder = { Text("Free-text PCR narrative…", color = SubtleText) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 12,
            minLines = 5
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { clipboard.setText(AnnotatedString(narrative)) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Copy", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

// ── DCAP-BTLS Quick-Pick Chips ──────────────────────────────────────────────

private data class ChipGroup(val label: String, val chips: List<String>)

private val dcapGroups = listOf(
    ChipGroup("DCAP-BTLS", listOf("Deformity", "Contusion", "Abrasion", "Puncture", "Burn", "Tenderness", "Laceration", "Swelling")),
    ChipGroup("Detail", listOf("Crepitus", "Ecchymosis", "Hematoma", "Guarding", "Pain on palpation", "Open wound", "Impaled object")),
    ChipGroup("Severity", listOf("Mild", "Moderate", "Severe"))
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DcapChipGroup(selectedText: String, onChipToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        dcapGroups.forEach { group ->
            Text(
                group.label,
                style = MaterialTheme.typography.labelSmall,
                color = SubtleText,
                modifier = Modifier.padding(top = 2.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                group.chips.forEach { chip ->
                    val selected = selectedText.contains(chip, ignoreCase = true)
                    FilterChip(
                        selected = selected,
                        onClick = { onChipToggle(chip) },
                        label = { Text(chip, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ClinicalBlue.copy(alpha = 0.15f),
                            selectedLabelColor = ClinicalBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            selectedBorderColor = ClinicalBlue,
                            selectedBorderWidth = 1.5.dp
                        )
                    )
                }
            }
        }
    }
}
