package com.ems.domain.model

import java.time.Instant

data class VitalSigns(
    val id: String = "",
    val pcrId: String = "",
    val timestamp: Instant = Instant.now(),
    val systolicBp: Int? = null,
    val diastolicBp: Int? = null,
    val heartRate: Int? = null,
    val respiratoryRate: Int? = null,
    val spo2: Int? = null,
    val temperature: Float? = null,
    val gcsTotal: Int? = null,
    val painScale: Int? = null,
    val bloodGlucose: Int? = null,
    val etco2: Int? = null,
    val pupilsEqual: Boolean? = null,
    val pupilsReactive: Boolean? = null,
    val skinColor: String = "",
    val skinTemp: String = "",
    val skinMoisture: String = ""
) {
    val shockIndex: Float?
        get() = if (heartRate != null && systolicBp != null && systolicBp > 0) {
            heartRate.toFloat() / systolicBp.toFloat()
        } else null

    val shockIndexSeverity: VitalSeverity
        get() = when (val si = shockIndex) {
            null -> VitalSeverity.NORMAL
            else -> when {
                si > 0.9f -> VitalSeverity.CRITICAL
                si >= 0.6f -> VitalSeverity.CAUTION
                else -> VitalSeverity.NORMAL
            }
        }

    val bpDisplay: String
        get() = if (systolicBp != null && diastolicBp != null) "$systolicBp/$diastolicBp" else "--/--"
}
