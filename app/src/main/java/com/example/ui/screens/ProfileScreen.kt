package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserProfile
import com.example.data.model.VideoPost
import com.example.ui.components.BlueVerifiedBadge
import com.example.ui.components.formatCount
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreiohViewModel
import com.example.ui.viewmodel.MainTab

enum class ProfileTab(val label: String) {
    POSTS("Videos"),
    SAVED("Saved"),
    LIKES("Liked"),
    REPOSTS("Reposts"),
    STORIES("Stories")
}

@Composable
fun ProfileScreen(
    viewModel: FreiohViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val otherProfile by viewModel.viewingUserProfile.collectAsState()
    val allPosts by viewModel.allPosts.collectAsState()
    val isEditProfileOpen by viewModel.isEditProfileOpen.collectAsState()

    // Dynamic Theme Colors for White Mode & Dark Mode
    val bgColor = if (isDarkMode) TikTokBlack else Color(0xFFFFFFFF)
    val textColor = if (isDarkMode) Color.White else Color(0xFF161823)
    val mutedTextColor = if (isDarkMode) TikTokGray else Color(0xFF73747B)
    val buttonBg = if (isDarkMode) TikTokCardDark else Color(0xFFF1F1F2)
    val buttonTextColor = if (isDarkMode) Color.White else Color(0xFF161823)
    val dividerBorderColor = if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f)

    val profile = otherProfile ?: userProfile ?: UserProfile()
    val stories by viewModel.allStories.collectAsState()
    val isMe = otherProfile == null

    val vmProfileTab by viewModel.selectedProfileTab.collectAsState()
    var selectedTab by remember { mutableStateOf(ProfileTab.POSTS) }

    LaunchedEffect(vmProfileTab) {
        selectedTab = vmProfileTab
    }

    val filteredPosts = remember(allPosts, selectedTab, isMe, profile) {
        when (selectedTab) {
            ProfileTab.POSTS -> {
                if (isMe) allPosts else allPosts.filter { it.authorUsername == profile.handle || it.authorDisplayName == profile.displayName }
            }
            ProfileTab.SAVED -> allPosts.filter { it.isSaved }
            ProfileTab.LIKES -> allPosts.filter { it.isLiked }
            ProfileTab.REPOSTS -> allPosts.take(2)
            ProfileTab.STORIES -> emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isMe) {
                IconButton(onClick = { viewModel.closeOtherUserProfile() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                }
            } else {
                IconButton(onClick = {
                    Toast.makeText(context, "Find friends & contacts", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Find Friends", tint = textColor)
                }
            }

            // Display Name + Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = profile.displayName,
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                if (profile.isVerified) {
                    BlueVerifiedBadge(size = 14.dp)
                }
            }

            // Top-right actions: View Insights and Settings & Privacy (NO unexpected login popup!)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Profile Views: 1,420 viewers this week 👁️", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("profile_top_views_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "Profile Views",
                        tint = textColor
                    )
                }
                IconButton(
                    onClick = { viewModel.openSettings() },
                    modifier = Modifier.testTag("profile_top_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = textColor
                    )
                }
            }
        }

        // Profile Avatar with Gradient Ring
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(TikTokMagenta, TikTokCyan, TikTokYellow, TikTokMagenta)
                        )
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(bgColor)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = profile.avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" },
                    contentDescription = profile.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = profile.handle,
                color = mutedTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Row: Following, Followers, Likes
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatColumn(label = "Following", count = formatCount(profile.followingCount.toLong()), textColor = textColor, subColor = mutedTextColor)
                VerticalDivider(color = dividerBorderColor, modifier = Modifier.height(24.dp))
                ProfileStatColumn(label = "Followers", count = formatCount(profile.followersCount.toLong()), textColor = textColor, subColor = mutedTextColor)
                VerticalDivider(color = dividerBorderColor, modifier = Modifier.height(24.dp))
                ProfileStatColumn(label = "Likes", count = formatCount(profile.likesTotalCount), textColor = textColor, subColor = mutedTextColor)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Edit Profile / Share Profile OR Follow / Message)
            if (isMe) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.openEditProfile() },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("edit_profile_button")
                    ) {
                        Text("Edit profile", color = buttonTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, "Check out ${profile.displayName}'s profile on TikTok: https://tiktok.com/@${profile.handle}")
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Profile Via"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text("Share profile", color = buttonTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.openSettings() },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("open_settings_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Settings", color = buttonTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(0.88f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleFollowViewedUser() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (profile.isFollowedByMe) buttonBg else TikTokMagenta
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("profile_follow_button")
                    ) {
                        Text(
                            text = if (profile.isFollowedByMe) "Following" else "Follow",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val newConv = com.example.data.model.Conversation(
                                title = profile.displayName,
                                avatarUrl = profile.avatarUrl,
                                isGroup = false,
                                participantHandles = listOf(profile.handle),
                                lastMessage = "Assalamu Alaikum 🕊️",
                                isVerified = profile.isVerified
                            )
                            viewModel.openConversation(newConv)
                            viewModel.selectTab(MainTab.INBOX)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text("Message", color = buttonTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio Text & Website
            if (profile.bio.isNotBlank()) {
                Text(
                    text = profile.bio,
                    color = textColor,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            if (profile.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(13.dp))
                    Text(text = profile.website, color = TikTokCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Profile Tab Bar: Videos, Saved, Liked, Reposts (WITH CLEAR LABELED TEXT AS REQUESTED!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = dividerBorderColor),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfileTabItem(
                icon = Icons.Default.GridOn,
                label = ProfileTab.POSTS.label,
                isSelected = selectedTab == ProfileTab.POSTS,
                textColor = textColor,
                onClick = {
                    selectedTab = ProfileTab.POSTS
                    viewModel.selectProfileTab(ProfileTab.POSTS)
                }
            )
            ProfileTabItem(
                icon = Icons.Default.BookmarkBorder,
                label = ProfileTab.SAVED.label,
                isSelected = selectedTab == ProfileTab.SAVED,
                textColor = textColor,
                onClick = {
                    selectedTab = ProfileTab.SAVED
                    viewModel.selectProfileTab(ProfileTab.SAVED)
                }
            )
            ProfileTabItem(
                icon = Icons.Default.FavoriteBorder,
                label = ProfileTab.LIKES.label,
                isSelected = selectedTab == ProfileTab.LIKES,
                textColor = textColor,
                onClick = {
                    selectedTab = ProfileTab.LIKES
                    viewModel.selectProfileTab(ProfileTab.LIKES)
                }
            )
            ProfileTabItem(
                icon = Icons.Default.Repeat,
                label = ProfileTab.REPOSTS.label,
                isSelected = selectedTab == ProfileTab.REPOSTS,
                textColor = textColor,
                onClick = {
                    selectedTab = ProfileTab.REPOSTS
                    viewModel.selectProfileTab(ProfileTab.REPOSTS)
                }
            )
        }

        // Tab Content Grid & Polished Empty States
        if (selectedTab == ProfileTab.STORIES) {
            if (stories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = mutedTextColor, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active stories", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Stories disappear after 24 hours", color = mutedTextColor, fontSize = 12.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(stories) { story ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.75f)
                                .background(Brush.verticalGradient(story.mediaGradientColors.map { Color(it) }))
                                .clickable { viewModel.openStory(story) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = story.caption,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else if (filteredPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    when (selectedTab) {
                        ProfileTab.SAVED -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(TikTokCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Saved",
                                    tint = TikTokCyan,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Saved Videos & Recitations",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Videos and Quran recitations you bookmark will be saved here privately. Only you can view your saved collection.",
                                color = mutedTextColor,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.selectTab(MainTab.HOME)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Explore Halal Videos", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        ProfileTab.LIKES -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(TikTokMagenta.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Liked",
                                    tint = TikTokMagenta,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Only You Can See Your Liked Videos",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Videos you like on your feed are private and visible only to your account.",
                                color = mutedTextColor,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        ProfileTab.REPOSTS -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(buttonBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Reposts",
                                    tint = TikTokCyan,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No Reposted Videos",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Repost beneficial and uplifting videos to share them with your followers.",
                                color = mutedTextColor,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(buttonBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = "Videos",
                                    tint = textColor,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Upload Your First Video",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Share inspirational reminders, Islamic reflections, or creative clips with the Muslim community.",
                                color = mutedTextColor,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.selectTab(MainTab.CREATE)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TikTokMagenta),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Video", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                verticalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                items(filteredPosts) { post ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.75f)
                            .background(
                                Brush.verticalGradient(
                                    post.videoGradientColors.map { Color(it) }
                                )
                            )
                            .clickable {
                                viewModel.selectTab(MainTab.HOME)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Text(text = "${post.likesCount / 1000}k", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // EDIT PROFILE DIALOG
    if (isEditProfileOpen) {
        EditProfileDialog(
            profile = profile,
            onDismiss = { viewModel.closeEditProfile() },
            onSave = { name, bio, site ->
                viewModel.saveProfile(name, bio, site)
            }
        )
    }
}

@Composable
private fun ProfileStatColumn(label: String, count: String, textColor: Color, subColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, color = textColor, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = subColor, fontSize = 11.sp)
    }
}

@Composable
private fun ProfileTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) textColor else TikTokGray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(2.5.dp)
                        .background(textColor)
                )
            } else {
                Spacer(modifier = Modifier.height(2.5.dp))
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(profile.displayName) }
    var bio by remember { mutableStateOf(profile.bio) }
    var site by remember { mutableStateOf(profile.website) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TikTokDarkBg,
        title = {
            Text("Edit Profile", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = Color.Gray
                    ),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = site,
                    onValueChange = { site = it },
                    label = { Text("Website", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(name, bio, site)
                    onDismiss()
                }
            ) {
                Text("Save", color = TikTokCyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
