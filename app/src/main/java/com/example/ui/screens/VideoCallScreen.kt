package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.E2EEBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreiohViewModel
import com.example.ui.viewmodel.VideoCallState

@Composable
fun VideoCallScreen(
    callState: VideoCallState,
    viewModel: FreiohViewModel,
    modifier: Modifier = Modifier
) {
    val durationFormatted = remember(callState.callDurationSeconds) {
        val mins = callState.callDurationSeconds / 60
        val secs = callState.callDurationSeconds % 60
        "${if (mins < 10) "0$mins" else mins}:${if (secs < 10) "0$secs" else secs}"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
    ) {
        // Main Remote Video Stream Canvas / Group Grid
        if (callState.isGroupCall) {
            // 2x2 Group Video Grid
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    CallerTile(name = "Maya R.", avatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", gradient = listOf(0xFF2C3E50, 0xFF4CA1AF), modifier = Modifier.weight(1f).fillMaxHeight())
                    CallerTile(name = "Chef Marco", avatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150", gradient = listOf(0xFF8E2DE2, 0xFF4A00E0), modifier = Modifier.weight(1f).fillMaxHeight())
                }
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    CallerTile(name = "Antish Official", avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", gradient = listOf(0xFFD31027, 0xFFEA384D), modifier = Modifier.weight(1f).fillMaxHeight())
                    CallerTile(name = "You", avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150", gradient = listOf(0xFF1E3C72, 0xFF2A5298), isYou = true, isMuted = callState.isMuted, isCameraOff = callState.isCameraOff, modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }
        } else {
            // Single 1-on-1 Remote Video Stream
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1F4037), Color(0xFF99F2C8), Color(0xFF0F2027))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(3.dp, TikTokCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = callState.callerAvatar,
                            contentDescription = callState.callTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = callState.callTitle,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "HD 1080p Video • $durationFormatted",
                        color = TikTokLightGray,
                        fontSize = 14.sp
                    )
                }

                // PIP (Self preview)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(top = 60.dp, end = 16.dp)
                        .size(width = 110.dp, height = 150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E1E))
                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (callState.isCameraOff) {
                        Text("Camera Off", color = TikTokGray, fontSize = 11.sp)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF2C3E50), Color(0xFF000000))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("You 🤳", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Top Header: Security E2EE indicator & Call Timer
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent))
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                E2EEBadge()
                Text(
                    text = durationFormatted,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Call Controls Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)))
                )
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Watch Videos Collaboration Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x3325F4EE))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🍿", fontSize = 13.sp)
                Text(
                    text = "Co-Watching Short Videos Active",
                    color = TikTokCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute Mic
                CallActionButton(
                    icon = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (callState.isMuted) "Unmute" else "Mute",
                    isActive = callState.isMuted,
                    activeColor = Color.Red,
                    onClick = { viewModel.toggleCallMute() }
                )

                // Video Camera On/Off
                CallActionButton(
                    icon = if (callState.isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                    label = if (callState.isCameraOff) "Start Video" else "Stop Video",
                    isActive = callState.isCameraOff,
                    activeColor = Color.Red,
                    onClick = { viewModel.toggleCallCamera() }
                )

                // Flip Camera
                CallActionButton(
                    icon = Icons.Default.FlipCameraIos,
                    label = "Flip",
                    onClick = { viewModel.flipCallCamera() }
                )

                // Speaker
                CallActionButton(
                    icon = if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                    label = "Speaker",
                    isActive = callState.isSpeakerOn,
                    activeColor = TikTokCyan,
                    onClick = { viewModel.toggleCallSpeaker() }
                )

                // END CALL
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                        .clickable { viewModel.endVideoCall() }
                        .testTag("end_video_call_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CallerTile(
    name: String,
    avatar: String,
    gradient: List<Long>,
    isYou: Boolean = false,
    isMuted: Boolean = false,
    isCameraOff: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .background(Brush.verticalGradient(gradient.map { Color(it) })),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = avatar,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            if (isMuted) {
                Text("🔇 Muted", color = Color.Red, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean = false,
    activeColor: Color = TikTokMagenta,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else Color.White.copy(alpha = 0.15f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = TikTokLightGray, fontSize = 10.sp)
    }
}
