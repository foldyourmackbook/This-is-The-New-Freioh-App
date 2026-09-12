package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FreiohViewModel

/**
 * Authentic TikTok-style "Settings and privacy" screen crafted for Muslim creators & viewers.
 * Features comprehensive privacy, Islamic content filters (Haya & modesty mode, prayer quiet hours,
 * Halal audio/Nasheeds, Quran daily reminders), TikTok cache management, and multi-platform account login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: FreiohViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    // Preferences state
    var isPrivateAccount by remember { mutableStateOf(false) }
    var allowDirectMessages by remember { mutableStateOf(true) }
    var allowComments by remember { mutableStateOf(true) }
    var isTwoFactorEnabled by remember { mutableStateOf(true) }

    // Islamic & Halal Preferences
    var modestyFilterEnabled by remember { mutableStateOf(true) }
    var halalAudioOnly by remember { mutableStateOf(false) }
    var adhanQuietHours by remember { mutableStateOf(true) }
    var dailyAyahReminder by remember { mutableStateOf(true) }
    var restrictedMode by remember { mutableStateOf(true) }

    // Content & Display
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val colors = if (isDarkMode) {
        SettingsColors(
            bg = Color(0xFF121212),
            card = Color(0xFF1E1E1E),
            textPrimary = Color.White,
            textSecondary = Color(0xFF86878B),
            divider = Color(0xFF2A2A2A),
            iconTint = Color.White
        )
    } else {
        SettingsColors(
            bg = Color(0xFFF7F8FA),
            card = Color(0xFFFFFFFF),
            textPrimary = Color(0xFF161823),
            textSecondary = Color(0xFF73747B),
            divider = Color(0xFFEBEBEB),
            iconTint = Color(0xFF161823)
        )
    }

    var autoplayOnWifi by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English / العربية") }
    var dataSaver by remember { mutableStateOf(false) }
    var pushNotifications by remember { mutableStateOf(true) }

    // Cache state
    var cacheSizeMb by remember { mutableStateOf(64.2f) }
    var downloadsSizeMb by remember { mutableStateOf(128.5f) }

    // Dialog states
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showManageAccountDialog by remember { mutableStateOf(false) }
    var showIslamicGuidelinesDialog by remember { mutableStateOf(false) }
    var showPrayerTimesDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalSettingsColors provides colors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings and privacy",
                            color = colors.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("settings_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.textPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.bg
                    )
                )
            },
            containerColor = colors.bg,
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colors.bg)
                    .verticalScroll(rememberScrollState())
            ) {
            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // 1. ACCOUNT
            // ==========================================
            TikTokSettingsSection(title = "Account") {
                TikTokSettingsRow(
                    icon = Icons.Default.Person,
                    title = "Manage account",
                    valueText = userProfile?.handle ?: "@freioh_creator",
                    testTag = "settings_manage_account",
                    onClick = { showManageAccountDialog = true }
                )
                TikTokDivider()

                TikTokSettingsRow(
                    icon = Icons.Default.Lock,
                    title = "Privacy",
                    valueText = if (isPrivateAccount) "Private" else "Public",
                    testTag = "settings_privacy",
                    onClick = {
                        isPrivateAccount = !isPrivateAccount
                        Toast.makeText(
                            context,
                            if (isPrivateAccount) "Account is now Private" else "Account is now Public",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                TikTokDivider()

                TikTokSettingsRow(
                    icon = Icons.Default.Security,
                    title = "Security & login",
                    valueText = if (isTwoFactorEnabled) "Protected" else "Alert",
                    testTag = "settings_security",
                    onClick = {
                        viewModel.openAuthModal()
                        Toast.makeText(context, "Opening secure login verification...", Toast.LENGTH_SHORT).show()
                    }
                )
                TikTokDivider()

                TikTokSettingsRow(
                    icon = Icons.Default.Share,
                    title = "Share profile",
                    testTag = "settings_share_profile",
                    onClick = {
                        Toast.makeText(context, "Profile link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 2. ISLAMIC CONTENT & ETHICS (FOR MUSLIMS)
            // ==========================================
            TikTokSettingsSection(title = "Islamic Content & Modesty") {
                TikTokSwitchRow(
                    icon = Icons.Default.VisibilityOff,
                    title = "Modesty Mode (Lower the Gaze)",
                    subtitle = "Filter non-modest imagery and respect Islamic modesty (Haya)",
                    checked = modestyFilterEnabled,
                    testTag = "settings_modesty_toggle",
                    onCheckedChange = {
                        modestyFilterEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Modesty Filter enabled (Haya Active) 🌿" else "Modesty Filter disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.Audiotrack,
                    title = "Halal Audio & Nasheeds",
                    subtitle = "Prioritize vocal nasheeds, Quran recitation and beneficial talks",
                    checked = halalAudioOnly,
                    testTag = "settings_halal_audio_toggle",
                    onCheckedChange = {
                        halalAudioOnly = it
                        Toast.makeText(
                            context,
                            if (it) "Halal audio & Nasheeds prioritized 🎧" else "Standard audio enabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.AccessTime,
                    title = "Adhan & Prayer Quiet Hours",
                    subtitle = "Automatically pause video audio and alerts during Salah prayer times",
                    checked = adhanQuietHours,
                    testTag = "settings_adhan_quiet_toggle",
                    onCheckedChange = {
                        adhanQuietHours = it
                        Toast.makeText(
                            context,
                            if (it) "Salah quiet mode enabled for Fajr, Dhuhr, Asr, Maghrib, Isha 🕌" else "Quiet hours disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                TikTokDivider()

                TikTokSettingsRow(
                    icon = Icons.Default.AccessTimeFilled,
                    title = "Prayer Times & Qibla Direction",
                    valueText = "Makkah / Standard",
                    testTag = "settings_prayer_times_btn",
                    onClick = { showPrayerTimesDialog = true }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.MenuBook,
                    title = "Daily Qur'an & Dhikr Reminders",
                    subtitle = "Receive uplifting daily Ayat and authentic morning & evening Adhkar",
                    checked = dailyAyahReminder,
                    testTag = "settings_daily_ayah_toggle",
                    onCheckedChange = {
                        dailyAyahReminder = it
                        Toast.makeText(
                            context,
                            if (it) "Daily Ayat & Dhikr reminders active 📖" else "Reminders muted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.Shield,
                    title = "Strict Ethical Filter (Anti-Fitnah)",
                    subtitle = "Filter explicit comments, profane language, and unethical debates",
                    checked = restrictedMode,
                    testTag = "settings_restricted_mode_toggle",
                    onCheckedChange = {
                        restrictedMode = it
                        Toast.makeText(
                            context,
                            if (it) "Ethical filter active. Safe community environment." else "Filter relaxed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 3. CONTENT & DISPLAY
            // ==========================================
            TikTokSettingsSection(title = "Content & Display") {
                TikTokSettingsRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    valueText = selectedLanguage,
                    testTag = "settings_language_btn",
                    onClick = { showLanguageDialog = true }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark theme",
                    subtitle = if (isDarkMode) "High-contrast dark mode" else "Light white mode enabled",
                    checked = isDarkMode,
                    testTag = "settings_dark_mode_switch",
                    onCheckedChange = {
                        viewModel.setDarkMode(it)
                        Toast.makeText(context, if (it) "Dark theme enabled 🌙" else "White mode enabled ☀️", Toast.LENGTH_SHORT).show()
                    }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.Notifications,
                    title = "Push notifications",
                    subtitle = "Interactions, mentions, and creator updates",
                    checked = pushNotifications,
                    testTag = "settings_notifications_switch",
                    onCheckedChange = { pushNotifications = it }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.PlayCircleOutline,
                    title = "Autoplay videos",
                    subtitle = "Smooth continuous playback on feed",
                    checked = autoplayOnWifi,
                    testTag = "settings_autoplay_switch",
                    onCheckedChange = { autoplayOnWifi = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 4. CACHE & CELLULAR DATA
            // ==========================================
            TikTokSettingsSection(title = "Cache & Cellular Data") {
                TikTokSettingsRow(
                    icon = Icons.Default.DeleteSweep,
                    title = "Free up space",
                    valueText = "${"%.1f".format(cacheSizeMb + downloadsSizeMb)} MB",
                    testTag = "settings_free_up_space",
                    onClick = {
                        cacheSizeMb = 0.0f
                        downloadsSizeMb = 0.0f
                        Toast.makeText(context, "Cache and temporary downloads cleared! 🧹", Toast.LENGTH_SHORT).show()
                    }
                )
                TikTokDivider()

                TikTokSwitchRow(
                    icon = Icons.Default.DataUsage,
                    title = "Data Saver",
                    subtitle = "Reduce cellular data usage when browsing video feeds",
                    checked = dataSaver,
                    testTag = "settings_data_saver_switch",
                    onCheckedChange = { dataSaver = it }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 5. SUPPORT & ABOUT
            // ==========================================
            TikTokSettingsSection(title = "Support & About") {
                TikTokSettingsRow(
                    icon = Icons.Default.Gavel,
                    title = "Islamic Community Guidelines",
                    testTag = "settings_community_guidelines",
                    onClick = { showIslamicGuidelinesDialog = true }
                )
                TikTokDivider()

                TikTokSettingsRow(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Report a problem",
                    testTag = "settings_report_problem",
                    onClick = {
                        Toast.makeText(context, "Support ticket opened. Baraka Allahu feek! ✉️", Toast.LENGTH_SHORT).show()
                    }
                )
                TikTokDivider()

                TikTokSettingsRow(
                    icon = Icons.Default.Info,
                    title = "About Freioh",
                    valueText = "v2.5.0 (Muslim Creators)",
                    testTag = "settings_about_version",
                    onClick = {
                        Toast.makeText(context, "Freioh: Ethical Muslim Social Video Platform · Build 88", Toast.LENGTH_LONG).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 6. LOGIN & ACCOUNT ACTIONS
            // ==========================================
            TikTokSettingsSection(title = "Login") {
                TikTokSettingsRow(
                    icon = Icons.Default.SwitchAccount,
                    title = "Switch account",
                    valueText = "Google · FB · 𝕏",
                    testTag = "settings_switch_account",
                    onClick = {
                        viewModel.openAuthModal()
                    }
                )
                TikTokDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true }
                        .padding(horizontal = 16.dp, vertical = 15.dp)
                        .testTag("settings_log_out_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Log out",
                        tint = Color(0xFFFF4D4D),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Log out",
                        color = Color(0xFFFF4D4D),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // =========================================================================
    // DIALOGS & BOTTOM SHEETS
    // =========================================================================

    // LOG OUT CONFIRMATION DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = colors.card,
            shape = RoundedCornerShape(14.dp),
            title = {
                Text(
                    text = "Log out of Freioh?",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "You can log back in at any time with your account.",
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.loginWith("Guest", "Guest Viewer")
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                ) {
                    Text("Log out", color = Color(0xFFFF4D4D), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }

    // LANGUAGE SELECTOR DIALOG
    if (showLanguageDialog) {
        val languages = listOf("English", "العربية (Arabic)", "Français (French)", "Türkçe (Turkish)", "Bahasa Indonesia", "Urdu (اردو)")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = colors.card,
            shape = RoundedCornerShape(14.dp),
            title = {
                Text(
                    text = "Select App Language",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    Toast.makeText(context, "Language updated to $lang", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang,
                                color = if (selectedLanguage.startsWith(lang.take(4))) TikTokCyan else colors.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = if (selectedLanguage.startsWith(lang.take(4))) FontWeight.Bold else FontWeight.Normal
                            )
                            if (selectedLanguage.startsWith(lang.take(4))) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = TikTokCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = colors.textPrimary)
                }
            }
        )
    }

    // MANAGE ACCOUNT DIALOG
    if (showManageAccountDialog) {
        AlertDialog(
            onDismissRequest = { showManageAccountDialog = false },
            containerColor = colors.card,
            shape = RoundedCornerShape(14.dp),
            title = {
                Text(
                    text = "Account Information",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("User: ${userProfile?.displayName ?: "Freioh Creator"}", color = colors.textPrimary, fontSize = 14.sp)
                    Text("Handle: ${userProfile?.handle ?: "@freioh"}", color = colors.textSecondary, fontSize = 13.sp)
                    Text("Email: ${userProfile?.email ?: "creator@freioh.com"}", color = colors.textSecondary, fontSize = 13.sp)
                    Text("Account Type: Muslim Creator / Community Verified", color = TikTokCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showManageAccountDialog = false }) {
                    Text("Done", color = TikTokCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ISLAMIC COMMUNITY GUIDELINES DIALOG
    if (showIslamicGuidelinesDialog) {
        AlertDialog(
            onDismissRequest = { showIslamicGuidelinesDialog = false },
            containerColor = colors.card,
            shape = RoundedCornerShape(14.dp),
            title = {
                Text(
                    text = "Islamic Community Ethics",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Freioh is built upon dignified Islamic principles:",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "1. Respect & Akhlaq: We maintain courteous discourse and forbid mocking, slander, or sectarian strife.\n" +
                                "2. Haya & Modesty: Content must observe modesty in dress, speech, and topics.\n" +
                                "3. Truthful Sharing: Spreading unverified rumors or fabricated hadiths is strictly prohibited.\n" +
                                "4. Beneficial Knowledge: Elevate content that inspires spiritual growth, education, charity, and brotherhood.",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showIslamicGuidelinesDialog = false }) {
                    Text("I Understand", color = TikTokCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // PRAYER TIMES DIALOG
    if (showPrayerTimesDialog) {
        AlertDialog(
            onDismissRequest = { showPrayerTimesDialog = false },
            containerColor = colors.card,
            shape = RoundedCornerShape(14.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccessTimeFilled, contentDescription = null, tint = TikTokCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Salah Times & Quiet Hours",
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val prayers = listOf(
                        "Fajr (Dawn)" to "04:45 AM",
                        "Dhuhr (Noon)" to "12:30 PM",
                        "Asr (Afternoon)" to "03:45 PM",
                        "Maghrib (Sunset)" to "06:20 PM",
                        "Isha (Night)" to "07:45 PM"
                    )
                    prayers.forEach { (name, time) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, color = colors.textPrimary, fontSize = 14.sp)
                            Text(time, color = TikTokCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Feed audio is smoothly muted 10 minutes before and during each prayer.",
                        color = colors.textSecondary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrayerTimesDialog = false }) {
                    Text("Done", color = TikTokCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    }
}

// =========================================================================
// TIKTOK SECTION & ROW REUSABLE COMPONENTS
// =========================================================================

data class SettingsColors(
    val bg: Color = Color(0xFF121212),
    val card: Color = Color(0xFF1E1E1E),
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(0xFF86878B),
    val divider: Color = Color(0xFF2A2A2A),
    val iconTint: Color = Color.White
)

val LocalSettingsColors = staticCompositionLocalOf { SettingsColors() }

@Composable
private fun TikTokSettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalSettingsColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = colors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.card)
        ) {
            content()
        }
    }
}

@Composable
private fun TikTokSettingsRow(
    icon: ImageVector,
    title: String,
    valueText: String? = null,
    testTag: String,
    onClick: () -> Unit
) {
    val colors = LocalSettingsColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (valueText != null) {
                Text(
                    text = valueText,
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = colors.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun TikTokSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalSettingsColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF20D5EC),
                uncheckedThumbColor = if (colors.textPrimary == Color.White) Color(0xFFAAAAAA) else Color(0xFF888888),
                uncheckedTrackColor = if (colors.textPrimary == Color.White) Color(0xFF2C2C2C) else Color(0xFFE2E2E2)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun TikTokDivider() {
    val colors = LocalSettingsColors.current
    HorizontalDivider(
        color = colors.divider,
        thickness = 0.6.dp,
        modifier = Modifier.padding(start = 50.dp, end = 16.dp)
    )
}
