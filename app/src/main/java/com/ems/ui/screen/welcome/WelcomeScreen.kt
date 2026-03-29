package com.ems.ui.screen.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.ems.R
import com.ems.ui.theme.ClinicalBlue
import com.ems.ui.theme.ClinicalBlueDark
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onFinished: () -> Unit) {
    val context = LocalContext.current

    // Fade-in for text elements
    var textVisible by remember { mutableStateOf(false) }
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOut),
        label = "text_fade"
    )

    // Auto-advance after 3.5 s
    LaunchedEffect(Unit) {
        delay(300)
        textVisible = true
        delay(3200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ClinicalBlueDark, ClinicalBlue, Color(0xFF2196F3))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Animated GIF
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.anim_3d)
                    .size(Size.ORIGINAL)
                    .build(),
                contentDescription = "EMS animation",
                modifier = Modifier.size(260.dp)
            )

            // App name + tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha)
                    .padding(horizontal = 32.dp)
            ) {
                Text(
                    text = "EMS PCR",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Patient Assessment &\nCare Reporting",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "EMS · TWSP Batch 12",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(32.dp))

                // Progress indicator
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(3.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(Modifier.height(20.dp))

                // Skip button
                TextButton(onClick = onFinished) {
                    Text(
                        "Skip",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
