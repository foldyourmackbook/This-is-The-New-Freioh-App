package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartParticle

/**
 * Facebook / TikTok Blue Verified Badge
 */
@Composable
fun BlueVerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 15.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1877F2), Color(0xFF20D5EC))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Verified Badge",
            tint = Color.White,
            modifier = Modifier.size(size * 0.72f)
        )
    }
}

/**
 * Iconic TikTok Center Navigation Create Button with Cyan and Magenta 3D offset edges
 */
@Composable
fun TikTokCenterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier
            .testTag("center_create_button")
            .size(width = 48.dp, height = 32.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Amber glow background layer offset left
        Box(
            modifier = Modifier
                .offset(x = (-3).dp)
                .size(width = 42.dp, height = 30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFFF9F43))
        )

        // Sunset Coral/Magenta background layer offset right
        Box(
            modifier = Modifier
                .offset(x = 3.dp)
                .size(width = 42.dp, height = 30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFFFF3B5C))
        )

        // Center White core button
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Video",
                tint = Color(0xFF1E1E2F),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Animated Floating Heart Particle from Double-Tap
 */
@Composable
fun HeartParticleView(particle: HeartParticle) {
    val transition = rememberInfiniteTransition(label = "heart")
    val alphaAnim by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = 2.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .offset(x = particle.x.dp, y = particle.y.dp)
            .scale(scaleAnim)
            .rotate(particle.rotation)
            .alpha(alphaAnim)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = TikTokMagenta,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * End-to-End Encryption Badge Indicator
 */
@Composable
fun E2EEBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x3300C853))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = "🔒", fontSize = 11.sp)
        Text(
            text = "End-to-end encrypted",
            color = E2EEGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
