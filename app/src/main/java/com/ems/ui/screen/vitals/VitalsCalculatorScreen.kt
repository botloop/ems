package com.ems.ui.screen.vitals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.domain.model.VitalSeverity
import com.ems.ui.components.CriticalAlertBanner
import com.ems.ui.components.EmsTopBar
import com.ems.ui.components.SectionHeader
import com.ems.ui.components.WarningBanner
import com.ems.ui.theme.*

@Composable
fun VitalsCalculatorScreen(
    onBack: () -> Unit,
    viewModel: VitalsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(state.shockIndexSeverity) {
        if (state.shockIndexSeverity == VitalSeverity.CRITICAL) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        topBar = {
            EmsTopBar(
                title = "Vital Signs Interpreter",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { viewModel.reset() }) {
                        Icon(Icons.Filled.Refresh, "Reset", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shock Index Card
            ShockIndexCard(state = state)

            if (state.shockIndexSeverity == VitalSeverity.CRITICAL) {
                CriticalAlertBanner("CRITICAL: Shock Index > 0.9 — Hemodynamic instability likely")
            } else if (state.shockIndexSeverity == VitalSeverity.CAUTION) {
                WarningBanner("CAUTION: Shock Index 0.6–0.9 — Monitor closely")
            }

            SectionHeader("Blood Pressure")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitalInputField(
                    label = "Systolic (mmHg)",
                    value = state.sbpText,
                    onValueChange = viewModel::updateSbp,
                    severity = state.bpSeverity,
                    modifier = Modifier.weight(1f)
                )
                VitalInputField(
                    label = "Diastolic (mmHg)",
                    value = state.dbpText,
                    onValueChange = viewModel::updateDbp,
                    severity = VitalSeverity.NORMAL,
                    modifier = Modifier.weight(1f)
                )
            }

            SectionHeader("Cardiorespiratory")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitalInputField(
                    label = "Heart Rate (bpm)",
                    value = state.hrText,
                    onValueChange = viewModel::updateHr,
                    severity = state.hrSeverity,
                    modifier = Modifier.weight(1f)
                )
                VitalInputField(
                    label = "Resp Rate (/min)",
                    value = state.rrText,
                    onValueChange = viewModel::updateRr,
                    severity = state.rrSeverity,
                    modifier = Modifier.weight(1f)
                )
            }

            SectionHeader("Oxygenation & Temperature")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitalInputField(
                    label = "SpO₂ (%)",
                    value = state.spo2Text,
                    onValueChange = viewModel::updateSpo2,
                    severity = state.spo2Severity,
                    modifier = Modifier.weight(1f)
                )
                VitalInputField(
                    label = "Temp (°F)",
                    value = state.tempText,
                    onValueChange = viewModel::updateTemp,
                    severity = VitalSeverity.NORMAL,
                    modifier = Modifier.weight(1f)
                )
            }

            // Normal Ranges Reference
            SectionHeader("Normal Ranges (Adult)")
            NormalRangesCard()
        }
    }
}

@Composable
private fun ShockIndexCard(state: VitalsUiState) {
    val si = state.vitals.shockIndex
    val color = when (state.shockIndexSeverity) {
        VitalSeverity.CRITICAL -> EmergencyRed
        VitalSeverity.CAUTION -> WarningAmber
        VitalSeverity.NORMAL -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Shock Index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "HR ÷ SBP",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubtleText
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    when (state.shockIndexSeverity) {
                        VitalSeverity.CRITICAL -> "Critical (>0.9)"
                        VitalSeverity.CAUTION -> "Borderline (0.6–0.9)"
                        VitalSeverity.NORMAL -> "Normal (<0.6)"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = si?.let { "%.2f".format(it) } ?: "—",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun VitalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    severity: VitalSeverity,
    modifier: Modifier = Modifier
) {
    val borderColor = when (severity) {
        VitalSeverity.CRITICAL -> EmergencyRed
        VitalSeverity.CAUTION -> WarningAmber
        VitalSeverity.NORMAL -> DividerGray
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ClinicalBlue,
                unfocusedBorderColor = borderColor
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (severity != VitalSeverity.NORMAL) {
            Text(
                text = when (severity) {
                    VitalSeverity.CRITICAL -> "CRITICAL"
                    VitalSeverity.CAUTION -> "CAUTION"
                    else -> ""
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = borderColor,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
private fun NormalRangesCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "BP Systolic" to "90–140 mmHg",
                "Heart Rate" to "60–100 bpm",
                "Respiratory Rate" to "12–20 /min",
                "SpO₂" to "≥94%",
                "Temperature" to "97.8–99.1°F",
                "Shock Index" to "<0.6 (Normal)"
            ).forEach { (label, range) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = SubtleText)
                    Text(range, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
                if (label != "Shock Index") Divider(color = DividerGray, thickness = 0.5.dp)
            }
        }
    }
}
