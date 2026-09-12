package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContactPerson
import com.example.data.model.VideoPost
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    post: VideoPost,
    contacts: List<ContactPerson>,
    onClose: () -> Unit,
    onSendDirect: (ContactPerson) -> Unit
) {
    val context = LocalContext.current
    var sentContacts by remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = TikTokDarkBg,
        contentColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(TikTokGray.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Send to Friends",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TikTokGray)
                }
            }

            // Quick DM Contacts Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                contacts.forEach { contact ->
                    val isSent = sentContacts.contains(contact.id)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(60.dp)
                            .clickable {
                                onSendDirect(contact)
                                sentContacts = sentContacts + contact.id
                                Toast.makeText(context, "Sent to ${contact.name} 🔒", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = contact.avatarUrl,
                                contentDescription = contact.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(TikTokCardDark)
                            )
                            if (isSent) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(E2EEGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sent",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSent) "Sent" else contact.name.split(" ").first(),
                            color = if (isSent) E2EEGreen else TikTokLightGray,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))

            // Social Platforms Share Row
            Text(
                text = "Share to",
                color = TikTokGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShareActionItem(
                    title = "More",
                    icon = Icons.Default.Share,
                    bgColor = TikTokCyan,
                    iconColor = Color.Black,
                    onClick = {
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "Watch this video on TikTok: ${post.caption} - by ${post.authorUsername}")
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Video Via")
                        context.startActivity(shareIntent)
                        onClose()
                    }
                )
                ShareActionItem(
                    title = "Copy Link",
                    icon = Icons.Default.Link,
                    bgColor = Color(0xFF3B3B4F),
                    iconColor = Color.White,
                    onClick = {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("TikTok Video", "https://tiktok.com/@${post.authorUsername}/video/${post.id}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        onClose()
                    }
                )
                ShareActionItem(
                    title = "WhatsApp",
                    icon = Icons.Default.Chat,
                    bgColor = Color(0xFF25D366),
                    iconColor = Color.White,
                    onClick = {
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                `package` = "com.whatsapp"
                                putExtra(android.content.Intent.EXTRA_TEXT, "Check out this video: ${post.caption}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback to system chooser
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, "Check out this video: ${post.caption}")
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Video"))
                        }
                        onClose()
                    }
                )
                ShareActionItem(
                    title = "Facebook",
                    icon = Icons.Default.ThumbUp,
                    bgColor = FacebookBlue,
                    iconColor = Color.White,
                    onClick = {
                        Toast.makeText(context, "Sharing to Facebook Feed...", Toast.LENGTH_SHORT).show()
                        onClose()
                    }
                )
                ShareActionItem(
                    title = "Messenger",
                    icon = Icons.Default.Send,
                    bgColor = Color(0xFF00B2FF),
                    iconColor = Color.White,
                    onClick = {
                        Toast.makeText(context, "Sharing to Messenger...", Toast.LENGTH_SHORT).show()
                        onClose()
                    }
                )
                ShareActionItem(
                    title = "Save Video",
                    icon = Icons.Default.Download,
                    bgColor = TikTokCardDark,
                    iconColor = Color.White,
                    onClick = {
                        Toast.makeText(context, "Video saved to Gallery 📥", Toast.LENGTH_SHORT).show()
                        onClose()
                    }
                )
                ShareActionItem(
                    title = "Duet",
                    icon = Icons.Default.GroupWork,
                    bgColor = TikTokCardDark,
                    iconColor = Color.White,
                    onClick = {
                        Toast.makeText(context, "Opening Duet Studio 🎬", Toast.LENGTH_SHORT).show()
                        onClose()
                    }
                )
            }
        }
    }
}

@Composable
private fun ShareActionItem(
    title: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(58.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = TikTokLightGray,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}
