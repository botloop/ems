package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ems.ui.components.LabeledTextField
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import com.ems.ui.theme.ClinicalBlue
import com.ems.ui.theme.EmergencyRed
import com.ems.ui.theme.SubtleText
import com.ems.ui.theme.WarningAmber

@Composable
fun DispositionTab(state: PcrFormState, viewModel: PcrViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Transport Destination")
        LabeledTextField(
            "Receiving Facility",
            state.pcr.transportDestination,
            viewModel::updateTransportDestination,
            placeholder = "Hospital name and unit"
        )

        SectionHeader("Patient Condition on Arrival")
        val conditions = listOf("Improved", "Unchanged", "Deteriorated", "Critical")
        conditions.forEach { condition ->
            val color = when (condition) {
                "Improved" -> ClinicalBlue
                "Unchanged" -> SubtleText
                "Deteriorated" -> WarningAmber
                "Critical" -> EmergencyRed
                else -> SubtleText
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state.pcr.patientConditionOnArrival == condition,
                    onClick = { viewModel.updateConditionOnArrival(condition) },
                    colors = RadioButtonDefaults.colors(selectedColor = color)
                )
                Text(
                    condition,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (state.pcr.patientConditionOnArrival == condition) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (state.pcr.patientConditionOnArrival == condition) color else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        SectionHeader("Refusal of Care")
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (state.pcr.refusalSigned)
                    EmergencyRed.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Patient Refused Treatment/Transport",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "AMA form signed and witnessed",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtleText
                    )
                }
                Switch(
                    checked = state.pcr.refusalSigned,
                    onCheckedChange = viewModel::updateRefusalSigned,
                    colors = SwitchDefaults.colors(checkedThumbColor = EmergencyRed, checkedTrackColor = EmergencyRed.copy(alpha = 0.5f))
                )
            }
        }

        SectionHeader("Transfer of Care")
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ClinicalBlue)
                    Text("Transfer Checklist", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                listOf(
                    "Verbal report given to receiving nurse/physician",
                    "Patient name band verified",
                    "Allergies communicated",
                    "Medication list transferred",
                    "IV/IO access reported",
                    "PCR signed and submitted"
                ).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckBoxOutlineBlank, contentDescription = null, tint = ClinicalBlue, modifier = Modifier.size(18.dp))
                        Text(item, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.savePcr() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue),
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save & Submit PCR", fontWeight = FontWeight.Bold)
            }
        }
    }
}
