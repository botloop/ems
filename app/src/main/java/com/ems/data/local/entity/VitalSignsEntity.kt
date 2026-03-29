package com.ems.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ems.domain.model.VitalSigns
import java.time.Instant

@Entity(
    tableName = "vital_signs",
    foreignKeys = [ForeignKey(
        entity = PcrEntity::class,
        parentColumns = ["id"],
        childColumns = ["pcrId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pcrId")]
)
data class VitalSignsEntity(
    @PrimaryKey val id: String,
    val pcrId: String,
    val timestamp: Long,
    val systolicBp: Int?,
    val diastolicBp: Int?,
    val heartRate: Int?,
    val respiratoryRate: Int?,
    val spo2: Int?,
    val temperature: Float?,
    val gcsTotal: Int?,
    val painScale: Int?,
    val bloodGlucose: Int?,
    val etco2: Int?,
    val pupilsEqual: Boolean?,
    val pupilsReactive: Boolean?,
    val skinColor: String,
    val skinTemp: String,
    val skinMoisture: String
)

fun VitalSignsEntity.toDomain(): VitalSigns = VitalSigns(
    id = id, pcrId = pcrId, timestamp = Instant.ofEpochMilli(timestamp),
    systolicBp = systolicBp, diastolicBp = diastolicBp, heartRate = heartRate,
    respiratoryRate = respiratoryRate, spo2 = spo2, temperature = temperature,
    gcsTotal = gcsTotal, painScale = painScale, bloodGlucose = bloodGlucose,
    etco2 = etco2, pupilsEqual = pupilsEqual, pupilsReactive = pupilsReactive,
    skinColor = skinColor, skinTemp = skinTemp, skinMoisture = skinMoisture
)

fun VitalSigns.toEntity(): VitalSignsEntity = VitalSignsEntity(
    id = id, pcrId = pcrId, timestamp = timestamp.toEpochMilli(),
    systolicBp = systolicBp, diastolicBp = diastolicBp, heartRate = heartRate,
    respiratoryRate = respiratoryRate, spo2 = spo2, temperature = temperature,
    gcsTotal = gcsTotal, painScale = painScale, bloodGlucose = bloodGlucose,
    etco2 = etco2, pupilsEqual = pupilsEqual, pupilsReactive = pupilsReactive,
    skinColor = skinColor, skinTemp = skinTemp, skinMoisture = skinMoisture
)
