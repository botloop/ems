package com.ems.ui.screen.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.ems.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onFinished: () -> Unit) {
    val context = LocalContext.current

    var textVisible by remember { mutableStateOf(false) }
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOut),
        label = "text_fade"
    )

    LaunchedEffect(Unit) {
        delay(300)
        textVisible = true
        delay(14_800)
        onFinished()
    }

    // White background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // GIF fills the full screen as a background layer
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.anim_3d)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = "EMS animation",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Text overlay anchored to the bottom — dark text on white-ish bg
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(textAlpha),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.88f))
                    .padding(horizontal = 32.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "EMS PCR",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A),   // dark text on white
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Patient Assessment &\nCare Reporting",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF2B2B2B).copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    color = Color(0xFF1A1A1A).copy(alpha = 0.2f),
                    thickness = 1.dp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "EMS · TWSP Batch 12",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(24.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(3.dp),
                    color = Color(0xFF1A1A1A),
                    trackColor = Color(0xFF1A1A1A).copy(alpha = 0.15f)
                )
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onFinished) {
                    Text(
                        "Skip",
                        color = Color(0xFF1A1A1A).copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
