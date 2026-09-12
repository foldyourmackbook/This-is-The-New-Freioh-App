package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreiohViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraStudioScreen(
    viewModel: FreiohViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingDuration by viewModel.recordingDurationSeconds.collectAsState()
    val speed by viewModel.selectedSpeed.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()
    val durationMode by viewModel.maxDurationMode.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    val isFlashOn by viewModel.isFlashOn.collectAsState()
    val selectedSound by viewModel.selectedSound.collectAsState()
    val isSoundDrawerOpen by viewModel.isSoundDrawerOpen.collectAsState()
    val isInEditorScreen by viewModel.isInEditorScreen.collectAsState()
    val overlayText by viewModel.overlayText.collectAsState()
    val postCaption by viewModel.postCaption.collectAsState()

    val speeds = listOf("0.3x", "0.5x", "1x", "2x", "3x")
    val filters = listOf("Normal", "Cyber", "Vintage", "Glow", "B&W", "Golden")
    val durations = listOf("15s", "60s", "10m")

    // Filter Color preview simulation
    val filterGradient = when (filter) {
        "Cyber" -> listOf(Color(0xFF00F2FE), Color(0xFF4FACFE), Color(0xFF000000))
        "Vintage" -> listOf(Color(0xFFE65C00), Color(0xFFF9D423), Color(0xFF1F1C18))
        "Glow" -> listOf(Color(0xFFFF0844), Color(0xFFFFB199), Color(0xFF2C1117))
        "B&W" -> listOf(Color(0xFF757575), Color(0xFF424242), Color(0xFF111111))
        "Golden" -> listOf(Color(0xFFF7971E), Color(0xFFFFD200), Color(0xFF332000))
        else -> listOf(Color(0xFF1E3C72), Color(0xFF2A5298), Color(0xFF050510))
    }

    if (isInEditorScreen) {
        // POST-RECORDING VIDEO EDITOR & PUBLISHER
        VideoEditorPublisherView(
            overlayText = overlayText,
            onOverlayTextChange = { viewModel.setOverlayText(it) },
            caption = postCaption,
            onCaptionChange = { viewModel.setPostCaption(it) },
            selectedSound = selectedSound,
            filter = filter,
            onDiscard = { viewModel.discardRecording() },
            onPublishVideo = { viewModel.publishVideo(asStory = false) },
            onPublishStory = { viewModel.publishVideo(asStory = true) }
        )
    } else {
        // CAMERA LIVE RECORDING STUDIO
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Live Camera Viewfinder Simulation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(filterGradient)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isFrontCamera) "🤳 Front Studio Camera" else "📷 Rear Ultra HD Camera",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (isRecording) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Red.copy(alpha = 0.8f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = "REC 00:${if (recordingDuration < 10) "0$recordingDuration" else recordingDuration}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Top Bar: Close, Add Sound, Flash
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                // Add Sound Selector Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { viewModel.openSoundDrawer() }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound",
                        tint = TikTokMagenta,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = selectedSound?.title ?: "Add Sound",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                IconButton(onClick = { viewModel.toggleFlash() }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) TikTokYellow else Color.White
                    )
                }
            }

            // Right Toolbar: Flip, Speed, Filters, Beauty, Timer
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ToolIcon(icon = Icons.Default.FlipCameraAndroid, label = "Flip", onClick = { viewModel.flipCamera() })
                ToolIcon(icon = Icons.Default.Speed, label = speed, onClick = {
                    val nextIdx = (speeds.indexOf(speed) + 1) % speeds.size
                    viewModel.setSpeed(speeds[nextIdx])
                })
                ToolIcon(icon = Icons.Default.ColorLens, label = "Filter", onClick = {
                    val nextIdx = (filters.indexOf(filter) + 1) % filters.size
                    viewModel.setFilter(filters[nextIdx])
                })
                ToolIcon(icon = Icons.Default.AutoAwesome, label = "Beauty", onClick = { })
                ToolIcon(icon = Icons.Default.Timer, label = "Timer", onClick = { })
            }

            // Bottom Controls: Speed Bar, Record Button, Duration Selectors
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Speed Selector Bar
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    speeds.forEach { sp ->
                        Text(
                            text = sp,
                            color = if (speed == sp) TikTokMagenta else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (speed == sp) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable { viewModel.setSpeed(sp) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Record Button
                val recordPulse by rememberInfiniteTransition(label = "rec").animateFloat(
                    initialValue = 1f,
                    targetValue = if (isRecording) 1.15f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .scale(recordPulse)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(TikTokMagenta)
                        .clickable { viewModel.toggleRecording() }
                        .testTag("camera_record_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(TikTokMagenta)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Durations: 15s / 60s / 10m
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    durations.forEach { dur ->
                        Text(
                            text = dur,
                            color = if (durationMode == dur) Color.White else TikTokGray,
                            fontSize = 13.sp,
                            fontWeight = if (durationMode == dur) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable { viewModel.setMaxDuration(dur) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }

    // Sound Picker Modal
    if (isSoundDrawerOpen) {
        SoundPickerBottomSheet(
            sounds = viewModel.trendingSounds,
            onSelect = { viewModel.selectSound(it) },
            onClose = { viewModel.closeSoundDrawer() }
        )
    }
}

@Composable
private fun ToolIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoundPickerBottomSheet(
    sounds: List<AudioTrack>,
    onSelect: (AudioTrack) -> Unit,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = TikTokDarkBg,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(16.dp)
        ) {
            Text(
                text = "🎵 Trending Music & Sounds",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            sounds.forEach { sound ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(sound) }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TikTokMagenta),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                        Column {
                            Text(text = sound.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${sound.artist} • ${sound.duration}", color = TikTokGray, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { onSelect(sound) },
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Use", fontSize = 12.sp)
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    }
}

@Composable
private fun VideoEditorPublisherView(
    overlayText: String,
    onOverlayTextChange: (String) -> Unit,
    caption: String,
    onCaptionChange: (String) -> Unit,
    selectedSound: AudioTrack?,
    filter: String,
    onDiscard: () -> Unit,
    onPublishVideo: () -> Unit,
    onPublishStory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TikTokBlack)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDiscard) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Discard", tint = Color.White)
            }
            Text("Edit & Publish", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Thumbnail Preview Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(TikTokMagenta.copy(alpha = 0.8f), TikTokCyan.copy(alpha = 0.8f), Color.Black)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                if (overlayText.isNotBlank()) {
                    Text(
                        text = overlayText,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                if (selectedSound != null) {
                    Text(
                        text = "🎵 ${selectedSound.title}",
                        color = TikTokLightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overlay Text Editor
        OutlinedTextField(
            value = overlayText,
            onValueChange = onOverlayTextChange,
            label = { Text("Video On-Screen Text (Sticker)") },
            placeholder = { Text("e.g., Amazing dance drop! 🔥") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = TikTokCyan,
                unfocusedBorderColor = TikTokGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Caption & Hashtags
        OutlinedTextField(
            value = caption,
            onValueChange = onCaptionChange,
            label = { Text("Caption & #Hashtags") },
            placeholder = { Text("Describe your video... #viral #freioh #trending") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = TikTokMagenta,
                unfocusedBorderColor = TikTokGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Publish Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onPublishStory,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TikTokCyan),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(TikTokCyan, TikTokMagenta))),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("publish_story_button")
            ) {
                Text("Add to Story 📖", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onPublishVideo,
                colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(50.dp)
                    .testTag("publish_post_button")
            ) {
                Text("Post to Feed 🚀", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
