package com.ems.ui.screen.pcr.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ems.ui.components.LabeledTextField
import com.ems.ui.components.SectionHeader
import com.ems.ui.screen.pcr.PcrFormState
import com.ems.ui.screen.pcr.PcrViewModel

@Composable
fun PatientTab(state: PcrFormState, viewModel: PcrViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Patient Demographics")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LabeledTextField("First Name", state.pcr.patientFirstName, viewModel::updateFirstName, modifier = Modifier.weight(1f))
            LabeledTextField("Last Name", state.pcr.patientLastName, viewModel::updateLastName, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LabeledTextField(
                label = "Date of Birth",
                value = state.pcr.patientDob,
                onValueChange = viewModel::updateDob,
                modifier = Modifier.weight(1f),
                placeholder = "MM/DD/YYYY"
            )
            // Age — auto-computed from DOB; falls back to manual entry
            val ageAutoComputed = state.pcr.patientDob.isNotBlank() && state.pcr.patientAge != null
            OutlinedTextField(
                value = state.pcr.patientAge?.toString() ?: "",
                onValueChange = viewModel::updateAge,
                label = { Text("Age") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                readOnly = ageAutoComputed,
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    if (ageAutoComputed) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = "Auto-computed",
                            tint = com.ems.ui.theme.ClinicalBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                supportingText = if (ageAutoComputed) {
                    { Text("From DOB", style = MaterialTheme.typography.labelSmall, color = com.ems.ui.theme.ClinicalBlue) }
                } else null
            )
        }

        // Gender selection
        SectionHeader("Gender")
        val genders = listOf("Male", "Female", "Non-binary", "Unknown")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            genders.forEach { gender ->
                FilterChip(
                    selected = state.pcr.patientGender == gender,
                    onClick = { viewModel.updateGender(gender) },
                    label = { Text(gender, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        SectionHeader("Contact Information")
        LabeledTextField("Patient Address", state.pcr.patientAddress, viewModel::updatePatientAddress)
        LabeledTextField("Phone Number", state.pcr.patientPhone, viewModel::updatePhone)

        SectionHeader("Insurance")
        LabeledTextField("Insurance Provider", state.pcr.insuranceProvider, viewModel::updateInsuranceProvider)
        LabeledTextField("Policy / Member ID", state.pcr.insuranceId, viewModel::updateInsuranceId)

        Spacer(Modifier.height(16.dp))
    }
}
