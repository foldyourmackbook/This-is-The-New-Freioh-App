package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FacebookBlue
import com.example.ui.viewmodel.FreiohViewModel

enum class HuddleMood {
    IDLE,
    LOOK_EMAIL,
    SHY_PASSWORD
}

/**
 * Interactive Huddle Login Screen / Card with Animated Characters
 * Exactly replicating the component from the user's video reference:
 * - Characters look at you/email input
 * - Characters look away & close eyes when typing password! ("Type your password. Watch them look away.")
 * - Complete with Google, Facebook, and X login buttons.
 */
@Composable
fun HuddleInteractiveLogin(
    viewModel: FreiohViewModel? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var emailText by remember { mutableStateOf("hasib@gmail.com") }
    var passwordText by remember { mutableStateOf("hasib123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRememberMeChecked by remember { mutableStateOf(true) }

    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    val mood = when {
        isPasswordFocused -> HuddleMood.SHY_PASSWORD
        isEmailFocused -> HuddleMood.LOOK_EMAIL
        passwordText.isNotEmpty() && !isEmailFocused -> HuddleMood.SHY_PASSWORD
        else -> HuddleMood.IDLE
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F13))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP APP BAR / DISMISS - Clean Native Mobile Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E26))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Log in to Freioh",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (viewModel != null) {
                IconButton(
                    onClick = {
                        onDismiss?.invoke()
                        viewModel.openSettings()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E26))
                        .testTag("login_settings_gear_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // MAIN LOGIN CARD CONTAINER (White rounded card from video)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ANIMATED CHARACTERS HEADER SECTION
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF6F6F9))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    HuddleCharactersStage(
                        mood = mood,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // INTERACTIVE HINT & DEMO CREDENTIAL CHIPS
                Text(
                    text = if (mood == HuddleMood.SHY_PASSWORD) {
                        "🙈 They look away when you type your password!"
                    } else {
                        "Type your password. Watch them look away."
                    },
                    color = if (mood == HuddleMood.SHY_PASSWORD) Color(0xFFE03131) else Color(0xFF495057),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp, bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoChip(text = "hasib@gmail.com") {
                        emailText = "hasib@gmail.com"
                        isEmailFocused = true
                        isPasswordFocused = false
                    }
                    DemoChip(text = "hasib123") {
                        passwordText = "hasib123"
                        isPasswordFocused = true
                        isEmailFocused = false
                    }
                }

                // WELCOME TITLE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(text = "✦", color = Color(0xFF1E1E22), fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Welcome back",
                        color = Color(0xFF1E1E22),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Please enter your details.",
                    color = Color(0xFF868E96),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // EMAIL INPUT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        color = Color(0xFF343A40),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = emailText,
                        onValueChange = { emailText = it },
                        placeholder = { Text("Enter your email", color = Color(0xFFADB5BD), fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E1E22),
                            unfocusedTextColor = Color(0xFF1E1E22),
                            focusedBorderColor = Color(0xFF7048E8),
                            unfocusedBorderColor = Color(0xFFDEE2E6),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF8F9FA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isEmailFocused = it.isFocused }
                            .testTag("huddle_email_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PASSWORD INPUT
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password",
                        color = Color(0xFF343A40),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        placeholder = { Text("Enter your password", color = Color(0xFFADB5BD), fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF868E96)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E1E22),
                            unfocusedTextColor = Color(0xFF1E1E22),
                            focusedBorderColor = Color(0xFFE03131),
                            unfocusedBorderColor = Color(0xFFDEE2E6),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF8F9FA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isPasswordFocused = it.isFocused }
                            .testTag("huddle_password_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // REMEMBER ME & FORGOT PASSWORD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isRememberMeChecked = !isRememberMeChecked }
                    ) {
                        Checkbox(
                            checked = isRememberMeChecked,
                            onCheckedChange = { isRememberMeChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1E1E22),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Remember for 30 days",
                            color = Color(0xFF495057),
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "Forgot password?",
                        color = Color(0xFF7048E8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Password reset link sent to $emailText", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PRIMARY LOG IN BUTTON (Black pill)
                Button(
                    onClick = {
                        val name = emailText.substringBefore("@").ifBlank { "User" }
                        viewModel?.loginWith("Huddle", name)
                        Toast.makeText(context, "Welcome back, $name! 🎉", Toast.LENGTH_SHORT).show()
                        onDismiss?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E1E22),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("huddle_login_btn")
                ) {
                    Text(
                        text = "Log in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // OR DIVIDER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE9ECEF))
                    Text(
                        text = "or",
                        color = Color(0xFFADB5BD),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE9ECEF))
                }

                // SOCIAL LOGINS: GOOGLE, FACEBOOK, X
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // GOOGLE LOGIN BUTTON
                    SocialAuthButton(
                        text = "Log in with Google",
                        icon = { GoogleLetterGIcon() },
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E1E22),
                        borderColor = Color(0xFFDEE2E6),
                        testTag = "login_with_google_btn",
                        onClick = {
                            viewModel?.loginWith("Google", "Google User")
                            Toast.makeText(context, "Logged in with Google! 🌐", Toast.LENGTH_SHORT).show()
                            onDismiss?.invoke()
                        }
                    )

                    // FACEBOOK LOGIN BUTTON (User requested)
                    SocialAuthButton(
                        text = "Log in with Facebook",
                        icon = { FacebookIconLetterF() },
                        containerColor = FacebookBlue,
                        contentColor = Color.White,
                        borderColor = FacebookBlue,
                        testTag = "login_with_facebook_btn",
                        onClick = {
                            viewModel?.loginWith("Facebook", "Facebook User")
                            Toast.makeText(context, "Logged in with Facebook! 🔵", Toast.LENGTH_SHORT).show()
                            onDismiss?.invoke()
                        }
                    )

                    // X (TWITTER) LOGIN BUTTON (User requested)
                    SocialAuthButton(
                        text = "Log in with X",
                        icon = { XLogoIcon() },
                        containerColor = Color(0xFF000000),
                        contentColor = Color.White,
                        borderColor = Color(0xFF333333),
                        testTag = "login_with_x_btn",
                        onClick = {
                            viewModel?.loginWith("X", "X User")
                            Toast.makeText(context, "Logged in with X! 𝕏", Toast.LENGTH_SHORT).show()
                            onDismiss?.invoke()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SIGN UP FOOTER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = Color(0xFF868E96),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Sign up",
                        color = Color(0xFF7048E8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Sign up sheet opened", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Animated Vector Stage for the 4 Characters:
 * 1. Purple Slanted Box
 * 2. Orange Freckled Dome
 * 3. Tall Black Eyed Pill
 * 4. Yellow Curved Pill
 */
@Composable
private fun HuddleCharactersStage(
    mood: HuddleMood,
    modifier: Modifier = Modifier
) {
    // Springs for physics-based eye movement, bend, and rotation
    val isShy = mood == HuddleMood.SHY_PASSWORD
    val isLooking = mood == HuddleMood.LOOK_EMAIL

    // Black pill pupil shift:
    // When shy: look away to bottom left (-12px, 8px) or close eyes!
    // When looking at email: look right (10px, 2px)
    // Idle: center (0px, 0px)
    val targetPupilX = when {
        isShy -> -14f
        isLooking -> 10f
        else -> 0f
    }
    val targetPupilY = when {
        isShy -> 8f
        isLooking -> 2f
        else -> 0f
    }

    val animatedPupilX by animateFloatAsState(
        targetValue = targetPupilX,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
        label = "pupilX"
    )
    val animatedPupilY by animateFloatAsState(
        targetValue = targetPupilY,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
        label = "pupilY"
    )

    // Characters rotation / lean
    val purpleRotation by animateFloatAsState(
        targetValue = if (isShy) -24f else if (isLooking) -4f else -12f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "purpleRot"
    )
    val blackRotation by animateFloatAsState(
        targetValue = if (isShy) -14f else if (isLooking) 4f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "blackRot"
    )
    val orangeSquish by animateFloatAsState(
        targetValue = if (isShy) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "orangeSquish"
    )

    // Eyelid / Shyness factor (0f = open, 1f = shut)
    val eyelidShutFactor by animateFloatAsState(
        targetValue = if (isShy) 1.0f else 0.0f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "eyelid"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val groundY = h - 2.dp.toPx()

        val centerX = w * 0.46f

        // 1. PURPLE SLANTED RECTANGLE (Left back)
        drawPurpleMate(
            centerX = centerX - 60.dp.toPx(),
            groundY = groundY,
            rotation = purpleRotation,
            isShy = isShy,
            pupilX = animatedPupilX * 0.6f
        )

        // 2. TALL BLACK PILL WITH BIG EYES (Center back)
        drawTallBlackMate(
            centerX = centerX - 6.dp.toPx(),
            groundY = groundY,
            rotation = blackRotation,
            pupilX = animatedPupilX,
            pupilY = animatedPupilY,
            eyelidFactor = eyelidShutFactor,
            isShy = isShy
        )

        // 3. YELLOW PILL (Right back)
        drawYellowMate(
            centerX = centerX + 46.dp.toPx(),
            groundY = groundY,
            isShy = isShy,
            pupilX = animatedPupilX * 0.7f
        )

        // 4. ORANGE DOME (Front left foreground)
        drawOrangeDome(
            centerX = centerX - 48.dp.toPx(),
            groundY = groundY,
            squish = orangeSquish,
            isShy = isShy,
            pupilX = animatedPupilX * 0.5f
        )
    }
}

private fun DrawScope.drawPurpleMate(
    centerX: Float,
    groundY: Float,
    rotation: Float,
    isShy: Boolean,
    pupilX: Float
) {
    val cardW = 52.dp.toPx()
    val cardH = 78.dp.toPx()

    rotate(degrees = rotation, pivot = Offset(centerX, groundY)) {
        // Body: Rounded tilted box
        drawRoundRect(
            color = Color(0xFF6741D9),
            topLeft = Offset(centerX - cardW / 2, groundY - cardH),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        )

        // Face: Two eyes
        val eyeY = groundY - cardH + 24.dp.toPx()
        val eyeSpacing = 14.dp.toPx()
        val eyeRadius = 3.5.dp.toPx()

        if (isShy) {
            // Closed / shy slit eyes
            drawLine(
                color = Color(0xFF2B0A6B),
                start = Offset(centerX - eyeSpacing / 2 - 4.dp.toPx(), eyeY),
                end = Offset(centerX - eyeSpacing / 2 + 4.dp.toPx(), eyeY),
                strokeWidth = 2.5.dp.toPx()
            )
            drawLine(
                color = Color(0xFF2B0A6B),
                start = Offset(centerX + eyeSpacing / 2 - 4.dp.toPx(), eyeY),
                end = Offset(centerX + eyeSpacing / 2 + 4.dp.toPx(), eyeY),
                strokeWidth = 2.5.dp.toPx()
            )
        } else {
            // Open dot eyes
            drawCircle(
                color = Color(0xFF1E074D),
                radius = eyeRadius,
                center = Offset(centerX - eyeSpacing / 2 + pupilX, eyeY)
            )
            drawCircle(
                color = Color(0xFF1E074D),
                radius = eyeRadius,
                center = Offset(centerX + eyeSpacing / 2 + pupilX, eyeY)
            )
        }

        // Tiny cute mouth
        drawCircle(
            color = Color(0xFF1E074D),
            radius = 2.5.dp.toPx(),
            center = Offset(centerX + if (isShy) -4f else 0f, eyeY + 14.dp.toPx())
        )
    }
}

private fun DrawScope.drawTallBlackMate(
    centerX: Float,
    groundY: Float,
    rotation: Float,
    pupilX: Float,
    pupilY: Float,
    eyelidFactor: Float,
    isShy: Boolean
) {
    val pillW = 46.dp.toPx()
    val pillH = 100.dp.toPx()

    rotate(degrees = rotation, pivot = Offset(centerX, groundY)) {
        // Tall black capsule body
        drawRoundRect(
            color = Color(0xFF19191C),
            topLeft = Offset(centerX - pillW / 2, groundY - pillH),
            size = Size(pillW, pillH),
            cornerRadius = CornerRadius(pillW / 2, pillW / 2)
        )

        // BIG EXPRESSIVE WHITE EYES
        val eyeRadius = 9.dp.toPx()
        val eyeY = groundY - pillH + 28.dp.toPx()
        val eyeLeftX = centerX - 10.dp.toPx()
        val eyeRightX = centerX + 10.dp.toPx()

        // Left white sclera
        drawCircle(
            color = Color.White,
            radius = eyeRadius,
            center = Offset(eyeLeftX, eyeY)
        )
        // Right white sclera
        drawCircle(
            color = Color.White,
            radius = eyeRadius,
            center = Offset(eyeRightX, eyeY)
        )

        // Pupils
        val pupilRadius = 4.2.dp.toPx()
        drawCircle(
            color = Color(0xFF19191C),
            radius = pupilRadius,
            center = Offset(eyeLeftX + pupilX, eyeY + pupilY)
        )
        drawCircle(
            color = Color(0xFF19191C),
            radius = pupilRadius,
            center = Offset(eyeRightX + pupilX, eyeY + pupilY)
        )

        // Pupil highlights (reflection catch-lights)
        drawCircle(
            color = Color.White,
            radius = 1.6.dp.toPx(),
            center = Offset(eyeLeftX + pupilX - 1.5.dp.toPx(), eyeY + pupilY - 1.5.dp.toPx())
        )
        drawCircle(
            color = Color.White,
            radius = 1.6.dp.toPx(),
            center = Offset(eyeRightX + pupilX - 1.5.dp.toPx(), eyeY + pupilY - 1.5.dp.toPx())
        )

        // Eyelid overlay (comes down when shy or looking away!)
        if (eyelidFactor > 0.05f) {
            val lidHeight = eyeRadius * 2 * eyelidFactor
            drawRoundRect(
                color = Color(0xFF19191C),
                topLeft = Offset(eyeLeftX - eyeRadius, eyeY - eyeRadius),
                size = Size(eyeRadius * 2, lidHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF19191C),
                topLeft = Offset(eyeRightX - eyeRadius, eyeY - eyeRadius),
                size = Size(eyeRadius * 2, lidHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Shy curved lash line
            if (eyelidFactor > 0.8f) {
                drawArc(
                    color = Color.White.copy(alpha = 0.7f),
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(eyeLeftX - eyeRadius + 1.dp.toPx(), eyeY - 2.dp.toPx()),
                    size = Size((eyeRadius - 1.dp.toPx()) * 2, (eyeRadius - 1.dp.toPx()) * 2),
                    style = Stroke(width = 1.8.dp.toPx())
                )
                drawArc(
                    color = Color.White.copy(alpha = 0.7f),
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(eyeRightX - eyeRadius + 1.dp.toPx(), eyeY - 2.dp.toPx()),
                    size = Size((eyeRadius - 1.dp.toPx()) * 2, (eyeRadius - 1.dp.toPx()) * 2),
                    style = Stroke(width = 1.8.dp.toPx())
                )
            }
        }
    }
}

private fun DrawScope.drawYellowMate(
    centerX: Float,
    groundY: Float,
    isShy: Boolean,
    pupilX: Float
) {
    val pillW = 44.dp.toPx()
    val pillH = 74.dp.toPx()

    // Half pill yellow body
    drawRoundRect(
        color = Color(0xFFFCC419),
        topLeft = Offset(centerX - pillW / 2, groundY - pillH),
        size = Size(pillW, pillH),
        cornerRadius = CornerRadius(pillW / 2, pillW / 2)
    )

    // Face features
    val eyeY = groundY - pillH + 26.dp.toPx()
    val eyeRadius = 3.2.dp.toPx()

    if (isShy) {
        // Slit eye looking left/away
        drawLine(
            color = Color(0xFF5C4400),
            start = Offset(centerX - 8.dp.toPx(), eyeY),
            end = Offset(centerX - 2.dp.toPx(), eyeY),
            strokeWidth = 2.5.dp.toPx()
        )
    } else {
        drawCircle(
            color = Color(0xFF5C4400),
            radius = eyeRadius,
            center = Offset(centerX - 5.dp.toPx() + pupilX, eyeY)
        )
    }

    // Straight line mouth
    drawLine(
        color = Color(0xFF5C4400),
        start = Offset(centerX - 12.dp.toPx(), eyeY + 16.dp.toPx()),
        end = Offset(centerX + 6.dp.toPx(), eyeY + 16.dp.toPx()),
        strokeWidth = 2.5.dp.toPx()
    )
}

private fun DrawScope.drawOrangeDome(
    centerX: Float,
    groundY: Float,
    squish: Float,
    isShy: Boolean,
    pupilX: Float
) {
    val domeW = 92.dp.toPx()
    val domeH = (52.dp.toPx()) * squish

    // Semi-circle / dome path
    val path = Path().apply {
        moveTo(centerX - domeW / 2, groundY)
        cubicTo(
            centerX - domeW / 2, groundY - domeH * 1.35f,
            centerX + domeW / 2, groundY - domeH * 1.35f,
            centerX + domeW / 2, groundY
        )
        close()
    }
    drawPath(path = path, color = Color(0xFFFF6B4A))

    // Two black eyes
    val eyeY = groundY - domeH * 0.65f
    val eyeLeftX = centerX - 12.dp.toPx()
    val eyeRightX = centerX + 12.dp.toPx()

    if (isShy) {
        // Cute sleeping / shy curved eyes `⌣ ⌣`
        drawArc(
            color = Color(0xFF6B1D0B),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(eyeLeftX - 4.dp.toPx(), eyeY - 2.dp.toPx()),
            size = Size(8.dp.toPx(), 7.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
        drawArc(
            color = Color(0xFF6B1D0B),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(eyeRightX - 4.dp.toPx(), eyeY - 2.dp.toPx()),
            size = Size(8.dp.toPx(), 7.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
    } else {
        drawCircle(
            color = Color(0xFF6B1D0B),
            radius = 3.5.dp.toPx(),
            center = Offset(eyeLeftX + pupilX, eyeY)
        )
        drawCircle(
            color = Color(0xFF6B1D0B),
            radius = 3.5.dp.toPx(),
            center = Offset(eyeRightX + pupilX, eyeY)
        )
    }

    // 3 Freckle / mouth dots below eyes (as seen in video: • • •)
    val dotY = eyeY + 9.dp.toPx()
    drawCircle(color = Color(0xFF6B1D0B), radius = 1.8.dp.toPx(), center = Offset(centerX - 6.dp.toPx(), dotY))
    drawCircle(color = Color(0xFF6B1D0B), radius = 1.8.dp.toPx(), center = Offset(centerX, dotY))
    drawCircle(color = Color(0xFF6B1D0B), radius = 1.8.dp.toPx(), center = Offset(centerX + 6.dp.toPx(), dotY))
}

@Composable
private fun DemoChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE9ECEF))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF495057),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SocialAuthButton(
    text: String,
    icon: @Composable () -> Unit,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GoogleLetterGIcon() {
    Text(
        text = "G",
        color = Color(0xFF4285F4),
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.SansSerif
    )
}

@Composable
private fun FacebookIconLetterF() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "f",
            color = FacebookBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun XLogoIcon() {
    Text(
        text = "𝕏",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}
