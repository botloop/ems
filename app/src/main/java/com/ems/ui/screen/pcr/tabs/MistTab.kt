package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ems.domain.model.VitalSigns
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// MIST = Mechanism · Injuries · Signs · Treatment
// Auto-populated from the other tabs; editable override notes field.

@Composable
fun MistTab(state: PcrFormState, viewModel: PcrViewModel) {
    val pcr = state.pcr
    val latestVitals = state.vitalsList.lastOrNull()

    // Auto-build each section from existing PCR data
    val mechanismText = pcr.mechanismOfInjury.ifBlank { "Not recorded" }

    val injuriesText = buildString {
        if (pcr.chiefComplaint.isNotBlank()) append(pcr.chiefComplaint)
        if (pcr.pertinentNegatives.isNotBlank()) append("\nPertinent negatives: ${pcr.pertinentNegatives}")
        if (isBlank()) append("Not documented")
    }

    val signsText = buildString {
        latestVitals?.let { v ->
            val fmt = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
            append("@${fmt.format(v.timestamp)}  ")
            v.systolicBp?.let { append("BP ${v.bpDisplay}  ") }
            v.heartRate?.let { append("HR $it  ") }
            v.respiratoryRate?.let { append("RR $it  ") }
            v.spo2?.let { append("SpO₂ ${it}%  ") }
            v.gcsTotal?.let { append("GCS $it  ") }
            v.painScale?.let { append("Pain ${it}/10") }
        } ?: append("No vitals recorded")
        if (pcr.levelOfConsciousness.isNotBlank()) append("\nLOC: ${pcr.levelOfConsciousness}")
    }

    val treatmentText = buildString {
        val interventions = pcr.treatmentNotes.split("|").filter { it.isNotBlank() }
        if (interventions.isNotEmpty()) {
            append(interventions.joinToString(", "))
        }
        val meds = pcr.medicationsGiven.split("||").filter { it.isNotBlank() }
        if (meds.isNotEmpty()) {
            if (isNotBlank()) append("\n")
            append("Medications: ")
            append(meds.joinToString("; ") { entry ->
                val p = entry.split("|")
                buildString {
                    append(p.getOrNull(0) ?: "")
                    p.getOrNull(1)?.takeIf { it.isNotBlank() }?.let { append(" $it") }
                    p.getOrNull(2)?.takeIf { it.isNotBlank() }?.let { append(" $it") }
                }
            })
        }
        if (isBlank()) append("No treatment documented")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "MIST Handover Report",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
                Text(
                    "Auto-populated from PCR data",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0984E3).copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text("MIST", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0984E3))
            }
        }

        HorizontalDivider()

        // M
        MistBlock(
            letter = "M",
            title = "Mechanism of Injury",
            content = mechanismText,
            icon = Icons.Outlined.DirectionsCar,
            accentColor = Color(0xFF0984E3)
        )
        // I
        MistBlock(
            letter = "I",
            title = "Injuries Sustained",
            content = injuriesText,
            icon = Icons.Outlined.PersonSearch,
            accentColor = Color(0xFFE17055)
        )
        // S
        MistBlock(
            letter = "S",
            title = "Signs & Vital Signs",
            content = signsText,
            icon = Icons.Outlined.MonitorHeart,
            accentColor = Color(0xFFF59E0B)
        )
        // T
        MistBlock(
            letter = "T",
            title = "Treatment Given",
            content = treatmentText,
            icon = Icons.Outlined.MedicalServices,
            accentColor = Color(0xFF22C55E)
        )

        HorizontalDivider()

        // Full name + unit
        if (pcr.patientFirstName.isNotBlank() || pcr.patientLastName.isNotBlank()) {
            PatientSummaryRow(
                name = "${pcr.patientLastName}, ${pcr.patientFirstName}".trim().trimStart(',').trim(),
                age = pcr.patientAge?.toString() ?: "",
                gender = pcr.patientGender,
                unit = pcr.unitNumber
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MistBlock(
    letter: String,
    title: String,
    content: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Letter badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    letter,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF2D3436))
                }
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4A4A),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun PatientSummaryRow(name: String, age: String, gender: String, unit: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2D3436))
                Text(
                    listOf(age.let { if (it.isNotBlank()) "$it yo" else "" }, gender).filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }
            if (unit.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2D3436))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("Unit $unit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}
