package com.ems.ui.screen.gcs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.domain.model.GcsDescriptions
import com.ems.ui.components.CriticalAlertBanner
import com.ems.ui.components.EmsTopBar
import com.ems.ui.components.SectionHeader
import com.ems.ui.theme.*

@Composable
fun GcsScreen(
    onBack: () -> Unit,
    onNavigateToVitals: () -> Unit,
    viewModel: GcsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    // Haptic on critical
    LaunchedEffect(state.showCriticalWarning) {
        if (state.showCriticalWarning) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    if (state.showCriticalWarning) {
        LowGcsWarningDialog(
            total = state.score.total,
            onDismiss = { viewModel.dismissWarning() }
        )
    }

    Scaffold(
        topBar = {
            EmsTopBar(
                title = "GCS Calculator",
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
            // Score display
            GcsTotalDisplay(score = state.score)

            if (state.score.isCritical) {
                CriticalAlertBanner("CRITICAL: GCS ≤ 8 — Consider immediate airway management")
            }

            // Three column selectors
            SectionHeader("Eye Opening (E)")
            GcsComponentSelector(
                options = GcsDescriptions.eyeOptions,
                selectedValue = state.score.eyeOpening,
                onSelect = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.setEyes(it) },
                accentColor = ClinicalBlue
            )

            SectionHeader("Verbal Response (V)")
            GcsComponentSelector(
                options = GcsDescriptions.verbalOptions,
                selectedValue = state.score.verbalResponse,
                onSelect = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.setVerbal(it) },
                accentColor = WarningAmber
            )

            SectionHeader("Motor Response (M)")
            GcsComponentSelector(
                options = GcsDescriptions.motorOptions,
                selectedValue = state.score.motorResponse,
                onSelect = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.setMotor(it) },
                accentColor = SuccessGreen
            )

            // Formula display
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FormulaComponent("E", state.score.eyeOpening, ClinicalBlue)
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = SubtleText)
                    FormulaComponent("V", state.score.verbalResponse, WarningAmber)
                    Text("+", style = MaterialTheme.typography.headlineMedium, color = SubtleText)
                    FormulaComponent("M", state.score.motorResponse, SuccessGreen)
                    Text("=", style = MaterialTheme.typography.headlineMedium, color = SubtleText)
                    FormulaComponent("GCS", state.score.total, if (state.score.isCritical) EmergencyRed else ClinicalBlue)
                }
            }

            Button(
                onClick = onNavigateToVitals,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ClinicalBlue)
            ) {
                Icon(Icons.Filled.MonitorHeart, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Continue to Vital Signs")
            }
        }
    }
}

@Composable
private fun GcsTotalDisplay(score: com.ems.domain.model.GcsScore) {
    val scoreColor = when {
        score.total == 0 -> SubtleText
        score.isCritical -> EmergencyRed
        score.total <= 12 -> WarningAmber
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (score.total > 0) score.total.toString() else "—",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = "GCS Total  (3–15)",
                style = MaterialTheme.typography.labelMedium,
                color = SubtleText
            )
            if (score.total > 0) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = scoreColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = score.severity,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = scoreColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GcsComponentSelector(
    options: List<Pair<Int, String>>,
    selectedValue: Int,
    onSelect: (Int) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.reversed().forEach { (value, label) ->
            val selected = selectedValue == value
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
                    .border(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) accentColor else DividerGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(value) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (selected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            value.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else SubtleText
                        )
                    }
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) accentColor else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FormulaComponent(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (value > 0) value.toString() else "—",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = SubtleText)
    }
}

@Composable
private fun LowGcsWarningDialog(total: Int, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(48.dp))
                Text(
                    "LOW GCS ALERT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EmergencyRed,
                    textAlign = TextAlign.Center
                )
                Text(
                    "GCS = $total  (Severe Brain Injury)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = EmergencyRed,
                    textAlign = TextAlign.Center
                )
                Text(
                    "• Consider immediate airway management\n• Prepare for rapid transport\n• Notify receiving facility\n• Reassess frequently",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Acknowledged", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
