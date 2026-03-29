package com.ems.ui.theme

import androidx.compose.ui.graphics.Color

// Salt and Pepper palette
val Salt        = Color(0xFFFFFFFF) // pure white — salt
val Pepper      = Color(0xFF2B2B2B) // near black — pepper
val PepperLight = Color(0xFFB3B3B3) // medium gray
val PepperMid   = Color(0xFFD4D4D4) // light gray
val PepperDark  = Color(0xFF1A1A1A) // deeper black for dark surfaces

// Semantic aliases kept for existing code
val ClinicalBlue      = Pepper        // primary actions now use Pepper
val ClinicalBlueLight = PepperLight
val ClinicalBlueDark  = PepperDark

// Severity — retained as-is (clinical meaning must stay clear)
val EmergencyRed          = Color(0xFFD93025)
val EmergencyRedLight     = Color(0xFFEF5350)
val EmergencyRedContainer = Color(0xFFFFEBEE)

val WarningAmber          = Color(0xFFFBBC04)
val WarningAmberContainer = Color(0xFFFFF8E1)

val SuccessGreen          = Color(0xFF34A853)
val SuccessGreenContainer = Color(0xFFE8F5E9)

// Surface / text
val CleanWhite  = Salt
val SurfaceGray = PepperMid
val DividerGray = PepperMid
val SubtleText  = PepperLight
val PrimaryText = Pepper

// Dark theme
val DarkSurface    = Color(0xFF1C1C1C)
val DarkBackground = Color(0xFF111111)
val DarkCard       = Color(0xFF2B2B2B)
val DarkDivider    = Color(0xFF3A3A3A)

// Severity aliases
val SeverityNormal   = SuccessGreen
val SeverityCaution  = WarningAmber
val SeverityCritical = EmergencyRed
