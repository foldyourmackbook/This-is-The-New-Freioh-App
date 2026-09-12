package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.components.BlueVerifiedBadge
import com.example.ui.components.HuddleInteractiveLogin
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreiohViewModel

@Composable
fun AuthModalDialog(
    viewModel: FreiohViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            HuddleInteractiveLogin(
                viewModel = viewModel,
                onDismiss = onDismiss,
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            )
        }
    }
}

@Composable
private fun AuthButton(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    testTag: String,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

/**
 * Multi-Platform & App Store Availability Hub Modal
 */
@Composable
fun StorePlatformsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TikTokDarkBg,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = TikTokCyan)
                Text(
                    text = "Freioh Cross-Platform Hub",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Freioh is 100% Free and available across all Mobile & Desktop platforms so you can stay updated everywhere! 🚀",
                    color = TikTokLightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                // 1. Google Play Store (Android)
                PlatformCard(
                    name = "Google Play Store",
                    platform = "Android Devices",
                    badge = "Installed & Active",
                    icon = Icons.Default.Android,
                    accentColor = Color(0xFF00E676),
                    onClick = {
                        Toast.makeText(context, "Google Play Services active • Latest version", Toast.LENGTH_SHORT).show()
                    }
                )

                // 2. Apple App Store (iOS)
                PlatformCard(
                    name = "Apple App Store",
                    platform = "iPhone & iPad",
                    badge = "Official Release",
                    icon = Icons.Default.PhoneIphone,
                    accentColor = Color(0xFF00B0FF),
                    onClick = {
                        Toast.makeText(context, "Available on App Store for iOS", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta)
            ) {
                Text("Got It 👍")
            }
        }
    )
}

@Composable
private fun PlatformCard(
    name: String,
    platform: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TikTokSurfaceDark)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = name, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = platform, color = TikTokGray, fontSize = 11.sp)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.25f))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text(text = badge, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
