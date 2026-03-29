package com.ems.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ems.ui.theme.*
import androidx.compose.foundation.gestures.detectTapGestures

enum class BodyZone(val label: String) {
    HEAD("Head / Skull"),
    NECK("Neck"),
    CHEST("Chest / Thorax"),
    ABDOMEN("Abdomen"),
    PELVIS("Pelvis"),
    LEFT_ARM("Left Arm"),
    RIGHT_ARM("Right Arm"),
    LEFT_LEG("Left Leg"),
    RIGHT_LEG("Right Leg"),
    BACK("Back / Posterior")
}

private data class ZoneFraction(
    val zone: BodyZone,
    val l: Float, val t: Float, val r: Float, val b: Float
)

private fun computeZoneFractions(): List<ZoneFraction> {
    val cx = 0.5f
    val hw = 0.09f   // head half-width = radius fraction
    val bw = 0.22f   // body half-width
    val aw = 0.09f   // arm width
    val lw = 0.11f   // leg width

    val headT = 0.04f
    val headB = headT + hw * 2
    val neckT = headB + 0.01f
    val neckB = neckT + 0.05f
    val chestT = neckB
    val chestB = chestT + 0.15f
    val abdT = chestB
    val abdB = abdT + 0.12f
    val pelT = abdB
    val pelB = pelT + 0.09f
    val legT = pelB
    val legB = legT + 0.30f

    return listOf(
        ZoneFraction(BodyZone.HEAD,     cx - hw, headT, cx + hw, headB),
        ZoneFraction(BodyZone.NECK,     cx - 0.07f, neckT, cx + 0.07f, neckB),
        ZoneFraction(BodyZone.CHEST,    cx - bw / 2, chestT, cx + bw / 2, chestB),
        ZoneFraction(BodyZone.ABDOMEN,  cx - bw * 0.48f, abdT, cx + bw * 0.48f, abdB),
        ZoneFraction(BodyZone.PELVIS,   cx - bw * 0.45f, pelT, cx + bw * 0.45f, pelB),
        ZoneFraction(BodyZone.LEFT_ARM, cx - bw / 2 - aw - 0.01f, chestT, cx - bw / 2 - 0.01f, chestT + 0.28f),
        ZoneFraction(BodyZone.RIGHT_ARM,cx + bw / 2 + 0.01f, chestT, cx + bw / 2 + aw + 0.01f, chestT + 0.28f),
        ZoneFraction(BodyZone.LEFT_LEG, cx - lw - 0.01f, legT, cx - 0.01f, legB),
        ZoneFraction(BodyZone.RIGHT_LEG,cx + 0.01f, legT, cx + lw + 0.01f, legB),
    )
}

private val ZONE_FRACTIONS = computeZoneFractions()

@Composable
fun BodyMap(
    selectedZones: Set<BodyZone> = emptySet(),
    zoneFindings: Map<BodyZone, String> = emptyMap(),
    onZoneTapped: (BodyZone) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize(1, 1)) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Body Map — Tap zone to document findings",
            style = MaterialTheme.typography.labelSmall,
            color = SubtleText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .onSizeChanged { canvasSize = it }
                .pointerInput(canvasSize) {
                    detectTapGestures { offset ->
                        val w = canvasSize.width.toFloat()
                        val h = canvasSize.height.toFloat()
                        val tapped = ZONE_FRACTIONS.firstOrNull { zf ->
                            offset.x >= zf.l * w && offset.x <= zf.r * w &&
                            offset.y >= zf.t * h && offset.y <= zf.b * h
                        }
                        tapped?.let { onZoneTapped(it.zone) }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                ZONE_FRACTIONS.forEach { zf ->
                    val color = if (zf.zone in selectedZones)
                        EmergencyRed.copy(alpha = 0.45f)
                    else
                        Color(0xFFBBDEFB).copy(alpha = 0.55f)

                    val borderColor = if (zf.zone in selectedZones) EmergencyRed else ClinicalBlue
                    val topLeft = Offset(zf.l * w, zf.t * h)
                    val sz = Size((zf.r - zf.l) * w, (zf.b - zf.t) * h)
                    val cr = CornerRadius(10f, 10f)

                    if (zf.zone == BodyZone.HEAD) {
                        val cx = (zf.l + zf.r) / 2 * w
                        val cy = (zf.t + zf.b) / 2 * h
                        val radius = sz.width / 2f
                        drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                        drawCircle(color = borderColor, radius = radius, center = Offset(cx, cy), style = Stroke(2f))
                    } else {
                        drawRoundRect(color = color, topLeft = topLeft, size = sz, cornerRadius = cr)
                        drawRoundRect(color = borderColor, topLeft = topLeft, size = sz, cornerRadius = cr, style = Stroke(2f))
                    }
                }
            }

            Text("L", style = MaterialTheme.typography.labelSmall, color = SubtleText,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp))
            Text("R", style = MaterialTheme.typography.labelSmall, color = SubtleText,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp))
        }

        if (selectedZones.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                selectedZones.forEach { zone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EmergencyRed.copy(alpha = 0.07f), RoundedCornerShape(6.dp))
                            .border(1.dp, EmergencyRed.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(zone.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = EmergencyRed)
                        Text(
                            zoneFindings[zone]?.takeIf { it.isNotBlank() } ?: "Documented",
                            style = MaterialTheme.typography.bodySmall, color = SubtleText
                        )
                    }
                }
            }
        }
    }
}
