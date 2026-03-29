package com.ems.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateToAssessment: () -> Unit,
    onNavigateToPcrList: () -> Unit,
    onNavigateToGcs: () -> Unit,
    onNavigateToVitals: () -> Unit,
    onNavigateToMnemonics: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val pcrCount by viewModel.pcrCount.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        val columns = if (maxWidth >= 600.dp) 3 else 2

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            // ── Header ────────────────────────────────────────────────────
            item(span = { GridItemSpan(columns) }) {
                DashboardHeader(pcrCount = pcrCount)
            }

            // ── Start Assessment — dominant full-width card ───────────────
            item(span = { GridItemSpan(columns) }) {
                StartAssessmentCard(onClick = onNavigateToAssessment)
            }

            // ── 2×2 module grid ───────────────────────────────────────────
            item {
                MinimalModuleCard(
                    title = "PCR Reports",
                    subtitle = if (pcrCount == 1) "1 saved" else "$pcrCount saved",
                    icon = Icons.Outlined.Description,
                    iconTint = Color(0xFF2B2B2B),
                    badge = if (pcrCount > 0) pcrCount.toString() else null,
                    onClick = onNavigateToPcrList
                )
            }
            item {
                MinimalModuleCard(
                    title = "GCS Score",
                    subtitle = "Glasgow Coma Scale",
                    icon = Icons.Outlined.Psychology,
                    iconTint = Color(0xFFF59E0B),
                    onClick = onNavigateToGcs
                )
            }
            item {
                MinimalModuleCard(
                    title = "Vital Signs",
                    subtitle = "Shock index · Trends",
                    icon = Icons.Outlined.MonitorHeart,
                    iconTint = Color(0xFFEF4444),
                    onClick = onNavigateToVitals
                )
            }
            item {
                MinimalModuleCard(
                    title = "Mnemonics",
                    subtitle = "SAMPLE, OPQRST +6",
                    icon = Icons.Outlined.MenuBook,
                    iconTint = Color(0xFF22C55E),
                    badge = "8",
                    onClick = onNavigateToMnemonics
                )
            }

            // ── Quick reference footer ────────────────────────────────────
            item(span = { GridItemSpan(columns) }) {
                QuickRefFooter()
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader(pcrCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "EMS PCR",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Patient Care Reporting",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Normal
                )
            }
            // Avatar / unit indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2B2B2B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EMS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Status pills
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
                dot = Color(0xFF22C55E),
                label = "Offline Ready"
            )
            StatusPill(
                dot = Color(0xFF9CA3AF),
                label = "$pcrCount ${if (pcrCount == 1) "Report" else "Reports"}"
            )
        }
    }
}

@Composable
private fun StatusPill(dot: Color, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dot)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
    }
}

// ── Start Assessment ──────────────────────────────────────────────────────────

@Composable
private fun StartAssessmentCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF1A1A1A))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Start Assessment",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Scene Size-Up  →  Transport",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ── Module Card ───────────────────────────────────────────────────────────────

@Composable
private fun MinimalModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    badge: String? = null
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon row + optional badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.09f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Label
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827),
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// ── Quick Reference Footer ────────────────────────────────────────────────────

@Composable
private fun QuickRefFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(18.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Quick Reference",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151)
            )
            Text(
                text = "PENMAN · Primary · Secondary · Transport",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
