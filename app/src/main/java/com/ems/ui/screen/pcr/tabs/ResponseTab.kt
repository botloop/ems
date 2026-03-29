package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ems.ui.components.LabeledTextField
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import com.ems.ui.theme.ClinicalBlue
import com.ems.ui.theme.SubtleText
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ResponseTab(state: PcrFormState, viewModel: PcrViewModel) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Unit & Incident")
        LabeledTextField("Unit Number", state.pcr.unitNumber, viewModel::updateUnitNumber)
        LabeledTextField("Incident Number / CAD#", state.pcr.incidentNumber, viewModel::updateIncidentNumber)
        LabeledTextField("Call Type / Nature of Call", state.pcr.callType, viewModel::updateCallType)
        LabeledTextField("Incident Address", state.pcr.incidentAddress, viewModel::updateIncidentAddress)

        SectionHeader("Response Times")

        TimestampRow(
            label = "Dispatch",
            time = state.pcr.dispatchTime?.let { formatter.format(it) },
            onSetNow = viewModel::setDispatchNow
        )
        TimestampRow(
            label = "On Scene",
            time = state.pcr.onSceneTime?.let { formatter.format(it) },
            onSetNow = viewModel::setOnSceneNow
        )
        TimestampRow(
            label = "Transport",
            time = state.pcr.transportTime?.let { formatter.format(it) },
            onSetNow = viewModel::setTransportNow
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TimestampRow(label: String, time: String?, onSetNow: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = SubtleText)
            Text(
                time ?: "Not recorded",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (time != null) FontWeight.SemiBold else FontWeight.Normal,
                color = if (time != null) MaterialTheme.colorScheme.onSurface else SubtleText
            )
        }
        OutlinedButton(
            onClick = onSetNow,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Filled.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Set Now", style = MaterialTheme.typography.labelMedium)
        }
    }
    Divider(thickness = 0.5.dp)
}
