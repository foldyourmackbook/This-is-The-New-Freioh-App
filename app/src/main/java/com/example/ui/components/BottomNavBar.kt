package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainTab

/**
 * Animated Black Capsule Navigation Bar with Glowing Lamp Wave Arch
 * Matches the interactive navigation tabs animation from the design reference.
 */
@Composable
fun FreiohBottomNavBar(
    currentTab: MainTab,
    unreadMessageCount: Int,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeIndex = when (currentTab) {
        MainTab.HOME -> 0
        MainTab.FRIENDS -> 1
        MainTab.CREATE -> 2
        MainTab.INBOX -> 3
        MainTab.PROFILE -> 4
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val slotWidthPx = totalWidthPx / 5f
            val targetCenterX = (activeIndex + 0.5f) * slotWidthPx

            // Physics-based spring animation for the sliding wave dome and spotlight
            val animatedCenterX by animateFloatAsState(
                targetValue = targetCenterX,
                animationSpec = spring(
                    dampingRatio = 0.74f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "arch_center_spring"
            )

            // Custom Canvas: draws the black capsule, fluid wave arch, red spotlight glow, and neon stroke
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val archH = 16.dp.toPx()
                val baseTop = archH
                val cornerRad = 26.dp.toPx()
                val archW = 58.dp.toPx()
                val halfArchW = archW / 2f
                val centerX = animatedCenterX
                val peakY = 2.dp.toPx()

                // 1. Base pill container in deep obsidian black
                drawRoundRect(
                    color = Color(0xFF111114),
                    topLeft = Offset(0f, baseTop),
                    size = Size(w, h - baseTop),
                    cornerRadius = CornerRadius(cornerRad, cornerRad)
                )

                // 2. Animated smooth wave dome arch rising over active tab
                val x1 = (centerX - halfArchW).coerceIn(0f, w)
                val x2 = (centerX + halfArchW).coerceIn(0f, w)

                val domePath = Path().apply {
                    moveTo(x1, baseTop)
                    cubicTo(
                        x1 + halfArchW * 0.44f, baseTop,
                        centerX - halfArchW * 0.38f, peakY,
                        centerX, peakY
                    )
                    cubicTo(
                        centerX + halfArchW * 0.38f, peakY,
                        x2 - halfArchW * 0.44f, baseTop,
                        x2, baseTop
                    )
                    lineTo(x1, baseTop)
                    close()
                }
                drawPath(
                    path = domePath,
                    color = Color(0xFF111114)
                )

                // 3. Glowing red lamp spotlight radiating down into the active tab
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xCCFF2B54),
                            Color(0x60FF2B54),
                            Color(0x18FF2B54),
                            Color.Transparent
                        ),
                        center = Offset(centerX, peakY + 14.dp.toPx()),
                        radius = 40.dp.toPx()
                    ),
                    radius = 40.dp.toPx(),
                    center = Offset(centerX, peakY + 14.dp.toPx())
                )

                // 4. Subtle sleek dark border around the capsule base
                drawRoundRect(
                    color = Color(0xFF26262E),
                    topLeft = Offset(0f, baseTop),
                    size = Size(w, h - baseTop),
                    cornerRadius = CornerRadius(cornerRad, cornerRad),
                    style = Stroke(width = 1.dp.toPx())
                )

                // 5. Neon red wave crest / arch stroke (the signature wave outline)
                val archCurveOnly = Path().apply {
                    moveTo(x1, baseTop)
                    cubicTo(
                        x1 + halfArchW * 0.44f, baseTop,
                        centerX - halfArchW * 0.38f, peakY,
                        centerX, peakY
                    )
                    cubicTo(
                        centerX + halfArchW * 0.38f, peakY,
                        x2 - halfArchW * 0.44f, baseTop,
                        x2, baseTop
                    )
                }

                // Ambient outer red glow
                drawPath(
                    path = archCurveOnly,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x22FF2B54),
                            Color(0x88FF2B54),
                            Color(0xFFFF2B54),
                            Color(0x88FF2B54),
                            Color(0x22FF2B54)
                        ),
                        startX = x1,
                        endX = x2
                    ),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Sharp radiant neon stroke
                drawPath(
                    path = archCurveOnly,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x33FF2B54),
                            Color(0xFFFF2B54),
                            Color(0xFFFF5277),
                            Color(0xFFFF2B54),
                            Color(0x33FF2B54)
                        ),
                        startX = x1,
                        endX = x2
                    ),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Interactive Row with the 5 Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home
                AnimatedTabItem(
                    label = "Home",
                    isActive = activeIndex == 0,
                    activeIcon = Icons.Filled.Home,
                    inactiveIcon = Icons.Outlined.Home,
                    testTag = "nav_home",
                    onClick = { onTabSelected(MainTab.HOME) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 1: Search
                AnimatedTabItem(
                    label = "Search",
                    isActive = activeIndex == 1,
                    activeIcon = Icons.Filled.Search,
                    inactiveIcon = Icons.Outlined.Search,
                    testTag = "nav_search",
                    onClick = { onTabSelected(MainTab.FRIENDS) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Create (Center button - clean plus icon matching screenshot)
                AnimatedTabItem(
                    label = "Create",
                    isActive = activeIndex == 2,
                    activeIcon = Icons.Filled.Add,
                    inactiveIcon = Icons.Filled.Add,
                    testTag = "nav_create",
                    onClick = { onTabSelected(MainTab.CREATE) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 3: Inbox
                AnimatedTabItem(
                    label = "Inbox",
                    isActive = activeIndex == 3,
                    activeIcon = Icons.Filled.ChatBubble,
                    inactiveIcon = Icons.Outlined.ChatBubbleOutline,
                    badgeCount = unreadMessageCount,
                    testTag = "nav_inbox",
                    onClick = { onTabSelected(MainTab.INBOX) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 4: Profile (Person silhouette icon)
                AnimatedTabItem(
                    label = "Profile",
                    isActive = activeIndex == 4,
                    activeIcon = Icons.Filled.Person,
                    inactiveIcon = Icons.Outlined.Person,
                    testTag = "nav_profile",
                    onClick = {
                        onTabSelected(MainTab.PROFILE)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AnimatedTabItem(
    label: String,
    isActive: Boolean,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Lift active tab slightly upwards into the glowing wave dome
    val iconOffsetY by animateDpAsState(
        targetValue = if (isActive) (-4).dp else 8.dp,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tab_offset_y"
    )

    // Smooth bounce scale for active tab
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.14f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tab_scale"
    )

    // Color transition from crisp white to radiant neon red
    val contentColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFFFF2B54) else Color(0xFFE2E2E6),
        label = "tab_color"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(y = iconOffsetY)
                .scale(scale)
        ) {
            if (badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Color(0xFFFF2B54),
                            contentColor = Color.White,
                            modifier = Modifier.offset(x = 6.dp, y = (-2).dp)
                        ) {
                            Text(
                                text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isActive) activeIcon else inactiveIcon,
                        contentDescription = label,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = if (isActive) activeIcon else inactiveIcon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = contentColor,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
