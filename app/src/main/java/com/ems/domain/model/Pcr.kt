package com.ems.domain.model

import java.time.Instant

data class Pcr(
    val id: String = "",
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val syncedAt: Instant? = null,

    // Response info
    val unitNumber: String = "",
    val incidentNumber: String = "",
    val dispatchTime: Instant? = null,
    val enRouteTime: Instant? = null,
    val onSceneTime: Instant? = null,
    val patientContactTime: Instant? = null,
    val transportTime: Instant? = null,
    val hospitalArrivalTime: Instant? = null,

    // Patient demographics
    val patientFirstName: String = "",
    val patientLastName: String = "",
    val patientDob: String = "",
    val patientAge: Int? = null,
    val patientGender: String = "",
    val patientAddress: String = "",
    val patientPhone: String = "",
    val insuranceProvider: String = "",
    val insuranceId: String = "",

    // Incident
    val incidentAddress: String = "",
    val callType: String = "",
    val chiefComplaint: String = "",
    val mechanismOfInjury: String = "",

    // Clinical
    val levelOfConsciousness: String = "Alert",
    val positionFound: String = "",
    val pertinentNegatives: String = "",

    // Disposition
    val transportDestination: String = "",
    val patientConditionOnArrival: String = "",
    val refusalSigned: Boolean = false,
    val narrativeNotes: String = "",

    val status: PcrStatus = PcrStatus.DRAFT
)

enum class PcrStatus { DRAFT, COMPLETE, SUBMITTED }
