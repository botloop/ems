package com.ems.domain.model

data class GcsScore(
    val eyeOpening: Int = 0,       // 1-4
    val verbalResponse: Int = 0,    // 1-5
    val motorResponse: Int = 0,     // 1-6
) {
    val total: Int get() = eyeOpening + verbalResponse + motorResponse
    val isCritical: Boolean get() = total in 3..8 && total > 0
    val severity: String get() = when {
        total == 0 -> "Not assessed"
        total <= 8 -> "Severe"
        total <= 12 -> "Moderate"
        else -> "Mild / Normal"
    }
}

object GcsDescriptions {
    val eyeOptions = listOf(
        1 to "No Response",
        2 to "To Pain",
        3 to "To Voice",
        4 to "Spontaneous"
    )
    val verbalOptions = listOf(
        1 to "No Response",
        2 to "Incomprehensible",
        3 to "Inappropriate Words",
        4 to "Confused",
        5 to "Oriented"
    )
    val motorOptions = listOf(
        1 to "No Response",
        2 to "Abnormal Extension",
        3 to "Abnormal Flexion",
        4 to "Withdraws from Pain",
        5 to "Localizes Pain",
        6 to "Obeys Commands"
    )
}
