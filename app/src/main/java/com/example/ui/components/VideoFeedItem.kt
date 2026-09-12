package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VideoPost
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.DecimalFormat

@Composable
fun VideoFeedItem(
    post: VideoPost,
    isPlaying: Boolean,
    progress: Float,
    onTogglePlay: () -> Unit,
    onDoubleTap: (Float, Float) -> Unit,
    onLikeToggle: () -> Unit,
    onSaveToggle: () -> Unit,
    onFollowToggle: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenShare: () -> Unit,
    onAvatarClick: () -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpandedCaption by remember { mutableStateOf(false) }

    // Vinyl Disc Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc"
    )

    // Musical Note Floating Animation
    val noteFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "noteY"
    )
    val noteAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "noteAlpha"
    )

    val isHuddlePost = post.id == "post_1" || post.authorUsername.contains("SETTIGATION", ignoreCase = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .then(
                if (!isHuddlePost) {
                    Modifier.pointerInput(post.id) {
                        detectTapGestures(
                            onTap = { onTogglePlay() },
                            onDoubleTap = { offset ->
                                onDoubleTap(offset.x, offset.y)
                            }
                        )
                    }
                } else {
                    Modifier
                }
            )
    ) {
        // Video Background Content
        if (isHuddlePost) {
            HuddleInteractiveLogin(
                onDismiss = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp, bottom = 100.dp, end = 66.dp)
            )
        } else if (post.videoCoverDrawableId != null) {
            Image(
                painter = painterResource(id = post.videoCoverDrawableId),
                contentDescription = post.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Stylized dynamic animated video gradient
            val gradientColors = post.videoGradientColors.map { Color(it) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                if (post.overlayText.isNotBlank()) {
                    Text(
                        text = post.overlayText,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(24.dp)
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    )
                }
            }
        }

        // Dark gradient overlays for readable text & controls (skip for Huddle to keep login card clean)
        if (!isHuddlePost) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )
        }

        // Center Pause Indicator (when paused)
        if (!isPlaying && !isHuddlePost) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // RIGHT ACTION SIDEBAR
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Author Avatar with Follow '+' button
            Box(
                modifier = Modifier
                    .size(54.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(1.5.dp)
                        .clip(CircleShape)
                        .clickable { onAvatarClick() }
                ) {
                    if (post.authorAvatarUrl == "settigation_logo" || post.authorUsername.contains("SETTIGATION", ignoreCase = true)) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.settigation_logo),
                            contentDescription = post.authorDisplayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = post.authorAvatarUrl,
                            contentDescription = post.authorDisplayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Red '+' Follow Button
                if (!post.authorFollowed) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(TikTokMagenta)
                            .clickable { onFollowToggle() }
                            .testTag("feed_follow_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Follow",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // 2. Like Button
            ActionColumnItem(
                icon = if (post.isLiked) Icons.Default.Favorite else Icons.Filled.Favorite,
                iconTint = if (post.isLiked) TikTokMagenta else Color.White,
                label = formatCount(post.likesCount),
                testTag = "feed_like_button",
                onClick = onLikeToggle
            )

            // 3. Comment Button
            ActionColumnItem(
                icon = Icons.Filled.ChatBubble,
                iconTint = Color.White,
                label = formatCount(post.commentsCount),
                testTag = "feed_comment_button",
                onClick = onOpenComments
            )

            // 4. Bookmark / Save Button
            ActionColumnItem(
                icon = Icons.Filled.Bookmark,
                iconTint = if (post.isSaved) TikTokYellow else Color.White,
                label = formatCount(post.savesCount),
                testTag = "feed_save_button",
                onClick = onSaveToggle
            )

            // 5. Share Button
            ActionColumnItem(
                icon = Icons.Filled.Reply,
                iconTint = Color.White,
                label = formatCount(post.sharesCount),
                iconRotate = 180f,
                testTag = "feed_share_button",
                onClick = onOpenShare
            )

            // 6. Rotating Vinyl Disc & Musical Notes
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onSoundClick() },
                contentAlignment = Alignment.Center
            ) {
                // Floating musical note
                if (isPlaying) {
                    Text(
                        text = "🎵",
                        fontSize = 12.sp,
                        modifier = Modifier
                            .offset(x = (-12).dp, y = noteFloatY.dp)
                            .rotate(15f)
                    )
                }

                // Vinyl Disc
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .rotate(if (isPlaying) discRotation else 0f)
                        .clip(CircleShape)
                        .background(Color(0xFF161616))
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C2C))
                        .padding(4.dp)
                        .clip(CircleShape)
                ) {
                    if (post.soundCoverUrl == "settigation_logo" || post.soundCoverUrl.isBlank()) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.settigation_logo),
                            contentDescription = "Sound Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = post.soundCoverUrl,
                            contentDescription = "Sound Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // BOTTOM LEFT CAPTION & SOUND INFO
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 14.dp, bottom = 22.dp)
        ) {
            // Username + Verified Blue Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { onAvatarClick() }
            ) {
                Text(
                    text = post.authorUsername,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (post.isVerified) {
                    BlueVerifiedBadge(size = 14.dp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Caption with ...more expansion
            Text(
                text = post.caption,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                maxLines = if (isExpandedCaption) 8 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isExpandedCaption = !isExpandedCaption }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Music Sound Title Ticker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onSoundClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio Track",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = post.musicTrackTitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // BOTTOM SCRUBBER PROGRESS BAR
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.White.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun ActionColumnItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    testTag: String,
    iconRotate: Float = 0f,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 26.dp),
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier
                .size(33.dp)
                .rotate(iconRotate)
                .shadow(elevation = 2.dp, shape = CircleShape)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    blurRadius = 4f
                )
            )
        )
    }
}

fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> DecimalFormat("#.#M").format(count / 1_000_000.0)
        count >= 1_000 -> DecimalFormat("#.#K").format(count / 1_000.0)
        else -> count.toString()
    }
}
