package com.ems.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Dashboard : Screen("dashboard")
    data object Assessment : Screen("assessment")
    data object PcrList : Screen("pcr_list")
    data object PcrDetail : Screen("pcr_detail/{pcrId}") {
        fun createRoute(pcrId: String) = "pcr_detail/$pcrId"
        const val ARG_PCR_ID = "pcrId"
    }
    data object GcsCalculator : Screen("gcs_calculator")
    data object VitalsCalculator : Screen("vitals_calculator")
    data object Mnemonics : Screen("mnemonics")
}

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Dashboard", "dashboard"),
    BottomNavItem(Screen.Assessment, "Assessment", "assessment"),
    BottomNavItem(Screen.PcrList, "PCR", "pcr"),
    BottomNavItem(Screen.GcsCalculator, "Calculators", "calculators"),
    BottomNavItem(Screen.Mnemonics, "Mnemonics", "mnemonics")
)

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconKey: String
)
