package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OverComerViewModel(application: Application) : AndroidViewModel(application) {

    // --- Firebase Authentication States ---
    val isFirebaseLive: StateFlow<Boolean> = FirebaseAuthManager.isFirebaseLive
    val firebaseUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = FirebaseAuthManager.userState
    val mockUser: StateFlow<FirebaseAuthManager.MockUser?> = FirebaseAuthManager.mockUser

    val isLoggedIn: StateFlow<Boolean> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser != null || mkUser != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUserEmail: StateFlow<String> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser?.email ?: mkUser?.email ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val currentUserName: StateFlow<String> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser?.displayName ?: mkUser?.displayName ?: "OverComer"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "OverComer")

    val currentUserUid: StateFlow<String> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser?.uid ?: mkUser?.uid ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val database = OverComerDatabase.getDatabase(application)
    private val repository = OverComerRepository(database)

    // Reactive streams from the database, filtered by current user's UID to enforce session security
    val victoryLogs: StateFlow<List<VictoryLog>> = repository.victoryLogs
        .combine(currentUserUid) { logs, uid ->
            logs.filter { it.userId == uid }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val communityTestimonies: StateFlow<List<VictoryLog>> = repository.victoryLogs
        .map { logs ->
            logs.filter { it.type == "COMMUNITY_SHARED" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isUserAdmin: StateFlow<Boolean> = currentUserEmail
        .map { it.trim().lowercase() == "mshanemerrill@gmail.com" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _moderationPrefs = application.getSharedPreferences("overcomer_moderation_prefs", Context.MODE_PRIVATE)
    private val _removedPostNotes = MutableStateFlow(
        _moderationPrefs.getStringSet("removed_notes", emptySet())?.toSet() ?: emptySet()
    )
    val removedPostNotes: StateFlow<Set<String>> = _removedPostNotes.asStateFlow()

    val freedomGoal: StateFlow<FreedomGoal?> = repository.freedomGoal
        .combine(currentUserUid) { goal, uid ->
            if (goal == null || goal.userId != uid) {
                // Returns a placeholder goal linked to the current ID to seed initial view state
                FreedomGoal(
                    id = if (uid.isEmpty()) 1 else kotlin.math.abs(uid.hashCode()),
                    startDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 3), // Pre-load 3 days of victory
                    struggleType = "Substance Use",
                    customDeclaration = "An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory!",
                    userId = uid
                )
            } else {
                goal
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // --- Custom Gemini API Key State ---
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _customApiKeyStatus = MutableStateFlow("unverified")
    val customApiKeyStatus: StateFlow<String> = _customApiKeyStatus.asStateFlow()

    private val _verifiedApiKeyFingerprint = MutableStateFlow("")
    val verifiedApiKeyFingerprint: StateFlow<String> = _verifiedApiKeyFingerprint.asStateFlow()

    fun getSha256Fingerprint(input: String): String {
        val sanitized = input.trim().removeSurrounding("\"").removeSurrounding("'").trim()
        if (sanitized.isBlank()) return ""
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(sanitized.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }.take(8)
    }

    fun onKeyInputChanged(key: String) {
        val sanitized = key.trim().removeSurrounding("\"").removeSurrounding("'").trim()
        val fp = getSha256Fingerprint(sanitized)
        if (sanitized.isBlank()) {
            _customApiKeyStatus.value = "unverified"
        } else if (fp == _verifiedApiKeyFingerprint.value) {
            _customApiKeyStatus.value = "verified"
        } else {
            _customApiKeyStatus.value = "unverified"
        }
    }

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    fun clearChatError() {
        _chatError.value = null
    }

    // --- Bible Translation / Version Selector ---
    private val _selectedBibleVersion = MutableStateFlow("NIV")
    val selectedBibleVersion: StateFlow<String> = _selectedBibleVersion.asStateFlow()

    fun selectBibleVersion(version: String) {
        _selectedBibleVersion.value = version
    }

    // --- User Path Selection States ---
    private val _userPath = MutableStateFlow<String?>(null)
    val userPath: StateFlow<String?> = _userPath.asStateFlow()

    // UI state for the AI Support Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Welcome to OverComer Support. I am your guide here. I believe that through Christ's grace, you can be set free completely and walk in full victory.\n\n" +
                       "If you are feeling tempted, struggling with a habit, or feeling anxious, talk to me. We can walk through thought reframing or calming grounding exercises together, anchored in God's mercy.",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _freeTurnsCount = MutableStateFlow(0)
    val freeTurnsCount: StateFlow<Int> = _freeTurnsCount.asStateFlow()

    // Keep track of the current session's auto-saved chat ID
    private var currentAutoSavedChatId: Int? = null

    // Reactive stream of ALL saved chats (both user saved and auto saved) for counselor memory
    private val allSavedChatsFlow: StateFlow<List<SavedChat>> = repository.savedChats
        .combine(currentUserUid) { chats, uid ->
            chats.filter { it.userId == uid }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Reactive stream from DB for optional/user-explicit saved chats, filtered securely by authenticated UID
    val savedChats: StateFlow<List<SavedChat>> = repository.savedChats
        .combine(currentUserUid) { chats, uid ->
            chats.filter { it.userId == uid && !it.isAutoSaved }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAnalyzingDistortion = MutableStateFlow(false)
    val isAnalyzingDistortion: StateFlow<Boolean> = _isAnalyzingDistortion.asStateFlow()

    private val _distortionAnalysisResult = MutableStateFlow<com.example.network.DistortionAnalysisResult?>(null)
    val distortionAnalysisResult: StateFlow<com.example.network.DistortionAnalysisResult?> = _distortionAnalysisResult.asStateFlow()

    private val _verseOfTheDay = MutableStateFlow<com.example.network.VerseOfTheDay?>(com.example.network.GeminiClient.getFallbackVerse())
    val verseOfTheDay: StateFlow<com.example.network.VerseOfTheDay?> = _verseOfTheDay.asStateFlow()

    private val _isLoadingVerse = MutableStateFlow(false)
    val isLoadingVerse: StateFlow<Boolean> = _isLoadingVerse.asStateFlow()

    private val _aiScriptureResult = MutableStateFlow<com.example.network.AIScriptureResult?>(null)
    val aiScriptureResult: StateFlow<com.example.network.AIScriptureResult?> = _aiScriptureResult.asStateFlow()

    private val _isSearchingScripture = MutableStateFlow(false)
    val isSearchingScripture: StateFlow<Boolean> = _isSearchingScripture.asStateFlow()

    init {
        // Load custom API key
        val apiPrefs = application.getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
        val savedKey = apiPrefs.getString("custom_gemini_api_key", "") ?: ""
        val sanitizedKey = savedKey.trim().removeSurrounding("\"").removeSurrounding("'").trim()
        _customApiKey.value = sanitizedKey
        com.example.network.GeminiClient.customApiKey = sanitizedKey.ifBlank { null }

        val verifiedFingerprint = apiPrefs.getString("verified_api_key_fingerprint", "") ?: ""
        _verifiedApiKeyFingerprint.value = verifiedFingerprint

        val currentFingerprint = getSha256Fingerprint(sanitizedKey)
        val savedStatus = apiPrefs.getString("custom_api_key_status", "unverified") ?: "unverified"
        _customApiKeyStatus.value = if (sanitizedKey.isBlank()) {
            "unverified"
        } else if (currentFingerprint == verifiedFingerprint && verifiedFingerprint.isNotBlank()) {
            "verified"
        } else {
            "unverified"
        }

        // Load free companion turns count
        val freePrefs = application.getSharedPreferences("overcomer_free_prefs", Context.MODE_PRIVATE)
        _freeTurnsCount.value = freePrefs.getInt("companion_free_turns_count", 0)

        // Always reset path to null on fresh app startup so the user is given the 3 focus paths selection page first.
        _userPath.value = null

        // Guarantee a default configuration on initial launch so the UI is immediately functional
        viewModelScope.launch {
            val uid = currentUserUid.value
            val existing = repository.getFreedomGoal()
            if (existing == null) {
                repository.updateFreedomGoal(
                    FreedomGoal(
                        id = if (uid.isEmpty()) 1 else kotlin.math.abs(uid.hashCode()),
                        startDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 3), // Pre-load 3 days of victory for demo purposes
                        struggleType = "Substance Use",
                        customDeclaration = "A OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory!",
                        userId = uid
                    )
                )
            }
        }
        fetchVerseOfTheDay()
    }

    fun selectUserPath(path: String) {
        _userPath.value = path
        val prefs = getApplication<Application>().getSharedPreferences("overcomer_user_path_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("user_path", path).apply()
        updateGreetingForPath(path)
    }

    fun saveCustomApiKey(key: String) {
        val sanitizedKey = key.trim().removeSurrounding("\"").removeSurrounding("'").trim()
        val oldKey = _customApiKey.value
        _customApiKey.value = sanitizedKey
        com.example.network.GeminiClient.customApiKey = sanitizedKey.ifBlank { null }
        val prefs = getApplication<Application>().getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("custom_gemini_api_key", sanitizedKey)
        
        val currentFp = getSha256Fingerprint(sanitizedKey)
        if (sanitizedKey.isBlank()) {
            _customApiKeyStatus.value = "unverified"
            _verifiedApiKeyFingerprint.value = ""
            editor.putString("custom_api_key_status", "unverified")
            editor.putString("verified_api_key_fingerprint", "")
        } else if (currentFp == _verifiedApiKeyFingerprint.value) {
            _customApiKeyStatus.value = "verified"
            editor.putString("custom_api_key_status", "verified")
        } else {
            _customApiKeyStatus.value = "unverified"
            _verifiedApiKeyFingerprint.value = ""
            editor.putString("custom_api_key_status", "unverified")
            editor.putString("verified_api_key_fingerprint", "")
        }
        editor.apply()
    }

    fun updateCustomApiKeyStatus(status: String) {
        _customApiKeyStatus.value = status
        val prefs = getApplication<Application>().getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("custom_api_key_status", status)
        if (status != "verified") {
            _verifiedApiKeyFingerprint.value = ""
            editor.putString("verified_api_key_fingerprint", "")
        }
        editor.apply()
    }

    private val _isTestingKey = MutableStateFlow(false)
    val isTestingKey: StateFlow<Boolean> = _isTestingKey.asStateFlow()

    fun testCustomApiKey(key: String, baseUrl: String = "https://generativelanguage.googleapis.com/", onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isTestingKey.value = true
            _customApiKeyStatus.value = "testing"
            val sanitizedKey = key.trim().removeSurrounding("\"").removeSurrounding("'").trim()
            if (sanitizedKey.isBlank()) {
                onResult(false, "API Key is empty. Please enter a valid Gemini API key from Google AI Studio.")
                _customApiKeyStatus.value = "unverified"
                _isTestingKey.value = false
                return@launch
            }
            try {
                val testRequest = com.example.network.GenerateContentRequest(
                    contents = listOf(
                        com.example.network.Content(
                            role = "user",
                            parts = listOf(com.example.network.Part(text = "Respond with 'Connected'"))
                        )
                    ),
                    generationConfig = com.example.network.GenerationConfig(
                        temperature = 0.1f
                    )
                )
                val response = com.example.network.safeCallGemini(sanitizedKey, testRequest, baseUrl = baseUrl)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    val currentFp = getSha256Fingerprint(sanitizedKey)
                    _verifiedApiKeyFingerprint.value = currentFp
                    _customApiKey.value = sanitizedKey
                    com.example.network.GeminiClient.customApiKey = sanitizedKey.ifBlank { null }
                    val prefs = getApplication<Application>().getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("custom_gemini_api_key", sanitizedKey)
                        .putString("verified_api_key_fingerprint", currentFp)
                        .putString("custom_api_key_status", "verified")
                        .apply()
                    _customApiKeyStatus.value = "verified"
                    onResult(true, "Connection Successful! Your key is valid and connected to Gemini.")
                } else {
                    val prefs = getApplication<Application>().getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("verified_api_key_fingerprint", "")
                        .putString("custom_api_key_status", "failed")
                        .apply()
                    _verifiedApiKeyFingerprint.value = ""
                    _customApiKeyStatus.value = "failed"
                    onResult(false, "Connected but received empty response from Gemini.")
                }
            } catch (e: Exception) {
                val prefs = getApplication<Application>().getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("verified_api_key_fingerprint", "")
                    .putString("custom_api_key_status", "failed")
                    .apply()
                _verifiedApiKeyFingerprint.value = ""
                _customApiKeyStatus.value = "failed"
                val errorDetails = when (e) {
                    is com.example.network.GeminiException.InvalidApiKeyException -> {
                        "Invalid API Key (HTTP 401/403). Please make sure you copied the entire key from AI Studio without any missing characters, or verify if the key is authorized."
                    }
                    is com.example.network.GeminiException.UnsupportedModelException -> {
                        "Model Not Found (HTTP 404). The gemini-3.5-flash model might not be available or enabled for this key."
                    }
                    is com.example.network.GeminiException.NoInternetException -> {
                        "Network Error. Please check your internet connection."
                    }
                    else -> {
                        e.message ?: "Authentication failed. Please verify your key in Google AI Studio."
                    }
                }
                onResult(false, errorDetails)
            } finally {
                _isTestingKey.value = false
            }
        }
    }

    // --- Safe Usage / Rate Limiter to prevent going over Free Tier ---
    fun isCustomKeyActive(): Boolean {
        return _customApiKey.value.isNotBlank()
    }

    fun incrementFreeTurnsCount() {
        val freePrefs = getApplication<Application>().getSharedPreferences("overcomer_free_prefs", Context.MODE_PRIVATE)
        val newCount = _freeTurnsCount.value + 1
        _freeTurnsCount.value = newCount
        freePrefs.edit().putInt("companion_free_turns_count", newCount).apply()
    }

    fun generateLocalCompanionResponse(userMessage: String, turnNumber: Int): String {
        val input = userMessage.lowercase()
        
        // Identify the spiritual or mental focus category based on keywords in the message
        val focus = when {
            input.contains("crave") || input.contains("craving") || input.contains("tempt") || input.contains("temptation") || input.contains("desire") -> "craving"
            input.contains("anxious") || input.contains("anxiety") || input.contains("fear") || input.contains("scared") || input.contains("worry") -> "anxiety"
            input.contains("fail") || input.contains("relapse") || input.contains("slip") || input.contains("messed up") || input.contains("sin") || input.contains("guilt") || input.contains("shame") -> "grace"
            input.contains("sad") || input.contains("depressed") || input.contains("depression") || input.contains("lonely") || input.contains("hopeless") -> "hope"
            input.contains("trigger") || input.contains("tempted") || input.contains("urge") -> "trigger"
            else -> "general"
        }

        return when (focus) {
            "craving", "trigger" -> """
                I hear you, and I want you to take a deep, slow breath right now. Cravings and temptations can feel incredibly intense, like a powerful wave crashing over you, but remember: **every wave peaks and then subsides**. You do not have to fight this battle in your own limited strength. 
                
                Let's practice a brief **Calming Grounding Step** (the STOP technique) together to steady your soul:
                • **S** - **Stop**: Pause whatever you are doing. Put down your phone, close your eyes, and sit still.
                • **T** - **Take a breath**: Inhale deeply for 4 seconds, hold for 4, and exhale slowly for 4. Feel God's presence surrounding you.
                • **O** - **Observe**: Notice your feelings. Cravings are just automatic bodily responses or thoughts passing through; they are not your identity, and they do not command your actions.
                • **P** - **Proceed with Christ**: Turn your thoughts to His delivering power.
                
                In Christ, you are a brand new creation. Your old dependence is dead and broken, and you are unquestionably free.
                
                Let this beautiful promise anchor you right now:
                > "No temptation has overtaken you except what is common to mankind. And God is faithful; he will not let you be tempted beyond what you can bear. But when you are tempted, he will also provide a way out so that you can endure it." — **1 Corinthians 10:13 (NIV)**
                
                You are an OverComer. Lean into His grace, and let Him carry you through this moment.
                
                *Note: I am your OverComer’s Companion, and while I am an AI and cannot pray myself, I encourage you to talk directly to your Heavenly Father about this. He loves you, He is always listening, and He will sustain you.*
            """.trimIndent()

            "anxiety" -> """
                My friend, I can feel the weight of anxiety and fear in your words, and I want to reassure you that you are perfectly safe in His hands. When anxious thoughts begin to multiply, our minds are often tricked by "cognitive distortions"—untrue, catastrophic projections of the future. But God has not given you a spirit of fear, but of power, love, and a sound mind!
                
                Let's use a simple **Sensory Grounding Step** right now to bring your racing thoughts back to the present moment:
                • Focus your eyes and name **three things** you can see in your room.
                • Reach out and touch **two objects** near you, noticing their physical texture.
                • Close your eyes for a moment and identify **one subtle sound** in the background.
                
                As your mind settles, remember that the Lord is right beside you. He cares deeply about every single detail of your life.
                
                Be comforted by this wonderful scripture:
                > "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God. And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus." — **Philippians 4:6-7 (NIV)**
                
                You do not have to figure out the whole future today. Just take this next single step with Him.
                
                *Note: I am your OverComer’s Companion. While I am an AI and cannot pray myself, I encourage you to bring these worries to God in your own prayer. He cares for you deeply and hears every whisper of your heart.*
            """.trimIndent()

            "grace" -> """
                Please hear me clearly: **there is absolutely zero condemnation for you in Christ Jesus!** If you have slipped, stumbled, or feel like you have failed, do not let shame convince you to run away from God. That is a lie from the enemy. Instead, run *straight into* His arms. 
                
                Genetics or environment can make us vulnerable, but choice is the root of our actions, and **His grace is the source of our complete restoration**. Repentance is not a heavy, shameful punishment; it is a beautiful U-turn back to the Father who is running to meet you.
                
                Let this incredible promise wash over your soul:
                > "If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness." — **1 John 1:9 (NIV)**
                
                Your identity is not "addict" or "failure." You are a beloved child of God, an OverComer who has been set unquestionably free. Shake off the dust, stand up, and let’s keep walking forward in His unconditional love.
                
                *Note: I am your OverComer’s Companion. Although I am an AI and cannot pray, I encourage you to speak directly to God. Confess your heart to Him—He is waiting with open arms to heal and restore.*
            """.trimIndent()

            "hope" -> """
                I am so glad you reached out. When feelings of sadness, loneliness, or hopelessness cloud your vision, it is easy to feel isolated and forgotten. But you are never alone. God is closer to you than your very breath, and He has a beautiful, restorative purpose for your life.
                
                Let's practice a quick **Thought Reframing** exercise to renew your mind:
                • **The Lie**: "Nothing will ever change, and I am permanently stuck in this dark place."
                • **The Truth**: "This season is temporary. God is working in my heart right now, and His light can pierce any darkness."
                
                Be encouraged by this wonderful promise:
                > "For I know the plans I have for you,' declares the Lord, 'plans to prosper you and not to harm you, plans to give you hope and a future.'" — **Jeremiah 29:11 (NIV)**
                
                Even when you cannot see it, He is working all things together for your ultimate good.
                
                *Note: I am your OverComer’s Companion. As an AI, I cannot pray myself, but I encourage you to share your heart with your Heavenly Father in prayer. He loves to bring hope and light to His children.*
            """.trimIndent()

            else -> """
                Hello! It is so wonderful to connect with you. As your OverComer's Companion, I am here to walk alongside you, celebrating your victories and standing with you in every moment of struggle. 
                
                Whatever is on your heart today—whether you are celebrating a day of absolute freedom, feeling a bit weary, or just needing some scriptural encouragement—know that God is with you and He is unconditionally for you.
                
                Let this beautiful truth strengthen you today:
                > "So if the Son sets you free, you will be free indeed." — **John 8:36 (NIV)**
                
                What has been on your mind today? I am here to listen and encourage you.
                
                *Note: I am your OverComer’s Companion. Since I am an AI, I cannot pray myself, but I highly encourage you to talk directly to your Heavenly Father in prayer. He loves you and is always listening.*
            """.trimIndent()
        }
    }

    fun isDailyLimitExceeded(): Boolean {
        if (isCustomKeyActive()) return false // Users using their own key have unlimited requests

        val prefs = getApplication<Application>().getSharedPreferences("overcomer_usage_prefs", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val savedDate = prefs.getString("last_request_date", "") ?: ""
        if (savedDate != today) {
            // New day, reset count
            prefs.edit().putString("last_request_date", today).putInt("daily_request_count", 0).apply()
            return false
        }
        val count = prefs.getInt("daily_request_count", 0)
        return count >= 30 // Safe free tier limit of 30 requests/day per device on the shared key
    }

    private fun incrementDailyRequestCount() {
        if (isCustomKeyActive()) return

        val prefs = getApplication<Application>().getSharedPreferences("overcomer_usage_prefs", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val savedDate = prefs.getString("last_request_date", "") ?: ""
        val count = if (savedDate == today) {
            prefs.getInt("daily_request_count", 0)
        } else {
            0
        }
        prefs.edit()
            .putString("last_request_date", today)
            .putInt("daily_request_count", count + 1)
            .apply()
    }

    private fun updateGreetingForPath(path: String) {
        val welcomeText = when (path) {
            "SUBSTANCE_RECOVERY" -> {
                "Welcome to OverComer Support. I am your guide here. I believe that through Christ's grace, you can be set free completely and walk in full victory.\n\n" +
                "If you are feeling tempted, struggling with a habit, or feeling anxious, talk to me. We can walk through thought reframing or calming grounding exercises together, anchored in God's mercy."
            }
            "MENTAL_HEALTH" -> {
                "Welcome to OverComer Mental Wellness Support. I am your guide here. I believe that through Christ's perfect love, you can experience peace that passeth all understanding.\n\n" +
                "If you are struggling with heavy thoughts, anxiety, depression, or distress, share it with me. We can walk through thought reframing or emotional grounding together."
            }
            "TOUGH_DAY" -> {
                "I am so sorry you are having an all-around tough day. I am here to listen, pray with you, and help lift your load.\n\n" +
                "Tell me what happened today, or vent freely. We can do direct calming breathing exercises or find encouraging scriptures to help you get through today."
            }
            "VETERAN_TRANSITION" -> {
                "Welcome to The Next Mission: Veteran Support & Wellness. I am your guide here. I honor your service and sacrifice, and believe that through Christ's grace, you can walk in civilian strength and perfect emotional peace.\n\n" +
                "If you are processing combat weight, coping with hypervigilance, experiencing PTSD triggers, or struggling with transition, let's talk. We have specialized, faith-based support and tools ready for you."
            }
            "REENTRY_RESTORATION" -> {
                "Welcome to Steps to Restoration: Life and Leadership After Incarceration. I am your guide here. I believe that your past does not dictate your destiny; Christ does, and in Him, you are a brand new creation.\n\n" +
                "If you are experiencing transition anxiety, struggling with decision fatigue, or rebuilding family boundaries, share it with me. We can walk through thought reframing, find curated resources, or seek comforting scriptures to guide your next steps toward victory."
            }
            "TESTIMONY_VICTORY" -> {
                "Glory to God! Today is a Testimony and Victory Day! I am so excited to hear about how the Lord has shown Himself strong on your behalf. As 1 Corinthians 15:57 says: 'But thanks be to God, which giveth us the victory through our Lord Jesus Christ!'\n\n" +
                "Share your victory story, testimony, or breakthroughs with me today! Whether it is overcoming a temptation, experiencing a mental health lift, or celebrating a major milestone, let's praise Him and converse about how you are walking in perfect freedom!"
            }
            else -> "Welcome to OverComer. Talk to me; I am here to help."
        }
        _chatMessages.value = listOf(
            ChatMessage(text = welcomeText, isUser = false)
        )
    }

    // --- Database Writers ---

    fun addVictoryLog(
        type: String, // "REFLECT", "TRIGGER", or "CBT"
        notes: String = "",
        triggerContext: String = "",
        automaticThought: String = "",
        identifiedDistortion: String = "",
        reframedTruth: String = "",
        scriptureReference: String = ""
    ) {
        viewModelScope.launch {
            repository.insertLog(
                VictoryLog(
                    type = type,
                    notes = notes,
                    triggerContext = triggerContext,
                    automaticThought = automaticThought,
                    identifiedDistortion = identifiedDistortion,
                    reframedTruth = reframedTruth,
                    scriptureReference = scriptureReference,
                    userId = currentUserUid.value,
                    userPath = _userPath.value ?: "SUBSTANCE_RECOVERY"
                )
            )
        }
    }

    private fun formatAuthorName(fullName: String): String {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) return "Anonymous"
        val parts = trimmed.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.isEmpty()) return "Anonymous"
        val firstName = parts[0]
        if (parts.size > 1) {
            val lastName = parts.last()
            if (lastName.isNotEmpty()) {
                return "$firstName ${lastName.take(1).uppercase()}."
            }
        }
        return firstName
    }

    fun addVictoryTestimony(notes: String, shareOnCommunityBoard: Boolean) {
        viewModelScope.launch {
            val uid = currentUserUid.value
            val path = _userPath.value ?: "TESTIMONY_VICTORY"
            val rawName = currentUserName.value
            val authorNameFormatted = formatAuthorName(rawName)
            
            // 1. Always save in Secure Private Journal
            repository.insertLog(
                VictoryLog(
                    type = "JOURNAL_SECURE",
                    notes = notes,
                    userId = uid,
                    userPath = path,
                    authorName = "Me"
                )
            )
            // 2. Optionally share on Community Board
            if (shareOnCommunityBoard) {
                repository.insertLog(
                    VictoryLog(
                        type = "COMMUNITY_SHARED",
                        notes = notes,
                        userId = "COMMUNITY", // Decoupled for community stream
                        userPath = path,
                        authorName = authorNameFormatted
                    )
                )
            }
        }
    }

    fun deleteVictoryLog(id: Int) {
        viewModelScope.launch {
            repository.deleteLogById(id)
        }
    }

    fun removeCommunityPost(notes: String, id: Int?) {
        viewModelScope.launch {
            if (id != null && id != 0) {
                repository.deleteLogById(id)
            }
            val currentSet = _removedPostNotes.value.toMutableSet()
            currentSet.add(notes)
            _removedPostNotes.value = currentSet
            _moderationPrefs.edit().putStringSet("removed_notes", currentSet).apply()
        }
    }

    fun updateFreedomGoal(startDateMillis: Long, struggleType: String, customDeclaration: String) {
        viewModelScope.launch {
            val uid = currentUserUid.value
            repository.updateFreedomGoal(
                FreedomGoal(
                    id = if (uid.isEmpty()) 1 else kotlin.math.abs(uid.hashCode()),
                    startDate = startDateMillis,
                    struggleType = struggleType,
                    customDeclaration = customDeclaration.ifBlank { "I can do all things through Christ who strengthens me!" },
                    userId = uid
                )
            )
        }
    }

    // --- Chat Services ---

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        if (_isChatLoading.value) return // Block concurrent requests to prevent double-sends and API rate violations

        _chatError.value = null

        // 1. Append user message
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.update { it + userMsg }
        autoSaveCurrentChat()

        val isCustomActive = isCustomKeyActive()
        val currentFreeCount = _freeTurnsCount.value

        // If they do not have a custom key and they used up all 3 free turns, prompt them to add their key
        if (!isCustomActive && currentFreeCount >= 3) {
            _chatMessages.update {
                it + ChatMessage(
                    text = "You have completed your 3 free trial companion interactions.\n\n" +
                           "To unlock unlimited, completely private, and 100% free companion chats, please tap the Settings ⚙️ icon at the top of the screen to paste your own free Gemini API key from Google AI Studio. It takes under a minute, requires no credit card, and ensures you have a private, dedicated channel for your journey!",
                    isUser = false
                )
            }
            autoSaveCurrentChat()
            return
        }

        // 2. Set loading state
        _isChatLoading.value = true

        // 3. Launch async network request in safety-conscious scope
        viewModelScope.launch {
            try {
                // Keep only the last 12 messages (6 turns) of conversation history to prevent rate limits and token bloat
                val conversationHistory = _chatMessages.value.takeLast(12).map { msg ->
                    Content(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(Part(text = msg.text))
                    )
                }

                // Compile database historical chats summary to enrich the response context
                val pastChatsContext = buildPastChatsSummary()

                // Call client with history overview
                val responseText = GeminiClient.generateSupportResponse(conversationHistory, pastChatsContext)

                if (isCustomActive) {
                    updateCustomApiKeyStatus("verified")
                }

                // 4. Append AI response
                _chatMessages.update {
                    it + ChatMessage(text = responseText, isUser = false)
                }
                
                if (!isCustomActive) {
                    incrementFreeTurnsCount()
                }
                
                incrementDailyRequestCount()
                autoSaveCurrentChat()
            } catch (e: Exception) {
                val errorMsg = com.example.network.GeminiClient.getFriendlyErrorMessage(e)
                _chatError.value = errorMsg
                if (isCustomActive) {
                    updateCustomApiKeyStatus("failed")
                }

                // If network fails or throws 404, check if we can fall back to local responder for the first 3 turns
                if (!isCustomActive && currentFreeCount < 3) {
                    incrementFreeTurnsCount()
                    val fallbackText = generateLocalCompanionResponse(text, currentFreeCount + 1)
                    
                    val finalMsg = if (currentFreeCount + 1 >= 3) {
                        "$fallbackText\n\n✨ **OverComer Guide Note**: You have completed your 3 free trial companion interactions! To continue this deep conversation with unlimited, private, and 100% free support, please tap the Settings ⚙️ icon at the top of the screen to enter your own free Gemini API key from Google AI Studio. It takes under a minute and requires no credit card."
                    } else {
                        fallbackText
                    }
                    
                    _chatMessages.update {
                        it + ChatMessage(text = finalMsg, isUser = false)
                    }
                    autoSaveCurrentChat()
                } else {
                    _chatMessages.update {
                        it + ChatMessage(
                            text = errorMsg,
                            isUser = false
                        )
                    }
                    autoSaveCurrentChat()
                }
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private fun autoSaveCurrentChat() {
        viewModelScope.launch {
            val list = _chatMessages.value
            if (list.size <= 1) return@launch // Don't auto-save if it's just the welcoming message
            val json = ChatSerializationHelper.toJson(list)
            val currentPath = _userPath.value ?: "SUBSTANCE_RECOVERY"
            val uid = currentUserUid.value
            
            val userFirstMsg = list.firstOrNull { it.isUser }?.text ?: ""
            val titleText = if (userFirstMsg.isNotBlank()) {
                val words = userFirstMsg.split(" ").take(4).joinToString(" ")
                val shortTitle = if (words.length > 30) words.take(27) + "..." else words
                "Auto Session - \"$shortTitle\""
            } else {
                "Auto Session - " + java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            }

            val savedChat = SavedChat(
                id = currentAutoSavedChatId ?: 0,
                title = titleText,
                messagesJson = json,
                userPath = currentPath,
                userId = uid,
                isAutoSaved = true
            )
            
            val insertedId = repository.insertSavedChat(savedChat)
            if (currentAutoSavedChatId == null) {
                currentAutoSavedChatId = insertedId.toInt()
            }
        }
    }

    private fun buildPastChatsSummary(): String {
        val chats = allSavedChatsFlow.value
        if (chats.isEmpty()) return ""
        val sb = java.lang.StringBuilder()
        sb.append("Here is the patient's context of previous saved sessions to recall past victories, struggles, thoughts or temptations they overcame:\n")
        chats.take(5).forEachIndexed { index, savedChat ->
            val msgs = ChatSerializationHelper.fromJson(savedChat.messagesJson)
            val snippet = msgs.filter { it.isUser }.take(3).joinToString(", ") { it.text.take(80) }
            val formattedDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(savedChat.timestamp))
            sb.append("${index + 1}. Session: \"${savedChat.title}\" (Date: $formattedDate, Path: ${savedChat.userPath}). Key user insights discussed: [$snippet]\n")
        }
        return sb.toString()
    }

    fun saveCurrentChat(title: String) {
        viewModelScope.launch {
            val list = _chatMessages.value
            if (list.isEmpty()) return@launch
            val json = ChatSerializationHelper.toJson(list)
            val currentPath = _userPath.value ?: "SUBSTANCE_RECOVERY"
            val defaultTitle = "Support Session - " + java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            repository.insertSavedChat(
                SavedChat(
                    title = title.trim().ifBlank { defaultTitle },
                    messagesJson = json,
                    userPath = currentPath,
                    userId = currentUserUid.value
                )
            )
        }
    }

    fun loadSavedChat(savedChat: SavedChat) {
        val msgs = ChatSerializationHelper.fromJson(savedChat.messagesJson)
        if (msgs.isNotEmpty()) {
            _chatMessages.value = msgs
            // Keep user path contextualized
            if (savedChat.userPath.isNotEmpty() && savedChat.userPath != _userPath.value) {
                _userPath.value = savedChat.userPath
            }
        }
    }

    fun deleteSavedChat(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedChatById(id)
        }
    }

    fun clearChatHistory() {
        currentAutoSavedChatId = null
        val path = _userPath.value ?: "SUBSTANCE_RECOVERY"
        val clearText = when (path) {
            "SUBSTANCE_RECOVERY" -> {
                "Chat history cleared. I'm here whenever you need a compassionate space to talk. Remember, you do not have to fight for victory, you are fighting FROM victory! What can we address together right now?"
            }
            "MENTAL_HEALTH" -> {
                "Chat history cleared. I'm here whenever you need support. In Christ, you have a sound mind of power and love. Let's find rest and peace together. What is on your mind?"
            }
            "TOUGH_DAY" -> {
                "Chat history cleared. Your slate is clean. Rest a bit and share whatever you're feeling. I am right here with you."
            }
            else -> "Chat history cleared. Speak freely, I am listening..."
        }
        _chatMessages.value = listOf(
            ChatMessage(text = clearText, isUser = false)
        )
    }

    fun analyzeJournalDistortion(text: String) {
        if (text.isBlank()) return
        _isAnalyzingDistortion.value = true
        _distortionAnalysisResult.value = null
        if (isDailyLimitExceeded()) {
            _distortionAnalysisResult.value = com.example.network.DistortionAnalysisResult(
                distortions = "Free Use Limit Reached",
                explanation = "You have reached the daily safety limit of 30 requests/day on the shared system key. Please configure a free custom API key in Settings to enjoy unlimited, private analysis at zero cost.",
                reframedTruth = "I can get unlimited cognitive analysis by using my own free Gemini key.",
                scriptureReference = "Philippians 4:19"
            )
            _isAnalyzingDistortion.value = false
            return
        }
        viewModelScope.launch {
            try {
                val result = GeminiClient.analyzeCognitiveDistortion(text)
                _distortionAnalysisResult.value = result
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _distortionAnalysisResult.value = com.example.network.DistortionAnalysisResult(
                    distortions = "Error running analysis",
                    explanation = "Failed to communicate with AI: ${e.message}",
                    reframedTruth = "God's strength is sufficient when I am weak.",
                    scriptureReference = "2 Corinthians 12:9"
                )
            } finally {
                _isAnalyzingDistortion.value = false
            }
        }
    }

    fun clearDistortionAnalysis() {
        _distortionAnalysisResult.value = null
    }

    fun fetchVerseOfTheDay(forceGenerate: Boolean = false) {
        _isLoadingVerse.value = true
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("overcomer_usage_prefs", Context.MODE_PRIVATE)
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            
            if (!forceGenerate) {
                val cachedRef = prefs.getString("cached_verse_ref", "") ?: ""
                val cachedText = prefs.getString("cached_verse_text", "") ?: ""
                val cachedReflection = prefs.getString("cached_verse_reflection", "") ?: ""
                val cachedDate = prefs.getString("cached_verse_date", "") ?: ""
                
                if (cachedDate == today && cachedRef.isNotBlank() && cachedText.isNotBlank()) {
                    _verseOfTheDay.value = com.example.network.VerseOfTheDay(
                        reference = cachedRef,
                        text = cachedText,
                        reflection = cachedReflection
                    )
                    _isLoadingVerse.value = false
                    return@launch
                }
            }

            if (isDailyLimitExceeded()) {
                _verseOfTheDay.value = GeminiClient.getFallbackVerse()
                _isLoadingVerse.value = false
                return@launch
            }
            try {
                val verse = GeminiClient.generateVerseOfTheDay()
                _verseOfTheDay.value = verse
                incrementDailyRequestCount()
                
                // Cache the newly generated verse for today
                prefs.edit()
                    .putString("cached_verse_date", today)
                    .putString("cached_verse_ref", verse.reference)
                    .putString("cached_verse_text", verse.text)
                    .putString("cached_verse_reflection", verse.reflection)
                    .apply()
            } catch (e: Exception) {
                _verseOfTheDay.value = GeminiClient.getFallbackVerse()
            } finally {
                _isLoadingVerse.value = false
            }
        }
    }

    fun lookupScripture(reference: String) {
        if (reference.isBlank()) return
        _isSearchingScripture.value = true
        _aiScriptureResult.value = null
        if (isDailyLimitExceeded()) {
            _aiScriptureResult.value = com.example.network.AIScriptureResult(
                reference = reference,
                text = "Free Use Limit Reached",
                explanation = "Daily free usage limit reached on the shared system key. Please configure a free custom API key in Settings to continue unlimited scripture studies."
            )
            _isSearchingScripture.value = false
            return
        }
        viewModelScope.launch {
            try {
                val result = GeminiClient.lookupScripture(reference, _selectedBibleVersion.value)
                _aiScriptureResult.value = result
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _aiScriptureResult.value = com.example.network.AIScriptureResult(
                    reference = reference,
                    text = "No scripture match found.",
                    explanation = "An error occurred during lookup: ${e.localizedMessage}"
                )
            } finally {
                _isSearchingScripture.value = false
            }
        }
    }

    fun clearScriptureSearch() {
        _aiScriptureResult.value = null
    }

    // --- Local Support & Church Locator States & Methods ---
    private val _localResources = MutableStateFlow<List<com.example.network.LocalResource>>(emptyList())
    val localResources: StateFlow<List<com.example.network.LocalResource>> = _localResources.asStateFlow()

    private val _isSearchingResources = MutableStateFlow(false)
    val isSearchingResources: StateFlow<Boolean> = _isSearchingResources.asStateFlow()

    fun searchLocalResources(location: String, searchType: String, prioritizeAlignment: Boolean = true, page: Int = 0) {
        if (location.isBlank()) return
        _isSearchingResources.value = true
        _localResources.value = emptyList()
        if (isDailyLimitExceeded()) {
            _localResources.value = GeminiClient.getFallbackResources(location, searchType, prioritizeAlignment, page)
            _isSearchingResources.value = false
            return
        }
        viewModelScope.launch {
            try {
                val results = GeminiClient.searchLocalResources(location, searchType, prioritizeAlignment, page)
                _localResources.value = results
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _localResources.value = GeminiClient.getFallbackResources(location, searchType, prioritizeAlignment, page)
            } finally {
                _isSearchingResources.value = false
            }
        }
    }

    fun clearResourceSearch() {
        _localResources.value = emptyList()
    }

    // --- Firebase Authentication Interaction Methods ---
    fun signUpWithEmailAndPassword(email: String, password: String, displayName: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            onResult(false, "All fields are required.")
            return
        }
        if (isFirebaseLive.value) {
            val auth = FirebaseAuthManager.getAuthInstance()
            if (auth != null) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build()
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { profileTask ->
                                        onResult(true, null)
                                    }
                            } else {
                                onResult(true, null)
                            }
                        } else {
                            onResult(false, task.exception?.localizedMessage ?: "Registration failed.")
                        }
                    }
            } else {
                onResult(false, "Firebase service not ready.")
            }
        } else {
            // Local Sandbox
            FirebaseAuthManager.mockSignUp(getApplication(), email, displayName, onResult)
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password are required.")
            return
        }
        if (isFirebaseLive.value) {
            val auth = FirebaseAuthManager.getAuthInstance()
            if (auth != null) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, task.exception?.localizedMessage ?: "Authentication failed.")
                        }
                    }
            } else {
                onResult(false, "Firebase service not ready.")
            }
        } else {
            // Local Sandbox
            FirebaseAuthManager.mockSignIn(getApplication(), email, onResult)
        }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "Please enter your email.")
            return
        }
        if (isFirebaseLive.value) {
            val auth = FirebaseAuthManager.getAuthInstance()
            if (auth != null) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, task.exception?.localizedMessage ?: "Failed to send reset email.")
                        }
                    }
            } else {
                onResult(false, "Firebase service not ready.")
            }
        } else {
            // Local Sandbox Reset
            onResult(true, "Sent! In Sandbox mode: Reset email simulation successfully triggered.")
        }
    }

    fun logout() {
        if (isFirebaseLive.value) {
            FirebaseAuthManager.getAuthInstance()?.signOut()
        } else {
            FirebaseAuthManager.mockSignOut(getApplication())
        }
    }
}
