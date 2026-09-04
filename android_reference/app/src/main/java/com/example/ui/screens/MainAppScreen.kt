package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.FreedomGoal
import com.example.data.VictoryLog
import com.example.data.ChatMessage
import com.example.data.SavedChat
import com.example.ui.OverComerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Speech-to-Text, Text-to-Speech, Permissions, and Dialog imports
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

fun cleanTextForSpeech(text: String): String {
    if (text.isBlank()) return text
    var clean = text
    // Replace slash, long dash, or bullets with space first to prevent word concatenation
    clean = clean.replace("—", " ")
    clean = clean.replace("-", " ")
    clean = clean.replace("/", " ")
    clean = clean.replace("•", " ")
    
    // Retain only letters, numbers, spaces, and natural reading punctuation
    clean = clean.replace(Regex("[^\\p{L}\\p{N}\\s.,!?'\"():;]"), "")
    
    // Clean up multiple spaces
    clean = clean.replace(Regex("\\s+"), " ").trim()
    return clean
}

enum class ActiveTab {
    FREEDOM, INSPIRATIONAL, CHAT, JOURNAL, BIBLE
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FocusSelectionScreen(
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AppCoverBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Text(
                text = "Welcome to Overcomer—where we live from Christ’s position of Victory",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, lineHeight = 26.sp),
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "This App is built to lift up those fighting addiction or mental health struggles, assist veterans processing service, or support individuals overcoming the weight of past incarceration. It is equally a refuge for anyone who doesn't face these specific battles but is simply having a rough day and needs a lift. Out of every struggle comes a story.\n\nStep into your focus path, claim your peace, or simply log in to share your testimony and Victory Day.",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect("TESTIMONY_VICTORY") }
                    .testTag("victory_board_link_card"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFFA000))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFA000).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Victory Board link",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tap here to view testimonies of other OverComers Here",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE65100)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Go to Victory Board",
                        tint = Color(0xFFFFA000)
                    )
                }
            }

            Text(
                text = "Please select a focus path below to customize your experience:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            // Rearranged order: 1. All Around Tough Day, 2. Substance Recovery, 3. Mental Health Wellness
            // Option 1: All Around Tough Day
            FocusOptionCard(
                title = "All Around Tough Day",
                description = "Direct encouragement, simplified calming breathing, and a compassionate space to vent your stress instantly.",
                icon = Icons.Default.Cloud,
                color = Color(0xFFD32F2F),
                onClick = { onSelect("TOUGH_DAY") },
                testTag = "focus_tough_day_card"
            )

            // Option 2: Substance Recovery
            FocusOptionCard(
                title = "Substance Recovery",
                description = "Our core Christian choice-based freedom theology, sobriety tracker, and relapse prevention thought reframing logs.",
                icon = Icons.Default.Shield,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onSelect("SUBSTANCE_RECOVERY") },
                testTag = "focus_substance_recovery_card"
            )

            // Option 3: Mental Health Wellness
            FocusOptionCard(
                title = "Mental Health Wellness",
                description = "Peace scriptures, anxiety logs, emotional distress resources, and beautiful breath exercises.",
                icon = Icons.Default.FavoriteBorder,
                color = Color(0xFF00796B),
                onClick = { onSelect("MENTAL_HEALTH") },
                testTag = "focus_mental_health_card"
            )

            // Option 4: The Next Mission: Veteran Support & Wellness
            FocusOptionCard(
                title = "The Next Mission: Veteran Support & Wellness",
                description = "You were trained to endure the hardest battles, but you don't have to fight the invisible ones alone. Whether you are navigating the transition to civilian life, processing the weight of your service, or simply having a tough day, find faith-based tools and a community that understands. Seeking support isn't stepping down—it's stepping up. Click here for specialized veteran resources and daily strength.",
                icon = Icons.Default.Star,
                color = Color(0xFF1B5E20), // Dark military green
                onClick = { onSelect("VETERAN_TRANSITION") },
                testTag = "focus_veteran_transition_card"
            )

            // Option 5: Steps to Restoration: Life and Leadership After Incarceration
            FocusOptionCard(
                title = "Steps to Restoration: Life and Leadership After Incarceration",
                description = "Your past does not dictate your destiny; Christ does. Moving forward requires strength, patience, and a strong support network. This space provides daily encouragement, targeted scripture, and a brotherhood of believers who understand the road you are walking. Your story isn't over—it’s just getting started. Click here to discover your value, your worth, and your next steps toward victory.",
                icon = Icons.Default.Refresh,
                color = Color(0xFF3F51B5), // Deep royal blue
                onClick = { onSelect("REENTRY_RESTORATION") },
                testTag = "focus_reentry_restoration_card"
            )

            // Option 6: Today is a Testimony/Victory Day
            FocusOptionCard(
                title = "Today is a Testimony/Victory Day",
                description = "Celebrate what God has done! Enjoy victorious scriptures, battle-winning quotes, and share your triumphs with your OverComer companion.",
                icon = Icons.Default.Star,
                color = Color(0xFFFFA000),
                onClick = { onSelect("TESTIMONY_VICTORY") },
                testTag = "focus_testimony_victory_card"
            )
        }
    }
}

@Composable
fun FocusOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun AppCoverBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // 1. Draw Sunset Sky Gradient
                val sunsetBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E1C47), // Deep purple night sky
                        Color(0xFF6B2D5C), // Warm rich magenta
                        Color(0xFFD84A5A), // Soft peach pink
                        Color(0xFFFF9E79)  // Warm horizon glow
                    )
                )
                drawRect(brush = sunsetBrush)

                // 2. Draw Sun rising/setting at the bottom center horizon
                val sunX = width * 0.5f
                val sunY = height * 0.75f
                val sunRadius = height * 0.18f
                
                // Outer soft glow
                drawCircle(
                    color = Color(0xFFFFEB3B).copy(alpha = 0.25f),
                    radius = sunRadius * 1.8f,
                    center = Offset(sunX, sunY)
                )
                // Inner bright sun core
                drawCircle(
                    color = Color(0xFFFFFDE7).copy(alpha = 0.8f),
                    radius = sunRadius,
                    center = Offset(sunX, sunY)
                )

                // 3. Draw Shore/Beach Ground Silhouette at the bottom
                val groundPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, height * 0.8f)
                    quadraticTo(
                        width * 0.35f, height * 0.76f,
                        width * 0.7f, height * 0.82f
                    )
                    quadraticTo(
                        width * 0.85f, height * 0.85f,
                        width, height * 0.78f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = groundPath,
                    color = Color(0xFF160E22) // Near-black dark sand silhouette
                )

                // 4. Draw Reflection on the Wet Sand / Waves
                val waveColor = Color(0xFFFFF8E1).copy(alpha = 0.2f)
                val wavePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, height * 0.82f)
                    quadraticTo(
                        width * 0.4f, height * 0.80f,
                        width * 0.75f, height * 0.84f
                    )
                    quadraticTo(
                        width * 0.9f, height * 0.86f,
                        width, height * 0.80f
                    )
                    lineTo(width, height * 0.82f)
                    quadraticTo(
                        width * 0.88f, height * 0.88f,
                        width * 0.73f, height * 0.86f
                    )
                    quadraticTo(
                        width * 0.38f, height * 0.83f,
                        0f, height * 0.85f
                    )
                    close()
                }
                drawPath(path = wavePath, color = waveColor)

                // 5. Draw Victory Silhouette of the Man (centered at sunX)
                val personHeight = height * 0.38f
                val groundY = height * 0.78f
                val headY = groundY - personHeight
                val headRadius = personHeight * 0.12f
                val headCenter = Offset(sunX, headY + headRadius)

                // Draw Head
                drawCircle(
                    color = Color(0xFF160E22),
                    radius = headRadius,
                    center = headCenter
                )
                // Hoody extra outline
                drawCircle(
                    color = Color(0xFF160E22),
                    radius = headRadius * 1.2f,
                    center = headCenter,
                    style = Stroke(width = 3f)
                )

                // Torso
                val shoulderY = headCenter.y + headRadius * 1.2f
                val waistY = groundY - personHeight * 0.4f
                val torsoPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX - headRadius * 1.4f, shoulderY)
                    lineTo(sunX + headRadius * 1.4f, shoulderY)
                    lineTo(sunX + headRadius * 1.1f, waistY)
                    lineTo(sunX - headRadius * 1.1f, waistY)
                    close()
                }
                drawPath(path = torsoPath, color = Color(0xFF160E22))

                // Legs stand
                val legWidth = headRadius * 0.5f
                val leftFootX = sunX - headRadius * 0.9f
                val rightFootX = sunX + headRadius * 0.9f

                // Left leg
                val leftLegPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX - headRadius * 0.9f, waistY)
                    lineTo(sunX - headRadius * 0.2f, waistY)
                    lineTo(sunX - legWidth * 0.5f, groundY)
                    lineTo(leftFootX, groundY)
                    close()
                }
                drawPath(path = leftLegPath, color = Color(0xFF160E22))

                // Right leg
                val rightLegPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX + headRadius * 0.2f, waistY)
                    lineTo(sunX + headRadius * 0.9f, waistY)
                    lineTo(rightFootX, groundY)
                    lineTo(sunX + legWidth * 0.5f, groundY)
                    close()
                }
                drawPath(path = rightLegPath, color = Color(0xFF160E22))

                // Outstretched arms (Wide Open in T-pose representing victory)
                val leftArmPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX - headRadius * 1.2f, shoulderY + 2f)
                    val handX = sunX - personHeight * 0.55f
                    val handY = shoulderY - personHeight * 0.08f
                    lineTo(handX, handY)
                    lineTo(handX - 5f, handY - 12f)
                    lineTo(handX + 3f, handY + 15f)
                    lineTo(sunX - headRadius * 1.2f, shoulderY + headRadius * 1.3f)
                    close()
                }
                drawPath(path = leftArmPath, color = Color(0xFF160E22))

                val rightArmPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX + headRadius * 1.2f, shoulderY + 2f)
                    val handX = sunX + personHeight * 0.55f
                    val handY = shoulderY - personHeight * 0.08f
                    lineTo(handX, handY)
                    lineTo(handX + 5f, handY - 12f)
                    lineTo(handX - 3f, handY + 15f)
                    lineTo(sunX + headRadius * 1.2f, shoulderY + headRadius * 1.3f)
                    close()
                }
                drawPath(path = rightArmPath, color = Color(0xFF160E22))
            }

            // Write "I AM a OverComer" across the banner with "Rev. 12:11" right under it
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "I AM a OverComer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(2f, 2f),
                            blurRadius = 6f
                        )
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rev. 12:11",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(1.5f, 1.5f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: OverComerViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ActiveTab.FREEDOM) }
    val focusManager = LocalFocusManager.current
    var showPanicOverlay by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showApiSettingsDialog by remember { mutableStateOf(false) }

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isFirebaseLive by viewModel.isFirebaseLive.collectAsStateWithLifecycle()
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()

    if (userPath.isNullOrEmpty()) {
        FocusSelectionScreen(onSelect = { viewModel.selectUserPath(it) })
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        TextButton(
                            onClick = { showPanicOverlay = true },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .testTag("panic_sos_top_left_btn"),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "🚨 SOS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "OverComer",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, letterSpacing = 1.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    actions = {
                        AssistChip(
                            onClick = { viewModel.selectUserPath("") },
                            label = {
                                Text(
                                    text = when (userPath) {
                                        "SUBSTANCE_RECOVERY" -> "Recovery"
                                        "MENTAL_HEALTH" -> "Wellness"
                                        "TOUGH_DAY" -> "Tough Day"
                                        "VETERAN_TRANSITION" -> "Veteran"
                                        "REENTRY_RESTORATION" -> "Restoration"
                                        else -> "Select"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            modifier = Modifier.padding(end = 6.dp).testTag("change_focus_chip")
                        )
                        IconButton(
                            onClick = { showAuthDialog = true },
                            modifier = Modifier.testTag("auth_profile_button")
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Account credentials and sync statistics",
                                    tint = if (isLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isLoggedIn) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = if (isFirebaseLive) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                                shape = CircleShape
                                            )
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { showApiSettingsDialog = true },
                            modifier = Modifier.testTag("api_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Secure AI API Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("app_top_bar")
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(86.dp).testTag("app_bottom_bar")
                ) {
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.FREEDOM,
                        onClick = { activeTab = ActiveTab.FREEDOM; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Freedom home") },
                        label = { Text("Freedom", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_freedom")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.INSPIRATIONAL,
                        onClick = { activeTab = ActiveTab.INSPIRATIONAL; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.FormatQuote, contentDescription = "Inspirational Quotes") },
                        label = { Text("Inspiration", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_inspirational")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.CHAT,
                        onClick = { activeTab = ActiveTab.CHAT; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.Send, contentDescription = "Overcomer’s Companion") },
                        label = { 
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Overcomer’s",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        lineHeight = 10.5.sp,
                                        fontWeight = if (activeTab == ActiveTab.CHAT) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = "Companion",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        lineHeight = 10.5.sp,
                                        fontWeight = if (activeTab == ActiveTab.CHAT) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        },
                        modifier = Modifier.testTag("nav_tab_chat")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.JOURNAL,
                        onClick = { activeTab = ActiveTab.JOURNAL; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.List, contentDescription = "Victory logs") },
                        label = { Text("Victory Logs", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_journal")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.BIBLE,
                        onClick = { activeTab = ActiveTab.BIBLE; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Bible App") },
                        label = { Text("Bible", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_bible")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (activeTab) {
                    ActiveTab.FREEDOM -> FreedomTabScreen(viewModel, onNavigateToChat = { activeTab = ActiveTab.CHAT })
                    ActiveTab.INSPIRATIONAL -> InspirationalQuotesTabScreen(viewModel)
                    ActiveTab.CHAT -> ChatTabScreen(viewModel)
                    ActiveTab.JOURNAL -> JournalLogsTabScreen(viewModel)
                    ActiveTab.BIBLE -> BibleTabScreen(viewModel)
                }

            if (showAuthDialog) {
                AuthSyncDialog(
                    viewModel = viewModel,
                    onDismissRequest = { showAuthDialog = false }
                )
            }

            if (showApiSettingsDialog) {
                SecureApiSettingsDialog(
                    viewModel = viewModel,
                    onDismissRequest = { showApiSettingsDialog = false }
                )
            }

            if (showPanicOverlay) {
                PanicOverlayDialog(onDismiss = { showPanicOverlay = false })
            }
        }
    }
}
}

// ==========================================
// PANELS/SCREEN IMPLEMENTATIONS
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreedomTabScreen(
    viewModel: OverComerViewModel,
    onNavigateToChat: () -> Unit
) {
    val goal by viewModel.freedomGoal.collectAsStateWithLifecycle()
    val logs by viewModel.victoryLogs.collectAsStateWithLifecycle()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()
    
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var showBreathingExercise by remember { mutableStateOf(false) }
    var showDbtSkillsLibrary by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val pathKey = remember(userPath) { userPath ?: "SUBSTANCE_RECOVERY" }
    val pathPrefs = remember(pathKey) {
        context.getSharedPreferences("overcomer_path_settings_v3", Context.MODE_PRIVATE)
    }

    val defaultStartDate = remember(pathKey) {
        System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 3)
    }
    val defaultStruggle = when (pathKey) {
        "MENTAL_HEALTH" -> "Anxiety & Depression"
        "TESTIMONY_VICTORY" -> "Victorious Breakthrough"
        "REENTRY_RESTORATION" -> "Re-entry Reintegration"
        "VETERAN_TRANSITION" -> "Veteran Transition & PTSD"
        else -> "Substance Use"
    }
    val defaultDeclaration = when (pathKey) {
        "MENTAL_HEALTH" -> "In Christ, My mind is filled with quietness and stability. I declare that worry has no dominion over my thoughts, and He keeps me in perfect peace."
        "TESTIMONY_VICTORY" -> "I am more than a conqueror through Him who loved me! Today, I walk in absolute victory, sharing my testimony and glorifying Christ!"
        "REENTRY_RESTORATION" -> "My past is nailed to the cross, and I am a new creation in Christ. God has a future and a hope for me to lead and thrive in society."
        "VETERAN_TRANSITION" -> "I am commissioned by King Jesus. He is my shield, my deliverer, and my secure fortress in every battle."
        else -> "An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory!"
    }
    val defaultMilestone = when (pathKey) {
        "MENTAL_HEALTH" -> "The date my mental health began to feel good"
        "TESTIMONY_VICTORY" -> "since I celebrated my victory testimony"
        "REENTRY_RESTORATION" -> "since my release & step into restoration"
        "VETERAN_TRANSITION" -> "since taking off the uniform & entering civilian strength"
        else -> "since I OverCome addiction"
    }

    var savedStartDate by remember { mutableStateOf(pathPrefs.getLong("start_date_$pathKey", defaultStartDate)) }
    var savedStruggle by remember { mutableStateOf(pathPrefs.getString("struggle_$pathKey", defaultStruggle) ?: defaultStruggle) }
    var savedDeclaration by remember { mutableStateOf(pathPrefs.getString("declaration_$pathKey", defaultDeclaration) ?: defaultDeclaration) }
    var savedMilestone by remember {
        val raw = pathPrefs.getString("milestone_$pathKey", defaultMilestone) ?: defaultMilestone
        mutableStateOf(raw)
    }

    LaunchedEffect(pathKey, goal) {
        if (goal != null) {
            if (!pathPrefs.contains("start_date_$pathKey")) {
                pathPrefs.edit()
                    .putLong("start_date_$pathKey", goal!!.startDate)
                    .putString("struggle_$pathKey", goal!!.struggleType)
                    .putString("declaration_$pathKey", goal!!.customDeclaration)
                    .apply()
            }
        }
        savedStartDate = pathPrefs.getLong("start_date_$pathKey", goal?.startDate ?: defaultStartDate)
        savedStruggle = pathPrefs.getString("struggle_$pathKey", goal?.struggleType ?: defaultStruggle) ?: defaultStruggle
        savedDeclaration = pathPrefs.getString("declaration_$pathKey", goal?.customDeclaration ?: defaultDeclaration) ?: defaultDeclaration
        savedMilestone = pathPrefs.getString("milestone_$pathKey", defaultMilestone) ?: defaultMilestone
    }

    val daysCount = remember(savedStartDate) {
        val delta = System.currentTimeMillis() - savedStartDate
        if (delta <= 0) 0 else (delta / (1000 * 60 * 60 * 24)).toInt()
    }

    val headerText = when (userPath) {
        "SUBSTANCE_RECOVERY" -> "Remember: You are not defined by your struggle, but by His grace. You are a new creation in Christ. Cleanse your mind, breathe deep, and walk in absolute victory today."
        "MENTAL_HEALTH" -> "Remember: You are loved, cherished, and chosen by God. You are a new creation in Christ. Cleanse your mind, rest in His peace, and walk in emotional resilience today."
        "TOUGH_DAY" -> "Today might feel like an all round tough day, but God is your present help in times of trouble. Let His supernatural grace carry your load today."
        "VETERAN_TRANSITION" -> "Remember: Your identity is anchored in Jesus Christ, who has won the ultimate battle for you. It is alright to ask for help—God is your shield, your fortress, and your deliverer. Walk in His peace today."
        "REENTRY_RESTORATION" -> "Remember: Your past is completely forgiven and forgotten by God. Christ has broken every chain of the past and has chosen you to lead with honor and integrity. Walk in His restoration today."
        "TESTIMONY_VICTORY" -> "Today is a Testimony and Victory Day! Let's praise God for His absolute faithfulness, rejoice in His mercy, and walk in the fullness of His triumph today."
        else -> "Remember: You are not defined by your struggle, but by His grace. You are a new creation in Christ. Cleanse your mind, breathe deep, and walk in absolute victory today."
    }

    val greetingWord = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val greetingTitle = when (userPath) {
        "TOUGH_DAY" -> "Dear $currentUserName,"
        else -> "$greetingWord, $currentUserName"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppCoverBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        item {
            // Styled Greeting Card matching the HTML
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(24.dp), // rounded-3xl
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp) // p-6
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = greetingTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = headerText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            val verseState by viewModel.verseOfTheDay.collectAsStateWithLifecycle()
            val isVerseLoading by viewModel.isLoadingVerse.collectAsStateWithLifecycle()
            val context = LocalContext.current

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("verse_of_the_day_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row with Title and Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Scripture icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "VERSE OF THE DAY",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Copy Action
                            IconButton(
                                onClick = {
                                    verseState?.let { verse ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Verse of the Day", "\"${verse.text}\"\n— ${verse.reference}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Scripture copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(32.dp),
                                enabled = verseState != null && !isVerseLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Verse",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Refresh Action
                            IconButton(
                                onClick = { viewModel.fetchVerseOfTheDay(forceGenerate = true) },
                                modifier = Modifier.size(32.dp),
                                enabled = !isVerseLoading
                            ) {
                                if (isVerseLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Verse",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (isVerseLoading && verseState == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Anointing you with daily grace...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        verseState?.let { verse ->
                            // The actual verse text
                            Text(
                                text = "\"${verse.text}\"",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )
                            
                            // Reference citation
                            Text(
                                text = "— ${verse.reference}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Devotional reflection container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "MENTAL RESILIENCE INSIGHT:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        text = verse.reflection,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (userPath != "TOUGH_DAY") {
            item {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Numeric count (25% larger than 58.sp, let's use 74.sp)
                        Text(
                            text = "$daysCount",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 74.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("days_counter_text")
                        )
                        
                        // Days label row with 25% larger text and the Edit button clearly visible
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (userPath == "MENTAL_HEALTH") {
                                    if (daysCount == 1) "DAY OF TRANQUILITY" else "DAYS OF PEACE"
                                } else if (userPath == "VETERAN_TRANSITION") {
                                    if (daysCount == 1) "DAY OF INTEGRATION" else "DAYS OF CIVILIAN STRENGTH"
                                } else {
                                    if (daysCount == 1) "DAY OF FREEDOM" else "DAYS OF VICTORY"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalIconButton(
                                onClick = { showEditGoalDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("edit_freedom_goal_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit settings",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        if (userPath == "MENTAL_HEALTH" || userPath == "VETERAN_TRANSITION") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sovereign walk in $savedStruggle",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        val dateFormatted = remember(savedStartDate) {
                            val format = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
                            format.format(java.util.Date(savedStartDate))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = savedMilestone,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Started: $dateFormatted",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                val creedTitle = if (userPath == "MENTAL_HEALTH") {
                    "My Mental Peace Covenant"
                } else if (userPath == "VETERAN_TRANSITION") {
                    "My Veteran Covenant of Freedom"
                } else {
                    "My OverComer Creed"
                }
                val creedText = savedDeclaration

                // OverComer declaration card - Clean Minimal Secondary Container scheme
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "My Oath",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = creedTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"$creedText\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        item {
            BibleAffirmationsSection()
        }

        when (userPath) {
            "TESTIMONY_VICTORY" -> {
                item {
                    TestimonyVictoryBoard(viewModel = viewModel, onNavigateToChat = onNavigateToChat)
                }
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
            "SUBSTANCE_RECOVERY" -> {
                item {
                    RecoveryLessonsSection()
                }
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
            "MENTAL_HEALTH" -> {
                item {
                    MentalHealthSupportSection()
                }
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
            "VETERAN_TRANSITION" -> {
                item {
                    VeteranTargetedStruggleSupportCard()
                }
                item {
                    VeteranSupportSection()
                }
                item {
                    VeteranSupportResourcesOnlyCard()
                }
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
            "REENTRY_RESTORATION" -> {
                item {
                    ReentryTargetedStruggleSupportCard()
                }
                item {
                    ReentrySupportSection()
                }
                item {
                    ReentrySupportResourcesOnlyCard()
                }
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
            "TOUGH_DAY" -> {
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
            else -> {
                item {
                    SupportGroupLocatorSection(viewModel = viewModel)
                }
                item {
                    CuratedBiblicalLibrarySection()
                }
                item {
                    TheFaithConnectionSection()
                }
            }
        }

        item {
            // Calm Grounding Quick Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (userPath != "TESTIMONY_VICTORY") {
                    Text(
                        text = "Distress Tolerance & Grounding",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Calm Breathing trigger block
                    Button(
                        onClick = { showBreathingExercise = !showBreathingExercise },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dbt_breathing_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showBreathingExercise) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (showBreathingExercise) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showBreathingExercise) "Close Calming Breathing" else "Open Calming Breathing Support",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Breathing Exercise UI
                    AnimatedVisibility(
                        visible = showBreathingExercise,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        PacedBreathingGuide()
                    }

                    // Calming Skills Library Toggle Button
                    Button(
                        onClick = { showDbtSkillsLibrary = !showDbtSkillsLibrary },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dbt_skills_library_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showDbtSkillsLibrary) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (showDbtSkillsLibrary) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showDbtSkillsLibrary) "Close Calming Skills Library" else "Open Calming Grounding Skills ✨",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Calming Skills Library UI Panel
                    AnimatedVisibility(
                        visible = showDbtSkillsLibrary,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        DbtSkillsLibrarySection()
                    }
                }

                // AI Counselor clean button layout matching the HTML
                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("ai_counselor_card"), // keep the same test tag to avoid breaking existing/future tests
                    shape = RoundedCornerShape(28.dp), // h-14 rounded-full
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Chat",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Talk to your Companion",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
                Text(
                    text = "AI-DRIVEN EMPATHETIC SUPPORT • ABSOLUTE PRIVACY GUARANTEED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                )

                if (userPath != "TESTIMONY_VICTORY") {
                    Spacer(modifier = Modifier.height(8.dp))
                    SupportConnectionSmsCard()
                }
            }
        }

        item {
            // "What to do when triggered" - Scriptures
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (userPath) {
                        "TESTIMONY_VICTORY" -> {
                            Text(
                                text = "🏆 Today is a Testimony & Victory Day! Celebrate:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. But thanks be to God! He gives us the victory through our Lord Jesus Christ. (1 Corinthians 15:57)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. They triumphed over him by the blood of the Lamb and by the word of their testimony. (Revelation 12:11)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. No, in all these things we are more than conquerors through Him who loved us. (Romans 8:37)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        "MENTAL_HEALTH" -> {
                            Text(
                                text = "🕊️ Rest in His Perfect Peace:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. You will keep in perfect peace those whose minds are steadfast, because they trust in you. (Isaiah 26:3)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. Do not be anxious about anything, but in every situation, make your requests known to God... and the peace of God will guard your hearts and minds. (Philippians 4:6-7)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. Peace I leave with you; my peace I give you. Do not let your hearts be troubled. (John 14:27)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        "TOUGH_DAY" -> {
                            Text(
                                text = "🌤️ Comfort for a Hard Day:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. Cast all your anxiety on Him because He cares for you with deepest affection. (1 Peter 5:7)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. Come to me, all you who are weary and burdened, and I will give you rest. (Matthew 11:28)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. The Lord is near to the brokenhearted and saves those who are crushed in spirit. (Psalm 34:18)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        else -> {
                            Text(
                                text = "🔥 When Tempted or Triggered:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. Submit to God, Resist the devil, and he will flee from you! (James 4:7)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. Look for the exit! No temptation has overtaken you except what is common... God will also provide a way out so that you can endure it. (1 Corinthians 10:13)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. Cast all anxiety! Remember, Christ suffered when tempted, and He is fully able to help those being tempted today. (Hebrews 2:18)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- Goal Edit Dialog ---
    if (showEditGoalDialog) {
        var tempStruggleType by remember { mutableStateOf(savedStruggle) }
        var tempDeclaration by remember { mutableStateOf(savedDeclaration) }

        val cal = remember {
            java.util.Calendar.getInstance().apply {
                timeInMillis = savedStartDate
            }
        }
        var tempYear by remember { mutableStateOf(cal.get(java.util.Calendar.YEAR).toString()) }
        var tempMonth by remember { mutableStateOf((cal.get(java.util.Calendar.MONTH) + 1).toString()) }
        var tempDay by remember { mutableStateOf(cal.get(java.util.Calendar.DAY_OF_MONTH).toString()) }

        val preselectedMilestones = if (userPath == "MENTAL_HEALTH") {
            listOf(
                "The date my mental health began to feel good",
                "The date I started having good days",
                "The date I stopped having anxiety/panic"
            )
        } else if (userPath == "VETERAN_TRANSITION") {
            listOf(
                "The date I transitioned to civilian life",
                "The date I committed to walk in freedom with my platoon",
                "The date I stopped letting trauma define me"
            )
        } else {
            listOf(
                "since I OverCome addiction",
                "The date I stopped using substances / drugs",
                "The date I started having good days",
                "The date my mental health was good"
            )
        }

        var selectedMilestoneChip by remember { 
            mutableStateOf(preselectedMilestones.find { it.trim().lowercase() == savedMilestone.trim().lowercase() } ?: preselectedMilestones.first()) 
        }
        var customMilestoneActive by remember { 
            mutableStateOf(preselectedMilestones.none { it.trim().lowercase() == savedMilestone.trim().lowercase() }) 
        }
        var tempMilestoneText by remember { 
            mutableStateOf(if (customMilestoneActive) savedMilestone else "") 
        }

        val isValidDate = remember(tempYear, tempMonth, tempDay) {
            val y = tempYear.toIntOrNull() ?: 0
            val m = tempMonth.toIntOrNull() ?: 0
            val d = tempDay.toIntOrNull() ?: 0
            y in 1900..2100 && m in 1..12 && d in 1..31
        }

        AlertDialog(
            onDismissRequest = { showEditGoalDialog = false },
            title = {
                val titleText = when (userPath) {
                    "MENTAL_HEALTH" -> "Walk of Peace Settings"
                    "VETERAN_TRANSITION" -> "Veteran Transition Settings"
                    else -> "Walk of Freedom Settings"
                }
                Text(titleText)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Configure your specific focus area, journey start date, and custom covenant declaration statement.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Struggle focus area
                    Text("My Focus Area:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    val struggleTypes = if (userPath == "MENTAL_HEALTH") {
                        listOf("Anxiety", "Depression", "Fear", "Anger", "Trauma / PTSD", "Mental Peace")
                    } else if (userPath == "VETERAN_TRANSITION") {
                        listOf("Transition Stress", "Combat Trauma / PTSD", "Loss of Mission", "Hypervigilance", "Civilian Integration")
                    } else {
                        listOf("Substance Use", "Alcohol / Drinking", "Drugs", "Nicotine / Smoking", "Habitual Temptations")
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        struggleTypes.forEach { type ->
                            FilterChip(
                                selected = tempStruggleType == type,
                                onClick = { tempStruggleType = type },
                                label = { Text(type) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Date setup inputs
                    Text("Journey Start Date:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tempYear,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) tempYear = it },
                            label = { Text("Year") },
                            placeholder = { Text("YYYY") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.3f)
                        )
                        OutlinedTextField(
                            value = tempMonth,
                            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) tempMonth = it },
                            label = { Text("Month") },
                            placeholder = { Text("1-12") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = tempDay,
                            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) tempDay = it },
                            label = { Text("Day") },
                            placeholder = { Text("1-31") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isValidDate) {
                        val previewCal = java.util.Calendar.getInstance()
                        previewCal.set(tempYear.toInt(), tempMonth.toInt() - 1, tempDay.toInt(), 0, 0, 0)
                        val previewDate = previewCal.time
                        val previewDaysStr = remember(previewCal.timeInMillis) {
                            val diff = System.currentTimeMillis() - previewCal.timeInMillis
                            if (diff <= 1) 0 else (diff / 86400000L).toInt()
                        }
                        val previewFormatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        Text(
                            text = "Starts: ${previewFormatter.format(previewDate)} ($previewDaysStr Victory days)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Please enter a valid start date (e.g., 2026, 06, 05)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Milestone Event Picker
                    Text("What describes this start date?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Column {
                        preselectedMilestones.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMilestoneChip = option
                                        customMilestoneActive = false
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = !customMilestoneActive && selectedMilestoneChip == option,
                                    onClick = {
                                        selectedMilestoneChip = option
                                        customMilestoneActive = false
                                    }
                                )
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { customMilestoneActive = true }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = customMilestoneActive,
                                onClick = { customMilestoneActive = true }
                            )
                            Text(
                                text = "Custom celebration milestone...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (customMilestoneActive) {
                        OutlinedTextField(
                            value = tempMilestoneText,
                            onValueChange = { tempMilestoneText = it },
                            label = { Text("Custom Milestone") },
                            placeholder = { Text("e.g. My first peaceful day, or Stopped drinking") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Custom declaration covenant statement
                    OutlinedTextField(
                        value = tempDeclaration,
                        onValueChange = { tempDeclaration = it },
                        label = { Text("Custom Covenant Declaration") },
                        placeholder = { Text("e.g. In Christ, I have been set free indeed!") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_freedom_declaration_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalMilestone = if (customMilestoneActive) tempMilestoneText.ifBlank { "My Victory Date" } else selectedMilestoneChip
                        val finalDate = if (isValidDate) {
                            val finalCal = java.util.Calendar.getInstance()
                            finalCal.set(tempYear.toInt(), tempMonth.toInt() - 1, tempDay.toInt(), 0, 0, 0)
                            finalCal.timeInMillis
                        } else {
                            savedStartDate
                        }

                        pathPrefs.edit()
                            .putLong("start_date_$pathKey", finalDate)
                            .putString("struggle_$pathKey", tempStruggleType)
                            .putString("declaration_$pathKey", tempDeclaration)
                            .putString("milestone_$pathKey", finalMilestone)
                            .apply()

                        savedStartDate = finalDate
                        savedStruggle = tempStruggleType
                        savedDeclaration = tempDeclaration
                        savedMilestone = finalMilestone

                        viewModel.updateFreedomGoal(
                            startDateMillis = finalDate,
                            struggleType = tempStruggleType,
                            customDeclaration = tempDeclaration
                        )
                        showEditGoalDialog = false
                    },
                    modifier = Modifier.testTag("save_freedom_settings_btn"),
                    enabled = isValidDate
                ) {
                    Text("Apply Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ------------------------------------------
// DBT PACED BREATHING COMPOSABLE
// ------------------------------------------

@Composable
fun PacedBreathingGuide() {
    var isBreathingActive by remember { mutableStateOf(false) }
    var scaleFraction by remember { mutableStateOf(1f) }
    var breathingPhase by remember { mutableStateOf("Ready") }
    var secondsRemaining by remember { mutableStateOf(4) }

    // Launch periodic breathing animation when active
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (isBreathingActive) {
                // Inhale Phase
                breathingPhase = "INHALE (Fill your soul with God's Spirit)"
                secondsRemaining = 4
                animateScale(target = 2f) { scaleFraction = it }
                for (i in 4 downTo 1) {
                    secondsRemaining = i
                    delay(1000)
                }
                
                if (!isBreathingActive) break
                
                // Exhale Phase
                breathingPhase = "EXHALE (Release all fears into Christ's hands)"
                secondsRemaining = 4
                animateScale(target = 1f) { scaleFraction = it }
                for (i in 4 downTo 1) {
                    secondsRemaining = i
                    delay(1000)
                }
            }
        } else {
            scaleFraction = 1f
            breathingPhase = "Ready"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DBT Distress Tolerance: Paced Breathing",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "During intense distress or cravings, taking paced deep breaths immediately down-regulates your body's survival hijack (midbrain amygdala reactivity). Use this pacing tool:",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            // Dynamic expanding circle representing breath
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        // Drawing circles based on scale fraction
                        val maxRadius = size.minDimension / 2f
                        drawCircle(
                            color = Color(0xFF14B8A6).copy(alpha = 0.3f),
                            radius = maxRadius * (scaleFraction / 2f)
                        )
                        drawCircle(
                            color = Color(0xFF14B8A6).copy(alpha = 0.1f),
                            radius = maxRadius
                        )
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBreathingActive) "$secondsRemaining" else "Ready",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = breathingPhase,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            // Activator button
            Button(
                onClick = { isBreathingActive = !isBreathingActive },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBreathingActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isBreathingActive) "Stop Tool" else "Start Breathing Loop",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

// --- Custom simple animator utility to run without complex Compose transition declarations
private suspend fun animateScale(target: Float, update: (Float) -> Unit) {
    val duration = 4000
    val start = if (target == 2f) 1f else 2f
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < duration) {
        val fraction = (System.currentTimeMillis() - startTime).toFloat() / duration
        val currentScale = start + (target - start) * fraction
        update(currentScale)
        delay(16) // ~60fps
    }
    update(target)
}

// ------------------------------------------
// DBT DISTRESS TOLERANCE SKILLS LIBRARY
// ------------------------------------------

data class DbtSkill(
    val name: String,
    val acronym: String,
    val motto: String,
    val description: String,
    val moods: List<String>,
    val intensity: String, // "Low", "Moderate", "Extreme"
    val steps: List<String>,
    val bibleVerse: String,
    val bibleRef: String,
    val practicePrompt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbtSkillsLibrarySection() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("ALL") }
    var selectedIntensity by remember { mutableStateOf("ALL") }
    
    // Remember expanded status of each skill by acronym
    var expandedSkillAcronym by remember { mutableStateOf<String?>(null) }
    
    val moods = listOf("ALL", "Overwhelmed", "Anxious", "Angry", "Urge", "Lonely", "Sad")
    val intensities = listOf("ALL", "Low", "Moderate", "Extreme")
    
    val skills = remember {
        listOf(
            DbtSkill(
                name = "STOP Skill",
                acronym = "STOP",
                motto = "Pause before you act to keep control.",
                description = "This key distress tolerance skill helps you pause rather than acting impulsively on overwhelming emotions or unhealthy urges.",
                moods = listOf("Overwhelmed", "Angry", "Urge"),
                intensity = "Moderate",
                steps = listOf(
                    "S - STOP: Freeze! Do not react. Impulsive feelings will try to dictate actions.",
                    "T - Take a Step Back: Pause, take a physical or mental step away, and take a deep breath.",
                    "O - Observe: Notice your physical sensations, thoughts, and environment objectively without judgment.",
                    "P - Proceed Mindfully: Ask yourself what the most effective, Christ-honoring thing to do is right now."
                ),
                bibleVerse = "My dear brothers and sisters, take note of this: Everyone should be quick to listen, slow to speak and slow to get angry.",
                bibleRef = "James 1:19 (NIV)",
                practicePrompt = "Close your eyes, take one slow breath, and think of your next small step."
            ),
            DbtSkill(
                name = "TIPP Crisis Management",
                acronym = "TIPP",
                motto = "Quickly alter your physical state to de-escalate crisis.",
                description = "Designed for extreme, high-stress moments (Level 3 Crisis) to immediately down-regulate your nervous system using rapid biological resets.",
                moods = listOf("Anxious", "Overwhelmed", "Urge"),
                intensity = "Extreme",
                steps = listOf(
                    "T - Temperature: Splash ice-cold water on your face or hold an ice cube to activate your body's calming reflex.",
                    "I - Intense Exercise: Do 60 seconds of high-intensity movement (jumping jacks, high knees) to burn off stress adrenaline.",
                    "P - Paced Breathing: Breathe slowly and deeply. Inhale into your belly for 4 seconds, exhale fully for 6 seconds.",
                    "P - Paired Muscle Relaxation: Tense a muscle group tightly for 5 seconds, then release and notice the release."
                ),
                bibleVerse = "He makes my feet like the feet of a deer; he causes me to stand on the heights. He trains my hands for battle...",
                bibleRef = "Psalm 18:33-34 (NIV)",
                practicePrompt = "Hold a cold object or do 10 jumping jacks now to reset your physiology!"
            ),
            DbtSkill(
                name = "IMPROVE the Moment",
                acronym = "IMPROVE",
                motto = "Shift your mental atmosphere during painful times.",
                description = "Helps you replace immediate painful emotions and situations with more positive, comforting, or stabilizing feelings.",
                moods = listOf("Anxious", "Lonely", "Sad"),
                intensity = "Moderate",
                steps = listOf(
                    "I - Imagery: Mentally visualize a safe, peaceful pasture under God's warm, protective sky.",
                    "M - Meaning: Find a small purpose or lessons in the current struggle.",
                    "P - Prayer: Cast this specific heavy load onto Jesus. Ask Him for His supernatural strength.",
                    "R - Relaxing: Stretch, drink some herbal tea, or listen to a soft acoustic worship melody.",
                    "O - One thing: Focus on only this exact moment. Don't worry about tonight or tomorrow.",
                    "V - Vacation: Take a brief 15-minute healthy break from your environment or phone.",
                    "E - Encouragement: Say a faith-filled affirmation aloud: 'I can do all things through Christ!'"
                ),
                bibleVerse = "Cast all your anxiety on him because he cares for you.",
                bibleRef = "1 Peter 5:7 (NIV)",
                practicePrompt = "Identify one thing in this current room and describe its texture to ground yourself."
            ),
            DbtSkill(
                name = "WISE ACCEPTS Distractions",
                acronym = "ACCEPTS",
                motto = "Temporarily divert your attention from painful distress.",
                description = "Provides a structured set of safe distractions to help emotional storms pass safely without engaging in destructive behaviors.",
                moods = listOf("Urge", "Anxious", "Lonely"),
                intensity = "Low",
                steps = listOf(
                    "Activities: Keep active. Clean your room, write a note, make a meal, or read an encouraging book.",
                    "Contributing: Shift focus outward by praying for a friend or doing a small act of kindness.",
                    "Comparisons: Look back at where you were months ago and thank God for the small graces.",
                    "Emotions (Opposite): Create a different feeling. Watch a wholesome comedy or listen to upbeat worship music.",
                    "Pushing away: Put the trigger out of sight. Put your phone away, lock the app, or walk outside.",
                    "Thoughts: Occupy your mind with a structured cognitive task like repeating an encouraging scripture verse.",
                    "Sensations: Engage your body's senses. Sip freezing-cold water, wrap yourself in a heavy blanket."
                ),
                bibleVerse = "Fix your thoughts on what is true, and honorable, and right, and pure, and lovely, and admirable...",
                bibleRef = "Philippians 4:8 (NLT)",
                practicePrompt = "Perform one random act of kindness or text a friend a word of encouragement right now."
            ),
            DbtSkill(
                name = "Self-Soothing Technique",
                acronym = "5-SENSES",
                motto = "Nurture your soul and body using God's goodness.",
                description = "Provides deep physical comfort and emotional reassurance by mindfully stimulating your primary five physiological senses.",
                moods = listOf("Lonely", "Anxious", "Sad"),
                intensity = "Low",
                steps = listOf(
                    "Sight: Walk outdoors and gaze at God's beautiful sky, the trees, or a calming scenery photo.",
                    "Sound: Turn on peaceful acoustic strings, classical melodies, or steady falling rain sounds.",
                    "Smell: Ignite an aromatic candle, diffuse lavender oil, or breathe in fresh coffee beans.",
                    "Taste: Savor a square of dark chocolate, drink cozy chamomile tea, or chew refreshing mint gum.",
                    "Touch: Put on exceptionally soft socks, cuddle your pet, or rub cool smooth stones."
                ),
                bibleVerse = "Taste and see that the Lord is good; blessed is the one who takes refuge in him.",
                bibleRef = "Psalm 34:8 (NIV)",
                practicePrompt = "Touch something nearby and slowly name 3 things you can see right now."
            ),
            DbtSkill(
                name = "REST Decision Maker",
                acronym = "REST",
                motto = "Pause, process, and proceed in soundness of mind.",
                description = "A comprehensive framework to make deliberate, value-based decisions when tempted or under stress.",
                moods = listOf("Overwhelmed", "Angry", "Urge"),
                intensity = "Moderate",
                steps = listOf(
                    "R - Relax: Pause and take 3 deep full breaths. Release the tension from your shoulders and jaw.",
                    "E - Evaluate: Ask yourself, 'Is there an immediate danger? What emotion or trigger is causing the urge?'",
                    "S - Set a Plan: Formulate a healthy, God-honoring choice (e.g. text a mentor, use breathing exercise).",
                    "T - Take Action: Step out in courage and carry out the chosen behavior, trusting God for the outcome."
                ),
                bibleVerse = "For God has not given us a spirit of fear, but of power and of love and of a sound mind.",
                bibleRef = "2 Timothy 1:7 (NKJV)",
                practicePrompt = "Take 3 deep, steady breaths, exhaling longer than you inhale."
            )
        )
    }
    
    // Filtering logic
    val filteredSkills = remember(skills, searchQuery, selectedMood, selectedIntensity) {
        skills.filter { skill ->
            val matchQuery = skill.name.contains(searchQuery, ignoreCase = true) ||
                             skill.acronym.contains(searchQuery, ignoreCase = true) ||
                             skill.description.contains(searchQuery, ignoreCase = true) ||
                             skill.motto.contains(searchQuery, ignoreCase = true)
            val matchMood = selectedMood == "ALL" || skill.moods.contains(selectedMood)
            val matchIntensity = selectedIntensity == "ALL" || skill.intensity.equals(selectedIntensity, ignoreCase = true)
            matchQuery && matchMood && matchIntensity
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dbt_skills_library_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Grounding Tools",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Calming Distress Grounding Library",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Interactive grounding skills to conquer urges & intense feelings",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search skills e.g., TIPP, STOP...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dbt_skills_search_input"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            
            // Mood Filter Chips Header
            Text(
                text = "FILTER BY MOOD / CURRENT STATE:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Mood Filters Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                moods.forEach { mood ->
                    val isSelected = selectedMood == mood
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMood = mood },
                        label = { Text(mood) },
                        modifier = Modifier.testTag("dbt_mood_filter_chip_$mood")
                    )
                }
            }
            
            // Intensity Filter Chips Header
            Text(
                text = "FILTER BY CHALLENGE INTENSITY:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Intensity Filters Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                intensities.forEach { intensity ->
                    val isSelected = selectedIntensity == intensity
                    val label = when (intensity) {
                        "Low" -> "🟢 Low (Level 1)"
                        "Moderate" -> "🟡 Moderate (Level 2)"
                        "Extreme" -> "🔴 Extreme Crisis (Level 3)"
                        else -> "All Levels"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedIntensity = intensity },
                        label = { Text(label) },
                        modifier = Modifier.testTag("dbt_intensity_filter_chip_$intensity")
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Skill List Output list
            if (filteredSkills.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No clinical grounding skills found",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Try adjusting your search terms, current mood filter, or challenge level.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredSkills.forEach { skill ->
                        val isExpanded = expandedSkillAcronym == skill.acronym
                        DbtSkillCard(
                            skill = skill,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedSkillAcronym = if (isExpanded) null else skill.acronym
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DbtSkillCard(
    skill: DbtSkill,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    // Keep local states for the steps checklist
    val checkedStates = remember(skill.acronym) { 
        mutableStateMapOf<Int, Boolean>().apply {
            skill.steps.indices.forEach { put(it, false) }
        }
    }
    
    // Let's remember user's brief reflection notes
    var userReflectionInput by remember(skill.acronym) { mutableStateOf("") }
    val context = LocalContext.current
    
    val allChecked = checkedStates.values.all { it }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dbt_skill_card_${skill.acronym}")
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Title, Acronym pill, Intensity indicator, Expand arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = skill.acronym,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        
                        val levelBadgeColor = when(skill.intensity) {
                            "Low" -> Color(0xFF10B981)
                            "Moderate" -> Color(0xFFF59E0B)
                            "Extreme" -> Color(0xFFEF4444)
                            else -> MaterialTheme.colorScheme.primary
                        }
                        Surface(
                            shape = CircleShape,
                            color = levelBadgeColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = skill.intensity,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = levelBadgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = skill.motto,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { onToggleExpand() }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Expandable Area
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    // Main description explanation
                    Text(
                        text = skill.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Interactive checklist header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PRACTICAL GROUNDING CHECKLIST:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Show completion badge if done
                        if (allChecked) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "GROUNDED!",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Checklist steps
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        skill.steps.forEachIndexed { index, step ->
                            val isChecked = checkedStates[index] ?: false
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                        else Color.Transparent
                                    )
                                    .clickable { checkedStates[index] = !isChecked }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checkedStates[index] = it },
                                    modifier = Modifier.size(20.dp).testTag("step_checkbox_${skill.acronym}_$index"),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                        fontStyle = if (isChecked) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                                    ),
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Sacred Biblical Anchor Block
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "SACRED BIBLICAL ANCHOR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "\"${skill.bibleVerse}\"",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "— ${skill.bibleRef}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Action box: Practice notes and Submission button
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "PRACTICE & BRIEF REFLECTION NOTES:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = userReflectionInput,
                            onValueChange = { userReflectionInput = it },
                            placeholder = { Text(skill.practicePrompt) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp, max = 100.dp)
                                .testTag("practice_notes_input_${skill.acronym}"),
                            maxLines = 3,
                            singleLine = false,
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Button(
                            onClick = {
                                val message = if (allChecked) {
                                    "Stupendous job completing all practical steps! You successfully deployed ${skill.acronym} to anchor your mind."
                                } else {
                                    "Excellent job practicing the ${skill.acronym} skill. Keep taking proactive steps towards grounding!"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                userReflectionInput = ""
                                skill.steps.indices.forEach { checkedStates[it] = false }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("practice_submit_${skill.acronym}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Complete & Log Grounding Victory 🛡️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Small credit citation for public usage
                        Text(
                            text = "Note: Calming Grounding Steps are based on evidence-based distress tolerance exercises from Dialectical Behavior Therapy (DBT).",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------
// GEMINI LIVE CONVERSATIONAL CORE
// ------------------------------------------

enum class LiveModeState {
    INITIALIZING,
    LISTENING,
    THINKING,
    SPEAKING,
    PAUSED,
    ERROR
}

enum class CounselorVoice(
    val id: String,
    val displayName: String,
    val introEmoji: String,
    val pitch: Float,
    val rate: Float,
    val locale: java.util.Locale,
    val subtitle: String,
    val gender: String,
    val voiceKeywords: List<String>
) {
    CALMING_SAGE("calming_sage", "Calming Sage", "👴🏼", 1.00f, 0.92f, java.util.Locale.US, "Deep, slower, reassuring style", "Male", listOf("iod", "com", "scg", "masculine")),
    GENTLE_GUIDE("gentle_guide", "Gentle Guide", "👩🏻", 1.00f, 1.00f, java.util.Locale.US, "Soft, comforting, warm style", "Female", listOf("sfg", "ioe", "iog", "feminine")),
    VICTORIOUS_COACH("victorious_coach", "Victorious Coach", "🥊", 1.00f, 1.02f, java.util.Locale.US, "Inspiring, steady, coaching style", "Male", listOf("iom", "scg", "-guy-", "masculine")),
    GRACEFUL_BRITISH("graceful_british", "Graceful British", "🇬🇧", 1.00f, 1.00f, java.util.Locale.UK, "Peaceful, elegant UK style", "Female", listOf("fis", "female", "feminine", "-gb-")),
    ENCOURAGING_BROTHER("encouraging_brother", "Encouraging Brother", "🧔🏽", 1.00f, 0.98f, java.util.Locale.US, "Friendly, motivating brotherly style", "Male", listOf("iol", "com", "masculine")),
    SERENE_SISTER("serene_sister", "Serene Sister", "👱🏼‍♀️", 1.00f, 0.96f, java.util.Locale.US, "Peaceful, quiet, serene sisterly style", "Female", listOf("tpf", "lpf", "iog", "feminine"))
}

class LiveVoiceController(
    private val context: Context,
    private val onUserSpoken: (String) -> Unit,
    private val onStateChanged: (LiveModeState) -> Unit,
    private val onUserTextPartial: (String) -> Unit,
    private val onRmsChanged: (Float) -> Unit,
    private val onErrorMsg: (String) -> Unit
) {
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecognizerAvailable = SpeechRecognizer.isRecognitionAvailable(context)
    private val mainLooperHandler = Handler(Looper.getMainLooper())
    var isMuted = false
        private set

    var currentVoice: CounselorVoice = CounselorVoice.GENTLE_GUIDE
        set(value) {
            field = value
            applyVoiceSettings()
        }

    var isThinking = false
        set(value) {
            field = value
            if (value) {
                stopListening()
                mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
                restartRunnable?.let { mainLooperHandler.removeCallbacks(it) }
            }
        }

    private var isCurrentlyListening = false
    private var isTtsSpeaking = false
    private var consecutiveErrors = 0
    private val maxConsecutiveErrors = 3
    private var lastErrorTime = 0L

    private var restartRunnable: Runnable? = null

    private fun scheduleRestart(delay: Long) {
        restartRunnable?.let { mainLooperHandler.removeCallbacks(it) }
        val runnable = Runnable {
            if (!isMuted && !isTtsSpeaking && !isThinking) {
                startListening()
            }
        }
        restartRunnable = runnable
        mainLooperHandler.postDelayed(runnable, delay)
    }

    // Intelligent speech accumulator to support seamless, pause-friendly hands-free speaking
    private val accumulatedSpeech = StringBuilder()
    private val finalizeSpeechRunnable = Runnable {
        finalizeAndSendSpeech()
    }

    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        
        // Timeout customization: Set BOTH Int and Long entries so that all device architectures
        // and Google App updates read them successfully without fallback cast errors.
        putExtra("android.speech.extra.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extra.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000L)
        putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000L)
        
        putExtra("android.speech.extra.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extra.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        
        putExtra("android.speech.extra.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extra.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        
        putExtra("android.speech.extra.DICTATION_MODE", true)
    }

    fun applyVoiceSettings() {
        tts?.let { engine ->
            try {
                val result = engine.setLanguage(currentVoice.locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(java.util.Locale.US)
                }
                engine.setPitch(currentVoice.pitch)
                engine.setSpeechRate(currentVoice.rate)

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val availableVoices = try { engine.voices } catch (t: Throwable) { null }
                    if (!availableVoices.isNullOrEmpty()) {
                        val isMaleTarget = currentVoice.gender == "Male"

                        // Filter voices by locale language (e.g. "en")
                        val langMatch = availableVoices.filter { voice ->
                            voice.locale.language.equals(currentVoice.locale.language, ignoreCase = true)
                        }

                        // Filter out uninstalled voices to prevent silent TTS failures on missing high-quality assets
                        val installedLangMatch = langMatch.filter { voice ->
                            val features = try { voice.features } catch (_: Throwable) { null }
                            features?.contains("notInstalled") != true
                        }.ifEmpty { langMatch }

                        // Score voices: strongly prioritize high-quality natural, neural, wavenet, premium, or hq voices
                        // This prevents utilizing default digitized-sounding synthesizer voices
                        val prioritizedLangMatch = installedLangMatch.sortedWith(compareByDescending { voice ->
                            val voiceName = voice.name.lowercase()
                            var score = 0
                            if (voiceName.contains("premium")) score += 80
                            if (voiceName.contains("natural")) score += 80
                            if (voiceName.contains("neural")) score += 80
                            if (voiceName.contains("wavenet")) score += 80
                            if (voiceName.contains("-x-")) score += 60
                            if (voiceName.contains("hq")) score += 40
                            if (voiceName.contains("network")) score += 50
                            
                            val quality = try { voice.quality } catch (_: Throwable) { 300 }
                            if (quality >= 400) { // QUALITY_HIGH or QUALITY_VERY_HIGH
                                score += 100
                            }
                            if (voice.isNetworkConnectionRequired) {
                                score += 30 // Strongly prefer network-based high-fidelity voices for ultra realism
                            }
                            score
                        })

                        // Strictly partition the voices so Male selections only use actual male voices,
                        // and Female selections only use actual female voices, preventing robotic gender leakages
                        val genderCorrectMatch = prioritizedLangMatch.filter { voice ->
                            val name = voice.name.lowercase()
                            
                            var reflectiveIsMale = false
                            var reflectiveIsFemale = false
                            try {
                                val voiceClass = Class.forName("android.speech.tts.Voice")
                                val getGenderMethod = voiceClass.getMethod("getGender")
                                val genderMaleVal = voiceClass.getField("GENDER_MALE").get(null) as Int
                                val genderFemaleVal = voiceClass.getField("GENDER_FEMALE").get(null) as Int
                                val gender = getGenderMethod.invoke(voice) as? Int
                                if (gender == genderMaleVal) {
                                    reflectiveIsMale = true
                                } else if (gender == genderFemaleVal) {
                                    reflectiveIsFemale = true
                                }
                            } catch (_: Throwable) {}
                            
                            val nameSaysMale = name.contains("male") || name.contains("masculine") || name.contains("-guy-") ||
                                    name.contains("iom") || name.contains("iod") || name.contains("iol") || name.contains("rgm") ||
                                    name.contains("rjs") || name.contains("scg") || name.contains("bdf") || name.contains("com") || name.contains("ctg")
                                    
                            val nameSaysFemale = name.contains("female") || name.contains("feminine") || name.contains("-girl-") ||
                                    name.contains("sfg") || name.contains("tpf") || name.contains("ioe") || name.contains("iog") ||
                                    name.contains("fis") || name.contains("gfm") || name.contains("jfm") || name.contains("lpf")
                            
                            if (isMaleTarget) {
                                (reflectiveIsMale || nameSaysMale) && !reflectiveIsFemale && !nameSaysFemale
                            } else {
                                (reflectiveIsFemale || nameSaysFemale) && !reflectiveIsMale && !nameSaysMale
                            }
                        }.ifEmpty {
                            // Tier 2 fallback: Filter solely by reflective gender from the OS without name keywords
                            prioritizedLangMatch.filter { voice ->
                                var reflectiveIsMale = false
                                var reflectiveIsFemale = false
                                try {
                                    val voiceClass = Class.forName("android.speech.tts.Voice")
                                    val getGenderMethod = voiceClass.getMethod("getGender")
                                    val genderMaleVal = voiceClass.getField("GENDER_MALE").get(null) as Int
                                    val genderFemaleVal = voiceClass.getField("GENDER_FEMALE").get(null) as Int
                                    val gender = getGenderMethod.invoke(voice) as? Int
                                    if (gender == genderMaleVal) reflectiveIsMale = true
                                    else if (gender == genderFemaleVal) reflectiveIsFemale = true
                                } catch (_: Throwable) {}
                                
                                if (isMaleTarget) reflectiveIsMale && !reflectiveIsFemale
                                else reflectiveIsFemale && !reflectiveIsMale
                            }
                        }.ifEmpty {
                            // Tier 3 fallback: Filter solely by name keywords
                            prioritizedLangMatch.filter { voice ->
                                val name = voice.name.lowercase()
                                val nameSaysMale = name.contains("male") || name.contains("masculine") || name.contains("-guy-") ||
                                        name.contains("iom") || name.contains("iod") || name.contains("iol") || name.contains("scg") || name.contains("com")
                                val nameSaysFemale = name.contains("female") || name.contains("feminine") || name.contains("-girl-") ||
                                        name.contains("sfg") || name.contains("tpf") || name.contains("ioe") || name.contains("iog")
                                        
                                if (isMaleTarget) nameSaysMale && !nameSaysFemale
                                else nameSaysFemale && !nameSaysMale
                            }
                        }.ifEmpty { prioritizedLangMatch }

                        var matchedVoice: android.speech.tts.Voice? = null

                        // 1. First, search for high-quality voices matching our specific voiceKeywords
                        for (keyword in currentVoice.voiceKeywords) {
                            matchedVoice = genderCorrectMatch.find { voice ->
                                val name = voice.name.lowercase()
                                name.contains(keyword) && (name.contains("natural") || name.contains("neural") || name.contains("-x-"))
                            }
                            if (matchedVoice != null) break
                        }

                        // 2. Fallback to any voice matching our specific voiceKeywords
                        if (matchedVoice == null) {
                            for (keyword in currentVoice.voiceKeywords) {
                                matchedVoice = genderCorrectMatch.find { voice ->
                                    voice.name.lowercase().contains(keyword)
                                }
                                if (matchedVoice != null) break
                            }
                        }

                        // 3. Fallback to any voice of that gender
                        if (matchedVoice == null) {
                            matchedVoice = genderCorrectMatch.firstOrNull()
                        }

                        if (matchedVoice != null) {
                            engine.setVoice(matchedVoice)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        var listener: TextToSpeech.OnInitListener? = null

        listener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                applyVoiceSettings()
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isTtsSpeaking = true
                        onStateChanged(LiveModeState.SPEAKING)
                    }
                    override fun onDone(utteranceId: String?) {
                        isTtsSpeaking = false
                        mainLooperHandler.post {
                            if (!isMuted) {
                                onStateChanged(LiveModeState.LISTENING)
                                startListening()
                            }
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        isTtsSpeaking = false
                        mainLooperHandler.post {
                            if (!isMuted) {
                                onStateChanged(LiveModeState.LISTENING)
                                startListening()
                            }
                        }
                    }
                })
            } else {
                onErrorMsg("TextToSpeech initialization failed.")
            }
        }

        // Initialize with the system default TTS engine to ensure compatibility across non-Pixel devices (e.g., Samsung).
        tts = try {
            TextToSpeech(context, listener!!)
        } catch (e: Exception) {
            onErrorMsg("TextToSpeech initialization failed.")
            null
        }

        if (isRecognizerAvailable) {
            setupRecognizer()
        }
    }

    private fun setupRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        if (!isMuted && !isTtsSpeaking) {
                            isCurrentlyListening = true
                            onStateChanged(LiveModeState.LISTENING)
                            this@LiveVoiceController.onRmsChanged(0f)
                        }
                    }
                    override fun onBeginningOfSpeech() {
                        if (!isMuted && !isTtsSpeaking) {
                            isCurrentlyListening = true
                            this@LiveVoiceController.onRmsChanged(0f)
                        }
                    }
                    override fun onRmsChanged(rmsdB: Float) {
                        if (!isMuted && !isTtsSpeaking) {
                            this@LiveVoiceController.onRmsChanged(rmsdB)
                        }
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    
                    override fun onError(error: Int) {
                        isCurrentlyListening = false
                        this@LiveVoiceController.onRmsChanged(0f)
                        
                        if (isMuted) return
                        if (isTtsSpeaking || isThinking) {
                            // Suppress errors caused by stopping/starting recognition while TTS is active or while thinking
                            return
                        }

                        val now = System.currentTimeMillis()
                        val isRapidError = (now - lastErrorTime) < 1200L
                        lastErrorTime = now

                        // Log error for debugging internally
                        android.util.Log.e("VoiceController", "Speech recognition error: $error (rapid: $isRapidError)")

                        // Handle permission error explicitly
                        if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                            onErrorMsg("Microphone permission denied.")
                            onStateChanged(LiveModeState.ERROR)
                            return
                        }

                        // Seamlessly restart for common speech timeouts and no-match pauses
                        // This prevents cutting the user off or frustratingly halting the continuous session
                        if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                            consecutiveErrors = 0
                            val retryInterval = 400L // Fast seamless restart
                            scheduleRestart(retryInterval)
                            return
                        }

                        // Solve immediate start lag/glitch by treating BUSY status with an immediate reset and fast restart
                        if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                            try { speechRecognizer?.cancel() } catch (_: Exception) {}
                            scheduleRestart(100L) // Ultra fast 100ms retry for busy states
                            return
                        }

                        consecutiveErrors++
                        if (consecutiveErrors >= 6) { // Generous threshold to prevent minor mic/noise jitters from pausing
                            android.util.Log.e("VoiceController", "Repeated speech errors. Pausing auto-restart to prevent beeping/crash.")
                            onStateChanged(LiveModeState.PAUSED)
                            onErrorMsg("Session paused. Tap 'TAP TO TALK' below to resume speaking.")
                            consecutiveErrors = 0
                            return
                        }

                        val retryInterval = 1000L
                        scheduleRestart(retryInterval)
                    }

                    override fun onResults(results: Bundle?) {
                        isCurrentlyListening = false
                        consecutiveErrors = 0
                        if (isTtsSpeaking) return
                        
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            appendSpeechChunk(text)
                        } else {
                            if (!isMuted && !isTtsSpeaking && !isThinking) startListening()
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        if (isTtsSpeaking) return
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            val displayText = if (accumulatedSpeech.isNotEmpty()) {
                                "$accumulatedSpeech $text"
                            } else {
                                text
                            }
                            onUserTextPartial(displayText)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isRecognizerAvailable = false
        }
    }

    private fun appendSpeechChunk(text: String) {
        mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
        if (accumulatedSpeech.isNotEmpty()) {
            accumulatedSpeech.append(" ")
        }
        accumulatedSpeech.append(text)
        
        onUserTextPartial(accumulatedSpeech.toString())
        
        // 3.5 seconds grace period of silence allows the user to pause, breathe, or pace without early cutoff, meeting user request for at least 3 seconds
        val speakDelay = 3500L
        mainLooperHandler.postDelayed(finalizeSpeechRunnable, speakDelay)
        
        // Continuous listening: restart recognizer immediately to catch next phrase
        if (!isMuted && !isTtsSpeaking && !isThinking) {
            startListening()
        }
    }

    private fun finalizeAndSendSpeech() {
        val completedText = accumulatedSpeech.toString().trim()
        if (completedText.isNotEmpty()) {
            accumulatedSpeech.clear()
            isThinking = true
            onUserSpoken(completedText)
        }
    }

    fun startListening(force: Boolean = false) {
        if (force) {
            isMuted = false
            isTtsSpeaking = false
            isThinking = false
            isCurrentlyListening = false
            accumulatedSpeech.clear()
            mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
            try { tts?.stop() } catch (_: Exception) {}
        } else {
            if (isMuted) return
            if (isTtsSpeaking) return // Never listen / interrupt TTS during speaking unless explicitly requested
            if (isThinking) return // Never listen while AI is generating response
            if (isCurrentlyListening) {
                // Already listening, don't trigger startListening to avoid double start and beeping
                return
            }
        }

        if (isRecognizerAvailable) {
            mainLooperHandler.post {
                try {
                    // Only cancel if we are currently listening, avoiding disrupting first start
                    if (isCurrentlyListening) {
                        speechRecognizer?.cancel()
                    }
                    speechRecognizer?.startListening(recognizerIntent)
                    isCurrentlyListening = true
                    // Set to INITIALIZING first. Only transition to LISTENING when onReadyForSpeech callback is received from the OS.
                    onStateChanged(LiveModeState.INITIALIZING)
                } catch (e: Exception) {
                    e.printStackTrace()
                    isCurrentlyListening = false
                }
            }
        } else {
            onStateChanged(LiveModeState.ERROR)
            onErrorMsg("Voice Recognition is not supported on this device.")
        }
    }

    fun stopListening() {
        isCurrentlyListening = false
        if (isRecognizerAvailable) {
            mainLooperHandler.post {
                try {
                    speechRecognizer?.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun speak(text: String) {
        isTtsSpeaking = true
        isThinking = false
        stopListening()
        // Cancel any pending speech listening retries or finalization runnables immediately when we transition to speaking
        mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
        restartRunnable?.let { mainLooperHandler.removeCallbacks(it) }
        accumulatedSpeech.clear()
        
        mainLooperHandler.post {
            try {
                onStateChanged(LiveModeState.SPEAKING)
                applyVoiceSettings()
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "LiveReply")
                }
                val cleanedText = cleanTextForSpeech(text)
                tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, "LiveReply")
            } catch (e: Exception) {
                e.printStackTrace()
                isTtsSpeaking = false
            }
        }
    }

    fun stopSpeak() {
        isTtsSpeaking = false
        accumulatedSpeech.clear()
        mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
        mainLooperHandler.post {
            try {
                tts?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        if (isMuted) {
            isCurrentlyListening = false
            accumulatedSpeech.clear()
            mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
            stopListening()
            stopSpeak()
            onStateChanged(LiveModeState.PAUSED)
        } else {
            consecutiveErrors = 0
            startListening()
        }
        return isMuted
    }

    fun shutdown() {
        isCurrentlyListening = false
        isTtsSpeaking = false
        mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
        restartRunnable?.let { mainLooperHandler.removeCallbacks(it) }
        stopSpeak()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopListening()
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun LiveVoiceSessionDialog(
    viewModel: OverComerViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var liveState by remember { mutableStateOf(LiveModeState.INITIALIZING) }
    var rmsLevel by remember { mutableStateOf(0f) }
    var userSpeechText by remember { mutableStateOf("") }
    var aiSpeakingText by remember { mutableStateOf("Guide here. I am listening. Speak freely...") }
    var errorMsg by remember { mutableStateOf("") }
    var isMutedState by remember { mutableStateOf(false) }

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val chatError by viewModel.chatError.collectAsStateWithLifecycle()

    // Keep instance reference of the controller
    var controller by remember { mutableStateOf<LiveVoiceController?>(null) }
    
    // Prevent duplicated speaking or race condition triggers
    var lastSpokenMessageText by remember {
        mutableStateOf(chatMessages.lastOrNull { !it.isUser }?.text ?: "")
    }

    // Handle chat error state immediately
    LaunchedEffect(chatError) {
        val err = chatError
        if (err != null) {
            errorMsg = err
            liveState = LiveModeState.ERROR
            controller?.isThinking = false
            controller?.stopListening()
        }
    }

    // Synchronize response complete and reading state immediately when message arrives and controller is ready
    LaunchedEffect(chatMessages, controller) {
        val lastMsg = chatMessages.lastOrNull()
        if (lastMsg != null && !lastMsg.isUser) {
            if (lastMsg.text != lastSpokenMessageText) {
                lastSpokenMessageText = lastMsg.text
                aiSpeakingText = lastMsg.text
                liveState = LiveModeState.SPEAKING
                controller?.isThinking = false
                controller?.speak(lastMsg.text)
            }
        }
    }

    val sharedPrefs = remember { context.getSharedPreferences("overcomer_path_settings_v3", Context.MODE_PRIVATE) }

    // Initialize controller on mount, clean on unmount
    DisposableEffect(Unit) {
        viewModel.clearChatError()
        val initialVoiceId = sharedPrefs.getString("counselor_selected_voice", CounselorVoice.GENTLE_GUIDE.id) ?: CounselorVoice.GENTLE_GUIDE.id
        val initialVoice = CounselorVoice.values().find { it.id == initialVoiceId } ?: CounselorVoice.GENTLE_GUIDE

        var activeController: LiveVoiceController? = null

        val voiceController = LiveVoiceController(
            context = context,
            onUserSpoken = { prompt ->
                if (!viewModel.isChatLoading.value && prompt.isNotBlank()) {
                    liveState = LiveModeState.THINKING
                    // Set thinking true to completely prevent listening or retries
                    try {
                        activeController?.isThinking = true
                    } catch (_: Exception) {}
                    viewModel.sendChatMessage(prompt)
                }
            },
            onStateChanged = { state ->
                // Keep paused in sync
                if (liveState != LiveModeState.THINKING || state == LiveModeState.SPEAKING) {
                    liveState = state
                }
            },
            onUserTextPartial = { partial ->
                userSpeechText = partial
            },
            onRmsChanged = { rms ->
                rmsLevel = rms
            },
            onErrorMsg = { err ->
                errorMsg = err
                liveState = LiveModeState.ERROR
            }
        ).apply {
            currentVoice = initialVoice
        }
        activeController = voiceController
        controller = voiceController

        // Gentle delay before starting to let context settle
        Handler(Looper.getMainLooper()).postDelayed({
            voiceController.startListening()
        }, 500)

        onDispose {
            voiceController.shutdown()
        }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141218)) // Matte Deep Charcoal background for studio vibe
                .testTag("gemini_live_dialog_overlay")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEMINI LIVE SESSION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .testTag("close_live_session_btn")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close session",
                            tint = Color.White
                        )
                    }
                }

                // Middle area: Transcripts & Visual Orbit
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Soft user text bubble
                    AnimatedVisibility(
                        visible = userSpeechText.isNotEmpty(),
                        enter = fadeIn() + expandVertically()
                    ) {
                        Text(
                            text = "\"$userSpeechText\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    // THE BEAUTIFUL INTERACTIVE VOICE ORBIT
                    VoiceAnimationOrbit(liveState = liveState, rmsLevel = rmsLevel)

                    Spacer(modifier = Modifier.height(30.dp))

                    // Large Guide output bubble
                    VerticalScrollCard(
                        text = aiSpeakingText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(horizontal = 16.dp)
                    )
                }

                // Bottom Controls Trays
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var selectedVoice by remember {
                        mutableStateOf(
                            CounselorVoice.values().find { it.id == sharedPrefs.getString("counselor_selected_voice", CounselorVoice.GENTLE_GUIDE.id) }
                                ?: CounselorVoice.GENTLE_GUIDE
                        )
                    }

                    var selectedGender by remember {
                        mutableStateOf(sharedPrefs.getString("counselor_selected_gender", "ALL") ?: "ALL")
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "COMPANION VOICE: ${selectedVoice.displayName.uppercase()} (${selectedVoice.gender.uppercase()})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = Color.White.copy(alpha = 0.5f)
                        )

                        // Interactive Male / Female Options Filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("ALL" to "👥 All", "MALE" to "🧔🏽 Male Only", "FEMALE" to "👩🏻 Female Only").forEach { (genderKey, label) ->
                                val isSelected = selectedGender == genderKey
                                val containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.05f)
                                val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else Color.White.copy(alpha = 0.7f)
                                val borderColor = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f)
                                
                                Row(
                                    modifier = Modifier
                                        .background(containerColor, RoundedCornerShape(12.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedGender = genderKey
                                            sharedPrefs.edit().putString("counselor_selected_gender", genderKey).apply()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("gender_filter_$genderKey"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = contentColor
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val filteredVoices = CounselorVoice.values().filter {
                                selectedGender == "ALL" || it.gender.uppercase() == selectedGender
                            }
                            filteredVoices.forEach { voice ->
                                val isSelected = voice == selectedVoice
                                val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f)
                                val contentColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                                val borderColor = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.15f)
                                
                                Column(
                                    modifier = Modifier
                                        .width(165.dp)
                                        .background(containerColor, RoundedCornerShape(16.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                        .clickable {
                                            selectedVoice = voice
                                            sharedPrefs.edit().putString("counselor_selected_voice", voice.id).apply()
                                            controller?.currentVoice = voice
                                            Toast.makeText(context, "${voice.displayName} selected!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("voice_chip_${voice.id}"),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(voice.introEmoji, fontSize = 16.sp)
                                        Text(
                                            text = voice.displayName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = contentColor,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = voice.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp,
                                            textAlign = TextAlign.Center
                                        ),
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.5f),
                                        maxLines = 2,
                                        minLines = 2
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Current active guidance note
                    Text(
                        text = when (liveState) {
                            LiveModeState.INITIALIZING -> "CONNECTING TO COMPANION..."
                            LiveModeState.LISTENING -> "LISTENING... SPEAK FREELY"
                            LiveModeState.THINKING -> "COMPASSIONATE ALIGNING..."
                            LiveModeState.SPEAKING -> "TALKING TO YOU ENCOURAGINGLY"
                            LiveModeState.PAUSED -> "PAUSED"
                            LiveModeState.ERROR -> "CONNECTION HALTED"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = when (liveState) {
                            LiveModeState.LISTENING -> Color(0xFF66BB6A)
                            LiveModeState.THINKING -> MaterialTheme.colorScheme.primaryContainer
                            LiveModeState.SPEAKING -> MaterialTheme.colorScheme.primary
                            LiveModeState.ERROR -> MaterialTheme.colorScheme.error
                            else -> Color.White.copy(alpha = 0.5f)
                        }
                    )

                    if (errorMsg.isNotEmpty()) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💡 Companion Speaking Tips:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFA000)
                            )
                            Text(
                                text = "1. Wait for the 'LISTENING... SPEAK FREELY' status to ensure the microphone picks up your first words.\n" +
                                       "2. To make voices sound incredibly realistic instead of robotic, go to Android Settings -> Language & Input -> Text-to-Speech output, and choose 'Speech Services by Google' as your Preferred Engine.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute button
                        IconButton(
                            onClick = {
                                val muted = controller?.toggleMute() ?: false
                                isMutedState = muted
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (isMutedState) MaterialTheme.colorScheme.error else Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                                .testTag("mute_live_session_btn")
                        ) {
                            Icon(
                                imageVector = if (isMutedState) Icons.Default.PlayArrow else Icons.Default.Pause, // Toggle play pause mic
                                contentDescription = if (isMutedState) "Unmute Microphone" else "Mute Microphone",
                                tint = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))

                        // Touch to hold / Interrupt button
                        Button(
                            onClick = {
                                // Instantly force clean and start a new recognizer session
                                controller?.startListening(force = true)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = if (liveState == LiveModeState.SPEAKING) "INTERRUPT TO SPEAK" else "TAP TO TALK",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceAnimationOrbit(liveState: LiveModeState, rmsLevel: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    
    // Smooth infinite breathe scale
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Orbit speed rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background halo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val scaleFactor = when (liveState) {
                        LiveModeState.LISTENING -> breatheScale + (rmsLevel.coerceIn(0f, 10f) / 10f)
                        LiveModeState.SPEAKING -> breatheScale + 0.08f
                        LiveModeState.THINKING -> 1.1f
                        else -> 1f
                    }
                    val haloColor = when (liveState) {
                        LiveModeState.LISTENING -> Color(0xFF14B8A6).copy(alpha = 0.15f) // Teal
                        LiveModeState.SPEAKING -> Color(0xFFD0BCFF).copy(alpha = 0.20f) // Soft purple
                        LiveModeState.THINKING -> Color(0xFFCCC2DC).copy(alpha = 0.10f) // Muted purple
                        LiveModeState.ERROR -> Color(0xFFEF4444).copy(alpha = 0.15f) // Red
                        else -> Color.White.copy(alpha = 0.05f)
                    }
                    drawCircle(
                        color = haloColor,
                        radius = size.minDimension / 2f * scaleFactor
                    )
                }
        )

        // Visual design: Render based on active voice state
        when (liveState) {
            LiveModeState.THINKING -> {
                // Circular loading ring spinning beautifully
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFD0BCFF),
                                Color(0xFF6750A4),
                                Color(0xFF141218)
                            )
                        ),
                        startAngle = rotation,
                        sweepAngle = 280f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Thinking ring",
                    tint = Color(0xFFD0BCFF),
                    modifier = Modifier.size(48.dp)
                )
            }
            LiveModeState.SPEAKING -> {
                // 5 Premium Purple animated sound bars mimicking natural output speech waves
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val heights = listOf(0.4f, 0.9f, 0.6f, 0.85f, 0.5f)
                    val specDurations = listOf(700, 1100, 900, 1300, 800)
                    
                    heights.forEachIndexed { index, baseVal ->
                        val scaleHeight by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = baseVal,
                            animationSpec = infiniteRepeatable(
                                animation = tween(specDurations[index], easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "bar_$index"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .width(8.dp)
                                .fillMaxHeight(scaleHeight)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFD0BCFF),
                                            Color(0xFF6750A4)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
            LiveModeState.LISTENING -> {
                // Interactive ripple wave that expands with actual mic RMS volume
                val micAura = (rmsLevel.coerceIn(0f, 12f) / 12f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Pulsing breathing circle
                    drawCircle(
                        color = Color(0xFF14B8A6), // Green teal
                        radius = (size.minDimension / 5f) * (breatheScale + micAura / 2f)
                    )
                    
                    // Ripple 1
                    drawCircle(
                        color = Color(0xFF14B8A6).copy(alpha = 0.3f),
                        radius = (size.minDimension / 3.2f) * (breatheScale + micAura),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Ripple 2 (only displays when talking)
                    if (micAura > 0.15f) {
                        drawCircle(
                            color = Color(0xFF14B8A6).copy(alpha = 0.15f),
                            radius = (size.minDimension / 2.2f) * (breatheScale + micAura * 1.5f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Listening",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            LiveModeState.PAUSED -> {
                // Static grey locked circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = size.minDimension / 4.5f
                    )
                }
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            else -> {
                // Warm, static circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFFFFB59F).copy(alpha = 0.4f),
                        radius = size.minDimension / 4.5f
                    )
                }
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFFFB59F),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun VerticalScrollCard(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ------------------------------------------
// AI SUPPORT COUNSELOR CHAT
// ------------------------------------------

@Composable
fun ChatTabScreen(viewModel: OverComerViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val savedChats by viewModel.savedChats.collectAsStateWithLifecycle()
    
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var isLiveSessionOpen by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleText by remember { mutableStateOf("") }
    var showSavedSessionsList by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val customApiKeyStatus by viewModel.customApiKeyStatus.collectAsStateWithLifecycle()
    var showLocalApiSettingsDialog by remember { mutableStateOf(false) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isLiveSessionOpen = true
        } else {
            Toast.makeText(context, "Microphone permission is required for Gemini Live.", Toast.LENGTH_LONG).show()
        }
    }

    // Auto-scroll to the end of conversation whenever messages expand or typing status triggers
    LaunchedEffect(chatMessages.size, isChatLoading) {
        if (chatMessages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    // Show Gemini Live Dialog Session when active
    if (isLiveSessionOpen) {
        LiveVoiceSessionDialog(
            viewModel = viewModel,
            onDismiss = { isLiveSessionOpen = false }
        )
    }

    // Save current session dialog
    if (showSaveDialog) {
        val defaultName = "Support Session - " + java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Companion Session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Give this discussion a title. The Overcomer’s Companion recalls saved conversations to remind you of past victories, battles, and thoughts you overcame!",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = saveTitleText,
                        onValueChange = { saveTitleText = it },
                        placeholder = { Text(defaultName) },
                        label = { Text("Session Title") },
                        modifier = Modifier.fillMaxWidth().testTag("save_session_title_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTitle = saveTitleText.trim().ifBlank { defaultName }
                        viewModel.saveCurrentChat(finalTitle)
                        saveTitleText = ""
                        showSaveDialog = false
                        Toast.makeText(context, "Session saved and Companion updated of achievements!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLocalApiSettingsDialog) {
        SecureApiSettingsDialog(
            viewModel = viewModel,
            onDismissRequest = { showLocalApiSettingsDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Overcomer's Companion Header & Micro-copy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Overcomer’s Companion",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your AI Overcomer’s companion for biblical encouragement. For professional medical or clinical crisis support, please see our resource page.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
            )
        }

        if (customApiKey.isBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("companion_api_setup_card"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1) // light beautiful amber
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color(0xFFFFA000))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Key Icon",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Connect Your Private Companion Key (100% Free)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE65100)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The Overcomer's Companion runs on Google's Gemini AI. To enjoy unlimited, completely private, and 100% free support, configure your own free API key from Google AI Studio. Setting up a key takes under 1 minute and does not require any credit card.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5D4037),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Button 1: Get free API key
                        Button(
                            onClick = {
                                try {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://aistudio.google.com/")
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open browser. Please visit https://aistudio.google.com/", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE65100),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("get_key_from_companion_screen_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Launch,
                                    contentDescription = "Get Key Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Get Free Key",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Button 2: Enter key
                        OutlinedButton(
                            onClick = { showLocalApiSettingsDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFE65100)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE65100)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("configure_key_from_companion_screen_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Configure Key Icon",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Configure Key",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (customApiKey.isNotBlank()) {
            val (colors, bannerIcon) = when (customApiKeyStatus) {
                "verified" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Private Companion Connection Active ✨") to Icons.Default.CheckCircle
                "failed" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Private Companion Connection Failed ❌") to Icons.Default.Error
                else -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Private Companion Connection Not Verified ⚠️") to Icons.Default.Warning
            }
            val (bannerBg, bannerFg, bannerText) = colors
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = bannerBg,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = bannerIcon,
                            contentDescription = "Active connection status",
                            tint = bannerFg,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = bannerText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = bannerFg
                        )
                    }
                    TextButton(
                        onClick = { showLocalApiSettingsDialog = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = "Change Key",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = bannerFg
                        )
                    }
                }
            }
        }

        // Safe privacy banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Lock icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Absolute Privacy. Chats are cached locally & never shared.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )
            
            // Save Session Button
            TextButton(
                onClick = { 
                    if (chatMessages.size <= 1) {
                        Toast.makeText(context, "Start chatting first before saving!", Toast.LENGTH_SHORT).show()
                    } else {
                        showSaveDialog = true 
                    }
                },
                modifier = Modifier.testTag("save_counselor_session_button")
            ) {
                Text(
                    "Save",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))

            TextButton(
                onClick = { viewModel.clearChatHistory() },
                modifier = Modifier.testTag("clear_chat_history_button")
            ) {
                Text(
                    "Clear",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Lazy scroll of bubbles
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // High-fidelity Hands-Free Voice card banner at the top of the chat area
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("gemini_live_callout_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎙️ Walk in Victory Hands-Free",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                              )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Talk live with OverComer Guide in real-time. The app listens, processes, and speaks back comfortingly with scriptural wisdom.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val permCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                if (permCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    isLiveSessionOpen = true
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("start_live_voice_btn")
                        ) {
                            Text("Talk", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Saved Sessions History section inside Lazy list
            if (savedChats.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("saved_sessions_card_container"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = "Past Sessions Symbol",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Saved Sessions (${savedChats.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                TextButton(
                                    onClick = { showSavedSessionsList = !showSavedSessionsList },
                                    modifier = Modifier.testTag("toggle_saved_sessions_btn")
                                ) {
                                    Text(
                                        text = if (showSavedSessionsList) "Hide Sessions" else "View Past",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            if (showSavedSessionsList) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    savedChats.forEach { savedChat ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = savedChat.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(savedChat.timestamp))
                                                val pathLabel = savedChat.userPath.replace("_", " ").lowercase()
                                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
                                                Text(
                                                    text = "$dateStr • Focus: $pathLabel",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { 
                                                        viewModel.loadSavedChat(savedChat)
                                                        Toast.makeText(context, "Session Restored!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(36.dp).testTag("restore_session_${savedChat.id}")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Refresh,
                                                        contentDescription = "Restore Session",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { viewModel.deleteSavedChat(savedChat.id) },
                                                    modifier = Modifier.size(36.dp).testTag("delete_session_${savedChat.id}")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Delete Session",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(chatMessages) { message ->
                ChatBubble(message = message)
            }

            if (isChatLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                "OverComer Guide is holding you in prayer and writing...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("chat_loading_indicator")
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Standard dynamic prompt helper chips
        val prompts = listOf(
            "I'm feeling triggered right now...",
            "What if I mess up?",
            "Can we do a CBT reframing?",
            "Give me a scripture for anxiety"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            prompts.forEach { prompt ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendChatMessage(prompt)
                    },
                    label = { Text(prompt, maxLines = 1, fontSize = 12.sp) },
                    modifier = Modifier.testTag("chip_prompt_${prompt.take(15).replace(" ", "_")}")
                )
            }
        }

        // Input bottom tray
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Talk through your struggle...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            val permCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            if (permCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                isLiveSessionOpen = true
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier.testTag("chat_mic_leading_icon")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Talk with voice",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendChatMessage(inputText)
                            inputText = ""
                        }
                    }
                ),
                maxLines = 4,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            FloatingActionButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendChatMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_button"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send text",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleBg = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val align = if (message.isUser) Alignment.End else Alignment.Start
    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleBg)
                .padding(14.dp)
                .widthIn(max = 280.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Header tag to make roles explicit
                Text(
                    text = if (message.isUser) "ME" else "OVERCOMER GUIDE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (message.isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ------------------------------------------
// VICTORY LOGS & CBT RECORDS PANEL
// ------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JournalLogsTabScreen(viewModel: OverComerViewModel) {
    val logs by viewModel.victoryLogs.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingDistortion.collectAsStateWithLifecycle()
    val analysisResult by viewModel.distortionAnalysisResult.collectAsStateWithLifecycle()
    val goal by viewModel.freedomGoal.collectAsStateWithLifecycle()
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()

    val isOnlyJournal = userPath == "MENTAL_HEALTH" || userPath == "TOUGH_DAY"

    var showAddLogForm by remember { mutableStateOf(false) }
    var selectedLogType by remember { mutableStateOf("CBT") } // "REFLECT", "TRIGGER", "CBT"
    var logFilter by remember { mutableStateOf("ALL") } // "ALL", "REFLECT", "TRIGGER", "CBT"

    // Export/backup states
    var showExportDialog by remember { mutableStateOf(false) }
    var exportIncludePublic by remember { mutableStateOf(true) }
    var exportIncludePrivate by remember { mutableStateOf(false) }

    // Inputs
    var notesInput by remember { mutableStateOf("") }
    var triggerContextInput by remember { mutableStateOf("") }
    var autoThoughtInput by remember { mutableStateOf("") }
    var identifiedDistortionInput by remember { mutableStateOf("") }
    var reframedTruthInput by remember { mutableStateOf("") }
    var scriptureRefInput by remember { mutableStateOf("") }

    // Dual-panel state
    var subTabMode by remember(userPath) { mutableStateOf(if (isOnlyJournal) "SECURE_JOURNAL" else "PUBLIC_TRACKER") } // "PUBLIC_TRACKER", "SECURE_JOURNAL"

    // PIN lock security states
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("overcomer_journal_prefs", Context.MODE_PRIVATE) }
    var pinLockEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("pin_enabled", false)) }
    var savedPin by remember { mutableStateOf(sharedPrefs.getString("pin_value", "") ?: "") }
    var isJournalUnlocked by remember { mutableStateOf(false) }

    // Temp PIN inputs
    var pinSetupInput by remember { mutableStateOf("") }
    var pinUnlockInput by remember { mutableStateOf("") }
    var pinSetupError by remember { mutableStateOf("") }
    var pinUnlockError by remember { mutableStateOf("") }
    var showSetPinDialog by remember { mutableStateOf(false) }

    // Secure Journal Input
    var journalBodyInput by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, logFilter) {
        val publicLogs = logs.filter { it.type != "JOURNAL_SECURE" }
        if (logFilter == "ALL") publicLogs else publicLogs.filter { it.type == logFilter }
    }

    val secureJournalLogs = remember(logs) {
        logs.filter { it.type == "JOURNAL_SECURE" }
    }

    Scaffold(
        floatingActionButton = {
            if (subTabMode == "PUBLIC_TRACKER") {
                FloatingActionButton(
                    onClick = { showAddLogForm = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_log_record_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add victory record")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isOnlyJournal) {
                            if (userPath == "TOUGH_DAY") "My Private Journal" else "Locked Private Journal"
                        } else {
                            if (subTabMode == "PUBLIC_TRACKER") "My Victory Tracker" else "Locked Private Journal"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        // Export Action Card button in the header
                        TextButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier.testTag("export_trigger_btn"),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share, 
                                    contentDescription = "Export logs", 
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Export", 
                                    style = MaterialTheme.typography.labelMedium, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = if (subTabMode == "PUBLIC_TRACKER") "${filteredLogs.size} Logs" else "${secureJournalLogs.size} Entries",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isOnlyJournal) {
                        if (userPath == "TOUGH_DAY") {
                            "A private, safe space to write down your thoughts, release your cares, and let OverComer AI check for any cognitive distortions with supportive scripture reframing."
                        } else {
                            "A completely private, passcode-secured sanctuary to pour out your thoughts. Let OverComer AI check for cognitive distortions using CBT principles and scripture reframing."
                        }
                    } else {
                        if (subTabMode == "PUBLIC_TRACKER") {
                            "Tracking your struggles physically proves they are behavioral choices. Record automatic thoughts, defeat lies with biblically cognitive reframing, and log the victory."
                        } else {
                            "A completely private, passcode-secured sanctuary to pour out your thoughts. Let OverComer AI check for cognitive distortions using CBT principles and scripture reframing."
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                if (!isOnlyJournal) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Beautiful custom M3 styled Segment Control
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("PUBLIC_TRACKER" to "Public Step Tracker 🛡️", "SECURE_JOURNAL" to "🔐 Private AI Journal").forEach { (mode, label) ->
                            val isSelected = subTabMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { subTabMode = mode }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Body Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                if (subTabMode == "PUBLIC_TRACKER") {
                    // Current step tracker list
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Horizontal Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("ALL", "CBT", "TRIGGER", "REFLECT").forEach { filter ->
                                FilterChip(
                                    selected = logFilter == filter,
                                    onClick = { logFilter = filter },
                                    label = {
                                        val labelText = when (filter) {
                                            "ALL" -> "Show All"
                                            "CBT" -> "Mind Renewal"
                                            "TRIGGER" -> "Triggers"
                                            "REFLECT" -> "Grace Notes"
                                            else -> filter
                                        }
                                        Text(labelText)
                                    },
                                    modifier = Modifier.testTag("log_filter_chip_$filter")
                                )
                            }
                        }

                        if (filteredLogs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.List,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Text(
                                        text = "No logs recorded for $logFilter",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = "Tap the '+' bubble to log your first step in victory.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredLogs) { log ->
                                    VictoryLogCard(log = log, onDelete = {
                                        viewModel.deleteVictoryLog(log.id)
                                    })
                                }
                            }
                        }
                    }
                } else {
                    // SECURE_JOURNAL mode!
                    if (pinLockEnabled && !isJournalUnlocked) {
                        // --- UNLOCK SCREEN PANEL ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 12.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Journal Locked",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Secure Journal Locked",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your personal thoughts & feelings are private. Enter your 4-digit passcode PIN to unlock them safely.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            OutlinedTextField(
                                value = pinUnlockInput,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                                        pinUnlockInput = newValue
                                        if (newValue == savedPin) {
                                            isJournalUnlocked = true
                                            pinUnlockError = ""
                                            pinUnlockInput = ""
                                        } else if (newValue.length == 4) {
                                            pinUnlockError = "Incorrect PIN code. Try again."
                                        } else {
                                            pinUnlockError = ""
                                        }
                                    }
                                },
                                label = { Text("4-Digit PIN") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .width(180.dp)
                                    .testTag("journal_pin_unlock_field"),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                            )

                            if (pinUnlockError.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pinUnlockError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            TextButton(
                                onClick = {
                                    sharedPrefs.edit().clear().apply()
                                    pinLockEnabled = false
                                    savedPin = ""
                                    isJournalUnlocked = false
                                    Toast.makeText(context, "Secure passcode has been reset.", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Forgot PIN? Clear Passcode (Clears PIN Lock)")
                            }
                        }
                    } else {
                        // --- UNLOCKED / CONFIGURED JOURNAL BODY ---
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Section Header Configuration Panel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (pinLockEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = if (pinLockEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (pinLockEnabled) "Passcode Guard Enabled" else "Insecure (No Passcode)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (pinLockEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    )
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Security Settings Trigger Button
                                    TextButton(
                                        onClick = {
                                            if (pinLockEnabled) {
                                                // Disable Lock
                                                sharedPrefs.edit()
                                                    .putBoolean("pin_enabled", false)
                                                    .putString("pin_value", "")
                                                    .apply()
                                                pinLockEnabled = false
                                                savedPin = ""
                                                isJournalUnlocked = false
                                                Toast.makeText(context, "Passcode lock disabled.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                // Trigger Setup Dialog
                                                showSetPinDialog = true
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (pinLockEnabled) "Disable Guard ⚠️" else "Setup Lock PIN 🔒",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (pinLockEnabled) {
                                        // Lock immediately item
                                        IconButton(
                                            onClick = { isJournalUnlocked = false },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Lock,
                                                contentDescription = "Lock journal now",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Write new entry field
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "DOCUMENT YOUR RAW THOUGHTS & FEELINGS:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    OutlinedTextField(
                                        value = journalBodyInput,
                                        onValueChange = { journalBodyInput = it },
                                        placeholder = {
                                            Text(
                                                "Write what is on your mind today... e.g., 'I feel completely overwhelmed. I messed up at work and now I feel like I'm a failure and everyone is going to judge me.'"
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 100.dp, max = 150.dp)
                                            .testTag("secure_journal_thought_input"),
                                        maxLines = 8
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Standard Save
                                        Button(
                                            onClick = {
                                                if (journalBodyInput.isNotBlank()) {
                                                    viewModel.addVictoryLog(
                                                        type = "JOURNAL_SECURE",
                                                        notes = journalBodyInput
                                                    )
                                                    journalBodyInput = ""
                                                    Toast.makeText(context, "Journal entry saved securely! 📖", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = journalBodyInput.isNotBlank() && !isAnalyzing,
                                            modifier = Modifier.weight(1f).testTag("secure_journal_quick_save_btn"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        ) {
                                            Text("Save Quietly 📖", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Premium AI Analyze
                                        Button(
                                            onClick = {
                                                if (journalBodyInput.isNotBlank()) {
                                                    viewModel.analyzeJournalDistortion(journalBodyInput)
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = journalBodyInput.isNotBlank() && !isAnalyzing,
                                            modifier = Modifier.weight(1.3f).testTag("secure_journal_analyze_btn"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text("Analyze Lie AI ✨", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Loading screen for AI scan
                            if (isAnalyzing) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = "Renewing mind alignment using cognitive mapping & biblical truths...",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            ),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Dynamic AI CBT analysis display container
                            analysisResult?.let { result ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🎯 COGNITIVE THOUGHT RENEWAL ANALYSIS",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            IconButton(
                                                onClick = { viewModel.clearDistortionAnalysis() },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Clear analysis",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }

                                        // Detected Distortions Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Distortions: ",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    text = result.distortions,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        // Clinical advice explanations
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = "HOW THIS LIE TRICKS THE MIND:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = result.explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Reframed truth
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                                .padding(10.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    text = "✝ THE TRUTH (REFRAMED COGNITIVELY):",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "\"${result.reframedTruth}\"",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                
                                                if (result.scriptureReference.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "⚓ Scripture: ${result.scriptureReference}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )
                                                }
                                            }
                                        }

                                        // Save Reframe Button
                                        Button(
                                            onClick = {
                                                viewModel.addVictoryLog(
                                                    type = "JOURNAL_SECURE",
                                                    notes = journalBodyInput,
                                                    automaticThought = journalBodyInput,
                                                    identifiedDistortion = result.distortions,
                                                    reframedTruth = result.reframedTruth,
                                                    scriptureReference = result.scriptureReference
                                                )
                                                journalBodyInput = ""
                                                viewModel.clearDistortionAnalysis()
                                                Toast.makeText(context, "Mind renewal reframe archived securely! 🛡️📖", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Save & Archive Renewed Thought 🛡️", fontWeight = FontWeight.Bold)
                                        }

                                        // Small credit citation for public usage
                                        Text(
                                            text = "Note: Cognitive Reframing tool applies clinical principles of Cognitive Behavioral Therapy (CBT) integrated with biblical truths.",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Secure journal ledger list
                            Text(
                                text = "MY SECURE JOURNAL ENTRIES:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.outline
                            )

                            if (secureJournalLogs.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = "Your Secure Journal is completely empty",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = "Document your thoughts above to build a timeline of mental health and victory.",
                                            style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(horizontal = 24.dp)
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(secureJournalLogs) { entry ->
                                        SecureJournalEntryCard(
                                            entry = entry,
                                            onDelete = { viewModel.deleteVictoryLog(entry.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- SETUP PIN CODE DIALOG ---
    if (showSetPinDialog) {
        AlertDialog(
            onDismissRequest = { showSetPinDialog = false; pinSetupError = ""; pinSetupInput = "" },
            title = { Text("Setup Secure PIN Lock") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose a secure 4-digit passcode PIN. This PIN will be required to decrypt and view your secure journaling entries.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = pinSetupInput,
                        onValueChange = { newValue ->
                            if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                                pinSetupInput = newValue
                            }
                        },
                        label = { Text("4-Digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_pin_field"),
                        singleLine = true
                    )
                    if (pinSetupError.isNotEmpty()) {
                        Text(
                            text = pinSetupError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinSetupInput.length == 4) {
                            sharedPrefs.edit()
                                .putBoolean("pin_enabled", true)
                                .putString("pin_value", pinSetupInput)
                                .apply()
                            pinLockEnabled = true
                            savedPin = pinSetupInput
                            isJournalUnlocked = true
                            pinSetupInput = ""
                            pinSetupError = ""
                            showSetPinDialog = false
                            Toast.makeText(context, "Secure passcode activated! 🔐", Toast.LENGTH_SHORT).show()
                        } else {
                            pinSetupError = "PIN must be exactly 4 digits."
                        }
                    }
                ) {
                    Text("Save & Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetPinDialog = false; pinSetupError = ""; pinSetupInput = "" }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- Add Log Dialog ---
    if (showAddLogForm) {
        AlertDialog(
            onDismissRequest = { showAddLogForm = false },
            title = { Text("Log a Victory Milestone") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Type Selector Tabs
                    Text("Select Record Schema:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("CBT", "TRIGGER", "REFLECT").forEach { type ->
                            ElevatedFilterChip(
                                selected = selectedLogType == type,
                                onClick = { selectedLogType = type },
                                label = {
                                    val labelText = when (type) {
                                        "CBT" -> "Mind Renewal"
                                        "TRIGGER" -> "Trigger Event"
                                        "REFLECT" -> "Grace Reflection"
                                        else -> type
                                    }
                                    Text(labelText)
                                },
                                modifier = Modifier.testTag("form_log_type_$type")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when (selectedLogType) {
                        "CBT" -> {
                            Text(
                                "Cognitive Thought Record helps trace the lie. Reframing alignment captures the negative thoughts mid-flight.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = autoThoughtInput,
                                onValueChange = { autoThoughtInput = it },
                                label = { Text("Automatic Lie / Negative Thought") },
                                placeholder = { Text("e.g. \"I need this drink to clear my anxiety\"") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_thought")
                            )
                            OutlinedTextField(
                                value = identifiedDistortionInput,
                                onValueChange = { identifiedDistortionInput = it },
                                label = { Text("Cognitive Distortion Type") },
                                placeholder = { Text("e.g. Emotional Reasoning, Catastrophizing") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_distortion")
                            )
                            OutlinedTextField(
                                value = reframedTruthInput,
                                onValueChange = { reframedTruthInput = it },
                                label = { Text("Biblical Reframing & God's Truth") },
                                placeholder = { Text("e.g. \"God has not given me a spirit of fear, but of power!\"") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_truth")
                            )
                            OutlinedTextField(
                                value = scriptureRefInput,
                                onValueChange = { scriptureRefInput = it },
                                label = { Text("Scripture Reference Anchor") },
                                placeholder = { Text("e.g. 2 Timothy 1:7, John 8:36") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_scripture")
                            )
                        }
                        "TRIGGER" -> {
                            Text(
                                "Confronting triggers helps map social environments, physical exhaustion, or emotional triggers directly.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = triggerContextInput,
                                onValueChange = { triggerContextInput = it },
                                label = { Text("Describe the Trigger Event") },
                                placeholder = { Text("e.g. Had a fight with a colleague, entered stressful atmosphere.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_trigger_context")
                            )
                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Immediate Escape plan executed") },
                                placeholder = { Text("e.g. Stepped out of the office, called an accountability partner, prayed.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_notes_trigger")
                            )
                        }
                        "REFLECT" -> {
                            Text(
                                "Repentance, daily logs, and testimonies of God's limitless grace.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("What did God's grace show you today?") },
                                placeholder = { Text("He healed my heart. I confessed my slips (1 John 1:9), and stood firm.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_notes_reflect")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addVictoryLog(
                            type = selectedLogType,
                            notes = notesInput,
                            triggerContext = triggerContextInput,
                            automaticThought = autoThoughtInput,
                            identifiedDistortion = identifiedDistortionInput,
                            reframedTruth = reframedTruthInput,
                            scriptureReference = scriptureRefInput
                        )
                        // Clear buffers
                        notesInput = ""
                        triggerContextInput = ""
                        autoThoughtInput = ""
                        identifiedDistortionInput = ""
                        reframedTruthInput = ""
                        scriptureRefInput = ""
                        showAddLogForm = false
                    },
                    modifier = Modifier.testTag("dialog_submit_log_btn")
                ) {
                    Text("Save To Shield")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLogForm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- CLINICAL EXPORT & BACKUP DIALOG ---
    if (showExportDialog) {
        val isPrivateUnlocked = !pinLockEnabled || isJournalUnlocked
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Clinical Export & Backup 📋",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Share a readable formatting of your progress with your therapist, counseling practitioner, or keep offline as a backup.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "SELECT DATA TO INCLUDE:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Checkbox for Public Logs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { exportIncludePublic = !exportIncludePublic }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = exportIncludePublic,
                            onCheckedChange = { exportIncludePublic = it },
                            modifier = Modifier.testTag("export_include_public_checkbox")
                        )
                        Column {
                            Text(
                                text = "Public Step Tracker Logs",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "CBT Reframing, triggers, grace notes (${filteredLogs.size} logs)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Checkbox for Private Journal Logs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = isPrivateUnlocked) { exportIncludePrivate = !exportIncludePrivate }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = exportIncludePrivate && isPrivateUnlocked,
                            onCheckedChange = { exportIncludePrivate = it && isPrivateUnlocked },
                            enabled = isPrivateUnlocked,
                            modifier = Modifier.testTag("export_include_private_checkbox")
                        )
                        Column {
                            Text(
                                text = "🔐 Locked Private Journal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isPrivateUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                            Text(
                                text = if (isPrivateUnlocked) {
                                    "Deep secure emotional entries & AI CBT analyses (${secureJournalLogs.size} entries)"
                                } else {
                                    "⚠️ PASSCODE LOCKED. Unlock the private journal tab to include secure entries."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPrivateUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    if (!exportIncludePublic && (!exportIncludePrivate || !isPrivateUnlocked)) {
                        Text(
                            text = "Please select at least one data source to export.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                val isExportEnabled = exportIncludePublic || (exportIncludePrivate && isPrivateUnlocked)
                Button(
                    onClick = {
                        val buildExportString = buildString {
                            appendLine("==================================================")
                            appendLine("               OVERCOMER VICTORY REPORT           ")
                            appendLine("==================================================")
                            appendLine("Generated on: ${SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date())}")
                            
                            val g = goal
                            if (g != null) {
                                val diff = System.currentTimeMillis() - g.startDate
                                val daysCount = if (diff > 0) (diff / 86400000L).toInt() else 0
                                appendLine("Sovereign Target Struggle: ${g.struggleType}")
                                appendLine("Faith-based Covenant Declaration: \"${g.customDeclaration}\"")
                                appendLine("Days Walked in Absolute Freedom: $daysCount days")
                            }
                            appendLine("--------------------------------------------------")
                            appendLine()

                            if (exportIncludePublic) {
                                appendLine("--- PUBLIC STEP TRACKER LOGS ---")
                                appendLine("Total Count: ${logs.filter { it.type != "JOURNAL_SECURE" }.size}")
                                appendLine()
                                logs.filter { it.type != "JOURNAL_SECURE" }.sortedByDescending { it.timestamp }.forEach { log ->
                                    val logDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                                    appendLine("📅 EVENT DATE: $logDateStr")
                                    when (log.type) {
                                        "CBT" -> {
                                            appendLine("🏷️ TYPE: Cognitive CBT Restructuring")
                                            if (log.automaticThought.isNotBlank()) {
                                                appendLine("🧠 Automatic Thought: \"${log.automaticThought}\"")
                                            }
                                            if (log.identifiedDistortion.isNotBlank()) {
                                                appendLine("⚠️ Cognitive Distortion: ${log.identifiedDistortion}")
                                            }
                                            if (log.reframedTruth.isNotBlank()) {
                                                appendLine("✨ Reframed Covenant Truth: \"${log.reframedTruth}\"")
                                            }
                                            if (log.scriptureReference.isNotBlank()) {
                                                appendLine("⚓ Bible Anchor: ${log.scriptureReference}")
                                            }
                                            if (log.notes.isNotBlank() && log.notes != log.automaticThought) {
                                                appendLine("📝 Context Notes: ${log.notes}")
                                            }
                                        }
                                        "TRIGGER" -> {
                                            appendLine("🏷️ TYPE: Trigger Identification Tracker")
                                            if (log.triggerContext.isNotBlank()) {
                                                appendLine("💥 Trigger Scenario: ${log.triggerContext}")
                                            }
                                            if (log.notes.isNotBlank()) {
                                                appendLine("📝 Recovery Action Taken: ${log.notes}")
                                            }
                                        }
                                        "REFLECT" -> {
                                            appendLine("🏷️ TYPE: Self Reflection & Grace Note")
                                            if (log.notes.isNotBlank()) {
                                                appendLine("📝 Reflection Notes: ${log.notes}")
                                            }
                                        }
                                        else -> {
                                            appendLine("🏷️ TYPE: Grounding Event (${log.type})")
                                            if (log.notes.isNotBlank()) {
                                                appendLine("📝 Notes: ${log.notes}")
                                            }
                                        }
                                    }
                                    appendLine("--------------------------------------------------")
                                }
                                appendLine()
                            }

                            if (exportIncludePrivate && isPrivateUnlocked) {
                                appendLine("--- SECURE MIND JOURNAL ENTRIES ---")
                                appendLine("Total Count: ${secureJournalLogs.size}")
                                appendLine()
                                secureJournalLogs.sortedByDescending { it.timestamp }.forEach { entry ->
                                    val entryDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))
                                    appendLine("📅 ENTRY DATE: $entryDateStr")
                                    appendLine("📝 Thoughts: ${entry.notes}")
                                    if (entry.identifiedDistortion.isNotBlank() && entry.identifiedDistortion != "None") {
                                        appendLine("🧠 Detected Cognitive Distortion: ${entry.identifiedDistortion}")
                                        appendLine("✨ Covenant Truth Reframe: ${entry.reframedTruth}")
                                        if (entry.scriptureReference.isNotBlank()) {
                                            appendLine("⚓ Scripture Anchor: ${entry.scriptureReference}")
                                        }
                                    }
                                    appendLine("--------------------------------------------------")
                                }
                                appendLine()
                            }

                            appendLine("Report generated safely via OverComer.")
                            appendLine("Walk in absolute victory & sovereign light.")
                            appendLine("==================================================")
                        }

                        // Share via Android System Chooser Send Intent
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, buildExportString)
                            type = "text/plain"
                        }
                        try {
                            val shareIntent = Intent.createChooser(sendIntent, "Export OverComer clinical study")
                            context.startActivity(shareIntent)
                            showExportDialog = false
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to initialize standard share sheet: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = isExportEnabled,
                    modifier = Modifier.testTag("export_action_share_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Share / Export 📤")
                    }
                }
            },
            dismissButton = {
                // Copy to Clipboard option for offline backup
                val isExportEnabled = exportIncludePublic || (exportIncludePrivate && isPrivateUnlocked)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            if (isExportEnabled) {
                                val buildExportString = buildString {
                                    appendLine("==================================================")
                                    appendLine("               OVERCOMER VICTORY REPORT           ")
                                    appendLine("==================================================")
                                    appendLine("Generated on: ${SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date())}")
                                    
                                    val g = goal
                                    if (g != null) {
                                        val diff = System.currentTimeMillis() - g.startDate
                                        val daysCount = if (diff > 0) (diff / 86400000L).toInt() else 0
                                        appendLine("Sovereign Target Struggle: ${g.struggleType}")
                                        appendLine("Faith-based Covenant Declaration: \"${g.customDeclaration}\"")
                                        appendLine("Days Walked in Absolute Freedom: $daysCount days")
                                    }
                                    appendLine("--------------------------------------------------")
                                    appendLine()

                                    if (exportIncludePublic) {
                                        appendLine("--- PUBLIC STEP TRACKER LOGS ---")
                                        appendLine("Total Count: ${logs.filter { it.type != "JOURNAL_SECURE" }.size}")
                                        appendLine()
                                        logs.filter { it.type != "JOURNAL_SECURE" }.sortedByDescending { it.timestamp }.forEach { log ->
                                            val logDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                                            appendLine("📅 EVENT DATE: $logDateStr")
                                            when (log.type) {
                                                "CBT" -> {
                                                    appendLine("🏷️ TYPE: Cognitive CBT Restructuring")
                                                    if (log.automaticThought.isNotBlank()) {
                                                        appendLine("🧠 Automatic Thought: \"${log.automaticThought}\"")
                                                    }
                                                    if (log.identifiedDistortion.isNotBlank()) {
                                                        appendLine("⚠️ Cognitive Distortion: ${log.identifiedDistortion}")
                                                    }
                                                    if (log.reframedTruth.isNotBlank()) {
                                                        appendLine("✨ Reframed Covenant Truth: \"${log.reframedTruth}\"")
                                                    }
                                                    if (log.scriptureReference.isNotBlank()) {
                                                        appendLine("⚓ Bible Anchor: ${log.scriptureReference}")
                                                    }
                                                    if (log.notes.isNotBlank() && log.notes != log.automaticThought) {
                                                        appendLine("📝 Context Notes: ${log.notes}")
                                                    }
                                                }
                                                "TRIGGER" -> {
                                                    appendLine("🏷️ TYPE: Trigger Identification Tracker")
                                                    if (log.triggerContext.isNotBlank()) {
                                                        appendLine("💥 Trigger Scenario: ${log.triggerContext}")
                                                    }
                                                    if (log.notes.isNotBlank()) {
                                                        appendLine("📝 Recovery Action Taken: ${log.notes}")
                                                    }
                                                }
                                                "REFLECT" -> {
                                                    appendLine("🏷️ TYPE: Self Reflection & Grace Note")
                                                    if (log.notes.isNotBlank()) {
                                                        appendLine("📝 Reflection Notes: ${log.notes}")
                                                    }
                                                }
                                                else -> {
                                                    appendLine("🏷️ TYPE: Grounding Event (${log.type})")
                                                    if (log.notes.isNotBlank()) {
                                                        appendLine("📝 Notes: ${log.notes}")
                                                    }
                                                }
                                            }
                                            appendLine("--------------------------------------------------")
                                        }
                                        appendLine()
                                    }

                                    if (exportIncludePrivate && isPrivateUnlocked) {
                                        appendLine("--- SECURE MIND JOURNAL ENTRIES ---")
                                        appendLine("Total Count: ${secureJournalLogs.size}")
                                        appendLine()
                                        secureJournalLogs.sortedByDescending { it.timestamp }.forEach { entry ->
                                            val entryDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))
                                            appendLine("📅 ENTRY DATE: $entryDateStr")
                                            appendLine("📝 Thoughts: ${entry.notes}")
                                            if (entry.identifiedDistortion.isNotBlank() && entry.identifiedDistortion != "None") {
                                                appendLine("🧠 Detected Cognitive Distortion: ${entry.identifiedDistortion}")
                                                appendLine("✨ Covenant Truth Reframe: ${entry.reframedTruth}")
                                                if (entry.scriptureReference.isNotBlank()) {
                                                    appendLine("⚓ Scripture Anchor: ${entry.scriptureReference}")
                                                }
                                            }
                                            appendLine("--------------------------------------------------")
                                        }
                                        appendLine()
                                    }

                                    appendLine("Report generated safely via OverComer.")
                                    appendLine("Walk in absolute victory & sovereign light.")
                                    appendLine("==================================================")
                                }

                                try {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("OverComer Journal Report", buildExportString)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Journal report copied to clipboard! 📋 Successfully backed up.", Toast.LENGTH_SHORT).show()
                                    showExportDialog = false
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Clipboard copy failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = isExportEnabled,
                        modifier = Modifier.testTag("export_action_copy_btn")
                    ) {
                        Text("Copy to Clipboard")
                    }
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Close", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

@Composable
fun SecureJournalEntryCard(entry: VictoryLog, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val dateStr = remember(entry.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(entry.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("secure_journal_card_${entry.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Encrypted Mind Journal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Path badge helper for merged journal entries
            val pathInfo = remember(entry.userPath) {
                when (entry.userPath) {
                    "TOUGH_DAY" -> Triple("All-Around Tough Day", Icons.Default.Cloud, Color(0xFFD32F2F))
                    "SUBSTANCE_RECOVERY" -> Triple("Substance Recovery", Icons.Default.Shield, Color(0xFF1976D2))
                    "MENTAL_HEALTH" -> Triple("Mental Health Wellness", Icons.Default.FavoriteBorder, Color(0xFF00796B))
                    "TESTIMONY_VICTORY" -> Triple("Today is a Testimony Day", Icons.Default.Star, Color(0xFFFFA000))
                    else -> null
                }
            }

            pathInfo?.let { (name, icon, color) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.08f),
                    border = BorderStroke(0.5.dp, color.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = color
                        )
                    }
                }
            }

            // Note context snippet / full
            Text(
                text = entry.notes,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
            )

            // If there's AI CBT analysis on this secure log, display it below
            if (entry.identifiedDistortion.isNotBlank() && entry.identifiedDistortion != "None") {
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "AI CBT: ${entry.identifiedDistortion}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Expanded detail section
            if (expanded) {
                // Showing fully if CBT was processed on this
                if (entry.identifiedDistortion.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        if (entry.automaticThought.isNotBlank()) {
                            Text(
                                text = "Analyzed Thought:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "\"${entry.automaticThought}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        if (entry.reframedTruth.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "✝ Biblical CBT Reframe:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = entry.reframedTruth,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    
                                    if (entry.scriptureReference.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "⚓ Anchor Scripture: ${entry.scriptureReference}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Delete entry button row
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete journal entry",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "Tap to expand details and CBT Reframing...",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VictoryLogCard(log: VictoryLog, onDelete: () -> Unit) {
    val dateStr = remember(log.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(log.timestamp))
    }

    val typeColor = when (log.type) {
        "CBT" -> MaterialTheme.colorScheme.secondary
        "TRIGGER" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_card_${log.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, typeColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header tag row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = typeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = log.type,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = typeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (log.type) {
                "CBT" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Automatic Thought (The Lie):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "\"${log.automaticThought}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Text(
                            text = "God's Truth (The Shield Reframe):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "\"${log.reframedTruth}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (log.scriptureReference.isNotBlank()) {
                            Text(
                                text = "⚓ Anchor Scribe: ${log.scriptureReference}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                "TRIGGER" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Trigger Event Context:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = log.triggerContext,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Text(
                            text = "Escape & Grounding Plan Executed:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = log.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                else -> { // REFLECT
                    Text(
                        text = log.notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("delete_log_btn_${log.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete log entry",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ------------------------------------------
// LOCAL SUPPORT & CORE BELIEFS PANEL
// ------------------------------------------

data class InspirationalQuote(
    val id: Int,
    val text: String,
    val reference: String,
    val contextReflection: String,
    val category: String
)

val inspirationalQuotes = listOf(
    InspirationalQuote(
        id = 1,
        text = "So if the Son sets you free, you will be free indeed.",
        reference = "John 8:36",
        contextReflection = "True freedom isn't about trial and error; it is an identity. In Christ, you are declared free from past bondages.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 2,
        text = "No temptation has overtaken you except what is common to mankind. And God is faithful; he will not let you be tempted beyond what you can bear.",
        reference = "1 Corinthians 10:13",
        contextReflection = "Every urge has an expiration date. God is providing a way out right now—take a slow breath and step forward.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 3,
        text = "Submit yourselves, then, to God. Resist the devil, and he will flee from you.",
        reference = "James 4:7",
        contextReflection = "You are not powerless. Submit your current urge to God, stand in His authority, and the temptation must yield.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 13,
        text = "The Holy Spirit will always meet you on the level of your faith, and if you believe that Jesus is able to deliver you completely, He will do it.",
        reference = "David Wilkerson",
        contextReflection = "Perfect deliverance is powered by simple faith in Jesus' resurrection power, rather than our willpower.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 14,
        text = "Addiction is worship gone wrong. True freedom begins when we reorient our hearts to worship the living God instead of our desires.",
        reference = "Edward T. Welch",
        contextReflection = "Our cravings point to deep spiritual thirst. Worship God with that intensity, and the lesser desires will fade.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 15,
        text = "Surrendering to Jesus isn't a sign of weakness; it is the ultimate expression of courage that breaks every chain of desire.",
        reference = "Nicky Cruz",
        contextReflection = "True strength begins when we stop fighting in our own power and surrender our battle to the Savior.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 4,
        text = "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
        reference = "Philippians 4:6-7",
        contextReflection = "Anxiety is a warning light, not your master. Exchange your worries for the peace of God that guards your mind.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 5,
        text = "For God has not given us a spirit of fear, but of power and of love and of a sound mind.",
        reference = "2 Timothy 1:7",
        contextReflection = "Anxiety is not from your Father. He has supplied you with power to stand, love to comfort, and a calm, quiet mind.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 6,
        text = "Peace I leave with you; my peace I give you. I do not give to you as the world gives. Do not let your hearts be troubled and do not be afraid.",
        reference = "John 14:27",
        contextReflection = "Jesus doesn't give transactional peace that depends on circumstances. He gives structural, spiritual peace. Rest in it.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 16,
        text = "I have learned that the way to overcome anxiety is to look away from self to the security of God's sovereign care.",
        reference = "C.S. Lewis",
        contextReflection = "Anxiety looks inward at our limits. Peace looks outward at God's limitless love and sovereignty.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 17,
        text = "Anxiety does not empty tomorrow of its sorrows, but only empties today of its strength.",
        reference = "Charles Spurgeon",
        contextReflection = "Sorrow and worry rob you of the grace given to live in the present. Trust Him for today's steps.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 18,
        text = "Real peace comes when we stop demanding that our circumstances change and instead rest in the unwavering love of the Father.",
        reference = "Dr. Larry Crabb",
        contextReflection = "True spiritual rest isn't found in a perfect environment, but in a perfect Relationship.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 7,
        text = "I can do all things through Christ who strengthens me.",
        reference = "Philippians 4:13",
        contextReflection = "This is not about self-sufficiency, but Christ-sufficiency. His resurrection power resides in you for today's tasks.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 8,
        text = "The Lord is my strength and my shield; my heart trusts in him, and he helps me.",
        reference = "Psalm 28:7",
        contextReflection = "God is active in your defense. Trust Him to shield your weak areas when you feel vulnerable or tired.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 9,
        text = "But those who hope in the Lord will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint.",
        reference = "Isaiah 40:31",
        contextReflection = "Power is gifted to those who wait in His presence. If you feel empty, take a silent moment to renew your hope in Him.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 19,
        text = "God's work done in God's way will never lack God's supply. Rely on His strength, not your own.",
        reference = "Hudson Taylor",
        contextReflection = "When we follow His path and guidelines, He is fully committed to providing the strength and resources we need.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 20,
        text = "Faith is not about believing God will do what you want; it is trusting that He is who He says He is, and that He is active in your weakness.",
        reference = "Paul David Tripp",
        contextReflection = "Faith is anchored in His character. He uses our moments of weakness to demonstrate His perfect grace.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 21,
        text = "God is looking for people through whom He can do the impossible. What a pity that we plan only what we can do by ourselves.",
        reference = "A.W. Tozer",
        contextReflection = "Step out of your comfort zone and trust Him to do what you could never accomplish in your human effort.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 10,
        text = "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here!",
        reference = "2 Corinthians 5:17",
        contextReflection = "Your past failures are dead. You do not have to carry the ghost of past mistakes into today.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 11,
        text = "If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness.",
        reference = "1 John 1:9",
        contextReflection = "A struggle doesn't cancel His love. Grace is immediately active the moment we step back into His presence.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 12,
        text = "But he said to me, 'My grace is sufficient for you, for my power is made perfect in weakness.'",
        reference = "2 Corinthians 12:9",
        contextReflection = "Weakness is not a disqualification; it is a canvas for His strength. Admit your struggle and let His grace power you through.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 22,
        text = "To be a Christian means to forgive the inexcusable because God has forgiven the inexcusable in you.",
        reference = "C.S. Lewis",
        contextReflection = "The power to forgive others flows directly from realizing how deeply we have been forgiven by Christ.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 23,
        text = "Grace means God is for us even when we fail. True accountability is built on the foundation of that absolute safety.",
        reference = "Dr. Henry Cloud",
        contextReflection = "We don't hide our flaws out of fear when we realize God's grace provides an unshakable shelter of love.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 24,
        text = "The cross shows us the extent of our sin, but it also shows us the limitless depth of God's mercy and grace.",
        reference = "Billy Graham",
        contextReflection = "Never let guilt keep you from Him. His mercy at the cross is infinitely greater than your failures.",
        category = "Grace & Forgiveness"
    )
)

enum class BibleSubTab {
    READER,
    AI_SEARCH,
    CREED
}

data class BiblePassage(
    val book: String,
    val chapter: String,
    val verses: List<Pair<Int, String>>,
    val theme: String,
    val context: String
)

val curatedPassages = listOf(
    BiblePassage(
        book = "Psalms",
        chapter = "23",
        verses = listOf(
            1 to "The Lord is my shepherd, I lack nothing.",
            2 to "He makes me lie down in green pastures, he leads me beside quiet waters,",
            3 to "he refreshes my soul. He guides me along the right paths for his name’s sake.",
            4 to "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.",
            5 to "You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows.",
            6 to "Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the Lord forever."
        ),
        theme = "Comfort & Peace",
        context = "When walking through the dark valleys of fear, isolation, or heavy temptations, this psalm anchors us in God's protective presence."
    ),
    BiblePassage(
        book = "Philippians",
        chapter = "4",
        verses = listOf(
            4 to "Rejoice in the Lord always. I will say it again: Rejoice!",
            5 to "Let your gentleness be evident to all. The Lord is near.",
            6 to "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
            7 to "And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
            8 to "Finally, brothers and sisters, whatever is true, whatever is noble, whatever is right, whatever is pure, whatever is lovely, whatever is admirable—if anything is excellent or praiseworthy—think about such things.",
            9 to "Whatever you have learned or received or heard from me, or seen in me—put it into practice. And the God of peace will be with you.",
            13 to "I can do all things through Christ who strengthens me."
        ),
        theme = "Anxiety & Resilience",
        context = "The ultimate cognitive reframing guide. It directs us to cast anxieties on God, focus deliberately on wholesome thoughts, and draw strength from Christ."
    ),
    BiblePassage(
        book = "John",
        chapter = "3",
        verses = listOf(
            16 to "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.",
            17 to "For God did not send his Son into the world to condemn the world, but to save the world through him."
        ),
        theme = "Unconditional Love",
        context = "Dethrones guilt and shame. Christ's primary mission is rescue and deliverance, not condemnation. Surrender brings instant security."
    ),
    BiblePassage(
        book = "Romans",
        chapter = "8",
        verses = listOf(
            1 to "Therefore, there is now no condemnation for those who are in Christ Jesus,",
            2 to "because through Christ Jesus the law of the Spirit who gives life has set you free from the law of sin and death.",
            31 to "What, then, shall we say in response to these things? If God is for us, who can be against us?",
            35 to "Who shall separate us from the love of Christ? Shall trouble or hardship or persecution or famine or nakedness or danger or sword?",
            37 to "No, in all these things we are more than conquerors through him who loved us.",
            38 to "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers,",
            39 to "neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord."
        ),
        theme = "Sovereign Victory",
        context = "Establishes our permanent, unshakable status in Christ. We do not fight FOR victory; we stand in the finished victory of the cross."
    ),
    BiblePassage(
        book = "James",
        chapter = "1",
        verses = listOf(
            2 to "Consider it pure joy, my brothers and sisters, whenever you face trials of many kinds,",
            3 to "because you know that the testing of your faith produces perseverance.",
            4 to "Let perseverance finish its work so that you may be mature and complete, not lacking anything.",
            12 to "Blessed is the one who perseveres under trial because, having stood the test, that person will receive the crown of life that the Lord has promised to those who love him."
        ),
        theme = "Trials, Faith & Endurance",
        context = "Encourages us through trials. Temptations and cravings are testing grounds where our character is strengthened by God's grace."
    ),
    BiblePassage(
        book = "Psalms",
        chapter = "91",
        verses = listOf(
            1 to "Whoever dwells in the shelter of the Most High will rest in the shadow of the Almighty.",
            2 to "I will say of the Lord, 'He is my refuge and my fortress, my God, in whom I trust.'",
            3 to "Surely he will save you from the fowler's snare and from the deadly pestilence.",
            4 to "He will cover you with his feathers, and under his wings you will find refuge; his faithfulness will be your shield and rampart."
        ),
        theme = "Protection & Safety",
        context = "A powerful prayer of physical and mental safety. It reminds us of angelic protection and shields our minds from dread."
    ),
    BiblePassage(
        book = "James",
        chapter = "3",
        verses = listOf(
            2 to "We all stumble in many ways. Anyone who is never at fault in what they say is perfect, able to keep their whole body in check.",
            5 to "Likewise, the tongue is a small part of the body, but it makes great boasts. Consider what a great forest is set on fire by a small spark."
        ),
        theme = "Self-Control & Intent",
        context = "Gives practical wisdom on managing words, speech, and impulses to walk uprightly and avoid self-destructive behavior."
    )
)

val curatedPassagesTranslations = mapOf(
    "NKJV" to listOf(
        // Psalms 23
        listOf(
            1 to "The Lord is my shepherd; I shall not want.",
            2 to "He makes me to lie down in green pastures; He leads me beside the still waters.",
            3 to "He restores my soul; He leads me in the paths of righteousness For His name's sake.",
            4 to "Yea, though I walk through the valley of the shadow of death, I will fear no evil; For You are with me; Your rod and Your staff, they comfort me.",
            5 to "You prepare a table before me in the presence of my enemies; You anoint my head with oil; My cup runs over.",
            6 to "Surely goodness and mercy shall follow me All the days of my life; And I will dwell in the house of the Lord Forever."
        ),
        // Philippians 4 (verses 4-9, 13)
        listOf(
            4 to "Rejoice in the Lord always. Again I will say, rejoice!",
            5 to "Let your gentleness be known to all men. The Lord is at hand.",
            6 to "Be anxious for nothing, but in everything by prayer and supplication, with thanksgiving, let your requests be made known to God;",
            7 to "and the peace of God, which surpasses all understanding, will guard your hearts and minds through Christ Jesus.",
            8 to "Finally, brethren, whatever things are true, whatever things are noble, whatever things are just, whatever things are pure, whatever things are lovely, whatever things are of good report, if there is any virtue and if there is any praise—meditate on these things.",
            9 to "The things which you learned and received and heard and saw in me, these do, and the God of peace will be with you.",
            13 to "I can do all things through Christ who strengthens me."
        ),
        // John 3 (verses 16-17)
        listOf(
            16 to "For God so loved the world that He gave His only begotten Son, that whoever believes in Him should not perish but have everlasting life.",
            17 to "For God did not send His Son into the world to condemn the world, but that the world through Him might be saved."
        ),
        // Romans 8 (verses 1-2, 31, 35, 37-39)
        listOf(
            1 to "There is therefore now no condemnation to those who are in Christ Jesus, who do not walk according to the flesh, but according to the Spirit.",
            2 to "For the law of the Spirit of life in Christ Jesus has made me free from the law of sin and death.",
            31 to "What then shall we say to these things? If God is for us, who can be against us?",
            35 to "Who shall separate us from the love of Christ? Shall tribulation, or distress, or persecution, or famine, or nakedness, or peril, or sword?",
            37 to "Yet in all these things we are more than conquerors through Him who loved us.",
            38 to "For I am persuaded that neither death nor life, nor angels nor principalities nor powers, nor things present nor things to come,",
            39 to "nor height nor depth, nor any other created thing, shall be able to separate us from the love of God which is in Christ Jesus our Lord."
        ),
        // James 1 (verses 2-4, 12)
        listOf(
            2 to "My brethren, count it all joy when you fall into various trials,",
            3 to "knowing that the testing of your faith produces patience.",
            4 to "But let patience have its perfect work, that you may be perfect and complete, lacking nothing.",
            12 to "Blessed is the man who endures temptation; for when he has been approved, he will receive the crown of life which the Lord has promised to those who love Him."
        ),
        // Psalms 91 (verses 1-4)
        listOf(
            1 to "He who dwells in the secret place of the Most High Shall abide under the shadow of the Almighty.",
            2 to "I will say of the Lord, 'He is my refuge and my fortress; My God, in Him I will trust.'",
            3 to "Surely He shall deliver you from the snare of the fowler And from the perilous peril.",
            4 to "He shall cover you with His feathers, And under His wings you shall take refuge; His truth shall be your shield and buckler."
        ),
        // James 3 (verses 2, 5)
        listOf(
            2 to "For we all stumble in many things. If anyone does not stumble in word, he is a perfect man, able also to bridle the whole body.",
            5 to "Even so the tongue is a little member and boasts great things. See how great a forest a little fire kindles!"
        )
    ),
    "KJV" to listOf(
        // Psalms 23
        listOf(
            1 to "The Lord is my shepherd; I shall not want.",
            2 to "He maketh me to lie down in green pastures: he leadeth me beside the still waters.",
            3 to "He restoreth my soul: he leadeth me in the paths of righteousness for his name's sake.",
            4 to "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me.",
            5 to "Thou preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over.",
            6 to "Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the Lord for ever."
        ),
        // Philippians 4 (verses 4-9, 13)
        listOf(
            4 to "Rejoice in the Lord alway: and again I say, Rejoice.",
            5 to "Let your moderation be known unto all men. The Lord is at hand.",
            6 to "Be careful for nothing; but in every thing by prayer and supplication with thanksgiving let your requests be made known unto God.",
            7 to "And the peace of God, which passeth all understanding, shall keep your hearts and minds through Christ Jesus.",
            8 to "Finally, brethren, whatsoever things are true, whatsoever things are honest, whatsoever things are just, whatsoever things are pure, whatsoever things are lovely, whatsoever things are of good report; if there be any virtue, and if there be any praise, think on these things.",
            9 to "Those things, which ye have both learned, and received, and heard, and seen in me, do: and the God of peace shall be with you.",
            13 to "I can do all things through Christ which strengtheneth me."
        ),
        // John 3 (verses 16-17)
        listOf(
            16 to "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.",
            17 to "For God sent not his Son into the world to condemn the world; but that the world through him might be saved."
        ),
        // Romans 8 (verses 1-2, 31, 35, 37-39)
        listOf(
            1 to "There is therefore now no condemnation to them which are in Christ Jesus, who walk not after the flesh, but after the Spirit.",
            2 to "For the law of the Spirit of life in Christ Jesus hath made me free from the law of sin and death.",
            31 to "What shall we then say to these things? If God be for us, who can be against us?",
            35 to "Who shall separate us from the love of Christ? shall tribulation, or distress, or persecution, or famine, or nakedness, or peril, or sword?",
            37 to "Nay, in all these things we are more than conquerors through him that loved us.",
            38 to "For I am persuaded, that neither death, nor life, nor angels, nor principalities, nor powers, nor things present, nor things to come,",
            39 to "Nor height, nor depth, nor any other creature, shall be able to separate us from the love of God, which is in Christ Jesus our Lord."
        ),
        // James 1 (verses 2-4, 12)
        listOf(
            2 to "My brethren, count it all joy when ye fall into divers temptations;",
            3 to "Knowing this, that the trying of your faith worketh patience.",
            4 to "But let patience have her perfect work, that ye may be perfect and entire, wanting nothing.",
            12 to "Blessed is the man that endureth temptation: for when he is tried, he shall receive the crown of life, which the Lord hath promised to them that love him."
        ),
        // Psalms 91 (verses 1-4)
        listOf(
            1 to "He that dwelleth in the secret place of the most High shall abide under the shadow of the Almighty.",
            2 to "I will say of the Lord, He is my refuge and my fortress: my God; in him will I trust.",
            3 to "Surely he shall deliver thee from the snare of the fowler, and from the noisome pestilence.",
            4 to "He shall cover thee with his feathers, and under his wings shalt thou trust: his truth shall be his shield and buckler."
        ),
        // James 3 (verses 2, 5)
        listOf(
            2 to "For in many things we offend all. If any man offend not in word, the same is a perfect man, and able also to bridle the whole body.",
            5 to "Even so the tongue is a little member, and boasteth great things. Behold, how great a matter a little fire kindleth!"
        )
    ),
    "The Amplified" to listOf(
        // Psalms 23
        listOf(
            1 to "The Lord is my Shepherd [to guide, protect, and provide for me], I shall not want.",
            2 to "He makes me lie down in green pastures; He leads me beside still waters.",
            3 to "He refreshes and restores my soul (life); He leads me in the paths of righteousness for His name's sake.",
            4 to "Even though I walk through the [sunless] valley of the shadow of death, I fear no evil, for You are with me; Your rod [to protect] and Your staff [to guide], they comfort me.",
            5 to "You prepare a table before me in the presence of my enemies. You have anointed my head with oil; My cup overflows.",
            6 to "Surely goodness and mercy and unfailing love shall follow me all the days of my life, And I shall dwell in the house of the Lord [and in His presence] forever."
        ),
        // Philippians 4 (verses 4-9, 13)
        listOf(
            4 to "Rejoice in the Lord always [delight, take pleasure in Him]; again I will say, rejoice!",
            5 to "Let your gentle spirit [your selflessness, mercy, and tolerance] be known to all people. The Lord is near.",
            6 to "Do not be anxious or worried about anything, but in everything [every circumstance and situation] by prayer and petition with thanksgiving, continue to make your [specific] requests known to God.",
            7 to "And the peace of God [that peace which transcends all understanding, that reassuring quiet of a soul-assured of its salvation through Christ] will guard your hearts and minds in Christ Jesus.",
            8 to "Finally, believers, whatever is true, whatever is honorable and worthy of respect, whatever is right and confirmed by God's word, whatever is pure and wholesome, whatever is lovely and brings peace, whatever is admirable and of good repute; if there is any excellence and if there is anything worthy of praise, think on these things [evaluate them, and fix your minds on them].",
            9 to "As for the things you have learned and received and heard and seen in me, practice these things in daily life, and the God of peace will be with you.",
            13 to "I can do all things through Him who strengthens and empowers me [to stand, to remain, to triumph]."
        ),
        // John 3 (verses 16-17)
        listOf(
            16 to "For God so [greatly] loved and dearly prized the world, that He [even] gave His [one and] only begotten Son, so that whoever believes and trusts in Him [as Savior] shall not perish, but have eternal life.",
            17 to "For God did not send the Son into the world to judge and condemn the world [that is, to initiate the judgment and doom of the world], but that the world might be saved through Him."
        ),
        // Romans 8 (verses 1-2, 31, 35, 37-39)
        listOf(
            1 to "Therefore there is now no condemnation [no guilty verdict, no punishment] for those who are in Christ Jesus, who walk not after the flesh, but after the Spirit.",
            2 to "For the law of the Spirit of life in Christ Jesus [the law of our new being] has set you free from the law of sin and of death.",
            31 to "What then shall we say to these things? If God is for us, who is against us? [Who can be our foe, if God is on our side?]",
            35 to "Who shall ever separate us from the love of Christ? Will tribulation, or distress, or persecution, or famine, or nakedness, or danger, or sword?",
            37 to "Yet in all these things we are more than conquerors and gain an overwhelming victory through Him who loved us.",
            38 to "For I am convinced [and continue to be convinced] that neither death, nor life, nor angels, nor principalities, nor things present and threatening, nor things to come, nor powers,",
            39 to "nor height, nor depth, nor any other created thing, will be able to separate us from the unlimited love of God, which is in Christ Jesus our Lord."
        ),
        // James 1 (verses 2-4, 12)
        listOf(
            2 to "Consider it nothing but joy, my brothers and sisters, whenever you fall into various trials,",
            3 to "be assured that the testing of your faith [through experience] produces endurance [leading to spiritual maturity, and inner peace].",
            4 to "And let endurance have its perfect result and do a thorough work, so that you may be perfect and completely developed [in your faith], lacking in nothing.",
            12 to "Blessed [happy, spiritually prosperous, favored by God] is the man who is steadfast under trial and perseveres when tempted; for when he has passed the test and been approved, he will receive the [victor's] crown of life which the Lord has promised to those who love Him."
        ),
        // Psalms 91 (verses 1-4)
        listOf(
            1 to "He who dwells in the shelter of the Most High will remain secure and rest in the shadow of the Almighty [whose power no enemy can withstand].",
            2 to "I will say of the Lord, 'He is my refuge and my fortress, My God, in whom I trust [with great confidence, and on whom I rely].'",
            3 to "For He will deliver you from the snare of the trapper and from the deadly pestilence.",
            4 to "He will cover you and shield you with His pinions, and under His wings you will find refuge; His faithfulness is a shield and a wall of protection."
        ),
        // James 3 (verses 2, 5)
        listOf(
            2 to "For we all stumble and sin in many ways. If anyone does not stumble in what he says [never saying the wrong thing], he is a perfect man [fully developed in character], able to bridle and guide his whole body as well.",
            5 to "In the same way, the tongue is a small part of the body, and yet it boasts of great things. See [by comparison] how great a forest is set on fire by a small spark!"
        )
    ),
    "The Message" to listOf(
        // Psalms 23
        listOf(
            1 to "God, my shepherd! I don't need a thing.",
            2 to "You have bedded me down in lush meadows, you find me quiet pools to drink from.",
            3 to "True to your word, you let me catch my breath and send me in the right direction.",
            4 to "Even when the way goes through Death Valley, I'm not afraid when you walk at my side. Your trusty shepherd's crook makes me feel secure.",
            5 to "You serve me a six-course dinner right in front of my enemies. You revive my drooping head; my cup brims with blessing.",
            6 to "Your beauty and love chase after me every day of my life. I'm back home in the House of God for the rest of my life."
        ),
        // Philippians 4 (verses 4-9, 13)
        listOf(
            4 to "Celebrate God all day, every day. I mean, revel in him!",
            5 to "Make it as clear as the wind what you are doing—that you're not claiming your own rights. The Master is about to arrive.",
            6 to "Don't fret or worry. Instead of worrying, pray. Let petitions and praises shape your worries into prayers, letting God know what is bothering you.",
            7 to "Before you know it, a sense of God’s wholeness, everything coming together for good, will come and settle you down. It’s wonderful what happens when Christ displaces worry at the center of your life.",
            8 to "Summing it all up, friends, I'd say you'll do best by filling your minds and meditating on things true, noble, reputable, authentic, compelling, gracious—the best, not the worst; the beautiful, not the ugly; things to praise, not things to curse.",
            9 to "Put into practice what you learned from me, what you heard and saw and realized. Do that, and God, who makes everything work together, will work you into his most excellent harmonies.",
            13 to "Whatever I have, wherever I am, I can make it through anything in the One who makes me who I am."
        ),
        // John 3 (verses 16-17)
        listOf(
            16 to "This is how much God loved the world: He gave his Son, his one and only Son. And why? So that no one need be destroyed; by believing in him, anyone can have a whole and lasting life.",
            17 to "God didn't go to all the trouble of sending his Son merely to point an accusing finger, telling the world how bad it was. He came to help, to put the world right again."
        ),
        // Romans 8 (verses 1-2, 31, 35, 37-39)
        listOf(
            1 to "With the arrival of Jesus, the Messiah, that fateful dilemma is resolved. Those who enter into Christ’s life-giving fellowship no longer have to live under a continuous, low-lying black cloud.",
            2 to "A new power is in operation. The Spirit of life in Christ, like a strong wind, has cleared the air and freed you from a fated lifetime of brutal tyranny at the hands of sin and death.",
            31 to "So, what do you think? With God on our side like this, how can we lose?",
            35 to "Do you think anyone is going to be able to drive a wedge between us and Christ’s love for us? There is no way! Not trouble, not hard times, not hatred, not hunger, not homelessness, not bullying threats, not backstabbing, not even the worst sins listed in Scripture.",
            37 to "None of this fusses us because Jesus loves us. I’m absolutely convinced that nothing—nothing living or dead, angelic or demonic, today or tomorrow, high or low, thinkable or unthinkable—absolutely nothing can get between us and God’s love because of the way that Jesus our Master has embraced us.",
            38 to "For I am persuaded that neither death nor life, nor angels nor principalities nor powers, nor things present nor things to come,",
            39 to "nor height nor depth, nor any other created thing, shall be able to separate us from the love of God which is in Christ Jesus our Lord."
        ),
        // James 1 (verses 2-4, 12)
        listOf(
            2 to "Consider it a sheer gift, friends, when tests and challenges come at you from all sides.",
            3 to "You know that under pressure, your faith-life is forced into the open and shows its true colors.",
            4 to "So don't try to get out of anything prematurely. Let it do its work so you become mature and well-developed, not deficient in any way.",
            12 to "Anyone who meets a testing challenge head-on and manages to stick it out is mighty fortunate. For such persons, loyally in love with God, the reward is life and more life."
        ),
        // Psalms 91 (verses 1-4)
        listOf(
            1 to "You who sit down in the High God's presence, spend the night in Almighty's shadow,",
            2 to "Say this: 'God, you're my refuge. I trust in you and I'm safe!'",
            3 to "That's right—he rescues you from hidden traps, he shields you from deadly hazards.",
            4 to "His huge outstretched arms protect you—under them you're perfectly safe; his arms fend off all harm."
        ),
        // James 3 (verses 2, 5)
        listOf(
            2 to "We all make mistakes of all kinds, constantly. If you find someone who never makes a mistake in what he says, then you've found a perfect person, in perfect control of himself.",
            5 to "A smoldering word can get a whole forest-fire going. It's easy to see what happens next..."
        )
    )
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BibleTabScreen(viewModel: OverComerViewModel) {
    var activeSubTab by remember { mutableStateOf(BibleSubTab.READER) }
    var selectedPassageIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    val aiResult by viewModel.aiScriptureResult.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearchingScripture.collectAsStateWithLifecycle()
    val selectedVersion by viewModel.selectedBibleVersion.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // App Header Banner
        AppCoverBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // Tab row header title
        Text(
            text = "📖 Holy Scripture Study",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        
        // Custom segmented selector
        BibleSubTabSelector(
            activeSubTab = activeSubTab,
            onSubTabSelected = { tab ->
                activeSubTab = tab
            }
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        // Bible Version Selector Chips
        Text(
            text = "Bible Translation Version:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val versions = listOf("NIV", "The Amplified", "NKJV", "KJV", "The Message")
            versions.forEach { version ->
                val isSelected = selectedVersion == version
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectBibleVersion(version) },
                    label = { Text(version, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("bible_version_chip_${version.replace(" ", "_").lowercase()}")
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Tab contents
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSubTab) {
                BibleSubTab.READER -> {
                    val activePassage = curatedPassages[selectedPassageIndex]
                    val displayedVerses = if (selectedVersion == "NIV") {
                        activePassage.verses
                    } else {
                        curatedPassagesTranslations[selectedVersion]?.getOrNull(selectedPassageIndex) ?: activePassage.verses
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "Select Curated Healing Scriptures:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(curatedPassages.size) { index ->
                                    val passage = curatedPassages[index]
                                    val isSelected = selectedPassageIndex == index
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedPassageIndex = index },
                                        label = { Text("${passage.book} ${passage.chapter}") },
                                        modifier = Modifier.testTag("bible_reader_chip_${index}")
                                    )
                                }
                            }
                        }
                        
                        item {
                            // Theme and context explainers
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = "Sovereign Theme", tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Core Theme: ${activePassage.theme}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activePassage.context,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                        
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${activePassage.book} ${activePassage.chapter}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = selectedVersion,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    
                                    displayedVerses.forEach { (number, text) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "$number",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.width(28.dp),
                                                textAlign = TextAlign.Start
                                            )
                                            Text(
                                                text = text,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        item {
                            Button(
                                onClick = {
                                    activeSubTab = BibleSubTab.AI_SEARCH
                                    searchQuery = "${activePassage.book} ${activePassage.chapter}"
                                    viewModel.lookupScripture("${activePassage.book} ${activePassage.chapter}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bible_explain_ai_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Explain ${activePassage.book} ${activePassage.chapter} with OverComer AI")
                            }
                        }
                    }
                }
                
                BibleSubTab.AI_SEARCH -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "Ask OverComer AI Study Guide to search and explain any scripture:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Bible Passage (e.g. John 8:36 or Psalms 91:4)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bible_search_input"),
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { searchQuery = ""; viewModel.clearScriptureSearch() }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear text")
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.lookupScripture(searchQuery)
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                        }
                        
                        item {
                            Button(
                                onClick = { viewModel.lookupScripture(searchQuery) },
                                enabled = searchQuery.isNotBlank() && !isSearching,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bible_search_submit_btn")
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Refining study plans...")
                                } else {
                                    Text("Seek Scripture & AI Explanation")
                                }
                            }
                        }
                        
                        item {
                            Text(
                                text = "Quick Study Suggestions:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val templates = listOf("John 8:36", "Romans 8:37", "James 4:7", "Proverbs 3:5-6", "1 Corinthians 10:13")
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                templates.forEach { label ->
                                    ElevatedFilterChip(
                                        selected = searchQuery == label,
                                        onClick = {
                                            searchQuery = label
                                            viewModel.lookupScripture(label)
                                        },
                                        label = { Text(label, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }
                        
                        if (isSearching) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(36.dp))
                                        Text(
                                            text = "Consulting the OverComer Scripture Archives...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Bringing down context, translations, and clinical pastoral reflections.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else if (aiResult != null) {
                            val result = aiResult!!
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bible_search_result_card"),
                                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Text(
                                            text = result.reference,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                        
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                text = "\"${result.text}\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                lineHeight = 20.sp
                                            )
                                        }
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "AI Study Commentary:",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        Text(
                                            text = result.explanation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = "Ready for Bible study!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Tap any suggestion chip above or write standard citations. Let God's word fuel your recovery.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                BibleSubTab.CREED -> {
                    ShieldCreedContent(viewModel)
                }
            }
        }
    }
}

@Composable
fun BibleSubTabSelector(
    activeSubTab: BibleSubTab,
    onSubTabSelected: (BibleSubTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = listOf(
            BibleSubTab.READER to "📖 Bible Reader",
            BibleSubTab.AI_SEARCH to "🔍 AI Study Guide",
            BibleSubTab.CREED to "🛡️ Creed & Shield"
        )
        tabs.forEach { (tab, label) ->
            val isSelected = activeSubTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                    .clickable { onSubTabSelected(tab) }
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ShieldCreedContent(viewModel: OverComerViewModel) {
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()

    val beliefs = remember(userPath) {
        val original = listOf(
            "True freedom begins in a personal, transformational relationship with Jesus Christ.",
            "The Holy Bible is where we learn who Christ is, who we are in Him, and find directional wisdom for our walk.",
            "Christ can completely deliver you from any addiction or struggle. John 8:36: \"So if the Son sets you free, you will be free indeed!\"",
            "Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage, but Christ sets us completely free of past chains.",
            "You have not gone too far nor been involved too much for Christ to accept you, clean you up, and crown you an OverComer.",
            "As soon as you repent and surrender your heart, your past is dead! You are a new creation! (2 Corinthians 5:17)",
            "You are NOT still an addict or permanently sick. You are now and forever an OverComer! (Revelation 12:11)",
            "When temptations trigger you, Christ is faithful to help you resist... He has suffered temptations too, and He always provides a way out."
        )
        when (userPath) {
            "MENTAL_HEALTH", "TOUGH_DAY" -> {
                original.map { belief ->
                    belief
                        .replace("addiction or struggle", "anxiety, depression, or heavy struggle")
                        .replace("Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage", "Your anxiety or distress is NOT your permanent emotional identity. It is a temporary weight")
                        .replace("addict or permanently sick", "defined by your distress or permanently broken")
                }
            }
            "VETERAN_TRANSITION" -> {
                original.map { belief ->
                    belief
                        .replace("addiction or struggle", "PTSD, transition struggles, or military-to-civilian hurdles")
                        .replace("Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage", "Your trauma or PTSD is NOT your permanent identity. It is a severe vulnerability, but Christ is your shield and fortress")
                        .replace("addict or permanently sick", "permanently damaged, broken, or isolated")
                }
            }
            "TESTIMONY_VICTORY" -> {
                original.map { belief ->
                    belief
                        .replace("addiction or struggle", "any struggle, trial, or challenge")
                        .replace("Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage", "Your battle is already won, and your identity is a victorious child of God")
                        .replace("addict or permanently sick", "defeated, broken, or discouraged")
                }
            }
            else -> original
        }
    }

    val mottoText = remember(userPath) {
        val originalMotto = "\"A OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory through the Power of our Savior and King Jesus Christ.\""
        when (userPath) {
            "MENTAL_HEALTH", "TOUGH_DAY" -> {
                originalMotto.replace("over addiction", "over fear, worry, and depression")
            }
            "VETERAN_TRANSITION" -> {
                originalMotto.replace("over addiction", "over PTSD, trauma, and civilian reintegration")
            }
            "TESTIMONY_VICTORY" -> {
                originalMotto.replace("over addiction", "over every battle, trial, and challenge")
            }
            else -> originalMotto
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛡️ OverComer Shield & Creed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Our Theological Foundation of Sovereign Victory",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            // Master Motto Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "THE OVERCOMER MOTTO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.3.sp
                    )
                    Text(
                        text = mottoText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "What an OverComer Believes:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        items(beliefs.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = beliefs[index],
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        }

        item {
            // Mission statement
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "OUR HOLY MISSION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "1. Be a safe place for those that are struggling with issues controlling their lives.\n" +
                               "2. Lead those struggling into a life-transforming relationship with Christ.\n" +
                               "3. Make Disciples for The Kingdom of God.\n" +
                               "4. Teach how to reproduce the life-transforming relationship we have had with Christ to others.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ShieldCreedTabScreen(viewModel: OverComerViewModel) {
    ShieldCreedContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspirationalQuotesTabScreen(viewModel: OverComerViewModel) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Overcoming Cravings", "Peace & Anxiety", "Strength & Faith", "Grace & Forgiveness")

    // Setup TextToSpeech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                     localTts?.setLanguage(Locale.getDefault())
                     localTts?.setSpeechRate(0.85f)
                     isTtsReady = true
                } catch (_: Exception) {}
            }
        }
        tts = localTts
        onDispose {
            localTts?.stop()
            localTts?.shutdown()
        }
    }

    fun speakQuote(text: String, reference: String) {
        if (isTtsReady) {
            val speechText = cleanTextForSpeech("\"$text\" from $reference")
            tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "InspirationalTabTTS")
        } else {
            Toast.makeText(context, "Voice reader is initializing...", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📖 Inspiration Bible Verses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reframing our minds with Truth and Victory",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("inspirational_category_${categories.indexOf(cat)}")
                )
            }
        }

        val userPath by viewModel.userPath.collectAsStateWithLifecycle()

        fun isRelevantToPath(category: String, path: String?): Boolean {
            if (path.isNullOrEmpty()) return true
            return when (path) {
                "SUBSTANCE_RECOVERY" -> category == "Overcoming Cravings" || category == "Grace & Forgiveness" || category == "Strength & Faith"
                "MENTAL_HEALTH" -> category == "Peace & Anxiety" || category == "Strength & Faith" || category == "Grace & Forgiveness"
                "TOUGH_DAY" -> category == "Peace & Anxiety" || category == "Strength & Faith"
                "VETERAN_TRANSITION" -> category == "Strength & Faith" || category == "Peace & Anxiety"
                "REENTRY_RESTORATION" -> category == "Grace & Forgiveness" || category == "Strength & Faith"
                else -> true
            }
        }

        val filteredVerses = inspirationalQuotes.filter { 
            (it.id in 1..12) && 
            (selectedCategory == "All" || it.category == selectedCategory) && 
            isRelevantToPath(it.category, userPath) 
        }
        val filteredFamousQuotes = inspirationalQuotes.filter { 
            (it.id in 13..24) && 
            (selectedCategory == "All" || it.category == selectedCategory) && 
            isRelevantToPath(it.category, userPath) 
        }

        var currentVerseIdx by remember(selectedCategory) { mutableStateOf(0) }
        var currentFamousQuoteIdx by remember(selectedCategory) { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Bible Verses
            if (filteredVerses.isNotEmpty()) {
                val verse = filteredVerses[currentVerseIdx.coerceIn(0, filteredVerses.size - 1)]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bible_verse_card_${verse.id}"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = verse.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Inspiration Bible Verse", "\"${verse.text}\"\n— ${verse.reference}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Bible verse copied! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Verse",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { speakQuote(verse.text, verse.reference) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen to verse",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "\"${verse.text}\"",
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "— ${verse.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "RENEW MY MIND:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = verse.contextReflection,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        currentVerseIdx = (currentVerseIdx + 1) % filteredVerses.size
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inspirational_next_verse_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Next Bible Verse", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Section 2: Inspirational Quotes by Famous People
            Text(
                text = "📜 Inspirational Quotes by Famous People",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Wisdom from trusted mentors & historical Christian figures",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (filteredFamousQuotes.isNotEmpty()) {
                val quote = filteredFamousQuotes[currentFamousQuoteIdx.coerceIn(0, filteredFamousQuotes.size - 1)]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("famous_quote_card_${quote.id}"),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = quote.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Inspirational Quote", "\"${quote.text}\"\n— ${quote.reference}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Quote copied! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Quote",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { speakQuote(quote.text, quote.reference) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen to quote",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "\"${quote.text}\"",
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "— ${quote.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "RENEW MY MIND:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = quote.contextReflection,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        currentFamousQuoteIdx = (currentFamousQuoteIdx + 1) % filteredFamousQuotes.size
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .testTag("inspirational_next_quote_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Next Quote", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Text(
                    text = "No quotes available in this category.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
    }
}

// ==========================================
// DAILY BIBLE-BASED AFFIRMATIONS
// ==========================================

data class BibleAffirmation(
    val id: Int,
    val text: String,
    val reference: String,
    val contextReflection: String
)

val bibleAffirmationsDefaults = listOf(
    BibleAffirmation(
        id = 1,
        text = "I am a new creation in Christ; the old is gone, the new has come.",
        reference = "2 Corinthians 5:17",
        contextReflection = "Because of Jesus, my past fails do not define me. Today, I walk in absolute brand-new beginnings!"
    ),
    BibleAffirmation(
        id = 2,
        text = "I can do all things through Christ who strengthens me.",
        reference = "Philippians 4:13",
        contextReflection = "My own willpower might falter, but Christ's internal resurrection power inside me is limitless. I am capable of resisting today's temptations."
    ),
    BibleAffirmation(
        id = 3,
        text = "God has not given me a spirit of fear, but of power, love, and a sound mind.",
        reference = "2 Timothy 1:7",
        contextReflection = "Anxiety and triggers have no legal hold over me. I claim a quiet, focused, disciplined mind under God's protection."
    ),
    BibleAffirmation(
        id = 4,
        text = "I am more than a conqueror through Him who loved me.",
        reference = "Romans 8:37",
        contextReflection = "I do not struggle FOR victory; I stand and fight FROM the victory already won for me on the Cross."
    ),
    BibleAffirmation(
        id = 5,
        text = "He who is in me is greater than he who is in the world.",
        reference = "1 John 4:4",
        contextReflection = "The Holy Spirit inside me is infinitely stronger than any chemical, habit, or sensory trigger in the surrounding environment."
    ),
    BibleAffirmation(
        id = 6,
        text = "Sin shall no longer have dominion over me, for I am under grace.",
        reference = "Romans 6:14",
        contextReflection = "I am completely delivered from the cage of guilt. Grace is my defense, my shield, and my master now."
    ),
    BibleAffirmation(
        id = 7,
        text = "The Lord is my strength and my shield; my heart trusts in Him, and I am helped.",
        reference = "Psalm 28:7",
        contextReflection = "I do not have to carry this burden alone. God is shielding my weak spots while I take one step at a time."
    ),
    BibleAffirmation(
        id = 8,
        text = "My body is a temple of the Holy Spirit, bought with a price to honor God.",
        reference = "1 Corinthians 6:19-20",
        contextReflection = "I declare my eyes, my thoughts, and my hands are tools of righteousness and holiness to bring God praise."
    ),
    BibleAffirmation(
        id = 9,
        text = "God's grace is sufficient for me, for His power is made perfect in weakness.",
        reference = "2 Corinthians 12:9",
        contextReflection = "When I feel exhausted and close to giving in, God's grace steps in to do what I cannot. I am strong in Him."
    ),
    BibleAffirmation(
        id = 10,
        text = "I am chosen, holy, and dearly loved by God.",
        reference = "Colossians 3:12",
        contextReflection = "My worth is set by my Father above. No bad day, relapse, or negative comment can change how much I am cherished."
    )
)

@Composable
fun BibleAffirmationsSection() {
    val items = bibleAffirmationsDefaults
    val initialIndex = remember {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        dayOfYear % bibleAffirmationsDefaults.size
    }
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val currentItem = items[currentIndex]
    
    // Track spoken/declared affirmation IDs in a stateful set
    var declaredIds by remember { mutableStateOf(setOf<Int>()) }
    val isDeclared = declaredIds.contains(currentItem.id)
    
    // Setup Local Text To Speech
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    val result = localTts?.setLanguage(Locale.getDefault())
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        localTts?.setLanguage(Locale.US)
                    }
                    localTts?.setSpeechRate(0.82f) // Comfortably slower, very natural reading pace
                    localTts?.setPitch(1.0f)
                } catch (_: Exception) {}
                isTtsReady = true
            }
        }
        tts = localTts
        onDispose {
            localTts?.stop()
            localTts?.shutdown()
        }
    }
    
    fun speakCurrent() {
        if (isTtsReady) {
            val speechText = cleanTextForSpeech("Affirmation: ${currentItem.text}. Scripture reference: ${currentItem.reference}.")
            tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "AffirmationTTS")
        } else {
            Toast.makeText(context, "Text to speech is initializing...", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("bible_affirmations_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VICTORY AFFIRMED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Read Aloud / Speaker Action
                IconButton(
                    onClick = { speakCurrent() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape)
                        .testTag("affirmation_speak_btn")
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Read affirmation aloud",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Carousel Text View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\"${currentItem.text}\"",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "— ${currentItem.reference}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Practical Devotional Reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "RENEW MY MIND:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = currentItem.contextReflection,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Interactive Declaration & Swiping Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Return previous index
                IconButton(
                    onClick = {
                        if (currentIndex > 0) currentIndex-- else currentIndex = items.size - 1
                    },
                    modifier = Modifier.testTag("affirmation_prev_btn")
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous affirmation"
                    )
                }

                // Center Declaration active stamp
                Button(
                    onClick = {
                        declaredIds = if (isDeclared) {
                            declaredIds - currentItem.id
                        } else {
                            declaredIds + currentItem.id
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDeclared) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isDeclared) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("affirmation_declare_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDeclared) Icons.Default.CheckCircle else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isDeclared) "DECLARED VICTORY! ✝" else "AFFIRM & DECLARE 🛡️",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Go to next index
                IconButton(
                    onClick = {
                        if (currentIndex < items.size - 1) currentIndex++ else currentIndex = 0
                    },
                    modifier = Modifier.testTag("affirmation_next_btn")
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next affirmation"
                    )
                }
            }

            // Scoreboard Progress bar metric of total affirmations completed
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Devotional Progress:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${declaredIds.size} of ${items.size} Spoken",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                val progress = declaredIds.size.toFloat() / items.size.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .testTag("affirmations_progress_bar"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            }
        }
    }
}

// ==========================================
// SUPPORT NETWORK SOS SMS CONNECTION CARD
// ==========================================

@Composable
fun SupportConnectionSmsCard() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("overcomer_sos_contacts", Context.MODE_PRIVATE) }

    // Retrieve custom role name (defaults to "Other/Add New")
    var customRoleName by remember {
        mutableStateOf(sharedPrefs.getString("custom_role_name", "Other/Add New") ?: "Other/Add New")
    }

    // Role options including Trusted Family and customRoleName
    val roles = remember(customRoleName) {
        listOf("Sponsor", "Mentor", "Spouse", "Best Friend", "Trusted Family", customRoleName)
    }
    
    var selectedRoleIndex by remember { mutableStateOf(0) }
    val selectedRole = roles.getOrElse(selectedRoleIndex) { "Sponsor" }

    // Helper to get phone key for selected role
    val phoneKey = remember(selectedRole, customRoleName) {
        if (selectedRole == customRoleName) "phone_custom_slot" else "phone_$selectedRole"
    }

    // Retrieve saved phone number for the selected role
    var phoneNumber by remember(phoneKey) {
        mutableStateOf(sharedPrefs.getString(phoneKey, "") ?: "")
    }

    // Default template options
    val templates = listOf(
        "I'm struggling pretty bad right now",
        "Please pray for me",
        "You said if I ever needed you to let you know. I need you"
    )
    var selectedTemplateIndex by remember { mutableStateOf(0) }
    var customMessageText by remember { mutableStateOf("") }

    // The message that will actually be sent
    val finalMessage = if (customMessageText.isNotBlank()) customMessageText else templates.getOrNull(selectedTemplateIndex) ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("support_sos_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "🚨 SOS SUPPORT NETWORK TEXT",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Add phone numbers for your inner circle below. In moments of temptation, tap a pre-built message or type your own to text them instantly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Horizontal Selectors for Roles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                roles.forEachIndexed { index, role ->
                    val isSelected = selectedRoleIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedRoleIndex = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = role,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Name Customization for custom role
            val isCustomRoleSelected = selectedRole == customRoleName
            if (isCustomRoleSelected) {
                var tempRoleName by remember { mutableStateOf(if (customRoleName == "Other/Add New") "" else customRoleName) }
                OutlinedTextField(
                    value = tempRoleName,
                    onValueChange = { newValue ->
                        tempRoleName = newValue
                        val savedName = if (newValue.trim().isBlank()) "Other/Add New" else newValue.trim()
                        customRoleName = savedName
                        sharedPrefs.edit().putString("custom_role_name", savedName).apply()
                    },
                    label = { Text("Customize Label (e.g. Pastor, Coach, Brother)") },
                    placeholder = { Text("Other/Add New") },
                    modifier = Modifier.fillMaxWidth().testTag("sos_custom_role_name_field"),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                )
            }

            // Phone Field corresponding to selected role
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    phoneNumber = newValue
                    sharedPrefs.edit().putString(phoneKey, newValue).apply()
                },
                label = { Text("$selectedRole's Phone / Contact") },
                placeholder = { Text("e.g. 704-555-0199") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().testTag("sos_phone_field"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                singleLine = true
            )

            // Preset templates header
            Text(
                text = "CHOOSE A QUICK TEMPLATE:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.secondary
            )

            // Vertical list of presets
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                templates.forEachIndexed { index, template ->
                    val isCurrentPreset = selectedTemplateIndex == index && customMessageText.isEmpty()
                    Surface(
                        onClick = {
                            selectedTemplateIndex = index
                            customMessageText = "" // clear custom text to default to chosen template
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrentPreset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isCurrentPreset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isCurrentPreset,
                                onClick = {
                                    selectedTemplateIndex = index
                                    customMessageText = ""
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = template,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCurrentPreset) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Space to create their own short text
            OutlinedTextField(
                value = customMessageText,
                onValueChange = { customMessageText = it },
                label = { Text("Or Type Your Custom SOS Message") },
                placeholder = { Text("e.g. I am in a trigger area. Please call me ASAP.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sos_custom_msg_field"),
                maxLines = 3
            )

            // Submit Button to open Text App
            Button(
                onClick = {
                    try {
                        val smsUriStr = if (phoneNumber.isNotBlank()) {
                            "smsto:$phoneNumber"
                        } else {
                            "smsto:"
                        }
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(smsUriStr)).apply {
                            putExtra("sms_body", finalMessage)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open messaging client.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sos_send_sms_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEXT YOUR ${selectedRole.uppercase()}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

// ==========================================
// CLINICAL EMERGENCY SOVEREIGN SHIELD (PANIC OVERLAY)
// ==========================================

@Composable
fun PanicOverlayDialog(onDismiss: () -> Unit) {
    var selectedPanicTab by remember { mutableStateOf(0) } // 0 = Grounding, 1 = Support Contacts, 2 = Global Help

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Block with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(12.dp)
                        ) {}
                        Text(
                            text = "SOVEREIGN SHIELD SOS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("panic_close_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close overlay")
                    }
                }
                
                Text(
                    text = "Pause immediately. Breathe deep. God is your refuge and strength, a very present help in trouble.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Beautiful Custom Segmented Tabs Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("🧘 Grounding", "📞 Inner Circle", "🛡️ Helplines")
                    tabs.forEachIndexed { i, title ->
                        val isSelected = selectedPanicTab == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .clickable { selectedPanicTab = i }
                                .padding(vertical = 10.dp)
                                .testTag("panic_tab_$i"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedPanicTab) {
                        0 -> GroundingExerciseTabContent()
                        1 -> SupportContactsTabContent()
                        2 -> GlobalHelplinesTabContent()
                    }
                }
            }
        }
    }
}

@Composable
fun GroundingExerciseTabContent() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            BreathingPulseAssist()
        }
        item {
            SensoryGroundingAssist()
        }
    }
}

@Composable
fun BreathingPulseAssist() {
    var phase by remember { mutableStateOf("Ready") }
    var secondsLeft by remember { mutableStateOf(0) }
    var pulseActive by remember { mutableStateOf(false) }

    LaunchedEffect(pulseActive) {
        if (pulseActive) {
            while (true) {
                // Inhale
                phase = "Breathe In (Fill your lungs)"
                secondsLeft = 4
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
                // Hold
                phase = "Hold (Be still)"
                secondsLeft = 4
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
                // Exhale
                phase = "Exhale (Release stress)"
                secondsLeft = 6
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
            }
        } else {
            phase = "Ready to Reset"
            secondsLeft = 0
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("breathing_assist_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Paced Breathing Assist 💨",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            val animatedScale by animateFloatAsState(
                targetValue = if (!pulseActive) 1.0f 
                else when {
                    phase.startsWith("Breathe") -> 1.3f
                    phase.startsWith("Hold") -> 1.3f
                    else -> 0.8f
                },
                animationSpec = tween(durationMillis = if (phase.startsWith("Ready")) 500 else 4000)
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (pulseActive) {
                        Text(
                            text = "$secondsLeft s",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Text(
                text = phase,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { pulseActive = !pulseActive },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pulseActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("panic_breathing_toggle_btn")
            ) {
                Text(
                    text = if (pulseActive) "Stop Breathing Guide" else "Begin Deep Breathing",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SensoryGroundingAssist() {
    var currentStep by remember { mutableStateOf(5) }
    val stepText = when (currentStep) {
        5 -> "👁️ Gaze around and name of 5 things you can see (e.g. lamp, table, wall photograph, clock, book)."
        4 -> "🖐️ Touch 4 distinct physical textures near you. Feel your clothing, table edge, phone bezel, soft pillow."
        3 -> "👂 Listen closely. Identify 3 sounds you can hear right now (e.g. air conditioning humming, generic ticking, birds chirping)."
        2 -> "👃 Actively inhale and notice 2 distinct aromas or scents (e.g. skin lotion, warm beverage, dry wood)."
        1 -> "👅 Name 1 taste inside your mouth (or take a comforting sip of fresh water to reset taste buds)."
        else -> "🎉 Magnificently Grounded! Your nervous system is de-escalating. You are safe in the present moment."
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("sensory_grounding_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "5-4-3-2-1 Sensory Grounding Method 🧘‍♀️",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Text(
                text = "This clinically proven exercise physically slows physiological distress responses by re-focusing active attention onto real-world sensations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (currentStep in 1..5) "STEP $currentStep OF 5" else "GROUNDING COMPLETE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    Button(
                        onClick = { currentStep-- },
                        modifier = Modifier.testTag("panic_grounding_next_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("I can sense this. Next →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (currentStep == 1) {
                    Button(
                        onClick = { currentStep = 0 },
                        modifier = Modifier.testTag("panic_grounding_finish_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Complete Exercise ✔️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = { currentStep = 5 },
                        modifier = Modifier.testTag("panic_grounding_restart_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Restart Grounding", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SupportContactsTabContent() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("overcomer_sos_contacts", Context.MODE_PRIVATE) }
    
    var customRoleName by remember {
        mutableStateOf(sharedPrefs.getString("custom_role_name", "Other/Add New") ?: "Other/Add New")
    }

    val roles = remember(customRoleName) {
        listOf("Sponsor", "Mentor", "Spouse", "Best Friend", "Trusted Family", customRoleName)
    }
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "My Personal Supporter Circle 🤝",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "These are your pre-defined supporters. Tap to dial or text immediately.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(roles.size) { index ->
            val role = roles[index]
            val isCustomRole = index == roles.size - 1
            val phoneKey = if (isCustomRole) "phone_custom_slot" else "phone_$role"

            var phone by remember(phoneKey) {
                mutableStateOf(sharedPrefs.getString(phoneKey, "") ?: "")
            }
            var showEditPhone by remember { mutableStateOf(false) }
            var editPhoneInput by remember { mutableStateOf(phone) }
            var editRoleNameInput by remember { mutableStateOf(customRoleName) }

            Card(
                modifier = Modifier.fillMaxWidth().testTag("supporter_card_$role"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = role,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (phone.isNotBlank()) {
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                Text(
                                    text = "No phone number saved",
                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Call Button
                            IconButton(
                                onClick = {
                                    if (phone.isNotBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Cannot open dialer.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Please configure phone first.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = phone.isNotBlank(),
                                modifier = Modifier.testTag("panic_call_$role")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call $role",
                                    tint = if (phone.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }

                            // SMS button
                            IconButton(
                                onClick = {
                                    if (phone.isNotBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
                                                putExtra("sms_body", "I'm experiencing high temptation / distress. Please reach out to me!")
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Cannot open messenger", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Please configure phone first.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = phone.isNotBlank(),
                                modifier = Modifier.testTag("panic_sms_$role")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Message $role",
                                    tint = if (phone.isNotBlank()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                                )
                            }

                            // Edit Button
                            IconButton(
                                onClick = { 
                                    editPhoneInput = phone
                                    editRoleNameInput = customRoleName
                                    showEditPhone = !showEditPhone 
                                },
                                modifier = Modifier.testTag("panic_edit_btn_$role")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit phone",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    if (showEditPhone) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isCustomRole) {
                                OutlinedTextField(
                                    value = editRoleNameInput,
                                    onValueChange = { editRoleNameInput = it },
                                    label = { Text("Custom Supporter Label") },
                                    placeholder = { Text("Other/Add New") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("phone_edit_name_input_$role")
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = editPhoneInput,
                                    onValueChange = { editPhoneInput = it },
                                    label = { Text("Phone Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("phone_edit_input_$role")
                                )
                                Button(
                                    onClick = {
                                        phone = editPhoneInput
                                        sharedPrefs.edit().putString(phoneKey, editPhoneInput).apply()
                                        if (isCustomRole) {
                                            val savedName = if (editRoleNameInput.trim().isBlank()) "Other/Add New" else editRoleNameInput.trim()
                                            customRoleName = savedName
                                            sharedPrefs.edit().putString("custom_role_name", savedName).apply()
                                        }
                                        showEditPhone = false
                                    },
                                    modifier = Modifier.testTag("phone_save_btn_$role")
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalHelplinesTabContent() {
    val context = LocalContext.current
    
    val helplines = listOf(
        CrisisHelpline(
            name = "988 Suicide & Crisis Lifeline",
            description = "Confidential, 24/7 free emotional support for anyone in high distress or experiencing triggering crises.",
            contact = "988",
            isSms = true,
            smsValue = "HOME"
        ),
        CrisisHelpline(
            name = "SAMHSA National Helpline",
            description = "Government mental health agency providing immediate guidance, referrals, and localized treatment resources.",
            contact = "1-800-662-4357",
            isSms = false
        ),
        CrisisHelpline(
            name = "Crisis Text Line",
            description = "Instantly correspond with a live certified mental health specialist via text messaging.",
            contact = "741741",
            isSms = true,
            smsValue = "HOME"
        ),
        CrisisHelpline(
            name = "National Emergency Services (911)",
            description = "Immediately request urgent tactical dispatcher assistance if you are in physical danger.",
            contact = "911",
            isSms = false
        )
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Professional Crisis Lifelines 🛡️",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Free, confidential, professional service networks available 24 hours a day, 7 days a week.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(helplines.size) { index ->
            val helpline = helplines[index]
            Card(
                modifier = Modifier.fillMaxWidth().testTag("helpline_card_${helpline.contact}"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = helpline.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = helpline.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${helpline.contact}"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot dial helpline.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("helpline_call_btn_${helpline.contact}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call ${helpline.contact}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (helpline.isSms) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${helpline.contact}")).apply {
                                            putExtra("sms_body", helpline.smsValue)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cannot open messenger.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("helpline_sms_btn_${helpline.contact}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (helpline.smsValue == "HOME") "Sms 'HOME'" else "Text ${helpline.contact}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CrisisHelpline(
    val name: String,
    val description: String,
    val contact: String,
    val isSms: Boolean,
    val smsValue: String = ""
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecoveryLessonsSection() {
    val context = LocalContext.current
    var activeLessonIndex by remember { mutableStateOf<Int?>(null) }
    var isAcademyExpanded by remember { mutableStateOf(false) }
    val expandedFolders = remember { mutableStateMapOf<Int, Boolean>() }
    
    val prefs = remember { context.getSharedPreferences("overcomer_lessons_data", Context.MODE_PRIVATE) }
    var completedCount by remember { mutableStateOf(0) }
    
    fun isLessonCompleted(index: Int): Boolean {
        return prefs.getBoolean("lesson_completed_$index", false)
    }
    
    fun setLessonCompleted(index: Int, completed: Boolean) {
        prefs.edit().putBoolean("lesson_completed_$index", completed).apply()
        var count = 0
        for (i in 0 until 14) {
            if (prefs.getBoolean("lesson_completed_$i", false)) count++
        }
        completedCount = count
    }

    LaunchedEffect(Unit) {
        var count = 0
        for (i in 0 until 14) {
            if (prefs.getBoolean("lesson_completed_$i", false)) count++
        }
        completedCount = count
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("recovery_academy_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeLessonIndex == null) {
                // Header (Click to expand)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAcademyExpanded = !isAcademyExpanded }
                        .padding(bottom = if (isAcademyExpanded) 8.dp else 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Overcomer",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Obedience Academy",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Substance Recovery Steps & Lessons",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = "$completedCount / 14 Completed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Reflectively study and answer guided lessons directly adapted, organized within our foldered Overcomer steps.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isAcademyExpanded) "Click to collapse" else "Click here",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        )
                        Icon(
                            imageVector = if (isAcademyExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isAcademyExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                val courses = listOf(
                    CourseSummary("The 7-Step Recovery Program", "Full theological choice-based 7-Step program with scriptural foundations.", Icons.Default.Shield, Color(0xFF0D47A1)),
                    CourseSummary("Trusting God's Process", "Waiting, Preparation, and Anointing vs Appointment (David & Bamboo Tree).", Icons.Default.Star, Color(0xFF00796B)),
                    CourseSummary("I AM An OverComer Affirmations", "Scripture values & identity declarations of who God says you are.", Icons.Default.Favorite, Color(0xFFE65100)),
                    CourseSummary("Trigger Prevention & Management", "Avoiding pitfalls, fleeing temptations, and divine escape options.", Icons.Default.Warning, Color(0xFFC62828)),
                    CourseSummary("OverComer Ministry & Fellowship", "Recovery fellowship, community meetings on Thursdays at The Refuge.", Icons.Default.Person, Color(0xFF512DA8)),
                    CourseSummary("Family & Parents Restoration", "Appreciating Mom and Dad, whether on Earth or in Heaven.", Icons.Default.Home, Color(0xFF4CAF50)),
                    CourseSummary("I Can't Catch My Breath", "Biblical and medical insights on Anxiety, Depression, and Trauma (PTSD).", Icons.Default.Warning, Color(0xFF00BCD4)),
                    CourseSummary("Freedom from Fear (Nakia's Miracle)", "Shane Merrill's testimony of divine preparation, crash, and miraculous healing.", Icons.Default.Star, Color(0xFFFF9800)),
                    CourseSummary("Overcoming Guilt & Shame", "Differentiating conduct (guilt) from identity (shame) with Brené Brown research.", Icons.Default.Favorite, Color(0xFF9C27B0)),
                    CourseSummary("The Emotions of the Journey", "The 12 emotions from Fear to Giving Back & The Rich Young Ruler (Matt 19).", Icons.Default.List, Color(0xFF009688)),
                    CourseSummary("True Repentance & Your Value", "What repentance truly means & our value shown by the Gilded Dollar Bill story.", Icons.Default.Star, Color(0xFFE91E63)),
                    CourseSummary("Facing Your Faults & Confession", "Pastor Wayne Cordeiro's study and Biblical principles of Confession.", Icons.Default.Check, Color(0xFF3F51B5)),
                    CourseSummary("Overcoming Temptation", "Wilderness temptation, Gethsemane anguish & Pastor DJ Byrd's tribute.", Icons.Default.Lock, Color(0xFF795548)),
                    CourseSummary("Follow the Voice (Anchorage Storm)", "The miracle landing in Alaska—the Cross of lights is the way home.", Icons.Default.Phone, Color(0xFF673AB7))
                )

                val stepFolders = listOf(
                    StepFolder(1, "Step 1: Admit Your Weakness", "Acknowledge powerless habits and reach for Him.", Icons.Default.Shield, Color(0xFF0D47A1), listOf(0, 6)),
                    StepFolder(2, "Step 2: Repent to God", "Deep sincere repentance and cleansing.", Icons.Default.Star, Color(0xFFE91E63), listOf(10)),
                    StepFolder(3, "Step 3: Release to God", "Sovereign surrender of control to God's lead.", Icons.Default.Favorite, Color(0xFF00796B), listOf(1, 7, 13)),
                    StepFolder(4, "Step 4: Examine Yourself", "Taking an honest moral inventory of habits.", Icons.Default.List, Color(0xFF009688), listOf(9, 8)),
                    StepFolder(5, "Step 5: Acknowledge & Apologize", "Confessing to God and making human amends.", Icons.Default.Check, Color(0xFF3F51B5), listOf(11, 5)),
                    StepFolder(6, "Step 6: Seek God's Presence", "Continuous morning devotion and combatting triggers.", Icons.Default.Search, Color(0xFF795548), listOf(2, 3, 12)),
                    StepFolder(7, "Step 7: Help Others", "Sowing seeds of restoration into other lives.", Icons.Default.Person, Color(0xFF512DA8), listOf(4))
                )

                if (isAcademyExpanded) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    stepFolders.forEach { folder ->
                        val completedInFolder = folder.lessonIndices.count { isLessonCompleted(it) }
                        val totalInFolder = folder.lessonIndices.size
                        val isExpanded = expandedFolders[folder.stepNumber] ?: false

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_step_folder_${folder.stepNumber}"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (completedInFolder == totalInFolder) folder.color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (completedInFolder == totalInFolder) folder.color.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                            )
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedFolders[folder.stepNumber] = !isExpanded }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = folder.icon,
                                        contentDescription = null,
                                        tint = folder.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = folder.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = folder.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$completedInFolder / $totalInFolder Completed",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (completedInFolder == totalInFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = "Expand folder",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                if (isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 2.dp)
                                            .padding(bottom = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        folder.lessonIndices.forEach { idx ->
                                            val course = courses[idx]
                                            val isDone = isLessonCompleted(idx)
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("recovery_lesson_option_$idx")
                                                    .clickable { activeLessonIndex = idx },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isDone) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                                ),
                                                border = BorderStroke(0.5.dp, if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .background(course.accentColor.copy(alpha = 0.12f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = course.icon,
                                                            contentDescription = null,
                                                            tint = course.accentColor,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = course.title,
                                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = course.description,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    if (isDone) {
                                                        Text(
                                                            text = "✓ DONE",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    } else {
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowRight,
                                                            contentDescription = "Start",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                }
            } else {
                val currentIdx = activeLessonIndex!!
                val isDone = isLessonCompleted(currentIdx)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { activeLessonIndex = null },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to lesson list"
                        )
                    }
                    Text(
                        text = "Lesson ${currentIdx + 1} of 14",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isDone) {
                        Text(
                            text = "✓ COMPLETED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                when (currentIdx) {
                    0 -> LessonOneContent(prefs) { setLessonCompleted(0, it) }
                    1 -> LessonTwoContent(prefs) { setLessonCompleted(1, it) }
                    2 -> LessonThreeContent(prefs) { setLessonCompleted(2, it) }
                    3 -> LessonFourContent(prefs) { setLessonCompleted(3, it) }
                    4 -> LessonFiveContent(prefs) { setLessonCompleted(4, it) }
                    5 -> LessonSixContent(prefs) { setLessonCompleted(5, it) }
                    6 -> LessonSevenContent(prefs) { setLessonCompleted(6, it) }
                    7 -> LessonEightContent(prefs) { setLessonCompleted(7, it) }
                    8 -> LessonNineContent(prefs) { setLessonCompleted(8, it) }
                    9 -> LessonTenContent(prefs) { setLessonCompleted(9, it) }
                    10 -> LessonElevenContent(prefs) { setLessonCompleted(10, it) }
                    11 -> LessonTwelveContent(prefs) { setLessonCompleted(11, it) }
                    12 -> LessonThirteenContent(prefs) { setLessonCompleted(12, it) }
                    13 -> LessonFourteenContent(prefs) { setLessonCompleted(13, it) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        setLessonCompleted(currentIdx, !isDone)
                        Toast.makeText(context, if (!isDone) "Lesson Marked as Completed! 🎉" else "Lesson Status Reset.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("toggle_lesson_completion_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isDone) "Reset Completion Status" else "Mark Lesson as Completed ✓", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class CourseSummary(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

data class StepFolder(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val lessonIndices: List<Int>
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LessonOneContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var activeStep by remember { mutableStateOf(1) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🛡️ Overcomers Recovery Ministries 7 Step Program",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Complete each step's study and write down your guided reflections under the guidance of the Holy Spirit.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(7) { sIdx ->
                val stepNum = sIdx + 1
                val isSelected = activeStep == stepNum
                FilterChip(
                    selected = isSelected,
                    onClick = { activeStep = stepNum },
                    label = { Text("Step $stepNum") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        when (activeStep) {
            1 -> StepOneDetail(prefs)
            2 -> StepTwoDetail(prefs)
            3 -> StepThreeDetail(prefs)
            4 -> StepFourDetail(prefs)
            5 -> StepFiveDetail(prefs)
            6 -> StepSixDetail(prefs)
            7 -> StepSevenDetail(prefs)
        }
    }
}

@Composable
fun ScriptureQuoteRow(quote: String, ref: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "\"$quote\"",
            style = MaterialTheme.typography.bodySmall.copy(
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "— $ref",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun GuidedQuestionsCard(
    stepIndex: Int,
    prefs: android.content.SharedPreferences,
    questions: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "🙋 GUIDED QUESTIONS (Allow the Holy Spirit to lead you):",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            questions.forEachIndexed { qIdx, question ->
                val answerKey = "step_${stepIndex}_q_${qIdx}"
                var answerText by remember(answerKey) {
                    mutableStateOf(prefs.getString(answerKey, "") ?: "")
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${qIdx + 1}. $question",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = {
                            answerText = it
                            prefs.edit().putString(answerKey, it).apply()
                        },
                        placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth().testTag("recovery_input_step_${stepIndex}_q_$qIdx"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = false,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

@Composable
fun StepOneDetail(prefs: android.content.SharedPreferences) {
    var check1 by remember { mutableStateOf(prefs.getBoolean("step1_indicator_1", false)) }
    var check2 by remember { mutableStateOf(prefs.getBoolean("step1_indicator_2", false)) }
    var check3 by remember { mutableStateOf(prefs.getBoolean("step1_indicator_3", false)) }
    var check4 by remember { mutableStateOf(prefs.getBoolean("step1_indicator_4", false)) }
    var check5 by remember { mutableStateOf(prefs.getBoolean("step1_indicator_5", false)) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 1: Admit Your Weakness",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Admit you have a problem and are powerless over addiction/weight.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow(
                    quote = "For I know that in me (that is, in my flesh,) dwells no good thing. I want to do what is right, but I can’t.",
                    ref = "Romans 7:18"
                )
                ScriptureQuoteRow(
                    quote = "If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness.",
                    ref = "1 John 1:9"
                )
                ScriptureQuoteRow(
                    quote = "Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.",
                    ref = "Proverbs 28:13"
                )
                ScriptureQuoteRow(
                    quote = "Are you tired? Worn out? Burned out on religion? Come to me. Get away with me and you’ll recover your life. I’ll show you how to take a real rest. Walk with me and work with me—watch how I do it. Learn the unforced rhythms of grace...",
                    ref = "Matthew 11:28-30 MSG"
                )
                ScriptureQuoteRow(
                    quote = "But he said to me, 'My grace is sufficient for you, for my power is made perfect in weakness.' Therefore I will boast all the more gladly about my weaknesses, so that Christ's power may rest on me.",
                    ref = "2 Corinthians 12:9-10 NIV"
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "❓ Why is Admitting a Problem So Important?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Admitting there is a problem indicates you are becoming aware of your problematic behaviors and how they contribute to a larger concern. It creates a healthy, humble mindset to begin working on the many difficult components of your weights, which were previously avoided or ignored.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🔍 Powerlessness Checklist (Honest Check-in)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Ask yourself these questions and check any that apply to your current situation:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                val indicators = listOf(
                    "Does my struggle interfere with healthy habits like spending time with family, reading my Bible, praying, or sleeping/eating well?" to 1,
                    "Does my struggle cause me to miss important obligations surrounding work, family, or personal commitments?" to 2,
                    "Do I fall back into my struggle to help relieve stress and anxiety?" to 3,
                    "Are my tolerance levels of dealing with people lower when faced with a situation?" to 4,
                    "Do I hide my struggle from my loved ones?" to 5
                )

                indicators.forEach { (text, idx) ->
                    val checked = when(idx) {
                        1 -> check1
                        2 -> check2
                        3 -> check3
                        4 -> check4
                        else -> check5
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            val newVal = !checked
                            when(idx) {
                                1 -> { check1 = newVal; prefs.edit().putBoolean("step1_indicator_1", newVal).apply() }
                                2 -> { check2 = newVal; prefs.edit().putBoolean("step1_indicator_2", newVal).apply() }
                                3 -> { check3 = newVal; prefs.edit().putBoolean("step1_indicator_3", newVal).apply() }
                                4 -> { check4 = newVal; prefs.edit().putBoolean("step1_indicator_4", newVal).apply() }
                                5 -> { check5 = newVal; prefs.edit().putBoolean("step1_indicator_5", newVal).apply() }
                            }
                        }.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { newVal ->
                                when(idx) {
                                    1 -> { check1 = newVal; prefs.edit().putBoolean("step1_indicator_1", newVal).apply() }
                                    2 -> { check2 = newVal; prefs.edit().putBoolean("step1_indicator_2", newVal).apply() }
                                    3 -> { check3 = newVal; prefs.edit().putBoolean("step1_indicator_3", newVal).apply() }
                                    4 -> { check4 = newVal; prefs.edit().putBoolean("step1_indicator_4", newVal).apply() }
                                    5 -> { check5 = newVal; prefs.edit().putBoolean("step1_indicator_5", newVal).apply() }
                                }
                            }
                        )
                        Text(text = text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "⚓ How Do You Complete Step 1?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "• Accept complete defeat: Acknowledge that something is wrong and you can't fix it on your own.\n" +
                           "• Embrace the truth: Break through self-deception and want to make an honest change.\n" +
                           "• Understand recovery can't be done alone: You need help from God and His body.\n" +
                           "• Abandon pride and seek humility: Modesty and humbleness are found in admitting your powerless state.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        GuidedQuestionsCard(
            stepIndex = 1,
            prefs = prefs,
            questions = listOf(
                "What specific addiction or weight are you struggling with? (Be fully honest before God).",
                "Share your story or testimony of how this struggle began and took hold of your life.",
                "How do you think taking this first step of admitting your weakness will help you?",
                "How can you begin to address and dismantle denial in your life?"
            )
        )
    }
}

@Composable
fun StepTwoDetail(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 2: Repent to God",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "To truly repent, you must not only turn away from your sins, but turn towards God. Repentance allows you to enjoy the freedom of your loving relationship with God.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow("Repent, then, and turn to God, so that your sins may be wiped out, that times of refreshing may come from the Lord.", "Acts 3:19")
                ScriptureQuoteRow("If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness.", "1 John 1:9")
                ScriptureQuoteRow("Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.", "Proverbs 28:13")
                ScriptureQuoteRow("For the Lord your God is gracious and compassionate. He will not turn his face from you if you return to him.", "2 Chronicles 30:9")
                ScriptureQuoteRow("Produce fruit in keeping with repentance.", "Matthew 3:8")
                ScriptureQuoteRow("The Lord is not slow in keeping his promise, as some understand slowness. Instead he is patient with you, not wanting anyone to perish, but everyone to come to repentance.", "2 Peter 3:9")
                ScriptureQuoteRow("From that time on Jesus began to preach, 'Repent, for the kingdom of heaven has come near.'", "Matthew 4:17")
                ScriptureQuoteRow("But go and learn what this means: 'I desire mercy, not sacrifice.' For I have not come to call the righteous, but sinners.", "Matthew 9:13")
                ScriptureQuoteRow("Come near to God and he will come near to you. Wash your hands, you sinners, and purify your hearts, you double-minded.", "James 4:8")
                ScriptureQuoteRow("Those whom I love I rebuke and discipline. So be earnest and repent.", "Revelation 3:19")
                ScriptureQuoteRow("Rend your heart and not your garments. Return to the Lord your God, for he is gracious and compassionate, slow to anger and abounding in love...", "Joel 2:13")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "⚙️ What Does Repentance Mean?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Repentance means:\n" +
                           "1. To turn from sin and dedicate oneself to the amendment of one's life.\n" +
                           "2. To feel regret or contrition.\n" +
                           "3. To change one's mind.\n\n" +
                           "It's not just feeling bad—it's a change of direction!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🔄 The TURN Framework",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "How do you turn your life over to Jesus Christ? You TRUST:\n\n" +
                           "• Trust: Deciding to turn your life and will over to God requires trust. Romans 10:9 says if you confess with your mouth and believe in your heart, you will be saved.\n\n" +
                           "• Understand: Relying solely on your own understanding got you into trouble. Proverbs 3:5-6 says trust in the Lord with all your heart, and He will direct your paths.\n\n" +
                           "• Repent: Turning away from sin and towards God. Romans 12:2 says be transformed by the renewing of your mind. You receive a new life (2 Corinthians 5:17) and are declared not guilty!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        GuidedQuestionsCard(
            stepIndex = 2,
            prefs = prefs,
            questions = listOf(
                "What is stopping you from asking Jesus Christ into your heart as your Lord and Savior? (If you already have, describe that experience).",
                "How has relying on your own understanding caused problems in your life? Be specific.",
                "What does repent mean to you? What do you feel you need to repent of today?",
                "What does the declaration of 'not guilty' in Romans 3:24-26 mean to you personally?",
                "When you turn your life over to Christ, you have a new life (2 Corinthians 5:17). What does that new life mean to you?"
            )
        )
    }
}

@Composable
fun StepThreeDetail(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 3: Release to God",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Turn the complete control of your life over to God. Cast off the past, failures, hurts, and struggles.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow("I urge you brothers and sisters, in the view of God’s mercy, to offer your bodies as a living sacrifice, holy and pleasing to God – this is your true and proper worship.", "Romans 12:1")
                ScriptureQuoteRow("Casting all your care upon him; for he careth for you. Be sober, be vigilant; because your adversary the devil, as a roaring lion, walketh about, seeking whom he may devour.", "1 Peter 5:7-8")
                ScriptureQuoteRow("Come unto me, all ye that labor and are heavy laden, and I will give you rest. Take my yoke upon you and learn of me; for I am meek and lowly in heart: and ye shall find rest unto your souls. For my yoke is easy and my burden is light.", "Matthew 11:28-30")
                ScriptureQuoteRow("He is before all things, and in him all things hold together.", "Colossians 1:17")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🧬 Laminin: The Protein Branded by the Cross",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Laminin is a cell adhesion molecule—the protein 'rebar' or 'glue' that literally holds the human body together.\n\n" +
                           "Remarkably, laminin is shaped exactly like a Cross! Colossians 1:15-17 says: 'He is before all things, and in him all things hold together.' God formed you so that your very body is held together by the shape of the Cross! You are branded by His love!\n\n" +
                           "Therefore, we must **RELEASE** things. We must cast our past failures, who we used to be, our hurts, struggles, and what others said off of us and onto Jesus, because HE holds us together!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "⚓ 'Casting' Literally Means 'Throw Upon'",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "In 1 Peter 5:7, 'casting' means to literally throw upon Him. Don't gently hand over your burdens and then take them back; throw them upon Jesus, because He is more than capable of holding your weight.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        GuidedQuestionsCard(
            stepIndex = 3,
            prefs = prefs,
            questions = listOf(
                "Explain what a life fully turned over to God looks like daily.",
                "Explain what Him taking control of your impulses and choices looks like.",
                "Write down your story of how Christ has worked for and through you in a past struggle or choice."
            )
        )
    }
}

@Composable
fun StepFourDetail(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 4: Examine Yourself",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Take a moral inventory of yourself and decisions that you’ve made.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow("Let us examine our ways and test them, and let us return to the Lord.", "Lamentations 3:40")
                ScriptureQuoteRow("Examine yourselves to see whether you are in the faith; test yourselves. Do you not realize that Christ Jesus is in you—unless, of course, you fail the test?", "2 Corinthians 13:5")
                ScriptureQuoteRow("Blessed are the pure in heart, for they will see God.", "Matthew 5:8")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🪞 3 Effective Ways to Examine Yourself",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "1. **Examine Your Faith** (2 Cor 13:5): The word examine is 'peirazo' (scrutinize/try), and test is 'dokimazo' (stronger word, test like metals with intense fire). Be like the Bereans (Acts 17:11) who searched scriptures daily.\n\n" +
                           "2. **Examine Your Works** (Gal 6:3-5): Ensure deeds agree with faith. 'It is not what we know that will save us. It is what we do with what we know.' Do good out of love for God and others, not for applause.\n\n" +
                           "3. **Examine Through God's Perspective** (Psalm 139:23-24): Pray 'Search me, O God, and know my heart...' The Bible is like a mirror—look into it to correct what needs to be changed, not just to admire its beauty.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "☀️ 4 Areas of Life That Begin to Improve",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Once you examine yourself honestly, these four outcomes will flourish:\n" +
                           "• **Face the Truth**: Break free from self-deception.\n" +
                           "• **Ease the Pain**: Stop letting secret wounds fester.\n" +
                           "• **Stop the Blame**: Take personal, healthy responsibility.\n" +
                           "• **Accept Forgiveness**: Rest in Christ's complete grace.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "✏️ INTROSPECTIVE SENTENCE COMPLETIONS:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                val items = listOf(
                    "I can be more honest with..." to "step4_completion_1",
                    "I can ease my pain by..." to "step4_completion_2",
                    "I can stop blaming..." to "step4_completion_3",
                    "I can accept God's forgiveness because..." to "step4_completion_4"
                )

                items.forEach { (label, key) ->
                    var value by remember(key) { mutableStateOf(prefs.getString(key, "") ?: "") }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                value = it
                                prefs.edit().putString(key, it).apply()
                            },
                            placeholder = { Text("Complete the sentence...", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        GuidedQuestionsCard(
            stepIndex = 4,
            prefs = prefs,
            questions = listOf(
                "What wrongs, resentments, or secret sins are keeping you awake at night? Wouldn't you like to get rid of them?",
                "What value do you see in confessing, in coming clean of the wreckage of your past?",
                "What results do you expect God to produce in your life through this inventory?",
                "What freedom do you feel because of Romans 8:1 and Romans 3:23-24? What specifically do 'No condemnation' and 'not guilty' mean to you?"
            )
        )
    }
}

@Composable
fun StepFiveDetail(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 5: Acknowledge & Apologize",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Admit to God, ourselves, and someone else our wrongdoings.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow("Therefore, confess your sins to each other and pray for each other so that you may be healed.", "James 5:16")
                ScriptureQuoteRow("Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.", "Proverbs 28:13")
                ScriptureQuoteRow("They cried to the Lord in their troubles, and he rescued them! He led them from the darkness and the shadow of death and snapped their chains.", "Psalms 107:13-14 TLB")
                ScriptureQuoteRow("If we confess our sins, he is faithful and just and will forgive us our sins and cleanse us from all unrighteousness.", "1 John 1:9")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "❓ Why Admit My Wrongs?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "1. **We Gain Healing**: Confessing is not to earn God's forgiveness (which is already free), but to receive mutual prayer and relational healing.\n\n" +
                           "2. **We Gain Freedom**: Secrets keep us bound up, frozen, and in chains. Confession breaks and snaps those chains.\n" +
                           "3. **We Gain Support**: Sharing your inventory with a trusted mentor provides feedback, keeps you focused, and gives you a compassionate listener.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🤝 How to Choose Someone to Share With",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "• Choose someone of the same sex whom you trust and respect.\n" +
                           "• Ask your mentor, sponsor, or a trusted accountability partner who understands and has walked this road.\n" +
                           "• Most importantly, ensure they are a strong believer and follower of Jesus Christ.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        GuidedQuestionsCard(
            stepIndex = 5,
            prefs = prefs,
            questions = listOf(
                "Who are you considering sharing your inventory with, and why?",
                "Most of us find it easier to confess to God than to another human. What is the most difficult part of sharing with a person for you?",
                "What is your biggest fear of sharing your inventory or faults with another person?",
                "Write down a list of people you have offended and what you plan to say to apologize humbly to them."
            )
        )
    }
}

@Composable
fun StepSixDetail(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 6: Seek God's Presence",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Seek God through daily prayer and meditation on His Word and His Works.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow("Let the message of Christ dwell among you richly as you teach and admonish one another with all wisdom through Psalms, hymns and songs from the spirit...", "Colossians 3:16")
                ScriptureQuoteRow("The Lord is a refuge for the oppressed, a stronghold in times of trouble. Those who know your name trust in you, for you, Lord, have never forsaken those who seek you.", "Psalms 9:9-10 NIV")
                ScriptureQuoteRow("Watch and pray so that you will not fall into temptation. The spirit is willing but the flesh is weak.", "Mark 14:38")
                ScriptureQuoteRow("Be still, and know that I am God.", "Psalm 46:10")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🛡️ Relapse Prevention: The R-E-L-A-P-S-E Acrostic",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• **R**eserve a daily quiet time: Dedicate daily time for Bible reading, self-examination, and prayer (Mark 14:38).\n" +
                           "• **E**valuate: Review your physical, emotional, relational, and spiritual health.\n" +
                           "• **L**isten to Jesus: Slow down enough to hear His directions. Test everything (1 Thess 5:21).\n" +
                           "• **A**lone and quiet time: Be still and know Him (Psalm 46:10).\n" +
                           "• **P**lug into God's power: Tell God your specific needs and pray about everything (Philippians 4:6).\n" +
                           "• **S**low down to hear: Remember that God's timing is perfect (Job 33:33, Phil 4:7).\n" +
                           "• **E**njoy your growth: Rejoice in small successes; celebrate victory!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "❤️ Do a H-E-A-R-T Check Right Now",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Are you currently feeling any of these danger zones?\n" +
                           "• **H**urting?\n" +
                           "• **E**xhausted?\n" +
                           "• **A**ngry?\n" +
                           "• **R**esentful?\n" +
                           "• **T**ense?\n\n" +
                           "Acknowledge these to your Mentor or in prayer immediately to prevent slip-ups.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        GuidedQuestionsCard(
            stepIndex = 6,
            prefs = prefs,
            questions = listOf(
                "What tools or methods have you developed in your recovery to prevent a relapse?",
                "Do a H-E-A-R-T check right now. Which of these are you feeling, and what do you do when you experience them?",
                "How would you rate your listening skills with God from 1 to 10? How can you improve your listening skills with others?",
                "Describe what a daily quiet time means to you, and specify when and where you usually pray.",
                "After you pray, do you slow down long enough to hear God's answer?"
            )
        )
    }
}

@Composable
fun StepSevenDetail(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Step 7: Help Others",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Help other struggling individuals the same way that you were supported.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    text = "📖 FOUNDATION SCRIPTURES:",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                ScriptureQuoteRow("Brothers and sisters, if someone is caught in a sin, you who live by the Spirit should restore that person gently. But watch yourselves, or you also may be tempted.", "Galatians 6:1")
                ScriptureQuoteRow("Freely you have received freely give.", "Matthew 10:8")
                ScriptureQuoteRow("Two are better than one, because together they can work more effectively. If one of them falls down, the other can help him up...", "Ecclesiastes 4:9-12")
                ScriptureQuoteRow("My children, our love should not be just words and talk, it must be true love, which shows itself in action.", "1 John 3:18")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🌱 I Become We — Victory Shared",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The road to recovery is not meant to be traveled alone. God never wastes a hurt! Step 7 gives us the opportunity to share our experiences and victories, carrying this message to others and practicing these steps in all our affairs.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🗣️ 10 Tips for Sharing Your Testimony",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "1. **You've Got the Power**: The Holy Spirit lives in you and gives you the power to witness (Acts 1:8).\n" +
                           "2. **Everyone's on a Journey**: God is already at work in others' lives (Acts 17:26-27).\n" +
                           "3. **God Desires to Use You**: Your story helps connect someone to Jesus (2 Cor 5:20).\n" +
                           "4. **Be Prepared**: Always be ready to give a reason for your hope (1 Peter 3:15).\n" +
                           "5. **Practice**: Practice out loud; keep it to about three minutes.\n" +
                           "6. **Pray**: Ask God to bring a friend or relative to mind and pray for them.\n" +
                           "7. **Ask Permission**: 'Can I tell you a little bit about my spiritual experience?'\n" +
                           "8. **Be a Good Listener**: Ask about their religious background and where they are on their journey.\n" +
                           "9. **Leave the Results to God**: It is the Holy Spirit who moves hearts.\n" +
                           "10. **Remember**: Sharing your testimony is about loving the person.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        GuidedQuestionsCard(
            stepIndex = 7,
            prefs = prefs,
            questions = listOf(
                "What does 'freely you have received, freely give' mean to you in your own recovery?",
                "How has your attempt to put God first in your life changed your understanding of what it means to give?",
                "List specific instances in your recovery where you have seen Ecclesiastes 4:9 ('two are better than one') in action.",
                "How can you be a doer of the Word (James 1:22) among: Family, OverComer Group, Church, Job, and your broader Community?"
            )
        )
    }
}

@Composable
fun StepSummaryCard(
    title: String,
    description: String,
    scriptures: List<ScriptureQuote>,
    questions: List<String>,
    stepIndex: Int,
    prefs: android.content.SharedPreferences
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "📖 FOUNDATION SCRIPTURES:",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            scriptures.forEach { script ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "\"${script.text}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "— ${script.reference}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "🙋 QUESTIONS TO ASK (Guided by the Holy Spirit):",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            questions.forEachIndexed { qIdx, question ->
                val answerKey = "step_${stepIndex}_q_${qIdx}"
                var answerText by remember(answerKey) {
                    mutableStateOf(prefs.getString(answerKey, "") ?: "")
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${qIdx + 1}. $question",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = {
                            answerText = it
                            prefs.edit().putString(answerKey, it).apply()
                        },
                        placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth().testTag("recovery_input_step_${stepIndex}_q_$qIdx"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = false,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

data class ScriptureQuote(
    val reference: String,
    val text: String
)

@Composable
fun ArticleTab(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📖 PROVERBS 13:12-23 (NIV)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Hope deferred makes the heart sick, but a longing fulfilled is a tree of life. Whoever scorns instruction will pay for it, but whoever respects a command is rewarded. The teaching of the wise is a fountain of life, turning a person from the snares of death. Good judgment wins favor, but the way of the unfaithful leads to their destruction. All who are prudent act with knowledge, but fools expose their folly. A wicked messenger falls into trouble, but a trustworthy envoy brings healing. Whoever disregards discipline comes to poverty and shame, but whoever heeds correction is honored. A longing fulfilled is sweet to the soul, but fools detest turning from evil. Walk with the wise and become wise, for a companion of fools suffers harm. Trouble pursues the sinner, but the righteous are rewarded with good things. A good person leaves an inheritance for their children’s children, but a sinner’s wealth is stored up for the righteous. An unplowed field produces food for the poor, but injustice sweeps it away.",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, lineHeight = 18.sp),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⏳ The Season of Waiting",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "There are times when God puts things in our hearts (dreams, aspirations, goals) and He gives them to us right away. There are other times, and this seems to be the majority, when God puts things in our hearts and then makes us wait.\n\n" +
                           "Often, we can wonder what God is doing. We can begin to ask, \"Did I really hear from God?\" Or perhaps the biggest question we can ask is, \"God, what is taking you so long?\"\n\n" +
                           "During those times of waiting, God prepares us for His promise. During our waiting, He teaches us things. He grows our character, our faith, and our ability to do what He has called us to do. God takes us through a process. The question is, \"Will you trust the process?\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🛡️ David's Anointing (1 Samuel 16)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "In 1 Samuel 16, God told Samuel to anoint one of Jesse's sons as king. When Samuel saw Eliab, the oldest, tallest, and physically fit son, he thought, \"Surely the Lord’s anointed stands here before the Lord.\"\n\n" +
                           "But the Lord said: \"Do not consider his appearance or his height, for I have rejected him. The Lord does not look at the things people look at. People look at the outward appearance, but the Lord looks at the heart.\"\n\n" +
                           "After all seven sons passed and were rejected, David, the youngest who was tending sheep, was brought in. The Lord said, \"Rise and anoint him; this is the one.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Text(
                    text = "🔄 Back to the Sheepfold",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "If this story was a movie, David would have taken the throne immediately. But instead, David went right back to what he was doing before: watching the sheep. Why would God anoint him and then send him back?\n\n" +
                           "Because God wanted to take David through a process. The anointing was a reminder of the promise that the process would bring.",
                      style = MaterialTheme.typography.bodySmall,
                      lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🎋 The Chinese Bamboo Tree",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "The Chinese Bamboo Tree begins as a nut planted in soil. It must be watered and fertilized every single day for five years before it finally breaks through the ground. If at any point the process stops, the tree dies inside the ground.\n\n" +
                           "But in that fifth year, it breaks through and grows to nearly ninety feet tall in just six weeks! The tree must take five long years of developing a strong, deep, wide root system so it doesn’t topple over when it is grown.\n\n" +
                           "We tend to get frustrated when we don’t get five-year results immediately. The truth is that the process is essential, and everyone must go through it to get results. Will you allow the waiting to develop you or embitter you? Bitterness is unbelief in the promises of God.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🎯 Anointed, Not Yet Appointed",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "A common hindrance is mistaking the anointing of God for the appointing of God. David had the anointing to be king, but he didn't yet have the appointment to be king.\n\n" +
                           "When you have talent, influence, or counseling ability, others see it and praise you. This can cause you to think: \"Well, I can see it, and others can see it, why can’t God see it? What is God waiting for?\" This leads to tunnel vision—focusing so much on the light at the end of the tunnel that you can't see what is happening around you. You trip over things because you can't see!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🔑 Preparation is Key",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "David's appointment after his anointing was to watch sheep. If you're watching sheep right now, that's your appointment. Preparation must come before the opportunity.\n\n" +
                           "While tending sheep, David developed his ability to play the lyre and write music (such as Psalm 19:1), which later got him into King Saul's service. While watching sheep, David fought off lions and bears, which prepared him to defeat Goliath. If you shortcut the process, you short-circuit the product!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🧹 Shane's Janitor Testimony",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Shane Merrill shares his testimony: He entered the car business at 18, became a young manager making lucrative money. When he got saved, God radically changed his life. He felt called to ministry, and Pastor David told him he must go to school. Deciding to go to school, he lost his car dealership job.\n\n" +
                           "He wanted to serve at James River Church, so he applied for a job as a janitor. It took its toll; he had a wife and two kids, and he went from making high income to cleaning toilets. He began to complain in his head to God at 5:30 AM while unlocking the building.\n\n" +
                           "Suddenly, Lead Pastor John Lindell's office door swung open, and Pastor John said: \"I don't do this very often, but I have a word of the Lord for you: Where you are at right now is not where you will always be. Be patient.\"\n\n" +
                           "Shane says: \"The next day, I woke up, went to work, and cleaned toilets. The day after that, I cleaned toilets. I did that job for another year and a half. Although nothing changed on the outside immediately, something changed on the inside. God was building my trust, character, and willingness to serve.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "By Shane Merrill & Micah Cartee • Christian Living",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OutlineTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "I. Introduction",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• **Proverbs 13:12-23 Overview**:\n" +
                           "  - Hope deferred vs. longing fulfilled.\n" +
                           "  - Wisdom, instruction, and discipline.\n" +
                           "  - The contrast between the righteous and the wicked.\n" +
                           "• **Personal Reflection**:\n" +
                           "  - God's timing in fulfilling dreams and aspirations.\n" +
                           "  - The struggle of waiting and questioning God's timing.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "II. Understanding Proverbs 13:12-23",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• **A. Hope Deferred and Longing Fulfilled (v.12)**:\n" +
                           "  - Emotional impact of delayed hope.\n" +
                           "  - Joy of fulfilled desires (\"a tree of life\").\n" +
                           "• **B. Wisdom and Instruction (v.13-14)**:\n" +
                           "  - Respecting commands and instructions.\n" +
                           "  - The value of wisdom as a fountain of life.\n" +
                           "• **C. Good Judgment vs. Foolishness (v.15-16)**:\n" +
                           "  - Favor through good judgment.\n" +
                           "  - Destruction of the unfaithful.\n" +
                           "• **D. Role of Messengers & Discipline (v.17-18)**:\n" +
                           "  - Consequences of wickedness vs. rewards of trustworthiness.\n" +
                           "  - Importance of heeding correction.\n" +
                           "• **E. Companionship and Influence (v.19-20)**:\n" +
                           "  - Delight in fulfilled desires.\n" +
                           "  - Impact of wise and foolish associations.\n" +
                           "• **F. Righteousness and Provision (v.21-23)**:\n" +
                           "  - Rewards for the righteous and stored wealth.\n" +
                           "  - Injustice sweeping away unplowed fields.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "III. Biblical Example: The Process of David's Anointing",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• **A. Samuel's Mission (1 Samuel 16)**:\n" +
                           "  - God's rejection of Saul and selection of David.\n" +
                           "  - Samuel's fear and obedience.\n" +
                           "• **B. The Anointing of David**:\n" +
                           "  - Misconception of Eliab as the chosen one.\n" +
                           "  - God's focus on the heart over appearance.\n" +
                           "  - David's anointing despite youth and minor role.\n" +
                           "• **C. The Waiting Period**:\n" +
                           "  - David's return to tending sheep.\n" +
                           "  - Seeming contradiction of anointing without immediate appointment.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "IV. The Purpose of the Waiting Process",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "• **A. Character and Faith Development**:\n" +
                           "  - Growth in trust and reliance on God.\n" +
                           "  - Strengthening of faith and character.\n" +
                           "• **B. Learning and Preparation**:\n" +
                           "  - Skills and experiences gained during wait.\n" +
                           "  - Comparison to the Chinese Bamboo Tree's growth.\n" +
                           "• **C. Avoiding Bitterness**:\n" +
                           "  - The danger of becoming bitter during delays.\n" +
                           "  - Embracing faith and the promise of God's timing.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "V. The Anointing vs. Appointment",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• **A. Recognizing the Difference**:\n" +
                           "  - Distinction between anointing and actualization.\n" +
                           "  - Significance of preparation before fulfillment.\n" +
                           "• **B. Avoiding Tunnel Vision**:\n" +
                           "  - Staying aware of God's current work and lessons.\n" +
                           "  - The risk of focusing solely on future goals.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "VI. Examples from David's Life",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• **A. David's Musical Skills**:\n" +
                           "  - Development while tending sheep (Psalm 19:1).\n" +
                           "  - Opportunity to play and bring relief to Saul.\n" +
                           "• **B. David's Courage and Strength**:\n" +
                           "  - Experiences with lions and bears.\n" +
                           "  - Preparation for facing Goliath.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "VII. The Importance of Trusting the Process",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "• **A. Embracing God's Timing**:\n" +
                           "  - Faith in God's perfect timing and process.\n" +
                           "  - Allowing the process to refine and prepare us.\n" +
                           "• **B. The Outcome of Faithfulness**:\n" +
                           "  - David's eventual kingship as a result of preparation.\n" +
                           "  - The importance of readiness for God's promises.\n\n" +
                           "**Conclusion**:\n" +
                           "- Encouragement to trust the process & reassurance of God's faithfulness.\n" +
                           "- Strengthening faith through understanding God's ways.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun JournalTab(prefs: android.content.SharedPreferences) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🙋 PERSONAL REFLECTION JOURNAL:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "What area of your life or recovery feels like a slow, invisible 'root growth' waiting process right now?",
            "What are your current 'bear and lion' routine assignments (like David watching sheep or Shane cleaning toilets) that God is using to prepare you?",
            "Have you ever mistook God's 'anointing' (your visible talent or call) for His 'appointing' (the actual timing and position)? How can you avoid tunnel vision?",
            "Shane's testimony reminds us that preparation comes before opportunity. Write down a prayer asking God to grow your character, service, and patience during your season of waiting."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_2_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_2_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = false,
                    maxLines = 5
                )
            }
        }
    }
}

@Composable
fun LessonTwoContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌱 Lesson 2: Trusting God's Process",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Proverbs 13:12-23 Study & 'Trust the Process' Article",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("📖 Read Article", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("📝 Study Outline", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("🙋 Journal", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedTab) {
            0 -> ArticleTab(prefs)
            1 -> OutlineTab()
            2 -> JournalTab(prefs)
        }
    }
}

@Composable
fun LessonThreeContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌟 Lesson 3: I AM An OverComer Affirmations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Declare who God says you are. Re-script your self-worth in Christ.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🗣️ SPEAK THESE ALOUD DAILY:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "I AM Loved By God • I AM NOT Who Others Say I Am • I AM NOT Who I Used To Be • I AM Who God Says I Am",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                val scriptures = listOf(
                    ScriptureQuote("Genesis 1:27", "I am created in the image of God."),
                    ScriptureQuote("Deuteronomy 28", "I am Blessed."),
                    ScriptureQuote("Psalms 17:8", "I am the apple of God's Eye."),
                    ScriptureQuote("Jeremiah 1:5", "I am known by Him, I am set apart, I am appointed."),
                    ScriptureQuote("Matthew 5:14", "I am the light of the world. A city set on a hill."),
                    ScriptureQuote("1 Corinthians 6:19-20", "I am the temple of the Holy Spirit. I am bought with a price."),
                    ScriptureQuote("2 Corinthians 5:17", "I am a New Creation. The old has gone, the new is here!"),
                    ScriptureQuote("1 John 3:1", "Behold what great love the Father has lavished on us, that we should be called children of God!"),
                    ScriptureQuote("Revelations 12:11", "And they overcame him by the blood of the Lamb, and by the word of their testimony.")
                )

                scriptures.forEach { script ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "\"${script.text}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "— ${script.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "When you feel tired or tempted, what negative identity lies do you tend to speak over yourself?",
            "Which identity verse above speaks directly to your heart? Write it custom as a personal declaration below."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_3_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your declaration...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_3_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonFourContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🔥 Lesson 4: Trigger Prevention & Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Avoid pitfalls and set healthy boundaries. An OverComer is NOT the same person they used to be!",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🚨 Proactive Focus: Avoid Triggers",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "We cannot control how others act/move or if they accept us as a New Creation. We can only control ourselves and influence others by our behavior and lifestyles.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🏃 scriptures on fleeing temptation:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                val avoidVerses = listOf(
                    ScriptureQuote("1 Thessalonians 5:22", "Abstain from all appearance of evil."),
                    ScriptureQuote("1 Corinthians 6:18", "Flee from sexual immorality."),
                    ScriptureQuote("2 Timothy 2:22", "Flee the evil desires of youth and pursue righteousness, faith, love and peace..."),
                    ScriptureQuote("Matthew 26:41", "Watch and pray so that you will not fall into temptation. The spirit is willing, but the flesh is weak.")
                )

                avoidVerses.forEach { script ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "\"${script.text}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "— ${script.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🛡️ What to do when triggered:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val handleVerses = listOf(
                    ScriptureQuote("James 4:7", "Submit yourselves, then, to God. Resist the devil, and he will flee from you."),
                    ScriptureQuote("1 Corinthians 10:13", "No temptation has overtaken you except what is common to mankind. And God is faithful; he will not let you be tempted beyond what you can bear. But when you are tempted, he will also provide a way out..."),
                    ScriptureQuote("1 Peter 5:8", "Be alert and of sober mind. Your enemy the devil prowls around like a roaring lion looking for someone to devour. Resist him, standing firm in the faith...")
                )

                handleVerses.forEach { script ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "\"${script.text}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "— ${script.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "What are your biggest 3 trigger triggers (e.g. certain friends, bars, stress and fatigue)?",
            "What is your immediate escape plan when faced with active temptations?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_4_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your defensive actions...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_4_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonFiveContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "⛪ Lesson 5: OverComer Fellowship & Community",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "You CAN Be Free • You DO Have Hope • You Are NOT Your Addiction!",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Weekly Gathering Details:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "📆 Every Thursday 7:00 PM - 9:00 PM\n" +
                           "⛪ The Refuge\n" +
                           "📍 290 Dunn Short Cut Rd, Conway, SC 29526",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📞 For Questions and Outrage Team Contacts:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "• Shane: 704-977-5735 / Outreach@therefugesc.org\n" +
                           "• Chatham Smith: 704-291-1540 / Chatham.smith@yahoo.com",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "Christ Desires To Set You Free completely from your bondages. There is absolutely nothing impossible for Him! Reach out, step into faith, and get connected to a loving team.",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Why is isolation the biggest trap of any addiction? How does team fellowship prevent a relapse?",
            "Write a brief message of prayer or encouragement for a fellow OverComer who might be struggling today."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_5_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_5_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonSixContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "👨‍👩‍👧‍👦 Lesson 6: Family & Parents Restoration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Appreciate, honor, and restore family connections, remembering we only have one Mom and one Dad under God's grace.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Love Mom",
                        tint = Color(0xFFD81B60),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "👩 Mom — The Gift of Nurture",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                val momLines = listOf(
                    "When we’re 5 years old, we say: “Mommy, I love you.”",
                    "When we’re 13, we say: “Mom, whatever.”",
                    "When we’re 16, we say: “My Mom is SO annoying!”",
                    "When we’re 18, we say: “I want out of this house!”",
                    "When we’re 21, we say: “Mom, you were right.”",
                    "When we’re 30, we say: “I want to go to Mom’s house.”",
                    "When we’re 50, we say: “I don’t want to lose my Mom.”",
                    "When we’re 70, we say: “I’d give up everything to have my Mom here with me again.”"
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        momLines.forEach { line ->
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Text(
                    text = "You only have one Mom. Appreciate and honor her, whether she is here on Earth or has gone home to Heaven.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Honor Dad",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "👨 Dad — The Gift of Strength",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val dadLines = listOf(
                    "When we’re 5 years old, we say: “Daddy, I love you.”",
                    "When we’re 13, we say: “Dad, whatever.”",
                    "When we’re 16, we say: “My Dad is SO annoying!”",
                    "When we’re 18, we say: “I want out of this house!”",
                    "When we’re 21, we say: “Dad, you were right.”",
                    "When we’re 30, we say: “I want to go to Dad’s house.”",
                    "When we’re 50, we say: “I don’t want to lose my Dad.”",
                    "When we’re 70, we say: “I’d give up everything to have my Dad here with me again.”"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        dadLines.forEach { line ->
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Text(
                    text = "You only have one Dad. Appreciate and honor him, whether he is here on Earth or has gone home to Heaven.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "What is one way you can show or express honor/appreciation to your parents (or honor their memory) today?",
            "How has God's grace helped heal any generational wounds or strained relationships in your family?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_6_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_6_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonSevenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌬️ Lesson 7: I Can't Catch My Breath",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Biblical and statistical insights on Anxiety, Depression, Trauma, and finding your breath in YHWH.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "📊 The Real Struggle (American Statistics):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• 32% (1 in 3) say drugs have been a problem in their family.\n" +
                           "• 33% (1 in 3) women and 25% (1 in 4) men have been sexually assaulted or raped.\n" +
                           "• 50% (1 in 2) marriages end in divorce.\n" +
                           "• Anxiety affects 40 million adults (18.1%), Major Depression affects 16 million (6.7%), Bipolar affects 5.7 million (2.6%), and PTSD affects 7.7 million (3.5%).\n" +
                           "• For every 1 person who commits suicide, 316 seriously contemplate it.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 Biblical Giants Faced Intense Mental Struggles:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Before any self-righteous warning, know that many of the greatest servants of God wrestled deeply:\n\n" +
                           "• David: \"My guilt has overwhelmed me like a burden too heavy to bear\" (Ps 38:4), \"Why are you downcast, O my soul? Why so disturbed within me?\" (Ps 42:11)\n\n" +
                           "• Elijah: Afraid, discouraged, tired. \"I have had enough Lord. Take my life...\" (1 Kings 19:4)\n\n" +
                           "• Jonah: Angry enough to die, seeking to run away. \"It is better for me to die than to live.\" (Jonah 4:3, 4:9)\n\n" +
                           "• Job: \"I loathe my very life, I have no peace, no quietness, I have no rest, but only turmoil.\" (Job 3:11, 3:26, 10:1, 30:15-17)\n\n" +
                           "• Moses: Broken by his followers' sins, asking: \"Blot me out of the book you have written.\" (Exodus 32:32)\n\n" +
                           "• Jeremiah: Wrestle with loneliness, defeat. \"Cursed be the day I was born! Why did I ever come out of the womb to see trouble and sorrow...\" (Jer 20:14-18)\n\n" +
                           "• Paul: \"When neither sun nor stars appeared... we finally gave up all hope of being saved.\" (Acts 27:20)\n\n" +
                           "• Jesus: \"My soul is deeply grieved to the point of death...\" (Mark 14:34-36). In extreme anguish, His sweat became like drops of blood in Gethsemane (Luke 22:44).",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "💨 YHWH - The True Source of Breath",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "God does not live in temples built by human hands. He Himself gives everyone life, breath, and everything else. (Acts 17:24-25)\n\n" +
                           "The name YHWH contains semi-consonants and semi-vowels. It is an aspirate word that linguists say is impossible to speak without breathing. The very first breath we take is breathing out YHWH.\n\n" +
                           "Therefore, when life knocks the breath out of you, stop running to different habits or distractions to get it back! Run to the CREATOR so He can breathe His supernatural LIFE back into you.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Which biblical figure's mental struggle helps you realize that mental battle does not mean you lack faith?",
            "When life knocks the breath out of you, what is your plan to run straight to YHWH instead of other coping mechanisms?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_7_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your reflection...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_7_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonEightContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var expandedGroup by remember { mutableStateOf<Int?>(null) }
    
    val groups = listOf(
        Triple(
            "🛡️ Group 1: Power, Peace & Quietness (Verses 1-8)",
            Color(0xFF0D47A1),
            listOf(
                "Isaiah 41:10" to "So do not fear, for I am with you; do not be dismayed, for I am your God. I will strengthen you and help you; I will uphold you with my righteous right hand.",
                "Psalm 56:3" to "When I am afraid, I put my trust in you.",
                "Philippians 4:6-7" to "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God. And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
                "John 14:27" to "Peace is what I leave with you; it is my own peace that I give you. I do not give it as the world does. Do not be worried and upset; do not be afraid.",
                "2 Timothy 1:7" to "For God has not given us a spirit of fear, but of power and of love and of a sound mind.",
                "1 John 4:18" to "There is no fear in love. But perfect love drives out fear, because fear has to do with punishment. The one who fears is not made perfect in love.",
                "Psalm 94:19" to "When anxiety was great within me, your consolation brought joy to my soul.",
                "Isaiah 43:1" to "But now, this is what the Lord says…Fear not, for I have redeemed you; I have summoned you by name; you are mine."
            )
        ),
        Triple(
            "🌿 Group 2: Strength in the Valley (Verses 9-16)",
            Color(0xFF00796B),
            listOf(
                "Proverbs 12:25" to "An anxious heart weighs a man down, but a kind word cheers him up.",
                "Psalm 23:4" to "Even though I walk through the valley of the shadow of death, I will fear no evil, for you are with me; your rod and your staff, they comfort me.",
                "Joshua 1:9" to "Have I not commanded you? Be strong and courageous. Do not be terrified; do not be discouraged, for the Lord your God will be with you wherever you go.",
                "Matthew 6:34" to "Therefore do not worry about tomorrow, for tomorrow will worry about itself. Each day has enough trouble of its own.",
                "1 Peter 5:6-7" to "Humble yourselves, then, under God’s mighty hand, so that he will lift you up in his own good time. Leave all your worries with him, because he cares for you.",
                "Isaiah 35:4" to "Tell everyone who is discouraged, Be strong and don’t be afraid! God is coming to your rescue…",
                "Luke 12:22-26" to "Do not worry about your life, what you will eat; or about your body, what you will wear. Life is more than food, and the body more than clothes. Consider the ravens: They do not sow or reap, they have no storeroom or barn; yet God feeds them. And how much more valuable you are than birds! Who of you by worrying can add a single hour to his life? Since you cannot do this very little thing, why do you worry about the rest.",
                "Psalm 27:1" to "The Lord is my light and my salvation—whom shall I fear? The Lord is the stronghold of my life—of whom shall I be afraid?"
            )
        ),
        Triple(
            "⚔️ Group 3: Our Shield and Helper (Verses 17-25)",
            Color(0xFFE65100),
            listOf(
                "Psalm 55:22" to "Cast your cares on the Lord and he will sustain you; he will never let the righteous fall.",
                "Mark 6:50" to "Immediately he spoke to them and said, 'Take courage! It is I. Don’t be afraid.'",
                "Deuteronomy 31:6" to "Be strong and courageous. Do not be afraid or terrifed because of them, for the Lord your God goes with you; he will never leave you nor forsake you.",
                "Isaiah 41:13-14" to "'For I am the Lord, your God, who takes hold of your right hand and says to you, Do not fear; I will help you. Do not be afraid, for I myself will help you,' declares the Lord, your Redeemer, the Holy One of Israel.",
                "Psalm 46:1" to "God is our refuge and strength, an ever-present help in trouble.",
                "Psalm 118:6-7" to "The Lord is with me; I will not be afraid. What can man do to me? The Lord is with me; he is my helper.",
                "Proverbs 29:25" to "Fear of man will prove to be a snare, but whoever trusts in the Lord is kept safe.",
                "Mark 4:39-40" to "He got up, rebuked the wind and said to the waves, “Quiet! Be still!” Then the wind died down and it was completely calm. He said to his disciples, “Why are you so afraid? Do you still have no faith?”",
                "Psalm 34:7" to "The angel of the Lord encamps around those who fear him, and he delivers them."
            )
        ),
        Triple(
            "👑 Group 4: Eternal Victory & Protection (Verses 26-33)",
            Color(0xFF8E24AA),
            listOf(
                "1 Peter 3:14" to "But even if you suffer for doing what is right, God will reward you for it. So don’t worry or be afraid of their threats.",
                "Psalm 34:4" to "I prayed to the Lord, and he answered me. He freed me from all my fears.",
                "Deuteronomy 3:22" to "Do not be afraid of them; the Lord your God himself will fight for you.",
                "Revelation 1:17" to "Then he placed his right hand on me and said: 'Do not be afraid. I am the First and the Last.'",
                "Mark 5:36" to "Jesus told him, ‘Don’t be afraid; just believe.’",
                "Romans 8:38-39" to "And I am convinced that nothing can ever separate us from God’s love. Neither death nor life, neither angels nor demons, neither our fears for today nor our worries about tomorrow—not even the powers of hell can separate us from God’s love.",
                "Zephaniah 3:17" to "The Lord your God is in your midst, A victorious warrior. He will exult over you with joy, He will be quiet in His love, He will rejoice over you with shouts of joy.",
                "Psalm 91:1-4" to "He who dwells in the shelter of the Most High will rest in the shadow of the Almighty. I will say of the Lord, “He is my refuge and my fortress, my God, in whom I trust.”…He will cover you with his feathers, and under his wings you will find refuge; his faithfulness will be your shield and rampart."
            )
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🛡️ Lesson 8: Freedom from Fear (Nakia's Miracle)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Shane and LaToya's testimony of how God prepares us for tough situations and provides supernatural peace in crises.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🚨 The Wreck & The Miracle:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "During a Thursday night OverComer meeting, Shane felt a strong impression to teach on fear, and read 33 select scriptures on fear back-to-back to the class. Little did he know, he was being prepared for Friday.\n\n" +
                           "On Friday, Shane and LaToya received a panic call: their daughter NaKia had been in a devastating car wreck. When they arrived, the car was destroyed, Nakia's face was bloody, and she was crying in pain.\n\n" +
                           "At the hospital, scans showed Nakia had severe brain bleeding and several broken bones in her face. Triggered by PTSD from losing two infant daughters in the past, Shane began to struggle with panic. But he called his pastor, prayed, and stood firm.\n\n" +
                           "The next morning, scans showed a miracle: the brain bleeding had fully stopped, and her facial fractures were completely gone, leaving only a broken nose! She came home on Sunday. God is faithful! He prepares us in advance for the broken and fearful times if we pay attention to His Word.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 33 Verses of Victory Over Fear:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "God's Word is our ultimate weapon against worry and anxiety. Below are all 33 calming scriptures Shane read to his class the night before the crash. Click on any category to meditate on these life-giving words.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Render Groups as beautiful expandable accordions
                groups.forEachIndexed { groupIdx, (title, color, verseList) ->
                    val isExpanded = expandedGroup == groupIdx
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedGroup = if (isExpanded) null else groupIdx },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) color.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(0.5.dp, if (isExpanded) color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isExpanded) color else MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = if (isExpanded) color else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier.padding(top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    verseList.forEach { (ref, text) ->
                                        Column {
                                            Text(
                                                text = "• \"$text\"",
                                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "— $ref",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = color,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.End
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "How does Shane's testimony of God preparing him on Thursday for Friday's storm change how you view consistent prayer and Bible study?",
            "Pick one of the verses on fear. Write it down here and speak it out loud. Why does this verse comfort you?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_8_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your declaration...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_8_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonNineContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var selectedSubTab by remember { mutableStateOf(0) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "⛓️ Lesson 9: Overcoming Guilt & Shame",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Differentiating conduct (guilt) from identity (shame) using biblical wisdom and clinical research.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        // Modern filter chips for sub-navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                label = { Text("⚖️ Differences") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)
            )
            FilterChip(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                label = { Text("🌳 Garden Study") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)
            )
            FilterChip(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                label = { Text("🧼 Healing & Empathy") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)
            )
            FilterChip(
                selected = selectedSubTab == 3,
                onClick = { selectedSubTab = 3 },
                label = { Text("📖 Quotes & Verses") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedSubTab) {
                    0 -> {
                        // Tab 0: Differences
                        Text(
                            text = "⚖️ Guilt vs. Shame — The Core Differences",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• Guilt has to do with our conduct. It is a fact or statement of wrongdoing. It is being responsible for having done something wrong. Guilt involves feeling bad about a specific action: \"I made a bad choice.\"\n\n" +
                                   "• Shame is a painful feeling of being dirty, tainted, humiliated, and somehow less worthy because of wrongdoing. Shame has to do with who we are: \"I AM a bad person.\"\n\n" +
                                   "• Healthy vs. Unhealthy: Guilt can be healthy (it serves as a built-in alarm system, signaling us when we violate what our hearts tell us is right, leading us to ask for forgiveness). Shame is never healthy. It causes us to hide, lie, isolate, and reinforces unhealthy beliefs that we are simply not capable of doing any better.\n\n" +
                                   "• Brain Science: Scientists examining MRIs can see that shame sets off high activity in the right area of the brain (but not the amygdala), whereas guilt activates the amygdala in the frontal lobe. Shame is a much more complex and dangerous emotion.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        
                        Text(
                            text = "🔄 Opposites Overview:",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Guilt Opposites:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                Text("• innocence\n• virtue\n• blameless\n• satisfaction\n• respect\n• sinlessness", style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Shame Opposites:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                Text("• honor\n• self-esteem\n• glory\n• regard\n• praise\n• worthiness", style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
                            }
                        }
                    }
                    1 -> {
                        // Tab 1: Garden Study
                        Text(
                            text = "🌳 Guilt & Shame in the Garden of Eden",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Adam and Eve were guilty of violating God's instruction and deserved the penalty of death because of their guilt. Instantly, they tried to avoid responsibility through blame-shifting:\n\n" +
                                   "• Adam blamed Eve and God: \"The woman you gave me...\"\n" +
                                   "• Eve blamed the serpent: \"The serpent deceived me...\"\n\n" +
                                   "When God entered the garden, they hid because they were ashamed. Before they were guilty, they were naked and unashamed, and had no need to hide.\n\n" +
                                   "But look at God's mercy: On that very day, God declared their guilt and declared their death sentence, but He also promised a Savior who would settle their guilt-debt (Genesis 3:14-15), and He provided a covering for their shame (Genesis 3:21 - garments of skin). God covered them!",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        
                        Text(
                            text = "📖 Hebrews 12:2",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "\"...looking unto Jesus the author and finisher of our faith; who for the joy that was set before him endured the cross, despising the shame, and is set down at the right hand of the throne of God.\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            lineHeight = 17.sp
                        )
                    }
                    2 -> {
                        // Tab 2: Healing & Empathy
                        Text(
                            text = "🧼 Breaking the Cycle (Brené Brown's Research)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Shame researcher Brené Brown teaches that shame needs three things to grow exponentially in our lives:\n\n" +
                                   "⚠️ SECRECY  •  ⚠️ SILENCE  •  ⚠️ JUDGMENT\n\n" +
                                   "As an antidote, shame cannot survive being spoken, and it cannot survive empathy. The first step in overcoming shame is telling someone you trust. Bringing it into the light with someone who listens with empathy automatically reduces shame.\n\n" +
                                   "Beware of the \"vulnerability hangover\" — that anxious worry after sharing where you wonder if people will judge you. Realize that empathetic connection destroys shame's power. Shame only has two lies: \"You are never good enough\" and \"Who do you think you are?\" Turn those lies off!",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                    3 -> {
                        // Tab 3: Quotes & Verses
                        Text(
                            text = "📝 Inspiring Quotes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "\"Guilt says, 'You failed.' Shame says, 'You're a failure.' Grace says, 'Your failures are forgiven.'\"",
                                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                    Text(text = "— Lecrae", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                                }
                            }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "\"The conscience is to our souls what pain sensors are to our bodies: it inflicts distress, in the form of guilt, whenever we violate what our hearts tell us is right.\"",
                                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                    Text(text = "— John MacArthur", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "\"When he says we're forgiven, let's unload the guilt. When he says we're valuable, let's believe him... God's efforts are strongest when our efforts are useless.\"",
                                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    )
                                    Text(text = "— Max Lucado", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            text = "📖 Scriptural Promises of Complete Cleansing:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• Isaiah 53:5 — \"But he was pierced for our transgressions, he was crushed for our iniquities; the punishment that brought us peace was on him, and by his wounds we are healed.\"\n\n" +
                                   "• Romans 8:1 — \"Therefore, there is now no condemnation for those who are in Christ Jesus.\"\n\n" +
                                   "• 1 John 1:9 — \"If we confess our sins, he is faithful and reliable... and cleanses us from everything we've done wrong.\"\n\n" +
                                   "• Psalm 32:5 — \"I said, 'I will confess my transgressions to the Lord' — and you forgave the guilt of my sin.\"\n\n" +
                                   "• Isaiah 43:25 — \"I, even I, am he who blots out your transgressions for my own sake, and remembers your sins no more.\"\n\n" +
                                   "• Hebrews 10:22 — \"...let us go right into the presence of God with sincere hearts fully trusting him. For our guilty consciences have been sprinkled with Christ's blood to make us clean...\"",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Shame tells you 'You are not good enough' or 'Who do you think you are?' triggering isolation. What is one area of shame you are ready to bring out of secrecy?",
            "Dwell on Romans 8:1 ('There is now no condemnation'). Write an honest declaration releasing your guilt and accepting Christ's full forgiveness."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_9_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_9_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonTenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌈 Lesson 10: The Emotions of the Journey",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Navigate the 12 emotional milestones on the path to freedom, knowing God is perpetually with you.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "📖 Scriptural Foundation & Perpetual Presence",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Matthew 28:20 — \"...And lo, I am with you always [remaining with you perpetually—regardless of circumstance, and on every occasion], even to the end of the age.\"\n\n" +
                           "No matter which emotional storm you encounter, your Savior resides in the vehicle with you, sustaining your faith.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "👑 Case Study: The Rich Young Ruler (Matthew 19:16-26)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The rich young man wanted to obtain eternal life, claiming to keep all commandments. But when Jesus challenged him to part with what he valued most (possessions/status as comfort/security), he left grieving and distressed.\n\n" +
                           "\"With people it is impossible, but with God all things are possible.\" In recovery, what comfort are we holding on to? When we place our ultimate faith in anything other than Christ, we invite spiritual grief.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🧭 The 12 Emotional Milestones Encountered:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val stages = listOf(
                    Triple("1. Fear", "Scared to start / of potential failure.", Icons.Default.Warning to Color(0xFFE53935)),
                    Triple("2. Guilt/Shame", "Feelings of inadequacy; not working hard enough.", Icons.Default.Lock to Color(0xFF757575)),
                    Triple("3. Regret", "Bitterly mourning where you could have been by now.", Icons.Default.Refresh to Color(0xFF1E88E5)),
                    Triple("4. Courage", "Gaining early wins and building solid spiritual confidence.", Icons.Default.PlayArrow to Color(0xFF43A047)),
                    Triple("5. Desire", "Seeing light at the end of the tunnel and pressing forward.", Icons.Default.CheckCircle to Color(0xFF00ACC1)),
                    Triple("6. Excitement", "Receiving multiple victories, driving fresh encouragement.", Icons.Default.ThumbUp to Color(0xFFD81B60)),
                    Triple("7. Pride", "Celebrating your hard work and divine milestones.", Icons.Default.Star to Color(0xFFFDD835)),
                    Triple("8. Anger", "A dip in performance or progress. Setbacks add tension; you need your Word (Your Mentor).", Icons.Default.Close to Color(0xFFD32F2F)),
                    Triple("9. Willingness", "Rediscovering resolve: 'I can truly do this through Christ.'", Icons.Default.Send to Color(0xFF3949AB)),
                    Triple("10. Acceptance", "Learning from the past; recognizing that you cannot do it alone.", Icons.Default.Face to Color(0xFF00897B)),
                    Triple("11. Freedom", "The sweet, victorious relief of breaking all active chains.", Icons.Default.Check to Color(0xFF2E7D32)),
                    Triple("12. Giving Back", "Desiring and being fully equipped to reach back and lift others up!", Icons.Default.Favorite to Color(0xFFC2185B))
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    stages.forEach { (title, desc, iconData) ->
                        val (icon, color) = iconData
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(color.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Which of the 12 emotional stages are you currently experiencing? Why?",
            "The Rich Young Ruler grieved because his security was in possessions rather than Christ. What security are you holding on to that you need to surrender to God?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_10_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_10_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonElevenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var dollarState by remember { mutableStateOf(1) } // 1: New, 2: Old, 3: Dirty, 4: Stomped, 5: Torn

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "💸 Lesson 11: True Repentance & Your Infinite Value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Grasp the radical nature of turning to God, and realize your value block is defined by your Creator, never your condition.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚙️ What True Repentance Looks Like",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Repentance is turning from sin, unrighteous surrounding ways, and corrupt generations—it is active conversion. It means turning to God, listening entirely to Christ's teachings, and walking toward sincere, daily obedience.\n\n" +
                           "• Accepting Christ as Lord of one's life means shifting lordship from Satan (Eph 2:2) to the lordship of Christ and His Word (Acts 26:18).\n" +
                           "• Mere saving faith with no radical break from sin is incomplete. Faith that includes deep, sincere repentance is always the requirement for salvation.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 The Word of Repentance",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Acts 3:19 — \"Repent, then, and turn to God, so that your sins may be wiped out, that times of refreshing may come from the Lord.\"\n\n" +
                           "• Psalm 51:1-2 — \"Have mercy upon me, O God... blott out my transgressions. Wash me thoroughly from mine iniquity, and cleanse me from my sin.\"\n\n" +
                           "• Isaiah 1:18 — \"Come now, let us settle the matter... Though your sins are like scarlet, they shall be as white as snow; though they are red as crimson, they shall be like wool.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "💵 Interactive Illustration: The Dollar Bill's Worth",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Consider a dollar bill. If it gets old, is it worth less? If it gets dirty, is it worth less? If it is dropped, stepped on, stomped on, and torn, is it worthless? NO! Every store will take that dollar because its worth is NOT in its condition. Its worth is defined by who created it and stands behind it.\n\n" +
                           "Your life may feel dirty, stomped on by trauma, or torn apart by addiction. But your value to God is eternal! He sent His Son to put you back together, and His Holy Spirit to guide you.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                // Interactive selector demonstration of Value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val states = listOf("🆕 Crisp", "🗂️ Old", "💩 Dirty", "👟 Stomped", "🩹 Torn")
                    states.forEachIndexed { sIdx, name ->
                        val active = dollarState == (sIdx + 1)
                        FilterChip(
                            selected = active,
                            onClick = { dollarState = sIdx + 1 },
                            label = { Text(name, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = when (dollarState) {
                                1 -> "💵 BRAND NEW crisp dollar: VALUE IS 100%!"
                                2 -> "🗂️ Old, wrinkled dollar: VALUE IS STILL 100%!"
                                3 -> "💩 Covered in dirt and mud: VALUE IS STILL 100%!"
                                4 -> "👟 Stomped and crushed into the concrete: VALUE IS STILL 100%!"
                                else -> "🩹 Torn, taped together, and damaged: VALUE IS STILL 100%!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Value Verified", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Recognizing that saving faith requires a radical break with sin, what sinful pattern are you fully repenting of right now?",
            "Reflecting on the dollar bill illustration, how does knowing that your value depends entirely on your Creator (not your shape/dirt) help you forgive yourself?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_11_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_11_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonTwelveContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🎯 Lesson 12: Facing Your Faults & Confession",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Gather courage to dismantle secrecy and shame. Experience spiritual healing by bringing faults into God's light.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🛠️ 3 Steps to Face Your Faults (Pastor Wayne Cordeiro)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "When we face our faults with courage, God performs a supernatural exchange:\n\n" +
                           "1. Recognize Your Faults: Drop the excuses, defensive shield, and blaming strategies.\n" +
                           "2. Emphasize His Lead: Settle your eyes on God's correction and guidelines.\n" +
                           "3. God Will Maximize Your Obedience: He converts your faults into assets, and your weakness into pure, unshakeable strength!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🧼 Demystifying Acknowledgment & Confession",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Acknowledge is a legal term: admission, avowal, recognition of authority, and declaring your act to give it legal validity.\n\n" +
                           "• James 5:16 — \"Therefore confess your sins to each other and pray for each other so that you may be healed. The prayer of a righteous person is powerful and effective.\"\n\n" +
                           "• Proverbs 28:13 — \"Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.\"\n\n" +
                           "• Psalm 32:3-5 — \"When I kept silent, my bones wasted away through my groaning all day long... Then I acknowledged my sin to you and did not cover up my iniquity... And you forgave the guilt of my sin.\"\n\n" +
                           "• Acts 19:18 — \"Many of those who believed now came and openly confessed what they had done.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "How has trying to keep silent or concealing your issues negatively impacted your physical and mental health (Psalm 32)?",
            "According to Cordeiro, God maximizes obedience by turning weaknesses into assets. What is a specific weakness you are committing to surrender to Him?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_12_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your confession/thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_12_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonThirteenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🔥 Lesson 13: Overcoming Temptation & Mentorship",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Study Christ's battles with temptational triggers, the sacrament of Communion, and the power of godly pastors.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚔️ How Christ Defeated Temptation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Matthew 4:1-11 (The Wilderness): Jesus defeated Satan's triggers by declaring: \"It is written!\" of God's Word directly back to the enemy. He relied entirely on written command.\n\n" +
                           "• Luke 22:39-46 (Gethsemane): Jesus faced the ultimate emotional battle. Grieved to death, sweating blood, He prayed: \"Not my will, but Yours be done.\" In Gethsemane, He teach us that prayer and submission is the shield against temptation.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🍞 Communion & Fellowship (1 Corinthians 11:23-26)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "\"This is my body, which is for you; do this in remembrance of me... Whenever you eat this bread and drink this cup, you proclaim the Lord's death until He comes.\"\n\n" +
                           "Communion establishes the covenant family, reminding us daily of the broken body that bought our complete healing.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "👔 Tribute to Rev DJ Byrd (Holding Your Head Up)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "We are blessed to have pastors who will go to any length to reach the lost. Rev DJ Byrd (Senior Pastor of McColl PH Church) has preaching power, but is also known for cutting the grass, cleaning the church, and standing with people when no one else would give them a chance.\n\n" +
                           "When Shane wanted to quit, Pastor DJ wouldn't allow him. He encouraged, supported, and held Shane's head up through major dark storms. We must honor the leaders God commissions to pull us up.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Jesus said 'It is written' in the wilderness, and 'not my will, but Yours' in Gethsemane. How can you combine scripture study and complete prayer to beat your triggers?",
            "Write a personal prayer or letter of appreciation for Pastor DJ Byrd (or a mentor who didn't let you quit during your dark days)."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_13_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_13_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonFourteenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "✈️ Lesson 14: Follow the Voice (The Anchorage Miracle)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "A thrilling testimony of survival, disorientation, and why trusting the Voice of your Instructor is the difference between life and death.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⛈️ Lost in the Alaskan Clouds",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "A traveler in the Aleutian Islands was offered a cheaper ride in a tiny personal plane instead of a commercial airline. Against his better judgment, he said yes. A few minutes into flight, they flew into blinding clouds. Suddenly, the pilot turned and said: \"I can't fly in clouds, they make me pass out,\" and passed out cold in the seat!\n\n" +
                           "Desperately shaking the pilot, with zero flight training, the passenger grabbed the radio microphone and screamed for help. A freight pilot flying to Tokyo heard him first, put his massive 747 in a loop, and connected him with Anchorage Emergency.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📻 'My Job is to Get You Home Safe. Promise Me You'll Obey My Voice.'",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The voice at Anchorage emergency gave a strict condition:\n" +
                           "\"My job is to get you home safe. If you want me to get you home safe, you gotta promise me you'll obey my voice. You can't see me, but I can see you on radar. If you don't obey my voice, you're gonna die.\"\n\n" +
                           "Disoriented in absolute darkness, the passenger wanted to steer by feelings. But the voice warned: \"You're 4 minutes from hitting a mountain/crashing. Don't look at anything outside. Don't watch the storm. Just listen to my voice, and I'll take you through.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "✝️ The Cross is the Way Home",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Other night cargo pilots over Alaska chimed in: \"Trust the voice, that is the key.\"\n\n" +
                           "Under strict audio control, the instructor lined them up directly down the runway. He said: \"At the foot of the runway, there are lights in the shape of a CROSS. Don't you forget: THE CROSS IS THE WAY HOME.\"\n\n" +
                           "Guided by the Voice, they descended and landed safely (seven bumpy bounces!). The moment they stopped, the pilot woke up. The controller's final message was a weeping warning: \"I watch them crash and burn all the time because they won't follow my voice. They get other voices in their heads and they self-destruct. Thank you for listening.\"\n\n" +
                           "\"My sheep hear my voice, and they follow me.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "The aircraft controller told the disoriented passenger, 'If you start watching the storm, you will die. Focus on my voice.' What storm in your life is currently distracting you from God’s Voice?",
            "At the foot of the Anchorage runway, a massive Cross of lights led them home. How does keeping your eyes permanently on the Cross of Christ prevent you from self-destructing?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_14_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_14_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun TestimonyVictoryBoard(viewModel: OverComerViewModel, onNavigateToChat: () -> Unit) {
    val context = LocalContext.current
    var testimonyInput by remember { mutableStateOf("") }
    var shareWithCommunity by remember { mutableStateOf(false) }
    var activeQuoteIndex by remember { mutableStateOf(0) }

    val communityLogs by viewModel.communityTestimonies.collectAsStateWithLifecycle()
    val removedPostNotes by viewModel.removedPostNotes.collectAsStateWithLifecycle()
    val isUserAdmin by viewModel.isUserAdmin.collectAsStateWithLifecycle()
    var devModeratorOverride by remember { mutableStateOf(false) }

    val staticCommunityLogs = remember {
        listOf(
            Triple("Surrendered my chemical struggle wholly to Jesus. Remained completely free this entire week! He is so faithful!", System.currentTimeMillis() - 7200000L, "John D."),
            Triple("Felt absolute peace during a heavy work trigger. Usually I would isolate, but the thought reframing and prayer broke the lock! Glory to God!", System.currentTimeMillis() - 21600000L, "Sarah M."),
            Triple("My marriage is being renewed as my husband and I aligned vertically with Christ first. God restores marriage!", System.currentTimeMillis() - 86400000L, "Mark W."),
            Triple("30 days of perfect freedom! Surrendering to Jesus broke the chains instantly. Walking as a new creation!", System.currentTimeMillis() - 172800000L, "Grace S.")
        )
    }

    val displayedCommunityLogs = remember(communityLogs, removedPostNotes) {
        val dbLogs = communityLogs
            .filter { it.notes !in removedPostNotes }
            .map { Triple(it.notes, it.timestamp, it.authorName.ifBlank { "Anonymous" }) to (it.id as Int?) }
        val staticFiltered = staticCommunityLogs
            .filter { it.first !in removedPostNotes }
            .map { Triple(it.first, it.second, it.third) to (null as Int?) }
        val all = dbLogs + staticFiltered
        all.sortedByDescending { it.first.second }
    }

    val quotes = remember {
        listOf(
            "\"God is not just a God of survival; He is the God of absolute deliverance and victory.\"\n— David Wilkerson (Teen Challenge)",
            "\"The blood of the Lamb has paid for your freedom, and the word of your testimony confirms it.\"\n— OverComer Guide (Revelation 12:11)",
            "\"We don't fight FOR victory; we walk in the victory already secured by Jesus on the cross.\"\n— OverComer Creed",
            "\"Our trials are the canvas on which God paints His greatest testimonies of grace.\"\n— Paul David Tripp",
            "\"Genuine surrender to Jesus breaks every chain instantly. You are unconditionally free.\"\n— David Wilkerson (Teen Challenge)",
            "\"He who began a good work in you will carry it on to completion until the day of Christ Jesus.\"\n— Philippians 1:6"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("testimony_victory_board_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDF5), // Warm, slightly yellow/gold background
            contentColor = Color(0xFF5D4037) // Warm brown text color
        ),
        border = BorderStroke(1.5.dp, Color(0xFFFFA000).copy(alpha = 0.6f)) // Amber border
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Victory Icon",
                    tint = Color(0xFFFFA000), // Gold
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "VICTORY & TESTIMONY BOARD",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = "Celebrate His Faithfulness",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF795548)
                    )
                }
            }

            // Quotes Carousel Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFB300).copy(alpha = 0.08f))
                    .border(0.5.dp, Color(0xFFFFB300).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = quotes[activeQuoteIndex],
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 20.sp
                        ),
                        color = Color(0xFF4E342E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quote cycle button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                activeQuoteIndex = (activeQuoteIndex + 1) % quotes.size
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE65100))
                        ) {
                            Text("Next Quote ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFFFB300).copy(alpha = 0.2f))

            // Action: Converse about Victorious Day
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Converse & Rejoice",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "Open up your OverComer Companion Chat to talk about your triumphs today, share how you overcame obstacles, and praise God together!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037)
                )

                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testimony_converse_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Text("Share Victory with Companion ➔", fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFFFB300).copy(alpha = 0.2f))

            // Action: Log a Victory/Testimony entry to the Secure Journal
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Record Today's Victory",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "Record today's breakthroughs so you can look back and remember how God delivered you. Saved in your combined private journal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037)
                )

                // Brief statement about the encouraging power of testimony
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📖 The Encouragement of Your Story",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sharing what God has done in your life is a powerful way to defeat despair and strengthen others! Revelation 12:11 says we overcome by the blood of the Lamb and the word of our testimony. Your story of victory can be the very hope a fellow OverComer needs today to keep moving forward in perfect freedom.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 16.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }

                OutlinedTextField(
                    value = testimonyInput,
                    onValueChange = { testimonyInput = it },
                    placeholder = {
                        Text(
                            "What is your breakthrough or testimony today? E.g., 'Experienced complete peace and overcame temptation at work today! Glory to God!'",
                            fontSize = 13.sp,
                            color = Color(0xFF8D6E63)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testimony_input_field"),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFB300),
                        unfocusedBorderColor = Color(0xFFFFB300).copy(alpha = 0.5f)
                    )
                )

                // Optional Share Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { shareWithCommunity = !shareWithCommunity }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = shareWithCommunity,
                        onCheckedChange = { shareWithCommunity = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE65100))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Share anonymously on Community Board 🌐 (Optional)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Checking this lets other users see your breakthrough on the community feed anonymously.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF795548)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (testimonyInput.isNotBlank()) {
                            viewModel.addVictoryTestimony(
                                notes = testimonyInput,
                                shareOnCommunityBoard = shareWithCommunity
                            )
                            val message = if (shareWithCommunity) {
                                "Recorded securely in your private journal and shared anonymously to encourage the community! 🙌🏆"
                            } else {
                                "Victory testimony recorded securely in your Private Journal! 🏆🔐"
                            }
                            testimonyInput = ""
                            shareWithCommunity = false
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testimony_save_btn"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = testimonyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = if (shareWithCommunity) "Share & Record Testimony 🌐🔐" else "Log in Secure Private Journal 🔐",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- Community Victory Board Feed ---
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFFFFB300).copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Encouragement Feed",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Community Victory Feed 📣",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE65100)
                        )
                    }

                    // Admin toggle / indicator
                    if (isUserAdmin) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFC8E6C9),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "🛡️ Admin Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32)),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        // Demo switch for creator/reviewer to quickly test and verify moderation behavior
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Demo Admin",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8D6E63)
                            )
                            Switch(
                                checked = devModeratorOverride,
                                onCheckedChange = { devModeratorOverride = it },
                                modifier = Modifier.scale(0.75f).testTag("dev_moderator_toggle"),
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE65100))
                            )
                        }
                    }
                }

                Text(
                    text = "See how God is delivering and restoring other OverComers in our community:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037)
                )

                // Render community list using a simple loop to avoid scroll crashes inside parent LazyColumn
                displayedCommunityLogs.take(12).forEach { (logInfo, logId) ->
                    val (notesText, timestampValue, authorName) = logInfo
                    val timeAgo = remember(timestampValue) {
                        val diff = System.currentTimeMillis() - timestampValue
                        when {
                            diff < 60000L -> "Just now"
                            diff < 3600000L -> "${diff / 60000L}m ago"
                            diff < 86400000L -> "${diff / 3600000L}h ago"
                            else -> "${diff / 86400000L}d ago"
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFFDE7), // Warm light yellow background
                            contentColor = Color(0xFF5D4037)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFFE082))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified OverComer",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = authorName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF388E3C)
                                        )
                                    )
                                }
                                Text(
                                    text = timeAgo,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = notesText,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4E342E),
                                lineHeight = 16.sp
                            )

                            // Show Erase Post button if user is real admin OR demo admin mode is switched on
                            if (isUserAdmin || devModeratorOverride) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            viewModel.removeCommunityPost(notesText, logId)
                                            Toast.makeText(context, "Post erased from Victory Board permanently! 🛡️🧹", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F)),
                                        modifier = Modifier.testTag("admin_erase_post_btn")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Erase Post",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Erase Post (Admin)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportGroupLocatorSection(viewModel: OverComerViewModel) {
    val context = LocalContext.current
    var locationInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Celebrate Recovery") } // "Celebrate Recovery", "Christian Support Groups", "Find a Church"
    var prioritizeAlignment by remember { mutableStateOf(true) }
    var currentPage by remember { mutableStateOf(0) }
    
    val searchResults by viewModel.localResources.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearchingResources.collectAsStateWithLifecycle()

    val categories = listOf("Celebrate Recovery", "Christian Support Groups", "Find a Church", "Veteran Support")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("support_locator_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Locator Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "SUPPORT & CHURCH LOCATOR",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Enter your Zip Code or City to find active Christ-centered support groups, Celebrate Recovery meetings, local Bible-believing churches, and post-military assistance close to you.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Category Selector Chips (horizontally scrollable to avoid overflow)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val displayLabel = when (cat) {
                        "Celebrate Recovery" -> "Celebrate Recovery"
                        "Christian Support Groups" -> "Support Groups"
                        "Find a Church" -> "Find a Church ⛪"
                        else -> "Veteran Support 🎖️"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { 
                            selectedCategory = cat
                            currentPage = 0
                            if (locationInput.isNotBlank()) {
                                val apiCategory = if (cat == "Find a Church") "Churches" else cat
                                viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment, page = 0)
                            }
                        },
                        label = {
                            Text(
                                text = displayLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Alignment Option (Discreet checkbox/toggle for custom searches)
            if (selectedCategory == "Find a Church" || selectedCategory == "Christian Support Groups") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = prioritizeAlignment,
                        onCheckedChange = { 
                            prioritizeAlignment = it
                            currentPage = 0
                            if (locationInput.isNotBlank()) {
                                val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                                viewModel.searchLocalResources(locationInput, apiCategory, it, page = 0)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Column {
                        Text(
                            text = "Prioritize fellowships aligned with OverComer's framework",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (prioritizeAlignment) 
                                "Focuses on ministries closely familiar with complete freedom & spirit-filled walk." 
                                else "Displays a broader list of all bible-believing denominations (Baptist, General, etc.).",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    placeholder = { Text("E.g. 29601 or Greenville, SC", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("locator_location_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (locationInput.isNotBlank()) {
                                currentPage = 0
                                val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                                viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment, page = 0)
                            }
                        }
                    )
                )

                Button(
                    onClick = {
                        if (locationInput.isNotBlank()) {
                            currentPage = 0
                            val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                            viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment, page = 0)
                        } else {
                            Toast.makeText(context, "Please enter a location first", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("locator_search_btn")
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Search Results Container
            if (searchResults.isNotEmpty()) {
                val displayCategoryName = if (selectedCategory == "Find a Church") "Christian Churches" else selectedCategory
                Text(
                    text = "Nearby matches for '$displayCategoryName' near '$locationInput':",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                searchResults.forEach { resource ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = resource.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = when (resource.type) {
                                                "Celebrate Recovery" -> "CR Group"
                                                "Christian Support Group" -> "Support Group"
                                                "Veteran Support" -> "Veteran Support"
                                                else -> "Church"
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // Address Row
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location Pin",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = resource.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Details Row
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Details",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = resource.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }

                            // Contact Row
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Contact Info",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = resource.contact,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Directions Action Button
                            Button(
                                onClick = {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(resource.directionUrl)
                                    )
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Directions,
                                        contentDescription = "Directions",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text("Get Directions & Details 🗺️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        currentPage += 1
                        val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                        viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment, page = currentPage)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locator_next_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text("Next Results", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else if (isSearching) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Searching local Christian databases...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun PostIncarcerationSupportSection() {
    var showReentryDetails by remember { mutableStateOf(false) }
    var showVeteransDetails by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("post_incarceration_support_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Release Support Icon",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "RE-ENTRY & VETERANS ASSISTANCE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "Transitioning back to society after separation or military service can feel overwhelming. Access customized guidance, supportive networks, and professional databases below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // 1. REENTRY SUPPORT AND TRANSITION FREEDOM RESOURCES - CLICK HERE
            Card(
                onClick = { showReentryDetails = !showReentryDetails },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reentry_support_toggle_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (showReentryDetails) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f) 
                                     else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, if (showReentryDetails) MaterialTheme.colorScheme.secondary else Color.Transparent)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "reentry support and transition freedom resources click here",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showReentryDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Details",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    if (showReentryDetails) {
                        Text(
                            text = "Transitioning back to society after long-term incarceration can feel overwhelming. If you feel like 'something is broken' inside or you are struggling to adjust, know that you are not alone, and you are NOT permanently broken. Here are powerful tools, curated resources, and coping strategies designed specifically to help you walk in full freedom.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.secondary,
                            edgePadding = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("📘 Coping", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("💼 Resources", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                text = { Text("🛡️ Mind Renewal", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                text = { Text("🧠 Post-Prison Habits", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        when (selectedTab) {
                            0 -> ReentryCopingStrategiesTab()
                            1 -> ReentryCuratedResourcesTab()
                            2 -> ReentryMindRenewalTab()
                            3 -> ReentryConditioningTab()
                        }
                    }
                }
            }

            // 2. VETERANS SUPPORT AND RESOURCES - CLICK HERE
            Card(
                onClick = { showVeteransDetails = !showVeteransDetails },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("veterans_support_toggle_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (showVeteransDetails) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) 
                                     else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, if (showVeteransDetails) MaterialTheme.colorScheme.primary else Color.Transparent)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "veterans support and resourses click here",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showVeteransDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Details",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (showVeteransDetails) {
                        VeteransSupportDetailsSection()
                    }
                }
            }
        }
    }
}

@Composable
fun VeteransSupportDetailsSection() {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Honoring your service. If you are a veteran transitioning back into life or seeking mental/spiritual stability, you have unique, dedicated networks of support ready to serve you.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "🚨 National Veterans Crisis Line",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Free, confidential, 24/7 support. Connect with compassionate responders who understand military life.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_DIAL,
                            android.net.Uri.parse("tel:988")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Dial 988 (Press 1 for Veterans)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "⚖️ Veterans Justice Outreach (VJO)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "VA program designed to avoid unnecessary criminalization and incarceration of veterans by facilitating access to VA clinical services, housing, and rehabilitation programs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BulletPointItem("Housing Solutions: Connect with VASH (VA Supportive Housing) vouchers.")
                BulletPointItem("Direct Healthcare Integration: Seamless connection with VA health clinics.")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "⚔️ Combat & PTSD Spiritual Recovery",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                BulletPointItem("REBOOT Recovery Courses: Christian combat trauma healing. Free 12-week courses designed specifically for veterans and spouses. (rebootrecovery.com)")
                BulletPointItem("Point Man Ministries: Veteran-to-veteran local peer support networks, teaching deep biblical restoration. (pointmanlr.org)")
            }
        }
    }
}

@Composable
fun ReentryCopingStrategiesTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "👁️ Managing Hypervigilance & Overload",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Prison requires constant alertness. In society, this can manifest as anxiety in loud, crowded places (like grocery stores) or always wanting to sit facing the door.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BulletPointItem("Gradual Exposure: Start small. Visit supermarkets or malls during slow, off-peak hours first.")
                    BulletPointItem("Safe Seating: It is perfectly okay to sit facing the door in public to keep your nervous system calm while you adapt.")
                    BulletPointItem("Mindful Grounding: Take slow breaths. Inhale 4s, hold 4s, exhale 4s. Recite Psalm 46:10: 'Be still, and know that I am God.'")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⚖️ Overcoming Decision Fatigue",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "When choices are made for you for years, everyday decisions (what to wear, buy, or eat) can suddenly trigger intense overwhelm or shutdown.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BulletPointItem("The 3-Choice Rule: Limit your immediate choices to three options to prevent cognitive overload.")
                    BulletPointItem("Write a Daily Plan: Spend 5 minutes every morning mapping out a basic schedule. Structure provides a safety net.")
                    BulletPointItem("Ask for Time: If pushed for a fast choice, practice saying: 'Let me think about that and get back to you.'")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "❤️ Rebuilding Family & Relationship Boundaries",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Separation changes family dynamics. Rushing to restore roles too fast can lead to misunderstanding and friction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BulletPointItem("Take it Slow: Trust is rebuilt through small, consistent acts of service and integrity, not overnight grand gestures.")
                    BulletPointItem("Healthy Boundaries: Be honest about your limits. Seek mutual respect. (See Cloud & Townsend's 'Boundaries').")
                    BulletPointItem("The Love & Respect Cycle: Break recursive arguments. Listen carefully, validate feelings, and seek first to understand.")
                }
            }
        }
    }
}

@Composable
fun ReentryCuratedResourcesTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🏠 Clean & Safe Housing Support",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletPointItem("Oxford House: Over 3,000 self-run, self-supported, drug-free homes nationwide. Excellent, affordable, felon-friendly sober housing option. (Website: oxfordhouse.org)")
                    BulletPointItem("Christian Transition Homes: Many local ministries operate reentry discipleship housing. Connect with a pastor via our Locator to find safe listings.")
                    BulletPointItem("Local Reentry Coalitions: County social services maintain directories of transitional housing specifically funded for newly released individuals.")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💼 Second-Chance Employment",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletPointItem("Honest Jobs: The largest employment platform designed specifically for people with criminal histories. Matches you with second-chance employers. (Website: honestjobs.com)")
                    BulletPointItem("Goodwill Reentry Programs: Offers specialized job training, resume preparation workshops, and direct local employment placement.")
                    BulletPointItem("Dave's Killer Bread Foundation: Active resources and guidance on finding and thriving in second-chance jobs.")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🤝 Mentorship & Discipleship Networks",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletPointItem("Prison Fellowship Academy: Direct post-release community networks, discipleship groups, and personal mentorship matching. (Website: prisonfellowship.org)")
                    BulletPointItem("Teen Challenge Reentry Outreach: Christian discipleship, community service support, and local fellowships welcoming OverComers without judgment.")
                    BulletPointItem("Celebrate Recovery: A safe, Christ-centered peer fellowship helping you walk free of hurts, habits, and hang-ups.")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💳 ID Recovery & Legal Aid",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletPointItem("Local Social Services: Reentry specialists can help obtain birth certificates, Social Security cards, and state IDs, often waiving standard fees.")
                    BulletPointItem("Legal Aid Societies: Non-profit legal networks offering free counsel for clearing backgrounds, restoring driver's licenses, and child support adjustments.")
                }
            }
        }
    }
}

@Composable
fun ReentryMindRenewalTab() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Renewing the mind means replacing survival 'prison rules' with Christ's liberating truth. Here are key transformations to pray over daily:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LieToTruthItem(
            lie = "I am institutionalized and will never fully adapt. I'm permanently broken.",
            truth = "I am a new creation (2 Corinthians 5:17). God is working in me daily, renewing my mind, and restoring everything that was lost."
        )

        LieToTruthItem(
            lie = "I must keep my guard up and trust absolutely no one to stay safe.",
            truth = "While wisdom is necessary, Christ is my ultimate shield. I can build healthy, grace-filled boundaries with a trusted brotherhood."
        )

        LieToTruthItem(
            lie = "I am a permanent burden because of my record and my past.",
            truth = "God has a custom, redemptive purpose for my life (Ephesians 2:10). My testimony of deliverance is a powerful beacon of hope."
        )

        LieToTruthItem(
            lie = "I am powerless against the system and my circumstances.",
            truth = "In Christ, I am an OverComer. Choice is the root, dependence is the fruit. I choose to depend fully on God's grace."
        )
    }
}

data class PrisonizationTrait(
    val id: Int,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val summary: String,
    val behaviorPoints: List<String>,
    val overcomerAdvice: String
)

val prisonizationTraits = listOf(
    PrisonizationTrait(
        1,
        "1. Strong Dependence on Structure",
        Icons.Default.Settings,
        "Need for a strict daily routine and feeling lost or anxious when plans change.",
        listOf(
            "Becoming anxious, irritable, or lost when plans change.",
            "Expecting meals, sleep, work, and activities to happen at exact times.",
            "Struggling to organize the day without external direction.",
            "Functioning better at work than at home because work has strict rules.",
            "Saying, 'Just tell me what I’m supposed to do,' rather than deciding independently."
        ),
        "Christ's freedom means we are active participants in our lives, not passive subjects. Start small by scheduling your own day with flexible buffer times. Pray for peace when plans shift."
    ),
    PrisonizationTrait(
        2,
        "2. Difficulty Making Ordinary Decisions",
        Icons.Default.Help,
        "Feeling overwhelmed by small, everyday choices after years of having choices controlled.",
        listOf(
            "Struggling to choose simple things: what to eat, what to wear, or how to spend free time.",
            "Procrastinating, repeatedly asking others what to do, or avoiding decisions altogether.",
            "Fearing making the wrong choice, which can resemble laziness or irresponsibility."
        ),
        "Overcoming this requires practice. Give yourself permission to make small, harmless mistakes. Remind yourself: 'God's grace covers my steps. I am allowed to choose.'"
    ),
    PrisonizationTrait(
        3,
        "3. Constant Hypervigilance",
        Icons.Default.Warning,
        "Remaining alert for danger, sitting near doors, watching exits, and sleeping lightly.",
        listOf(
            "Sitting where they can see the door and watching everyone who enters.",
            "Disliking people walking behind them or scanning crowds constantly.",
            "Sleeping lightly and reacting strongly to unexpected noises or touch.",
            "Interpreting ordinary behavior as disrespect, manipulation, or a potential threat."
        ),
        "Acknowledge that this survival response protected you inside, but thank God that you are now safe. Practice deep, paced breathing and pray: 'The Lord is my shield; I will lay down and sleep in peace.'"
    ),
    PrisonizationTrait(
        4,
        "4. Emotional Suppression",
        Icons.Default.Lock,
        "Appearing emotionally cold or detached because showing feelings inside was unsafe.",
        listOf(
            "Appearing emotionally cold, detached, or refusing to talk about feelings.",
            "Turning feelings of sadness or fear into anger.",
            "Refusing help, viewing vulnerability as weakness, or withdrawing from discussions."
        ),
        "Jesus wept and showed deep emotion. Vulnerability is a spiritual strength. Begin sharing small feelings with a trusted mentor, and allow the Holy Spirit to soften your heart."
    ),
    PrisonizationTrait(
        5,
        "5. Distrust & Difficulty with Close Relationships",
        Icons.Default.Person,
        "Questioning people's motives, assuming betrayal, and pushing people away.",
        listOf(
            "Questioning people's motives or assuming they will betray or abandon you.",
            "Testing people's loyalty or keeping secrets unnecessarily.",
            "Avoiding depending on anyone and pushing people away before they can reject you."
        ),
        "Trust is rebuilt slowly. Healing from isolation requires taking small risks of vulnerability in a safe, Christian community. Let God be the anchor of your trust."
    ),
    PrisonizationTrait(
        6,
        "6. Strong Reactions to Authority",
        Icons.Default.Info,
        "Either extreme compliance or intense defensiveness around authority figures.",
        listOf(
            "Automatically complying or needing permission for things that do not require it.",
            "Becoming unusually nervous around police, supervisors, or officials.",
            "Or becoming defensive, hostile, and resistant whenever someone gives instructions.",
            "Interpreting a simple correction or request as an attempt to dominate or humiliate you."
        ),
        "Authority inside was often punitive. True leadership is servant-hearted. Learn to pause, breathe, and distinguish between helper feedback and attempts to control."
    ),
    PrisonizationTrait(
        7,
        "7. Prison-Style Communication & Conflict",
        Icons.Default.Warning,
        "Viewing disagreement as disrespect and responding with threats or guardedness.",
        listOf(
            "Viewing disagreement as personal disrespect and believing you must never appear weak.",
            "Responding intensely to staring, touching possessions, or invading personal space.",
            "Using threats, intimidation, silence, or physical presence instead of conversation."
        ),
        "Proverbs says, 'A gentle answer turns away wrath.' Disagreement is a normal part of relationships. Practice walking away to cool down before discussing issues calmly."
    ),
    PrisonizationTrait(
        8,
        "8. Guarding Possessions & Personal Space",
        Icons.Default.Star,
        "Hoarding food, hiding belongings, or getting highly upset when items are touched.",
        listOf(
            "Hoarding food, toiletries, clothing, or money; hiding belongings.",
            "Becoming unusually upset when someone touches your things.",
            "Eating very quickly, guarding your plate, or keeping shoes close while sleeping."
        ),
        "In prison, privacy was zero and items were scarce. In freedom, God is your provider. Work on sharing small items and declaring: 'My Father supply all my needs according to His riches.'"
    ),
    PrisonizationTrait(
        9,
        "9. Social Discomfort & Isolation",
        Icons.Default.Home,
        "Avoiding crowds, restaurants, or malls; preferring to remain alone in one room.",
        listOf(
            "Avoiding crowds, unfamiliar places, celebrations, or malls.",
            "Feeling highly uncomfortable with casual conversation or reading social cues.",
            "Maintaining relationships mainly with other formerly incarcerated people."
        ),
        "Adjustment takes time, especially if you experienced prolonged isolation. Take small trips to quiet public places, and gradually increase your exposure. God did not create us for isolation."
    ),
    PrisonizationTrait(
        10,
        "10. Trouble Adjusting to Freedom & Tech",
        Icons.Default.Refresh,
        "Anxiety about smartphones, apps, social changes, and feeling left behind.",
        listOf(
            "Feeling overwhelmed by smartphones, online banking, transportation, or social media.",
            "Feeling embarrassed about asking for help or frustrated with too many options.",
            "Avoiding learning because not knowing makes you feel ashamed or powerless."
        ),
        "Shame is a lie. Everyone has to learn. Ask a trusted friend or family member to teach you one tech skill a week. Celebrate your small victories of learning!"
    ),
    PrisonizationTrait(
        11,
        "11. A Deeply Rooted Prison Identity",
        Icons.Default.Person,
        "Defining yourself as an inmate or felon rather than a child of God.",
        listOf(
            "Continuing to define yourself primarily as an inmate, felon, or convict.",
            "Believing you must always be tough and do not belong in ordinary society.",
            "Sabotaging positive opportunities because success feels unfamiliar or undeserved."
        ),
        "Your past record does not define your future potential. In Christ, you are a son, a spouse, a parent, a neighbor, and an OverComer. Speak your true identity in Christ daily."
    ),
    PrisonizationTrait(
        12,
        "12. Difficulty with Intimacy & Family Roles",
        Icons.Default.Favorite,
        "Struggling to share authority, express tenderness, or rebuild family trust.",
        listOf(
            "Trouble sharing authority with a spouse or parenting children who grew up without you.",
            "Difficulty receiving affection, expressing tenderness, or handling disagreements without withdrawing.",
            "Expecting immediate loyalty but struggling to understand that trust must be rebuilt over time."
        ),
        "Family roles shifted while you were away. Re-entry requires humility. Listen first, apologize when needed, and understand that rebuilding deep trust is a gradual process."
    ),
    PrisonizationTrait(
        13,
        "13. Survival Mentality Despite Stability",
        Icons.Default.Warning,
        "Expecting everything to disappear tomorrow, keeping bags packed, and avoiding long-term plans.",
        listOf(
            "Constantly expecting to return to prison or keeping bags packed.",
            "Spending money immediately or hiding it; avoiding emotional investment in stable things.",
            "Sabotaging employment/relationships when stable because chaos feels safer than calm."
        ),
        "Chaos may feel familiar, but Christ is your Prince of Peace. God's promise is stable and secure. Practice making 1-year and 5-year plans. Your future is secure in His hands."
    )
)

@Composable
fun ReentryConditioningTab() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Understanding Post-Prison Habits (\"Prisonization\")",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Physical release is immediate, but mental and behavioral conditioning can linger for years—even after being home for five years. This process is often called 'prisonization'—learned survival strategies that were necessary inside but cause friction in ordinary life. It is not a formal mental-health diagnosis, and not everyone who has been incarcerated develops the same traits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        Text(
            text = "Tap a trait below to explore signs & biblical advice:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        var expandedIndex by remember { mutableStateOf<Int?>(null) }

        prisonizationTraits.forEach { trait ->
            val isExpanded = expandedIndex == trait.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedIndex = if (isExpanded) null else trait.id }
                    .testTag("prisonization_trait_${trait.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = trait.icon,
                            contentDescription = trait.title,
                            tint = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = trait.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = trait.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    if (isExpanded) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "Common Behaviors:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        trait.behaviorPoints.forEach { point ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            ) {
                                Text("•", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "RENEWING THE MIND COUNSEL:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = trait.overcomerAdvice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Why would it still be present after five years?
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Why Does Conditioning Persist After 5 Years?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Five years outside does not automatically undo more than ten years of conditioning. The person may have physically left prison but never received support to relearn independent decision-making, emotional regulation, healthy conflict resolution, relationship/parenting skills, trauma recovery, financial management, community living, and establishing a positive identity beyond incarceration. Effective reentry is highly individualized—it requires deeper, loving guidance rather than simply telling someone to 'make better choices.'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        // Institutionalization vs Manipulation / Accountability
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Conditioning vs. Accountability",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Institutionalization explains behavior, but it does not excuse harmful actions. A person can be genuinely struggling with prison-related conditioning and still be responsible for treating others with respect. Compassion and accountability must exist together:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "\"I understand why you learned this behavior inside, but it is no longer safe or acceptable outside, and you are responsible for learning another way.\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        lineHeight = 16.sp
                    )
                }
                Text(
                    text = "Working with a trauma-informed therapist, reentry-trained counselor, or peer support specialist with lived incarceration experience can help determine whether behaviors are due to institutionalization, PTSD, depression, or substance concerns. Lived-experience peers are especially helpful in challenging prison-thinking without shaming.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PostIncarcerationSectionHeader(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BulletPointItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun LieToTruthItem(lie: String, truth: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Lie",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Prison Lie: \"$lie\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    lineHeight = 16.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Truth",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Biblical Freedom: \"$truth\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun VeteranSupportSection() {
    var selectedTab by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("veteran_support_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, Color(0xFF1B5E20).copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Veteran Support Icon",
                    tint = Color(0xFF1B5E20),
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "THE NEXT MISSION: VETERAN SUPPORT & WELLNESS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color(0xFF1B5E20)
                )
            }

            Text(
                text = "You were trained to endure the hardest battles, but you don't have to fight the invisible ones alone. Whether you are navigating the transition to civilian life, processing the weight of your service, or simply having a tough day, find faith-based tools and a community that understands. Seeking support isn't stepping down—it's stepping up. Click here for specialized veteran resources and daily strength.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Dynamic daily reminders/bulletins
            VeteranDailyFeedReminders()

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF1B5E20),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🛡️ Guidance", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🏛️ Experts", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🔑 Truths", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("🧰 Toolbox", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (selectedTab) {
                0 -> VeteranCopingStrategiesTab()
                1 -> VeteranCuratedResourcesTab()
                2 -> VeteranMindRenewalTab()
                3 -> VeteranToolboxTab()
            }
        }
    }
}

@Composable
fun VeteranCopingStrategiesTab() {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedIndex = if (expandedIndex == 0) null else 0 },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👁️ Managing Hypervigilance & PTSD",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Icon(
                        imageVector = if (expandedIndex == 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = Color(0xFF1B5E20)
                    )
                }
                
                AnimatedVisibility(visible = expandedIndex == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Constant alertness keeps you alive in service, but in civilian environments (like malls, traffic, or loud settings), this hyper-arousal triggers intense anxiety, weariness, or anger.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletPointItem("Tactical Seating: Sit in positions where your nervous system feels secure (e.g., facing the room or exit) as you adapt. Do not feel guilty about taking this space.")
                            BulletPointItem("The 4-4-4 Grounding: When triggered, slowly inhale for 4s, hold for 4s, and exhale for 4s. Recite Psalm 18:2: 'The Lord is my rock, my fortress, and my deliverer.'")
                            BulletPointItem("Acknowledge the Shift: Consciously tell your mind, 'The battle is behind me. I am safe under Christ's banner.'")
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedIndex = if (expandedIndex == 1) null else 1 },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🗺️ Restoring Mission & Purpose",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Icon(
                        imageVector = if (expandedIndex == 1) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                AnimatedVisibility(visible = expandedIndex == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Moving from a highly structured, unified platoon to a fragmented civilian world can trigger deep feelings of isolation, loss of identity, or aimlessness.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletPointItem("Identify Your Commander: Your commission has not ended. Christ has called you into His sovereign kingdom to represent grace and truth (2 Timothy 2:3-4).")
                            BulletPointItem("Set Micro-Missions: Establish 2-3 daily spiritual or physical goals. Rebuilding starts with structured daily victories.")
                            BulletPointItem("Gather a New Platoon: Connect with other believers, local small groups, or Christian veterans who understand absolute freedom.")
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedIndex = if (expandedIndex == 2) null else 2 },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️ Overcoming Moral Injury & Guilt",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (expandedIndex == 2) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(visible = expandedIndex == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Decisions made or witnessed in intense conditions can leave deep spiritual wounds or 'moral injury,' making you feel permanently stained or distant from God.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletPointItem("Complete Deliverance: You are a brand new creation (2 Corinthians 5:17). Christ's blood cleanses you of all unrighteousness; there is absolutely zero condemnation (Romans 8:1).")
                            BulletPointItem("It's Safe to Ask: Confession is not a weakness. Reach out to a trusted pastor, Christian counselor, or peer mentor who values absolute grace.")
                            BulletPointItem("Renew the Temple: Treat your body and mind with respect. Exercise, get healthy rest, and spend time in God's peaceful creation.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VeteranCuratedResourcesTab() {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Christian Veteran Authors & Experts Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🎖️ Trusted Christian Leaders & Trauma Experts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                // Expert 1: Chad Robichaux
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Chad Robichaux (Mighty Oaks Warrior Programs)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Former USMC Force Recon Marine. Mighty Oaks offers free, intensive recovery retreats helping veterans overcome PTS (Post-Traumatic Stress) through spiritual warfare, family restoration, and discipleship. Author of 'An Unfair Advantage'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Expert 2: Evan Owens
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Evan Owens (REBOOT Recovery)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Co-founder of REBOOT Recovery, the largest Christian trauma-healing program for military veterans. Focuses deeply on overcoming the 'soul wounds' of combat that clinical therapy often overlooks. Author of 'Healing What's Hidden'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Expert 3: H. Norman Wright
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Dr. H. Norman Wright",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "A pioneer in Christian trauma, grief, and crisis counseling. His clinically sound, scripture-grounded worksheets are the gold standard for processing deep combat-related loss.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Expert 4: Chaplain Doug Carver
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Chaplain (Major General) Doug Carver, US Army (Ret.)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Former Chief of Chaplains for the US Army. His publications on spiritual readiness, troop resilience, and moral injury are vital resources for military families.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }

        // Fast Link Buttons
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🌐 Direct Veteran Program Portals",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Button(
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://rebootrecovery.com/military"))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text("Explore REBOOT Recovery Portal 🎖️", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.mightyoaksprograms.org"))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Visit Mighty Oaks Warrior Programs 🌲", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Crisis Support Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🏛️ Federal & National Veteran Agencies",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletPointItem("Veterans Crisis Line: Free, confidential support available 24/7. Dial 988, then press 1. Text support to 838255.")
                    BulletPointItem("VA PTSD Program: Clinical assistance, coping guides, and expert medical directories tailored to military trauma.")
                    BulletPointItem("Military OneSource: Comprehensive resources, transition coaching, and free confidential non-medical counseling. (Call 800-342-9647)")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.va.gov"))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Visit VA.gov Portal 🏛️", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Community Advocacy & Local Support
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🤝 Advocacy, Community, & Local Support",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BulletPointItem("VFW & American Legion: Local community chapters offering advocacy, benefits claim navigation, peer mentorship, and veteran camaraderie.")
                    BulletPointItem("State Dept of Veterans Affairs: Access state-specific veteran benefits, property tax exemptions, and transition grants.")
                    BulletPointItem("Support Locator: Use the 'Support & Church Locator' above and search the 'Veteran Support' category with your Zip Code to find veteran-focused resources near you!")
                }
            }
        }
    }
}

@Composable
fun VeteranToolboxTab() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("veteran_toolbox_prefs", android.content.Context.MODE_PRIVATE) }

    // State for A.A.R.
    var aarObjective by remember { mutableStateOf("") }
    var aarExecution by remember { mutableStateOf("") }
    var aarAmbush by remember { mutableStateOf("") }
    var aarAdjustment by remember { mutableStateOf("") }
    var aarSavedMessage by remember { mutableStateOf("") }

    var savedAarsCount by remember { mutableStateOf(sharedPrefs.getInt("aar_count", 0)) }
    var showSavedAars by remember { mutableStateOf(false) }

    // State for 5-4-3-2-1 Drill
    var drillStep by remember { mutableStateOf(0) } // 0 = start, 1 = see, 2 = feel, 3 = hear, 4 = smell, 5 = scripture

    // State for Buddy Check
    var buddyCheckDone by remember { mutableStateOf(sharedPrefs.getBoolean("buddy_check_done_today", false)) }
    var buddyCheckSuccessMsg by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. The Daily After Action Review (A.A.R.)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 The Daily After Action Review (A.A.R.)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    if (savedAarsCount > 0) {
                        TextButton(
                            onClick = { showSavedAars = !showSavedAars },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1B5E20))
                        ) {
                            Text(if (showSavedAars) "Hide History" else "History ($savedAarsCount)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(
                    text = "A military-style post-mission debrief translated into a daily spiritual exercise. Evaluate today objectively to secure tomorrow's victory.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!showSavedAars) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = aarObjective,
                            onValueChange = { aarObjective = it },
                            label = { Text("1. Objective: What was my main spiritual goal today?", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.fillMaxWidth().testTag("aar_objective_input"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = aarExecution,
                            onValueChange = { aarExecution = it },
                            label = { Text("2. Execution: What actually happened? (Did I isolate?)", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.fillMaxWidth().testTag("aar_execution_input"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = aarAmbush,
                            onValueChange = { aarAmbush = it },
                            label = { Text("3. Ambush: What triggered me today, and how did I react?", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.fillMaxWidth().testTag("aar_ambush_input"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = aarAdjustment,
                            onValueChange = { aarAdjustment = it },
                            label = { Text("4. Adjustment: What tactic will I change tomorrow?", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.fillMaxWidth().testTag("aar_adjustment_input"),
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (aarObjective.isNotBlank() || aarAdjustment.isNotBlank()) {
                                    val count = savedAarsCount + 1
                                    sharedPrefs.edit()
                                        .putInt("aar_count", count)
                                        .putString("aar_obj_$count", aarObjective)
                                        .putString("aar_exec_$count", aarExecution)
                                        .putString("aar_ambush_$count", aarAmbush)
                                        .putString("aar_adj_$count", aarAdjustment)
                                        .apply()
                                    savedAarsCount = count
                                    aarSavedMessage = "Tactical After Action Review saved successfully. Mission secure!"
                                    aarObjective = ""; aarExecution = ""; aarAmbush = ""; aarAdjustment = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_aar_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = aarObjective.isNotBlank() || aarAdjustment.isNotBlank()
                        ) {
                            Text("Save After Action Review 💾", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        if (aarSavedMessage.isNotEmpty()) {
                            Text(
                                text = aarSavedMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1B5E20),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                } else {
                    // Show Saved History
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in savedAarsCount downTo Math.max(1, savedAarsCount - 4)) {
                            val obj = sharedPrefs.getString("aar_obj_$i", "") ?: ""
                            val exec = sharedPrefs.getString("aar_exec_$i", "") ?: ""
                            val ambush = sharedPrefs.getString("aar_ambush_$i", "") ?: ""
                            val adj = sharedPrefs.getString("aar_adj_$i", "") ?: ""

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("A.A.R. Entry #$i", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    if (obj.isNotEmpty()) Text("🎯 Obj: $obj", style = MaterialTheme.typography.bodySmall)
                                    if (exec.isNotEmpty()) Text("📋 Exec: $exec", style = MaterialTheme.typography.bodySmall)
                                    if (ambush.isNotEmpty()) Text("⚠️ Ambush: $ambush", style = MaterialTheme.typography.bodySmall)
                                    if (adj.isNotEmpty()) Text("🔄 Adjustment: $adj", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. The 5-4-3-2-1 "Stand Firm" Drill
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🛡️ The 5-4-3-2-1 \"Stand Firm\" Drill",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "High anxiety or PTSD trigger flares? Instantly ground your nervous system in the present and stand on God's Word.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (drillStep) {
                            0 -> {
                                Text("Engage Tactical Grounding", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("Click below to start the step-by-step physical-spiritual sensory alignment drill.", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Button(
                                    onClick = { drillStep = 1 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Start Stand Firm Drill 🛡️", color = Color.White)
                                }
                            }
                            1 -> {
                                Text("👀 STEP 5: VISUAL SCAN", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("Name 5 things you can see around you right now out loud (e.g. wall clock, chair, tree, lamp, paper).", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Button(
                                    onClick = { drillStep = 2 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Next Step (Step 4) 👉", color = Color.White)
                                }
                            }
                            2 -> {
                                Text("👉 STEP 4: PHYSICAL TOUCH", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("Focus on 4 things you can physically feel (e.g. your feet on the hard floor, the shirt on your back, the breeze, the cool desk).", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Button(
                                    onClick = { drillStep = 3 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Next Step (Step 3) 👉", color = Color.White)
                                }
                            }
                            3 -> {
                                Text("👂 STEP 3: AUDITORY DETECT", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("Listen closely. Name 3 distinct things you can hear (e.g. traffic outside, the hum of the refrigerator, birds chirping).", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Button(
                                    onClick = { drillStep = 4 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Next Step (Step 2) 👉", color = Color.White)
                                }
                            }
                            4 -> {
                                Text("👃 STEP 2: OLFACTORY CHECK", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("Focus on 2 things you can smell or taste (e.g. soap, coffee, rain, fresh cut wood).", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Button(
                                    onClick = { drillStep = 5 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Final Anchor (Step 1) 🛡️", color = Color.White)
                                }
                            }
                            5 -> {
                                Text("🛡️ STEP 1: SPIRITUAL SHIELD", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("Now stand firm on this Scripture. Speak it out loud:\n\n\"The Lord is my rock, my fortress, and my deliverer; my God is my strength, in whom I will trust!\" (Psalm 18:2)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color(0xFF1B5E20))
                                Button(
                                    onClick = { drillStep = 0 },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Secure Victory (Complete) ✅", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. The Buddy Check Protocol
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🤝 The Buddy Check Protocol",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "In the military, you never operate without a battle buddy. In civilian life, isolation is your number one enemy. Conduct one buddy check every 24 hours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val nextVal = !buddyCheckDone
                            buddyCheckDone = nextVal
                            sharedPrefs.edit().putBoolean("buddy_check_done_today", nextVal).apply()
                            buddyCheckSuccessMsg = if (nextVal) "Buddy Check completed today! Tactical connection secure. Iron sharpens iron." else ""
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (buddyCheckDone) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (buddyCheckDone) Color(0xFF2E7D32) else Color(0xFF1B5E20),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I completed my Buddy Check for today!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (buddyCheckDone) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (buddyCheckSuccessMsg.isNotEmpty()) {
                    Text(
                        text = buddyCheckSuccessMsg,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Quick Encouragement Templates (Tap to Copy):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                listOf(
                    "Hey battle buddy, just conducting a Buddy Check. Standing with you in the unit today, hope you are securing victories!",
                    "Just checking in on you, brother. If you ever need anything or want to chat, I'm here. You're an OverComer!"
                ).forEachIndexed { idx, msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Buddy Check Message", msg)
                                clipboard.setPrimaryClip(clip)
                                buddyCheckSuccessMsg = "Message #${idx + 1} copied to clipboard! Send it to your Battle Buddy."
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Copy", tint = Color(0xFF1B5E20), modifier = Modifier.size(16.dp))
                            Text(text = "\"$msg\"", style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VeteranDailyFeedReminders() {
    val feedItems = listOf(
        "🤝 BATTLE RECON: It’s time for your daily Buddy Check. Reach out, send a text, or call a battle buddy right now to check on their unit status. Isolation is the enemy's trap!",
        "🛡️ TACTICAL AMBUSH? Feeling high hyperarousal or panic? Tap into the 5-4-3-2-1 Grounding Drill right now. End on Psalm 18:2 - He is your ultimate fortress!",
        "📋 MISSION REVIEW: Don't forget to conduct your daily After Action Review (A.A.R.) tonight. Evaluate today's triggers, adjustments, and secure tomorrow's victory.",
        "👑 BRAND NEW UNIT: Your military training gave you incredible resilience. In Christ, that is supercharged by the Holy Spirit. You are not damaged; you are a brand new creation!"
    )
    var currentIndex by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(18.dp))
                    Text(
                        text = "📢 DAILY VETERAN BULLETIN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFF1B5E20)
                    )
                }
                Text(
                    text = "${currentIndex + 1}/${feedItems.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF1B5E20)
                )
            }
            
            Text(
                text = feedItems[currentIndex],
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { currentIndex = if (currentIndex > 0) currentIndex - 1 else feedItems.size - 1 },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1B5E20))
                ) {
                    Text("Previous", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { currentIndex = if (currentIndex < feedItems.size - 1) currentIndex + 1 else 0 },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1B5E20))
                ) {
                    Text("Next Bulletin", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VeteranMindRenewalTab() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Renewing your mind means swapping military-transition lies for God's sovereign, liberating truth. Pray over these daily:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        VeteranLieToTruthItem(
            lie = "I am permanently broken or damaged by the combat or struggles I experienced.",
            truth = "I am a new creation in Christ Jesus (2 Corinthians 5:17). He heals the brokenhearted, binds up their wounds, and restores my life completely."
        )

        VeteranLieToTruthItem(
            lie = "Asking for help is a sign of weakness and failure as a strong warrior.",
            truth = "Seeking help is a tactical sign of strength and alignment. King David, a fierce warrior, cried out to God and relied on trusted brothers in times of distress (Psalm 18:6)."
        )

        VeteranLieToTruthItem(
            lie = "My best years are behind me, and I have lost my purpose since taking off the uniform.",
            truth = "God's calling on my life is irrevocable (Romans 11:29). He has plans to give me a hope and a glorious future representing His kingdom (Jeremiah 29:11)."
        )

        VeteranLieToTruthItem(
            lie = "No civilian can ever understand or walk with me; I am entirely alone.",
            truth = "Christ has tasted death and pain for me; He understands me perfectly. I can find an authentic, loving platoon of brothers in a Bible-believing local church."
        )
    }
}

@Composable
fun VeteranLieToTruthItem(lie: String, truth: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Lie",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Military/Transition Lie: \"$lie\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    lineHeight = 16.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Truth",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Biblical Freedom: \"$truth\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

data class BookResource(
    val title: String,
    val author: String,
    val focus: String,
    val whyItWorks: String,
    val quote: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuratedBiblicalLibrarySection() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(-1) }

    val categories = listOf(
        "Substance Recovery",
        "Mental Health",
        "Marriage & Family",
        "Christian Counseling",
        "Biblical Care",
        "Specialty & Trauma"
    )

    val recoveryBooks = listOf(
        BookResource(
            title = "The Cross and the Switchblade / Global Teen Challenge Curriculum",
            author = "David Wilkerson",
            focus = "Spiritual power of deliverance & structured faith-based recovery.",
            whyItWorks = "As the founder of Teen Challenge, his foundational approach to addiction remains the gold standard in Pentecostal and Charismatic recovery ministries.",
            quote = "The Holy Spirit will always meet you on the level of your faith, and if you believe that Jesus is able to deliver you completely, He will do it."
        ),
        BookResource(
            title = "Crossroads: A Step-by-Step Guide Away from Addiction",
            author = "Edward T. Welch",
            focus = "Strictly biblical personal accountability and peer-support.",
            whyItWorks = "This workbook is excellent for peer support specialists leading small groups because it requires heavy personal accountability and directly addresses the behavioral choices that fuel addiction.",
            quote = "Addiction is worship gone wrong. True freedom begins when we reorient our hearts to worship the living God instead of our desires."
        ),
        BookResource(
            title = "Celebrate Recovery Curriculum",
            author = "John Baker",
            focus = "Christ-centered 12-step translation.",
            whyItWorks = "It translates the traditional 12-step model into a purely Christ-centered framework. It is highly structured, relies on peer leadership, and is easily integrated into transitional housing mandates.",
            quote = "You cannot heal a wound by saying it's not there. We must bring our hurts, habits, and hang-ups into the healing light of Jesus Christ."
        ),
        BookResource(
            title = "Nicky Cruz Outreach & Run Baby Run",
            author = "Nicky Cruz",
            focus = "Absolute deliverance and street-level transformation.",
            whyItWorks = "Demonstrates the power of the Gospel to reach the most hardened hearts, serving under David Wilkerson and later founding his own global outreach.",
            quote = "I looked at David Wilkerson and said, 'If you come near me, I'll kill you.' He looked back with tears in his eyes and said, 'You could cut me into a thousand pieces, Nicky, and every piece would love you.' That love broke my heart."
        )
    )

    val mentalHealthBooks = listOf(
        BookResource(
            title = "Grace for the Afflicted: A Clinical and Biblical Perspective on Mental Illness",
            author = "Dr. Matthew S. Stanford",
            focus = "Definitively bridging neuroscientific insights and spiritual dynamics.",
            whyItWorks = "Dr. Stanford is a Christian neuroscientist. This is the definitive book for ministry leaders trying to navigate the boundary between spiritual warfare and clinical mental illness. Reassures users that treating chemical imbalances is medically sound.",
            quote = "Mental illness is not a character flaw or a spiritual failure; it is a physical condition in a broken world that responds to medical treatment and is sustained by the grace of God."
        ),
        BookResource(
            title = "Boundaries: When to Say Yes, How to Say No to Take Control of Your Life",
            author = "Dr. Henry Cloud & Dr. John Townsend",
            focus = "Theological and psychological framework for healthy limits.",
            whyItWorks = "For a ministry operating on the philosophy of a 'hand up, not a handout,' this is essential reading. It provides a theological and psychological framework for deep compassion combined with strict, uncompromising accountability.",
            quote = "We change our behavior when the pain of staying the same becomes greater than the pain of changing. Boundaries help create that healthy pressure."
        ),
        BookResource(
            title = "Blame It on the Brain? Distinguishing Chemical Imbalances, Brain Disorders, and Disobedience",
            author = "Edward T. Welch",
            focus = "Discerning medical/clinical issues from spiritual/choice issues.",
            whyItWorks = "Helps ministry leaders and chaplains discern when a behavior is a spiritual/choice issue and when it is a medical/clinical issue requiring professional bridging.",
            quote = "Brain problems may explain our limitations, but they do not excuse our sins. We must treat the physical brain with medicine while caring for the spiritual soul with the Word."
        ),
        BookResource(
            title = "The Christian Counseling Companion & The Struggle is Real",
            author = "Dr. Jared Pingleton",
            focus = "Integrating clinical psychology with deep, pastoral, church-based care.",
            whyItWorks = "Written by a clinical psychologist and minister, it provides highly practical guidelines for caring for mental and relational health directly inside the church.",
            quote = "The church must be the safest place on earth to struggle. We must destigmatize mental health and provide a compassionate bridge between clinical excellence and biblical truth."
        )
    )

    val marriageFamilyBooks = listOf(
        BookResource(
            title = "Sacred Marriage: What if God Designed Marriage to Make Us Holy More Than to Make Us Happy?",
            author = "Gary Thomas",
            focus = "Marriage as an engine for spiritual sanctification and holiness.",
            whyItWorks = "This is a cornerstone book that shifts the focus of marriage from mere personal fulfillment to spiritual sanctification. It is excellent for pastoral counseling and helping couples in crisis find a higher, God-centered purpose.",
            quote = "God did not design marriage to be an easy path to personal happiness, but a sacred crucible that refines our character and makes us more like Christ."
        ),
        BookResource(
            title = "Love & Respect: The Love She Desires; The Respect He Desperately Needs",
            author = "Dr. Emerson Eggerichs",
            focus = "Grounded in Ephesians 5:33 communication cycles.",
            whyItWorks = "Grounded purely in Ephesians 5:33, this book breaks down the communication cycles that destroy marriages. It is highly practical, action-oriented, and universally applicable.",
            quote = "Without love, she reacts without respect. Without respect, he reacts without love. This is the crazy cycle. We must choose to break it with Christlike grace."
        ),
        BookResource(
            title = "Vertical Marriage: The One Secret That Will Change Your Marriage",
            author = "Dave & Ann Wilson",
            focus = "Prioritizing the vertical relationship with Christ first.",
            whyItWorks = "A highly accessible, engaging resource that emphasizes that a couple's horizontal relationship can only be fixed by addressing their vertical relationship with Christ first.",
            quote = "If you are looking to your spouse to satisfy the deepest longings of your soul, you are setting them up to fail. Only Jesus can fill that void. Align vertically first."
        ),
        BookResource(
            title = "Saving Your Marriage Before It Starts (SYMBIS)",
            author = "Dr. Les and Leslie Parrott",
            focus = "Premarital and marital structured assessment and relationship strengthening.",
            whyItWorks = "Provides highly structured, evidence-based relationship strengthening and assessments to build a lasting, bulletproof marriage under God.",
            quote = "A good marriage isn't something you find; it's something you make. It requires intentionality, communication, and a shared spiritual foundation."
        ),
        BookResource(
            title = "The New Dare to Discipline / Focus on the Family",
            author = "Dr. James Dobson",
            focus = "Biblical family foundations, healthy boundaries, and behavioral discipline.",
            whyItWorks = "Strong, compassionate family guidance that teaches respect, healthy behavioral boundaries, and character development under Christ.",
            quote = "Children do not respect a parent who allows them to dominate the household. Healthy discipline is an act of deep, protective love, not anger."
        )
    )

    val christianCounselingBooks = listOf(
        BookResource(
            title = "Christian Counseling: A Comprehensive Guide",
            author = "Dr. Gary R. Collins",
            focus = "Standard, comprehensive pastoral care and counseling textbook.",
            whyItWorks = "Widely considered the standard textbook for Christian counselors, this resource covers a vast range of counseling scenarios, developmental stages, and structural frameworks for running a counseling ministry.",
            quote = "Pastoral counseling is not about giving easy answers, but walking with people in their deepest pain while pointing them to the healing presence of Christ."
        ),
        BookResource(
            title = "Competent Christian Counseling: Foundations and Practice",
            author = "Dr. Timothy Clinton (and the AACC)",
            focus = "Clinical competence coupled with strict biblical grounding.",
            whyItWorks = "A definitive contemporary guide that combines clinical competence with strict biblical grounding, mapping out effective strategies for a modern landscape.",
            quote = "We are called to love people with our minds fully engaged. True competence is where cutting-edge clinical insight meets absolute scriptural authority."
        ),
        BookResource(
            title = "Understanding People: Why We Do What We Do & Connecting",
            author = "Dr. Larry Crabb",
            focus = "Deep psychological insight paired with understanding inner core longings.",
            whyItWorks = "Known for deep psychological insight paired with spiritual maturity, Crabb's work focuses on understanding inner core longings and how true community fosters healing.",
            quote = "Healing does not happen in isolation. The deepest wounds of our hearts are healed when we connect with others in a safe, grace-saturated fellowship."
        )
    )

    val biblicalCareBooks = listOf(
        BookResource(
            title = "Instruments in the Redeemer's Hands: People in Need of Change Helping People in Need of Change",
            author = "Paul David Tripp",
            focus = "Ordinary believers acting as tools of active grace in others' lives.",
            whyItWorks = "A highly respected book detailing how ordinary believers and ministers can engage in personal, transformative ministry with others in a safe redemptive community.",
            quote = "We are all people in need of change helping other people in need of change. None of us have arrived; we are simply walk-companions under God's grace."
        ),
        BookResource(
            title = "Seeing with New Eyes: Counseling and the Human Condition Through the Lens of Scripture",
            author = "Dr. David Powlison",
            focus = "Diagnosing human motives, core cravings, and worries.",
            whyItWorks = "Powlison offers profound insights into how Scripture diagnoses human motives and brings practical, grace-centered change to daily struggles.",
            quote = "The Bible is not a self-help manual; it is a story of a Rescuer. When we see our struggles through the lens of God's Word, everything changes."
        ),
        BookResource(
            title = "When People Are Big and God Is Small",
            author = "Edward T. Welch",
            focus = "Overcoming peer pressure, codependency, and the fear of man.",
            whyItWorks = "Welch expertly navigates how we become trapped by the fear of others' opinions, and shows how to find absolute safety, security, and identity in Christ.",
            quote = "We fear people because they can hurt us, reject us, or expose us. But when God becomes big, we see that His love is the only opinion that truly defines us."
        )
    )

    val specialtyTraumaBooks = listOf(
        BookResource(
            title = "The Wounded Heart: Hope for Adult Victims of Childhood Sexual Abuse",
            author = "Dr. Dan B. Allender",
            focus = "Restoring emotional and spiritual damage from abuse and childhood trauma.",
            whyItWorks = "A landmark text in Christian trauma care, addressing the deep emotional and spiritual damage of abuse with immense empathy and theological depth.",
            quote = "To run from our story of pain is to run from the very place where God wants to meet us and write a story of redemption. Healing requires facing the wounds with courage."
        )
    )

    val allBooks = mapOf(
        0 to recoveryBooks,
        1 to mentalHealthBooks,
        2 to marriageFamilyBooks,
        3 to christianCounselingBooks,
        4 to biblicalCareBooks,
        5 to specialtyTraumaBooks
    )

    val displayedBooks = if (searchQuery.isNotBlank()) {
        allBooks.values.flatten().filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.author.contains(searchQuery, ignoreCase = true) ||
            it.focus.contains(searchQuery, ignoreCase = true) ||
            it.whyItWorks.contains(searchQuery, ignoreCase = true)
        }
    } else {
        allBooks[selectedCategoryIndex] ?: emptyList()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("curated_library_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Library Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "VETTED BIBLICAL LIBRARY & RESOURCES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "Discover highly vetted, theologically sound resources that align with a biblically orthodox worldview while bridging faith and practical clinical competence.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by book, author, or keyword...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            if (searchQuery.isBlank()) {
                // Category Selector (chips scroll)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEachIndexed { index, category ->
                        val isSelected = selectedCategoryIndex == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryIndex = index },
                            label = { Text(category, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Book Resources List
            if (searchQuery.isBlank() && selectedCategoryIndex == -1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Search by keyword or select a category button above to view our vetted libraries of biblical counseling & recovery resources.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else if (displayedBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching resources found.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    displayedBooks.forEach { book ->
                        LibraryBookItem(book = book)
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryBookItem(book: BookResource) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "By ${book.author}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Focus Area",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Focus: ${book.focus}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Why it works: ${book.whyItWorks}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp),
                    lineHeight = 16.sp
                )
            }

            // Left-accent Quote Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\"${book.quote}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TheFaithConnectionSection() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("the_faith_connection_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Faith Connection Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "THE FAITH CONNECTION",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "We are dedicated to providing compassionate, Christ-centered, clinical-bridging support to individuals and families absolutely free of charge. Your generosity makes this refuge possible. To help us keep our resources, courses, and digital companions completely free, please consider viewing our ministry or donating to the cause.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Button(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.thefaithconnection.org"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("donate_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Donate Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "To Donate & Keep Services Free",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            TextButton(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.thefaithconnection.org"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("visit_website_button")
            ) {
                Text(
                    text = "Visit www.thefaithconnection.org",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun MentalHealthSupportSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "VETTED BIBLICAL CLINICAL COUNSELING",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "We align spiritual healing with healthy mind alignment under Christ, drawing from leading biblical and clinical counseling experts. Note: This app does not promote or utilize medication-assisted treatment (MAT) or chemical dependencies for soul care.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Text(
                text = "Trusted Counseling Guides & Literature",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Author 1
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "• Dr. Gary R. Collins (Christian Counseling)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Comprehensive clinical-spiritual integration framework for anxiety, depression, and personal crisis management.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            // Author 2
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "• Dr. Timothy Clinton & AACC (Competent Christian Counseling)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Clinically competent, scripture-grounded approaches for renewing the mind and processing deep trauma.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            // Author 3
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "• Dr. Larry Crabb (Connecting & Understanding People)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Deep relational healing model that addresses inner core longings and finding complete security in God.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}

@Composable
fun VeteranTargetedStruggleSupportCard() {
    var selectedSubtab by remember { mutableStateOf(0) }
    val struggles = listOf("PTSD & Anxiety", "Moral Injury", "Transition Fatigue", "Other")
    var customStruggleText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎯 CHOOSE YOUR FOCUS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = Color(0xFF1B5E20)
            )

            // Horizontal Tab Row for Struggles
            ScrollableTabRow(
                selectedTabIndex = selectedSubtab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF1B5E20),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                struggles.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedSubtab == index,
                        onClick = { selectedSubtab = index },
                        text = { Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (selectedSubtab) {
                0 -> {
                    Text(
                        text = "Managing PTSD & Hypervigilance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Your brain was trained to recognize and alert on any potential threat. In civilian settings, this response triggers false alarms. Ground your mind in Jesus. He is your fortress.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                1 -> {
                    Text(
                        text = "Resolving Moral Guilt & Sorrow",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Combat or operational stress can leave deep spiritual wounds. Understand that your past is completely covered under Christ's perfect blood. You are a clean, new creation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                2 -> {
                    Text(
                        text = "Navigating Transition Fatigue",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Leaving a highly structured, close-knit platoon can feel highly isolating. Create micro-missions daily for spiritual growth and stay connected to local church small groups.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                3 -> {
                    Text(
                        text = "Custom Reflection Focus",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    OutlinedTextField(
                        value = customStruggleText,
                        onValueChange = { customStruggleText = it },
                        label = { Text("Describe what you are navigating today...", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth().testTag("veteran_custom_struggle_input"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (customStruggleText.isNotEmpty()) {
                        Text(
                            text = "Take this to the Lord in prayer today. He cares for you deeply.",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VeteranSupportResourcesOnlyCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFF1B5E20).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Need Immediate Assistance?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Button(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:988"))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("veteran_support_resources_button")
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Veterans support and resources click here", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReentryTargetedStruggleSupportCard() {
    var selectedSubtab by remember { mutableStateOf(0) }
    val struggles = listOf("Structure & Habits", "Decision Fatigue", "Boundary Building", "Other")
    var customStruggleText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎯 CHOOSE YOUR RE-ENTRY FOCUS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = Color(0xFF3F51B5)
            )

            ScrollableTabRow(
                selectedTabIndex = selectedSubtab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF3F51B5),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                struggles.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedSubtab == index,
                        onClick = { selectedSubtab = index },
                        text = { Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (selectedSubtab) {
                0 -> {
                    Text(
                        text = "Managing Institutional Habits (Structure)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                    Text(
                        text = "Transitioning to ordinary life often means dealing with prisonization—seeking rigid schedules and finding changes highly disorienting. God is your ultimate orchestrator of time; build flexible schedules anchored in His peace.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                1 -> {
                    Text(
                        text = "Overcoming Decision Fatigue",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                    Text(
                        text = "When every choice was once made for you, simple civilian options can feel completely paralyzing. Practice deep breathing, make simple decisions, and remember that Christ holds your hand.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                2 -> {
                    Text(
                        text = "Healthy Boundary Building",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                    Text(
                        text = "Rebuilding relationships with family, friends, or coworkers requires wisdom and structure. Learn the power of honest, compassionate, biblical boundaries to guard your focus.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                3 -> {
                    Text(
                        text = "Custom Reflection Focus",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                    OutlinedTextField(
                        value = customStruggleText,
                        onValueChange = { customStruggleText = it },
                        label = { Text("Describe what you are navigating today...", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth().testTag("reentry_custom_struggle_input"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (customStruggleText.isNotEmpty()) {
                        Text(
                            text = "Commit your plans to the Lord, and your thoughts will be established (Proverbs 16:3).",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReentrySupportSection() {
    var selectedTab by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color(0xFF3F51B5)
                )
                Text(
                    text = "STEPS TO RESTORATION & FREEDOM",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color(0xFF3F51B5)
                )
            }

            Text(
                text = "Transitioning to ordinary life after incarceration is a journey that requires strength, structure, and active redemptive community. Draw from targeted scripture, mental exercises, and trusted biblical leaders below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Dynamic daily reminders
            ReentryDailyFeedReminders()

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF3F51B5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🛡️ Guidance", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🏛️ Experts", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🧰 Toolbox", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (selectedTab) {
                0 -> RestorationCopingStrategiesTab()
                1 -> ReentryExpertAdviceTab()
                2 -> ReentryToolboxTab()
            }
        }
    }
}

@Composable
fun RestorationCopingStrategiesTab() {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedIndex = if (expandedIndex == 0) null else 0 },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3F51B5).copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👁️ Managing Institutional Structure Habits",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                    Icon(
                        imageVector = if (expandedIndex == 0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = Color(0xFF3F51B5)
                    )
                }
                
                AnimatedVisibility(visible = expandedIndex == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Institutional conditioning, or 'prisonization,' often leads individuals to struggle to organize their day without rigid schedules or strict, directed rules.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletPointItem("Own Your Routine: Create a healthy daily schedule yourself, deciding when to wake, eat, study, and rest. This builds decision confidence.")
                            BulletPointItem("Adapt Gracefully: When plans shift, take a deep breath. Focus on what you can manage, and trust Proverbs 3:5-6.")
                            BulletPointItem("Work Structure: Use structured employment as a healthy bridge while learning to manage open-ended home environments.")
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedIndex = if (expandedIndex == 1) null else 1 },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🗺️ Overcoming Decision Fatigue",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Icon(
                        imageVector = if (expandedIndex == 1) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                AnimatedVisibility(visible = expandedIndex == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "After years of having choices made for you, standard civilian environments can trigger intense choice overload or anxiety.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletPointItem("Limit Options: Simplify choices initially (e.g. choose between 2 meals, buy similar clothing) to prevent cognitive overload.")
                            BulletPointItem("Seek Wise Council: For large choices, talk to a Christian mentor or support partner who values objective guidance.")
                            BulletPointItem("Take Pause: If you feel overwhelmed, step away from the immediate scene. Re-center in peaceful quietness.")
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { expandedIndex = if (expandedIndex == 2) null else 2 },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️ Constructing Healthy Family Boundaries",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (expandedIndex == 2) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(visible = expandedIndex == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Establishing new boundaries with loved ones can feel complex. Communication is key to restoring trust and respect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            BulletPointItem("Establish Boundaries: Use Henry Cloud and John Townsend's biblical boundaries to communicate limits with deep grace and absolute honesty.")
                            BulletPointItem("Earn Trust: Remember that trust is built step-by-step through consistent, honest, daily action, not overnight.")
                            BulletPointItem("Respect Their Time: Allow family members space to process the transition and heal at their own pace.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReentryExpertAdviceTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "✝️ Trusted Christian Voices & Psychologists",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F51B5)
                )

                // Author 1: Colson
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Chuck Colson & Prison Fellowship",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "The pioneer of modern Christian prison ministry. His classic work 'Born Again' and Prison Fellowship's free reentry workbooks offer incredibly practical spiritual structures for returning citizens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Author 2: Cloud & Townsend
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Drs. Henry Cloud & John Townsend (Boundaries)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "The absolute gold standard for relational and emotional health. Learn how to construct strong biblical boundaries to protect your walk from toxic family dynamics or former negative influences.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Author 3: Groeschel
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Pastor Craig Groeschel",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "His classic 'Winning the War in Your Mind' bridges Cognitive Behavioral Therapy (CBT) with Scripture, providing a step-by-step biblical filter to break free from the limiting inmate mindset.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Author 4: Jantz
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Dr. Gregory Jantz",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "A Christian psychologist pioneer in whole-person care. His teachings address the complete physical, emotional, and spiritual toll of trauma and chemical struggles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            border = BorderStroke(1.dp, Color(0xFF673AB7).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🧠 Trusted Secular Clinical Foundations",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF673AB7)
                )

                // Author 5: Robinson & Little
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Drs. Kenneth Robinson & Gregory Little (MRT Founders)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Creators of Moral Reconation Therapy (MRT), designed specifically for criminal justice. Focuses on elevating moral reasoning and taking absolute personal responsibility, with zero anti-faith bias.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                // Author 6: Frankl
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "• Dr. Viktor Frankl (Man's Search for Meaning)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Renowned psychiatrist, logotherapist, and Holocaust survivor. His timeless work teaches how humans can find profound, God-given meaning and resilience in the face of extreme confinement and transition.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReentryToolboxTab() {
    var haltHungry by remember { mutableStateOf(false) }
    var haltAngry by remember { mutableStateOf(false) }
    var haltLonely by remember { mutableStateOf(false) }
    var haltTired by remember { mutableStateOf(false) }

    var selectedBullseyeRing by remember { mutableStateOf(0) } // 0 = Inner, 1 = Middle, 2 = Outer

    var thoughtInput by remember { mutableStateOf("") }
    var isThoughtProcessed by remember { mutableStateOf(false) }

    var customGoalInput by remember { mutableStateOf("") }
    var goalsList by remember {
        mutableStateOf(
            listOf(
                "Spend 10 minutes in prayer and reading Scripture",
                "Apply for one job, study, or complete a vocational task",
                "Attend an OverComer group, church service, or call a mentor",
                "Secure healthy rest and proper sleep"
            )
        )
    }
    var checkedGoals by remember { mutableStateOf(setOf<String>()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. HALT Check-in Tool
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🍽️ The HALT Check-in Tool",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F51B5)
                )
                Text(
                    text = "Transitioning back can feel overwhelming. Pause and check if physical/emotional fatigue is masking itself as a spiritual relapse.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "🍕 Hungry" to { haltHungry = !haltHungry; haltHungry },
                        "😡 Angry" to { haltAngry = !haltAngry; haltAngry },
                        "👥 Lonely" to { haltLonely = !haltLonely; haltLonely },
                        "😴 Tired" to { haltTired = !haltTired; haltTired }
                    ).forEach { (label, toggle) ->
                        val isChecked = when (label.substring(3)) {
                            "Hungry" -> haltHungry
                            "Angry" -> haltAngry
                            "Lonely" -> haltLonely
                            "Tired" -> haltTired
                            else -> false
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isChecked) Color(0xFF3F51B5) else Color(0xFFE8EAF6),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { toggle() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isChecked) Color.White else Color(0xFF3F51B5),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (haltHungry || haltAngry || haltLonely || haltTired) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (haltHungry) {
                                Text("• Pizza/Food fuel: Go grab a healthy meal! God designed our bodies to require physical strength before our minds can rest.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            if (haltAngry) {
                                Text("• Calm the storm: Take a deep breath and pray. Invite Jesus to align your spirit before reacting to any trigger.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            if (haltLonely) {
                                Text("• Reach out: Text a mentor or Battle Buddy right now. Isolation is where old habits thrive; stay connected.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            if (haltTired) {
                                Text("• Safe Rest: It is time for proper sleep. Remember, Elijah was utterly exhausted when God fed him and let him sleep before speaking.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Boundaries Bullseye Tool
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "🎯 The Boundaries Bullseye",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F51B5)
                )
                Text(
                    text = "Sort your relationships into three concentric circles to safely build trust and avoid slipping back into old environments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("🟢 Center (Inner)", "🟡 Middle Circle", "🔴 Outer Circle").forEachIndexed { index, ringName ->
                        Box(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = { selectedBullseyeRing = index },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedBullseyeRing == index) Color(0xFF3F51B5) else Color(0xFFE8EAF6),
                                    contentColor = if (selectedBullseyeRing == index) Color.White else Color(0xFF3F51B5)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text(ringName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        when (selectedBullseyeRing) {
                            0 -> {
                                Text("🟢 CENTER (Inner Circle) - Full Trust", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text("This is reserved exclusively for safe, faith-filled, positive influences (Christian mentors, pastors, healthy family, sponsors). They actively hold you accountable and build up your faith.", style = MaterialTheme.typography.bodySmall)
                            }
                            1 -> {
                                Text("🟡 MIDDLE CIRCLE - Necessary Connections", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                                Text("This is for standard daily contacts (probation officers, work colleagues, neighbors). These are polite, structured relationships built on respect and necessary tasks, but not deep spiritual vulnerability.", style = MaterialTheme.typography.bodySmall)
                            }
                            2 -> {
                                Text("🔴 OUTER CIRCLE - Guarded Distance", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                Text("This is for old friends, acquaintances, or loved ones who are still actively engaged in toxic behaviors. You love them, pray for them, but only engage from a distance in safe public settings. Never enter vulnerable spaces with them.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // 3. Take Every Thought Captive Filter (CBT)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "💭 \"Take Every Thought Captive\" Filter",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F51B5)
                )
                Text(
                    text = "Aligning 2 Corinthians 10:5 with practical thought-reframing. When a limiting lie enters your mind (e.g. \"I'm ruined, I'll end up back inside\"), run it through the 3 C's.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = thoughtInput,
                    onValueChange = { thoughtInput = it; isThoughtProcessed = false },
                    label = { Text("Enter a negative or limiting lie you are feeling...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("reentry_thought_captive_input"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = { if (thoughtInput.isNotBlank()) isThoughtProcessed = true },
                    modifier = Modifier.fillMaxWidth().testTag("reentry_process_thought_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = thoughtInput.isNotBlank()
                ) {
                    Text("Apply 3 C's Filter 🛡️", fontWeight = FontWeight.Bold, color = Color.White)
                }

                AnimatedVisibility(visible = isThoughtProcessed) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Yellow alert-style card
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🛡️ THE 3 C's FILTER IN ACTION:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                            
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✊ 1. CATCH: Caught negative lie: \"$thoughtInput\"", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text("🔍 2. CHECK: Ask yourself: 'Is this true based on God's Word, or is it my past talking?' Romans 8:1 says there is zero condemnation in Christ.", style = MaterialTheme.typography.bodySmall)
                                Text("🔄 3. CHANGE: Replace it with Scripture! Confess: 'I am a brand new creation in Christ Jesus (2 Corinthians 5:17). The old has gone, the new has come!'", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }
        }

        // 4. Next 24 Hours Plan
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "📅 The \"Next 24 Hours\" Planner",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F51B5)
                )
                Text(
                    text = "Anxiety about finding a job, building credit, or long-term plans can cause panic. Shrink your world down to secured daily victories.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    goalsList.forEach { goal ->
                        val isChecked = checkedGoals.contains(goal)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    checkedGoals = if (isChecked) checkedGoals - goal else checkedGoals + goal
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isChecked) Color(0xFF2E7D32) else Color(0xFF3F51B5),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customGoalInput,
                        onValueChange = { customGoalInput = it },
                        label = { Text("Add custom 24-hour goal...", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f).testTag("reentry_custom_goal_input"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (customGoalInput.isNotBlank()) {
                                goalsList = goalsList + customGoalInput.trim()
                                customGoalInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = customGoalInput.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Add", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ReentryDailyFeedReminders() {
    val feedItems = listOf(
        "🍽️ OVERWHELMED? Check your HALT levels. Are you Hungry, Angry, Lonely, or Tired? Address those basic needs first before reacting to any triggers!",
        "🎯 TRUST PROTOCOL: Remember the Boundaries Bullseye. Protect your walk by keeping safe, faith-filled mentors in your center circle. Engage outer circles from a distance.",
        "💭 THOUGHT TRANSFORMATION: Caught a negative lie like 'I'm a failure'? Run it through the 3 C's Filter: Catch the lie, Check it against the Word, and Change it with Scripture!",
        "📅 MANAGING ANXIETY: Don't try to win the next five years today. Shrink your world to the next 24 hours. Focus on your Micro-Missions and secure a daily victory!"
    )
    var currentIndex by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
        border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF3F51B5), modifier = Modifier.size(18.dp))
                    Text(
                        text = "📢 DAILY RESTORATION FEED",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = Color(0xFF3F51B5)
                    )
                }
                Text(
                    text = "${currentIndex + 1}/${feedItems.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF3F51B5)
                )
            }
            
            Text(
                text = feedItems[currentIndex],
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { currentIndex = if (currentIndex > 0) currentIndex - 1 else feedItems.size - 1 },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF3F51B5))
                ) {
                    Text("Previous", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { currentIndex = if (currentIndex < feedItems.size - 1) currentIndex + 1 else 0 },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF3F51B5))
                ) {
                    Text("Next Reminder", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReentrySupportResourcesOnlyCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3F51B5).copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Looking for Re-Entry Assistance?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5)
            )
            Button(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.reentry.org"))
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("reentry_support_resources_button")
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Re-Entry Support and Transition Freedom resources click here", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}



