package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.OverComerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureApiSettingsDialog(
    viewModel: OverComerViewModel,
    onDismissRequest: () -> Unit
) {
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val customApiKeyStatus by viewModel.customApiKeyStatus.collectAsStateWithLifecycle()
    var keyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
    var showKey by remember { mutableStateOf(false) }
    
    val isTesting by viewModel.isTestingKey.collectAsStateWithLifecycle()
    var testResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 24.dp)
                .testTag("secure_api_settings_dialog_surface"),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "Secure Key Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Secure API Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("close_api_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Informational Warning Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Security Note",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "100% Free & Safely Enforced",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = "• No Cost Ever: Google AI Studio API Keys are 100% free of charge by default. You will never be billed for creating or using a key unless you actively link a paid Google Cloud billing account.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "• Private Preferences: Your custom key is saved safely in your device's private storage and is never shared outside official Google API gateways.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "• Shared Fallback Safety: To prevent high server usage or going over the shared system key's free limits, each device is locally limited to 30 requests/day on the fallback system key. Entering your own custom free key bypasses this limit completely!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // API Key Status Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Connection Status:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    val isCustomActive = keyInput.isNotBlank()
                    val verifiedFp by viewModel.verifiedApiKeyFingerprint.collectAsStateWithLifecycle()
                    val editedFp = viewModel.getSha256Fingerprint(keyInput)
                    
                    val (statusText, statusColor) = if (!isCustomActive) {
                        "System Fallback Active 🛡️" to MaterialTheme.colorScheme.primary
                    } else if (isTesting) {
                        "Testing connection..." to Color(0xFF2196F3)
                    } else if (customApiKeyStatus == "failed") {
                        "Connection failed ❌" to Color(0xFFE53935)
                    } else if (editedFp == verifiedFp && verifiedFp.isNotBlank() && keyInput == customApiKey) {
                        "Custom API key verified ✨" to Color(0xFF4CAF50)
                    } else {
                        "Custom key saved—not yet verified" to Color(0xFFFF9800)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = statusColor,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val context = androidx.compose.ui.platform.LocalContext.current
                Button(
                    onClick = {
                        try {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://aistudio.google.com/")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Could not open browser. Please visit https://aistudio.google.com/ in your browser.", android.widget.Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("get_free_api_key_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Get Key Icon",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Get Free Key from Google AI Studio",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Outlined Input field for API Key
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { newValue ->
                        keyInput = newValue
                        testResult = null
                        viewModel.onKeyInputChanged(newValue)
                    },
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("Enter your Google AI Studio API key") },
                    singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { showKey = !showKey },
                            modifier = Modifier.testTag("toggle_key_visibility_btn")
                        ) {
                            Icon(
                                imageVector = if (showKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showKey) "Hide key" else "Show key",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_api_key_field")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "If you don't have a custom key, the app will fall back to its system key configuration if set. Clear the inputs to revert to the default.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Test Connection Button
                Button(
                    onClick = {
                        viewModel.testCustomApiKey(keyInput.trim()) { success, msg ->
                            testResult = Pair(success, msg)
                        }
                    },
                    enabled = !isTesting && keyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (testResult?.first == true) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (testResult?.first == true) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_api_key_btn")
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Testing Connection...")
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (testResult?.first == true) Icons.Default.CheckCircle else Icons.Default.NetworkCheck,
                                contentDescription = "Test Connection Icon",
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (testResult?.first == true) "Connection Active & Verified" else "Test Connection",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                testResult?.let { (success, msg) ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (success) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            contentColor = if (success) Color(0xFF2E7D32) else Color(0xFFC62828)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("api_key_test_result_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = if (success) "Success" else "Error",
                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                            )
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Optional clear custom key button
                    if (customApiKey.isNotBlank() || keyInput.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                keyInput = ""
                                viewModel.saveCustomApiKey("")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("clear_api_key_btn")
                        ) {
                            Text("Clear Key")
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.saveCustomApiKey(keyInput)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("save_api_key_btn")
                    ) {
                        Text(
                            text = "Save Settings",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
