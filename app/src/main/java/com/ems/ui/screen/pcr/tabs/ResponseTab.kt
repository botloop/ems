package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ems.ui.components.LabeledTextField
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import com.ems.ui.theme.ClinicalBlue
import com.ems.ui.theme.SubtleText
import java.time.Instant
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
            existingInstant = state.pcr.dispatchTime,
            onSetNow = viewModel::setDispatchNow,
            onSetTime = viewModel::setDispatchTime
        )
        TimestampRow(
            label = "On Scene",
            time = state.pcr.onSceneTime?.let { formatter.format(it) },
            existingInstant = state.pcr.onSceneTime,
            onSetNow = viewModel::setOnSceneNow,
            onSetTime = viewModel::setOnSceneTime
        )
        TimestampRow(
            label = "Transport",
            time = state.pcr.transportTime?.let { formatter.format(it) },
            existingInstant = state.pcr.transportTime,
            onSetNow = viewModel::setTransportNow,
            onSetTime = viewModel::setTransportTime
        )

        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimestampRow(
    label: String,
    time: String?,
    existingInstant: Instant?,
    onSetNow: () -> Unit,
    onSetTime: (Int, Int) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    // Seed picker with existing time or current time
    val zone = ZoneId.systemDefault()
    val seedHour = existingInstant?.atZone(zone)?.hour
        ?: java.time.LocalTime.now().hour
    val seedMinute = existingInstant?.atZone(zone)?.minute
        ?: java.time.LocalTime.now().minute

    if (showPicker) {
        EmsTimePickerDialog(
            initialHour = seedHour,
            initialMinute = seedMinute,
            onDismiss = { showPicker = false },
            onConfirm = { h, m ->
                onSetTime(h, m)
                showPicker = false
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
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

        // Set Now
        OutlinedButton(
            onClick = onSetNow,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Filled.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(3.dp))
            Text("Now", style = MaterialTheme.typography.labelSmall)
        }

        Spacer(Modifier.width(8.dp))

        // Pick time
        FilledTonalIconButton(onClick = { showPicker = true }) {
            Icon(Icons.Filled.AccessTime, contentDescription = "Pick time", tint = ClinicalBlue)
        }
    }
    HorizontalDivider(thickness = 0.5.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmsTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Select Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectorColor = ClinicalBlue,
                        timeSelectorSelectedContainerColor = ClinicalBlue.copy(alpha = 0.15f),
                        timeSelectorSelectedContentColor = ClinicalBlue
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(state.hour, state.minute) },
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue)
                    ) { Text("Set Time") }
                }
            }
        }
    }
}
