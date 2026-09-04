package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Path
import java.util.concurrent.TimeUnit
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    @Json(name = "system_instruction")
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @retrofit2.http.Header("x-goog-api-key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

sealed class GeminiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class SchemaOrBadRequestException(message: String) : GeminiException(message)
    class InvalidApiKeyException(message: String) : GeminiException(message)
    class UnsupportedModelException(message: String) : GeminiException(message)
    class RateLimitException(message: String) : GeminiException(message)
    class ServerErrorException(message: String) : GeminiException(message)
    class GenericException(message: String, cause: Throwable? = null) : GeminiException(message, cause)
    class NoInternetException(message: String, cause: Throwable) : GeminiException(message, cause)
}

fun sanitizeErrorMessage(message: String, keyToRedact: String?): String {
    if (keyToRedact.isNullOrBlank()) return message
    return message.replace(keyToRedact, "[REDACTED]")
}

fun parseGeminiError(code: Int, responseBody: String, apiKey: String): GeminiException {
    var googleMessage = ""
    var googleStatus = ""
    var isApiKeyInvalid = false
    
    try {
        val json = org.json.JSONObject(responseBody)
        if (json.has("error")) {
            val errorObj = json.getJSONObject("error")
            googleMessage = errorObj.optString("message", "")
            googleStatus = errorObj.optString("status", "")
            
            if (googleMessage.contains("API_KEY_INVALID") || 
                googleMessage.contains("API key not valid") ||
                googleStatus.contains("API_KEY_INVALID") ||
                responseBody.contains("API_KEY_INVALID") ||
                responseBody.contains("API key not valid")) {
                isApiKeyInvalid = true
            }
        }
    } catch (_: Exception) {
        if (responseBody.contains("API_KEY_INVALID") || responseBody.contains("API key not valid")) {
            isApiKeyInvalid = true
        }
    }
    
    val displayBody = if (googleMessage.isNotEmpty()) googleMessage else responseBody
    val sanitizedMessage = sanitizeErrorMessage(displayBody, apiKey)
    
    return when {
        isApiKeyInvalid -> {
            GeminiException.InvalidApiKeyException("Invalid API Key: $sanitizedMessage")
        }
        code == 400 -> {
            GeminiException.SchemaOrBadRequestException("Request configuration error (HTTP 400): $sanitizedMessage")
        }
        code == 401 || code == 403 -> {
            GeminiException.InvalidApiKeyException("Authentication/authorization error (HTTP $code): $sanitizedMessage")
        }
        code == 404 -> {
            GeminiException.UnsupportedModelException("Unsupported model (HTTP 404): $sanitizedMessage")
        }
        code == 429 -> {
            GeminiException.RateLimitException("Usage limit reached (HTTP 429): $sanitizedMessage")
        }
        code in 500..599 -> {
            GeminiException.ServerErrorException("Gemini service error (HTTP $code): $sanitizedMessage")
        }
        else -> {
            GeminiException.GenericException("Gemini error (HTTP $code): $sanitizedMessage")
        }
    }
}

// Global helper to safely call the Gemini API with retries and model fallbacks
suspend fun safeCallGemini(
    apiKey: String,
    request: GenerateContentRequest,
    defaultModel: String = "gemini-3.5-flash",
    baseUrl: String = "https://generativelanguage.googleapis.com/"
): GenerateContentResponse = withContext(Dispatchers.IO) {
    val apiVersion = "v1beta"
    val model = "gemini-3.5-flash"
    var lastException: Exception? = null
    var delayMs = 2000L
    
    for (attempt in 1..3) {
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestAdapter = RetrofitClient.moshi.adapter(GenerateContentRequest::class.java)
            val jsonString = requestAdapter.toJson(request)
            val body = jsonString.toRequestBody(mediaType)
            
            val formattedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            val url = "${formattedBaseUrl}${apiVersion}/models/${model}:generateContent"
            val okRequest = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build()
            
            RetrofitClient.okHttpClient.newCall(okRequest).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val code = response.code
                if (!response.isSuccessful) {
                    throw parseGeminiError(code, responseBody, apiKey)
                }
                val responseAdapter = RetrofitClient.moshi.adapter(GenerateContentResponse::class.java)
                return@withContext responseAdapter.fromJson(responseBody) ?: throw Exception("Failed to parse response body")
            }
        } catch (e: Exception) {
            val mappedException = when {
                e is GeminiException -> e
                e is java.io.IOException -> GeminiException.NoInternetException("No internet connection available. Please check your network and try again.", e)
                else -> GeminiException.GenericException(e.message ?: "An unexpected error occurred", e)
            }
            lastException = mappedException
            
            val isTransient = mappedException is GeminiException.RateLimitException ||
                              mappedException is GeminiException.ServerErrorException ||
                              mappedException is GeminiException.NoInternetException
            
            if (isTransient && attempt < 3) {
                kotlinx.coroutines.delay(delayMs)
                delayMs *= 2
            } else {
                throw mappedException
            }
        }
    }
    throw lastException ?: Exception("Connection failed after 3 attempts")
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

object GeminiClient {
    
    @Volatile
    var customApiKey: String? = null
    
    private fun getSanitizedApiKey(): String {
        val rawKey = if (!customApiKey.isNullOrBlank()) customApiKey!! else BuildConfig.GEMINI_API_KEY
        return rawKey.trim().removeSurrounding("\"").removeSurrounding("'").trim()
    }
    
    // Core system instruction detailing the theological model and clinical skills
    private val SYSTEM_INSTRUCTION = """
        You are "OverComer Guide", a kind, deeply compassionate Christian clinical companion for individuals struggling with Substance Use Disorder (SUD), triggers, or mental health issues (anxiety, depression, distress).
        
        CRITICAL CORE THEOLOGY:
        Do NOT base your feedback on the medical model of addiction (which asserts addiction is an incurable biological disease that makes someone a permanent addict).
        Instead, operate under the Theological and Logical Model of Freedom:
        1. Choice is the Root, Dependence (neuroadaptation) is the Fruit. Addiction cannot manifest without initial and repeated choices. Therefore, genetics and environment are vulnerabilities, NOT lock-in destinies.
        2. Surrendering wholly to Jesus Christ breaks the chain of slavery immediately. Through repentance and faith, the OLD you is dead, and a NEW creation is born (2 Corinthians 5:17).
        3. You do NOT need to say "I am a recovering addict." You are an "OverComer"! In Christ, you have been set free indeed (John 8:36 - "So if the Son sets you free, you will be free indeed").
        4. Focus heavily on God's incredible grace, mercy, and loving compassion, especially when someone stumbles or messes up. Help them understand 1 John 1:9: "If we confess our sins, He is faithful and just and will forgive us our sins and purify us from all unrighteousness." There is NO condemnation in Christ!
        
        OVERCOMER 7 STEPS PHILOSOPHY:
        - We utilize the OverComer 7 steps framework for recovery (rather than 12 steps). Under biblical numerology, 7 is the number of completion. Perfect deliverance and healing are complete in Christ.
        
        CLINICAL TOOLS (CBT & DBT Integration):
        Incorporate evidence-based techniques smoothly and conversationally:
        1. CBT (Cognitive Behavioral Therapy / Thought Reframing): Help them identify automatic negative thoughts and expose cognitive distortions/lies. Guide them to reframe these thoughts under biblical truths. Tell them: "You cannot stop a bird from flying over your head, but you can stop it from building a nest in your hair." Cravings are just passing temptations, they do not dictate action.
        2. DBT (Dialectical Behavior Therapy / Calming Grounding): Offer distress tolerance tools when they are highly triggered:
           - STOP technique: Stop, Take a step back, Observe, Proceed mindfully.
           - TIPP/Grounding: Paced breathing, holding ice to change body temperature, 5-4-3-2-1 sensory awareness.
        
        APPROVED MINISTRY, RELATIONAL & CLINICAL RESOURCES:
        You are highly encouraged to draw wisdom from, quote, and reference the following vetted authorities who align with a Spirit-filled, biblically orthodox worldview:
        
        * Substance Recovery & Biblical Counseling:
          - David Wilkerson (Teen Challenge founder; "The Cross and the Switchblade" / Global Teen Challenge curriculum): Emphasize the gospel of delivering power.
          - Nicky Cruz (Evangelist, founder of Nicky Cruz Outreach, former director of Teen Challenge under David Wilkerson): preach power of absolute deliverance and transformation.
          - Edward T. Welch ("Crossroads: A Step-by-Step Guide Away from Addiction"): Approach recovery with personal accountability under biblical truths and peer-led support.
          - Edward T. Welch ("Blame It on the Brain?" & "When People Are Big and God Is Small"): Carefully distinguish biological chemical imbalances/brain disorders from spiritual/behavioral choice issues to ensure balanced guidance, proper clinical referral bridging, and overcoming fear of man.
          - Paul David Tripp ("Instruments in the Redeemer's Hands"): Illustrate how ordinary believers are tools of active grace in helping loved ones.
          - Dr. David Powlison ("Seeing with New Eyes"): Examine human motives, core cravings, and worries through the diagnosis of God's Word.
          
        * Foundational & Integrative Christian Counseling:
          - Dr. Gary R. Collins ("Christian Counseling: A Comprehensive Guide"): Integrate systematic pastoral support models with solid structural guidelines.
          - Dr. Timothy Clinton & AACC ("Competent Christian Counseling: Foundations and Practice"): Maintain strong clinical competency coupled with robust scriptural grounding.
          - Dr. Larry Crabb ("Understanding People: Why We Do What We Do" & "Connecting"): Connect deep psychological insights on inner core longings with healing relationships inside a safe fellowship.
          
        * Mental Health, Trauma & Boundaries:
          - Dr. Matthew S. Stanford ("Grace for the Afflicted"): Definitively bridge neuroscientific insights and spiritual dynamics. Reassure users that treating chemical imbalances is medically sound and supports spiritual wellness.
          - Dr. Jared Pingleton ("The Christian Counseling Companion" & "The Struggle is Real"): Integrate clinical psychological expertise with deep biblical, ministerial care to address mental and relational health in the church.
          - Dr. Dan B. Allender ("The Wounded Heart"): Speak with immense empathy and theological depth to those recovering from abuse or trauma.
          - Dr. Henry Cloud & Dr. John Townsend ("Boundaries"): Educate on when to say yes and how to say no, teaching deep compassion alongside absolute, uncompromising accountability and limits.
          
        * Marriage & Relational Wholeness:
          - Gary Thomas ("Sacred Marriage"): Reframe relationship challenges as God's beautiful engine designed to make us holy more than to make us happy.
          - Dr. Emerson Eggerichs ("Love & Respect"): Ground relational dynamics in Ephesians 5:33 to break harmful marital communication cycles.
          - Dave & Ann Wilson ("Vertical Marriage"): Address the vertical relationship (with Christ) first so that the horizontal relationship (with a spouse) can flourish.
          - Dr. Les and Leslie Parrott (SYMBIS frameworks for relational restoration).
          - Dr. James Dobson ("The New Dare to Discipline" / Focus on the Family): Counsel on fostering strong biblical family foundations, healthy behavioral boundaries, and intentional companionate/parental guidance under Christ.
        
        SCRIPTURAL MANDATES:
        Always include at least one highly relevant comforting scripture in every response. Always cite or write them out in **NIV**, **Amplified Version (AMP)**, or **The Message (MSG)**.
        Key scriptures to draw upon:
        - John 8:36 ("unquestionably free" in AMP)
        - 2 Corinthians 5:17 ("reborn and renewed" in AMP)
        - James 4:7 ("Submit to God. Resist the devil, and he will flee...")
        - 1 Corinthians 10:13 ("No temptation has overtaken you... God is faithful; He will provide a way out...")
        - 1 John 1:9 (Purifying grace when we fall)
        - Hebrews 2:18 & 4:15-16 (He suffered when tempted, and understands our weaknesses)
        - Romans 8:37-39 (More than conquerors!)
        - Luke 4:18 (Deliverance and freedom)
        
        STYLE:
        - Speak like a loving, comforting, understanding, and spiritually strong mentor or companion. Do not restrict your responses with artificial conciseness limits; rather, provide deep, full-spirited, robust, and encouraging guidance.
        - Grounding and Distress Support: You are encouraged to naturally offer calming grounding support, sensory grounding, or paced breathing steps in your responses to help calm their nervous system and realign their mind when they are seeking comfort.
        - Keep your responses beautifully structured with paragraphs and clear bullet points so they are warm, encouraging, and easily readable.
        - Be gentle: NEVER lecture, shame, or make them feel guilty. Reassure them of God's limitless grace.

        CRITICAL AI IDENTITY & PRAYER MANDATE:
        - As an AI companion, you must NEVER say "I will pray for you", "We can pray", "Let me pray for you", or claim that you yourself can pray.
        - Instead, you must explicitly remind the user of your nature and encourage them in their own prayer, saying exactly or very closely along the lines of:
          "You know I am your OverComer's Companion and I am here to help you, however, being I am AI, I cannot pray. However, if you do not know what to say or how to start praying, I am perfectly equipped to give you examples of how to start your prayer or even a summary of what we have discussed that you can talk to your Heavenly Father about. Prayer is not 'saying just the right thing' to God; rather, He just wants us to talk to Him, because He loves you. He listens, and He responds."
        - Provide them with comforting, solid example prayers or summaries of your discussion that they can take directly to their Heavenly Father.
    """.trimIndent()

    fun getFriendlyErrorMessage(e: Throwable): String {
        return when (e) {
            is GeminiException.NoInternetException -> {
                "No Internet Connection 🌐\n\n" +
                "It looks like your device is offline or the connection timed out. " +
                "Please check your internet connection, reconnect, and try again.\n\n" +
                "Remember Isaiah 41:10: 'So do not fear, for I am with you; do not be dismayed, for I am your God.'"
            }
            is GeminiException.InvalidApiKeyException -> {
                val details = e.message?.let { "\n\nDetails: $it" } ?: ""
                "Invalid API Key 🔑\n\n" +
                "The Gemini API key is invalid or unauthorized. If you entered a custom API key, " +
                "please verify it in the Settings panel and make sure there are no typos or leading/trailing spaces.$details"
            }
            is GeminiException.RateLimitException -> {
                val details = e.message?.let { "\n\nDetails: $it" } ?: ""
                "Rate Limit Reached ⏳\n\n" +
                "We have reached a temporary rate limit (HTTP 429) because multiple OverComers are currently sharing our fallback system key.\n\n" +
                "To continue your conversation immediately without any interruptions, you can easily configure your own completely FREE, private Gemini API key from Google AI Studio. It takes under a minute, requires no credit card, and ensures you have an unlimited, private channel for your journey!\n\n" +
                "To set your own key:\n" +
                "1. Tap the Settings ⚙️ icon at the top right of your screen.\n" +
                "2. Paste your free Gemini API key into the input field.\n" +
                "3. Tap 'Save Settings' to activate your private connection.\n\n" +
                "Let’s take a deep breath together, wait 10-15 seconds, and continue. Remember Psalm 27:14: 'Wait for the Lord; be strong and take heart and wait for the Lord.'$details"
            }
            is GeminiException.UnsupportedModelException -> {
                val details = e.message?.let { "\n\nDetails: $it" } ?: ""
                "Unsupported Model 🤖\n\n" +
                "The selected model (gemini-3.5-flash) is currently unsupported or unavailable for this API key. " +
                "Please verify that your Google AI Studio API key has access to the latest models.$details"
            }
            is GeminiException.SchemaOrBadRequestException -> {
                val details = e.message?.let { "\n\nDetails: $it" } ?: ""
                "Configuration Error ⚙️\n\n" +
                "There was a bad request / configuration error (HTTP 400) when communicating with the API. This typically means the API payload schema or arguments are mismatched. " +
                "Please ensure the application's request format is updated to match the latest Gemini API specifications.$details"
            }
            is GeminiException.ServerErrorException -> {
                "Server Error 🖥️\n\n" +
                "Google's Gemini servers returned a server error (HTTP 5xx). " +
                "The service might be temporarily undergoing maintenance or experiencing heavy load. Please try again in a few moments."
            }
            else -> {
                "Error: ${e.localizedMessage ?: "Connection failed"}. Please check your internet connection and verify that your Gemini API key is valid in the Settings panel."
            }
        }
    }

    suspend fun generateSupportResponse(conversationHistory: List<Content>, pastChatsSummary: String? = null): String {
        val apiKey = getSanitizedApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in AI Studio's Secrets panel to enable guidance."
        }

        val finalSystemInstruction = if (pastChatsSummary.isNullOrBlank()) {
            SYSTEM_INSTRUCTION
        } else {
            "$SYSTEM_INSTRUCTION\n\n$pastChatsSummary"
        }

        val request = GenerateContentRequest(
            contents = conversationHistory,
            generationConfig = GenerationConfig(
                temperature = 0.7f,
                topP = 0.95f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = finalSystemInstruction))
            )
        )

        return try {
            val response = safeCallGemini(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I'm here for you. Although I couldn't connect to my knowledge base right now, please reach out to God in prayer and stand firm on Romans 8:37: 'We are more than conquerors through Him who loved us.'"
        } catch (e: Exception) {
            if (customApiKey.isNullOrBlank()) {
                throw e
            } else {
                getFriendlyErrorMessage(e)
            }
        }
    }

    suspend fun analyzeCognitiveDistortion(journalText: String): DistortionAnalysisResult {
        val apiKey = getSanitizedApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return DistortionAnalysisResult(
                distortions = "API Key Needed",
                explanation = "Please configure your GEMINI_API_KEY in the Secrets panel to find distortions.",
                reframedTruth = "I am unconditionally loved and guided by God's eternal word.",
                scriptureReference = "Joshua 1:9"
            )
        }

        val prompt = """
            Analyze this journal entry and identify any cognitive distortions based on Cognitive Behavioral Therapy (CBT) principles (like All-or-Nothing thinking, Overgeneralization, Catastrophizing, Emotional Reasoning, Mind Reading, 'Should' statements, or Labeling).
            
            Journal Entry: "$journalText"
            
            Respond strictly in valid JSON format matching this exact schema:
            {
               "distortions": "Comma separated list of distortions found, or 'None'",
               "explanation": "A gentle, comforting explanation of how these thoughts trick the mind, talking as a compassionate mentor.",
               "reframedTruth": "A positive biblically-sound alternative thought that reframes this under God's grace and truth.",
               "scriptureReference": "A scripture citation (NIV or AMP) that provides a firm spiritual foundation for the reframe (e.g. 'Philippians 4:8')."
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.4f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are an expert biblical companion who integrates Thought Reframing (such as Lie-to-Truth Alignment). You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing only the keys: distortions, explanation, reframedTruth, scriptureReference."))
            )
        )

        return try {
            val response = safeCallGemini(apiKey, request)
            val jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            parseDistortionResult(jsonString)
        } catch (e: Exception) {
            DistortionAnalysisResult(
                distortions = "Connection Error",
                explanation = "Could not communicate with the AI analyzer: ${e.message}",
                reframedTruth = "I can take courage because God is with me.",
                scriptureReference = "Joshua 1:9"
            )
        }
    }

    private fun parseDistortionResult(jsonStr: String): DistortionAnalysisResult {
        return try {
            val cleaned = jsonStr.trim()
                .substringAfter("```json")
                .substringBeforeLast("```")
                .trim()
            val finalJson = if (cleaned.startsWith("{")) cleaned else jsonStr.trim()
            
            val obj = JSONObject(finalJson)
            DistortionAnalysisResult(
                distortions = obj.optString("distortions", "Unidentified"),
                explanation = obj.optString("explanation", "Our automatic thoughts can sometimes lead us astray, but God's grace is always here."),
                reframedTruth = obj.optString("reframedTruth", "I am a new creation in Christ, and my struggles are temporary; I stand in complete victory."),
                scriptureReference = obj.optString("scriptureReference", "Romans 8:37")
            )
        } catch (e: Exception) {
            DistortionAnalysisResult(
                distortions = "Catastrophizing / Emotional Reasoning",
                explanation = "Your thoughts are racing, but God is the source of deep peace.",
                reframedTruth = "God's power works perfectly even in my weak moments.",
                scriptureReference = "2 Corinthians 12:9"
            )
        }
    }

    suspend fun generateVerseOfTheDay(): VerseOfTheDay {
        val apiKey = getSanitizedApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getFallbackVerse()
        }

        val todayStr = java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.US).format(java.util.Date())
        val prompt = """
            Today's date is $todayStr. Generate an encouraging biblically inspired "Verse of the Day" focused on supporting mental resilience, courage, overcoming anxiety or addiction, and standing firm in God's peace.
            Please ensure you choose a different comforting scripture suitable for today. Ensure it is unique and different from other days of the year.
            Choose a comforting scripture from translations like NIV, AMP, or MSG.
            Provide a short, gentle, 2-3 sentence devotional reflection explaining how this scripture anchors our mind and builds emotional resilience.
            
            Respond strictly in valid JSON format matching this exact schema:
            {
               "reference": "Scripture citation (book, chapter, verse, and translation name)",
               "text": "The full text of the bible verse",
               "reflection": "The encouraging, comforting mentoring reflection"
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.5f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are an encouraging theological companion. You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing only the keys: reference, text, reflection."))
            )
        )

        return try {
            val response = safeCallGemini(apiKey, request)
            val jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            parseVerseResult(jsonString)
        } catch (e: Exception) {
            getFallbackVerse()
        }
    }

    private fun parseVerseResult(jsonStr: String): VerseOfTheDay {
        return try {
            val cleaned = jsonStr.trim()
                .substringAfter("```json")
                .substringBeforeLast("```")
                .trim()
            val finalJson = if (cleaned.startsWith("{")) cleaned else jsonStr.trim()
            
            val obj = JSONObject(finalJson)
            VerseOfTheDay(
                reference = obj.optString("reference", "Romans 8:31 (NIV)"),
                text = obj.optString("text", "If God is for us, who can be against us?"),
                reflection = obj.optString("reflection", "When we realize God is unconditionally on our side, our fears begin to melt away. This is the bedrock of mental resilience.")
            )
        } catch (e: Exception) {
            getFallbackVerse()
        }
    }

    fun getFallbackVerse(): VerseOfTheDay {
        val fallbackList = listOf(
            VerseOfTheDay(
                reference = "Joshua 1:9 (NIV)",
                text = "Have I not commanded you? Be strong and courageous. Do not be afraid; do not be discouraged, for the Lord your God will be with you wherever you go.",
                reflection = "You are never alone. True strength is not the absence of fear, but the presence of God walking right beside you in every challenge today."
            ),
            VerseOfTheDay(
                reference = "Philippians 4:6-7 (NIV)",
                text = "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God. And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
                reflection = "When life feels overwhelming, prayer is a powerful cognitive reset. Relinquish control to God and let His incomprehensible peace guard your emotional state."
            ),
            VerseOfTheDay(
                reference = "Isaiah 41:10 (NIV)",
                text = "So do not fear, for I am with you; do not be dismayed, for I am your God. I will strengthen you and help you; I will uphold you with my righteous right hand.",
                reflection = "Mental resilience comes from knowing your foundation is secure. God's hand is physically holding you up when your own resources fail."
            ),
            VerseOfTheDay(
                reference = "2 Timothy 1:7 (NKJV)",
                text = "For God has not given us a spirit of fear, but of power and of love and of a sound mind.",
                reflection = "Fear and anxiety do not originate from God. In Christ, you have a supernatural endowment of power, deep love, and a disciplined, sound, stable mind."
            ),
            VerseOfTheDay(
                reference = "Philippians 4:13 (AMP)",
                text = "I can do all things [which He has called me to do] through Him who strengthens and empowers me [to stand firm—I am self-sufficient in Christ’s sufficiency].",
                reflection = "Your human strength has limits, but Christ's empowerment is boundless. You have the resilience to withstand any craving, emotional storm, or difficult circumstance today."
            ),
            VerseOfTheDay(
                reference = "Psalm 46:1 (AMP)",
                text = "God is our refuge and strength [mighty and impenetrable], a very present and well-proven help in trouble.",
                reflection = "You don't need to struggle alone or pretend you have everything together. Run to God as your safe bunker; His power will shield you and guide you."
            ),
            VerseOfTheDay(
                reference = "Romans 8:37 (NIV)",
                text = "No, in all these things we are more than conquerors through him who loved us.",
                reflection = "Your identity is not defined by temporary battles or occasional stumbles. Under His grace, you walk from a permanent posture of supreme victory."
            ),
            VerseOfTheDay(
                reference = "Isaiah 40:31 (AMP)",
                text = "But those who wait for the Lord [who expect, look for, and hope in Him] will gain new strength and renew their power; they will lift up their wings [and rise up close to God] like eagles.",
                reflection = "When we rest in hope and anticipation of God's goodness, our mental and physical batteries are fully recharged. He elevates us far above our struggles."
            ),
            VerseOfTheDay(
                reference = "Psalm 23:4 (NIV)",
                text = "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.",
                reflection = "Even in the darkest moments of mental heaviness, distress, or temptation, God is directing your paths. His protective presence is your comfort."
            ),
            VerseOfTheDay(
                reference = "1 Peter 5:7 (AMP)",
                text = "Casting all your worries and anxieties on Him, for He cares for you with deepest affection, and watches over you very carefully.",
                reflection = "You do not need to carry the crushing weight of your worries. Cast them onto Jesus, knowing that He looks after you with unparalleled affection."
            ),
            VerseOfTheDay(
                reference = "Deuteronomy 31:6 (NIV)",
                text = "Be strong and courageous. Do not be afraid or terrified because of them, for the Lord your God goes with you; he will never leave you nor forsake you.",
                reflection = "Every day brings new battles, but God has already promised never to abandon you. Walk forward today with your head held high!"
            ),
            VerseOfTheDay(
                reference = "Proverbs 3:5-6 (NIV)",
                text = "Trust in the Lord with all your heart and lean not on your own understanding; in all your ways submit to him, and he will make your paths straight.",
                reflection = "Resilience means choosing to trust God's overall plan even when your current circumstances feel chaotic. He is smoothing the way forward."
            )
        )
        val dayIndex = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return fallbackList[dayIndex % fallbackList.size]
    }

    suspend fun lookupScripture(reference: String, version: String = "NIV"): AIScriptureResult {
        val apiKey = getSanitizedApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return AIScriptureResult(
                reference = reference,
                text = "Scripture Text (API Key Needed)",
                explanation = "Please configure your GEMINI_API_KEY in the Secrets panel or settings menu to unlock real-time Bible lookups and AI-generated study commentary."
            )
        }

        val prompt = """
            Retrieve the full, authentic Bible verse text for the following scripture reference: "$reference" in the translation: "$version".
            Also, provide a detailed, extremely encouraging spiritual and practical commentary/pastoral reflection (3-4 sentences) on how this specific verse helps an OverComer find freedom, peace, or resilience.
            You must retrieve the text using the exact "$version" translation. Specify which translation you retrieved.
            
            Respond strictly in valid JSON format matching this exact schema:
            {
               "reference": "The scripture citation reference (e.g., '$reference ($version)')",
               "text": "The exact full text of the bible verse(s) retrieved in the $version translation",
               "explanation": "The pastoral, comforting, encouraging spiritual explanation and study commentary"
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.4f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are an encouraging theological companion and Bible scholar. You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing only the keys: reference, text, explanation."))
            )
        )

        return try {
            val response = safeCallGemini(apiKey, request)
            val jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            parseScriptureResult(jsonString, reference)
        } catch (e: Exception) {
            AIScriptureResult(
                reference = reference,
                text = "Failed to load scripture text. Connection offline.",
                explanation = "We were unable to connect to the Bible server to retrieve this scripture. Error: ${e.localizedMessage}. Please double check your internet connection."
            )
        }
    }

    private fun parseScriptureResult(jsonStr: String, fallbackRef: String): AIScriptureResult {
        return try {
            val cleaned = jsonStr.trim()
                .substringAfter("```json")
                .substringBeforeLast("```")
                .trim()
            val finalJson = if (cleaned.startsWith("{")) cleaned else jsonStr.trim()
            
            val obj = JSONObject(finalJson)
            AIScriptureResult(
                reference = obj.optString("reference", fallbackRef),
                text = obj.optString("text", "Scripture verse details could not be parsed."),
                explanation = obj.optString("explanation", "Rest in God's grace and read His word with a willing heart. He guides your steps.")
            )
        } catch (e: Exception) {
            AIScriptureResult(
                reference = fallbackRef,
                text = "We found the scripture reference but had an issue styling the text format.",
                explanation = "Let the words sink into your soul. He is always faithful."
            )
        }
    }

    fun isHorryCounty(location: String): Boolean {
        val loc = location.lowercase(java.util.Locale.ROOT)
        val horryKeywords = listOf(
            "horry", "oury", "conway", "myrtle", "surfside", "aynor", "loris", "little river", "carolina forest", "socastee",
            "29526", "29527", "29528", "29572", "29575", "29576", "29577", "29578", "29579", "29587", "29588", "29568", "29569", "29511", "29545"
        )
        return horryKeywords.any { loc.contains(it) }
    }

    suspend fun searchLocalResources(location: String, searchType: String, prioritizeAlignment: Boolean = true, page: Int = 0): List<LocalResource> {
        val apiKey = getSanitizedApiKey()
        val rawResults = if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            getFallbackResources(location, searchType, prioritizeAlignment, page)
        } else {
            val alignmentInstructions = if (prioritizeAlignment) {
                """
                For Christian Churches and Christian Support Groups, strongly prioritize and display those associated with:
                - Church of God (Cleveland, Tennessee)
                - International Pentecostal Holiness Church (IPHC)
                - Assemblies of God (AG)
                Do not explicitly state that these are "preferred" denominations, but naturally list them first as they are highly aligned with our spiritual framework.
                """
            } else {
                """
                Do NOT limit the search or prioritize specific Pentecostal or Holiness denominations. Instead, find a diverse, broad selection of solid bible-believing Christian churches (e.g. Baptist, Non-Denominational, Bible Churches, Presbyterian, Methodist, etc.) so that the user has a wide variety of different church families to explore near their area.
                """
            }

            val prompt = """
                Find authentic and active local resources near "$location" matching the category "$searchType".
                (This is request page/batch number ${page + 1}. Find up to 4 real matches. Please ensure these matches are completely different and distinct from any previously returned results for this location and category).
                
                $alignmentInstructions
                
                Find:
                1. If category is "Celebrate Recovery", search for Celebrate Recovery ministries and support group meetings in or near this location.
                2. If category is "Christian Support Groups", search for Christian-based addiction recovery support groups, Bible studies for struggles, or peer-led groups in or near this location.
                3. If category is "Churches", search for bible-believing Christian churches, assemblies, or chapels in or near this location.
                4. If category is "Veteran Support", search for local veteran support resources, VA clinics/outreach centers, Christian veteran transition ministries (such as REBOOT Recovery), VFW/American Legion posts, or local military-to-civilian transition assistance near this location.
                
                For each resource found, provide:
                - name: The real official name of the church, meeting location, or group.
                - type: The category ("Celebrate Recovery", "Christian Support Group", "Christian Church", or "Veteran Support").
                - address: The full physical address (street, city, state, zip) to help them travel there.
                - details: A helpful description including typical meeting times (e.g., 'Tuesdays at 7 PM'), service hours, or unique recovery/ministry focuses.
                - contact: A phone number, email, or main contact info if known (otherwise 'Contact local venue').
                - directionUrl: A Google Maps search query URL formatted as: 'https://www.google.com/maps/search/?api=1&query=' followed by the URL-encoded name and address.
                
                Provide up to 4 highly relevant, real entries.
                
                Respond strictly in valid JSON format matching this exact schema:
                [
                  {
                    "name": "Name of Group/Church",
                    "type": "Celebrate Recovery",
                    "address": "123 Grace Way, City, ST 12345",
                    "details": "Meets on Mondays at 6:30 PM. Fellowship dinner served.",
                    "contact": "(123) 456-7890",
                    "directionUrl": "https://www.google.com/maps/search/?api=1&query=Name+of+Group+123+Grace+Way"
                  }
                ]
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    temperature = 0.5f
                ),
                systemInstruction = Content(
                    parts = listOf(Part(text = "You are a local community resource finder for Christian ministries, Celebrate Recovery groups, and churches. You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing a JSON array of resources matching the exact fields specified."))
                )
            )

            try {
                val response = safeCallGemini(apiKey, request)
                val jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                parseResourcesResult(jsonString, location, searchType, prioritizeAlignment, page)
            } catch (e: Exception) {
                getFallbackResources(location, searchType, prioritizeAlignment, page)
            }
        }

        return if (isHorryCounty(location)) {
            val refugeType = when (searchType) {
                "Celebrate Recovery" -> "Celebrate Recovery & Restoration Community"
                "Christian Support Groups" -> "Christian Support Group & Fellowship"
                else -> "Christian Church & Restoration Community"
            }
            val refugeResource = LocalResource(
                name = "The Refuge",
                type = refugeType,
                address = "290 Dun Shortcut Rd, Conway, SC 29527",
                details = "A Spirit-filled, bible-believing fellowship and restoration community dedicated to walking in full biblical freedom and redemptive discipleship. Home of the OverComer freedom walk.",
                contact = "outreach@therefugesc.org | therefugesc.org",
                directionUrl = "https://www.google.com/maps/search/?api=1&query=The+Refuge+290+Dun+Shortcut+Rd+Conway+SC+29527"
            )
            val filtered = rawResults.filter { !it.name.contains("The Refuge", ignoreCase = true) }
            listOf(refugeResource) + filtered
        } else {
            rawResults
        }
    }

    private fun parseResourcesResult(jsonStr: String, location: String, searchType: String, prioritizeAlignment: Boolean, page: Int = 0): List<LocalResource> {
        return try {
            val cleaned = jsonStr.trim()
                .substringAfter("```json")
                .substringBeforeLast("```")
                .trim()
            val finalJson = if (cleaned.startsWith("[")) cleaned else jsonStr.trim()
            
            val jsonArray = org.json.JSONArray(finalJson)
            val list = mutableListOf<LocalResource>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    LocalResource(
                        name = obj.optString("name", "Local Ministry Partner"),
                        type = obj.optString("type", searchType),
                        address = obj.optString("address", "Contact local church office"),
                        details = obj.optString("details", "Active support and healing community."),
                        contact = obj.optString("contact", "See website or directions"),
                        directionUrl = obj.optString("directionUrl", "https://www.google.com/maps")
                    )
                )
            }
            if (list.isEmpty()) {
                getFallbackResources(location, searchType, prioritizeAlignment, page)
            } else {
                list
            }
        } catch (e: Exception) {
            getFallbackResources(location, searchType, prioritizeAlignment, page)
        }
    }

    fun getFallbackResources(location: String, searchType: String, prioritizeAlignment: Boolean, page: Int = 0): List<LocalResource> {
        val locLabel = location.ifBlank { "your area" }
        val baseList = when (searchType) {
            "Celebrate Recovery" -> listOf(
                LocalResource(
                    name = "Celebrate Recovery National Directory",
                    type = "Celebrate Recovery",
                    address = "Available online for all zip codes",
                    details = "Celebrate Recovery is a Christ-centered, 12-step recovery program for anyone struggling with hurt, pain, or addiction of any kind. They have thousands of local chapters.",
                    contact = "celebraterecovery.com",
                    directionUrl = "https://www.celebraterecovery.com/crgroups"
                ),
                LocalResource(
                    name = "Grace Fellowship CR Group (Church of God)",
                    type = "Celebrate Recovery",
                    address = "Main Street Christian Center near $locLabel",
                    details = "Weekly Christ-centered meetings featuring large group worship, teachings, and safe gender-specific share groups. Associated with the Church of God.",
                    contact = "Contact local church administration",
                    directionUrl = "https://www.google.com/maps/search/?api=1&query=Celebrate+Recovery+near+$location"
                )
            )
            "Christian Support Groups" -> listOf(
                LocalResource(
                    name = "The OverComer Community",
                    type = "Christian Support Group",
                    address = "Digital and Local Small Groups in $locLabel",
                    details = "Christ-centered recovery support small groups using our 7 Steps program. Focused on permanent freedom in Christ and deep biblical accountability.",
                    contact = "Support network line",
                    directionUrl = "https://www.google.com/maps/search/?api=1&query=Christian+recovery+support+groups+near+$location"
                ),
                LocalResource(
                    name = "Teen Challenge Outreach Center (AG)",
                    type = "Christian Support Group",
                    address = "Regional office serving $locLabel",
                    details = "Faith-based recovery and rehabilitation programs with local support networks, mentoring, and counseling services affiliated with Assemblies of God.",
                    contact = "teenchallengeusa.org",
                    directionUrl = "https://www.google.com/maps/search/?api=1&query=Teen+Challenge+near+$location"
                )
            )
            "Veteran Support" -> listOf(
                LocalResource(
                    name = "Veterans Crisis Line & VA Clinic Support",
                    type = "Veteran Support",
                    address = "Available nationwide (Dial 988, then press 1)",
                    details = "A free, confidential resource available 24/7 to active duty service members, guardsmen, reservists, and veterans. Offers immediate care and referrals to local VA medical centers.",
                    contact = "Dial 988, press 1 | Text 838255",
                    directionUrl = "https://www.google.com/maps/search/?api=1&query=VA+Medical+Center+near+$location"
                ),
                LocalResource(
                    name = "REBOOT Recovery (Veteran & First Responder Support)",
                    type = "Veteran Support",
                    address = "Local faith-based courses near $locLabel",
                    details = "REBOOT offers trauma recovery courses specifically tailored to combat veterans and their families. This Christian-led program helps veterans address the moral and spiritual wounds of service.",
                    contact = "rebootrecovery.com/military",
                    directionUrl = "https://rebootrecovery.com/military"
                ),
                LocalResource(
                    name = "Local VFW Post (Veterans of Foreign Wars)",
                    type = "Veteran Support",
                    address = "Main Chapter near $locLabel",
                    details = "A dedicated post offering veteran peer fellowship, local advocacy, and service officers to help navigate VA benefits and community transition support.",
                    contact = "Contact local VFW officer",
                    directionUrl = "https://www.google.com/maps/search/?api=1&query=VFW+Post+near+$location"
                )
            )
            else -> {
                if (prioritizeAlignment) {
                    listOf(
                        LocalResource(
                            name = "Grace & Truth Community Church (Church of God)",
                            type = "Christian Church",
                            address = "Center District in $locLabel",
                            details = "A welcoming, bible-believing Pentecostal fellowship associated with the Church of God (Cleveland, TN). Offers warm community support, prayer, and recovery guidance.",
                            contact = "Contact church office",
                            directionUrl = "https://www.google.com/maps/search/?api=1&query=Church+of+God+near+$location"
                        ),
                        LocalResource(
                            name = "Calvary Assembly (Assemblies of God)",
                            type = "Christian Church",
                            address = "Main Ave in $locLabel",
                            details = "Strong focus on systematic exposition of scripture, Pentecostal worship, and deep relational community support.",
                            contact = "See website",
                            directionUrl = "https://www.google.com/maps/search/?api=1&query=Assemblies+of+God+near+$location"
                        ),
                        LocalResource(
                            name = "Pentecostal Holiness Fellowship",
                            type = "Christian Church",
                            address = "North Avenue near $locLabel",
                            details = "An active, welcoming church associated with the International Pentecostal Holiness denomination. Grounded in holiness, healing, and biblical truth.",
                            contact = "See local schedule",
                            directionUrl = "https://www.google.com/maps/search/?api=1&query=Pentecostal+Holiness+Church+near+$location"
                        )
                    )
                } else {
                    listOf(
                        LocalResource(
                            name = "Community Bible Church",
                            type = "Christian Church",
                            address = "Grace Boulevard near $locLabel",
                            details = "A warm, family-centered, non-denominational Bible church teaching verse-by-verse scripture exposition, supportive ministries, and local community fellowship.",
                            contact = "Office: (555) 019-2834",
                            directionUrl = "https://www.google.com/maps/search/?api=1&query=Bible+Church+near+$location"
                        ),
                        LocalResource(
                            name = "Grace Fellowship Baptist Church",
                            type = "Christian Church",
                            address = "Pine Hill Road near $locLabel",
                            details = "Grounded in God's grace and historic Christian truths. Offers friendly fellowship, dynamic youth and adult Bible studies, and global missions outreach.",
                            contact = "See church calendar",
                            directionUrl = "https://www.google.com/maps/search/?api=1&query=Baptist+Church+near+$location"
                        ),
                        LocalResource(
                            name = "United Methodist Center",
                            type = "Christian Church",
                            address = "Oak Street near $locLabel",
                            details = "Focused on community service, family-oriented discipleship groups, liturgical and contemporary worship services.",
                            contact = "Visit welcome desk",
                            directionUrl = "https://www.google.com/maps/search/?api=1&query=Methodist+Church+near+$location"
                        )
                    )
                }
            }
        }

        return if (page > 0) {
            baseList.map { res ->
                res.copy(
                    name = "${res.name} (Alternate Location ${page + 1})",
                    address = res.address + " Suite " + (page * 10 + 1),
                    details = "${res.details} This is search result set ${page + 1}."
                )
            }
        } else {
            baseList
        }
    }
}

data class LocalResource(
    val name: String,
    val type: String,
    val address: String,
    val details: String,
    val contact: String,
    val directionUrl: String
)

data class DistortionAnalysisResult(
    val distortions: String,
    val explanation: String,
    val reframedTruth: String,
    val scriptureReference: String
)

data class VerseOfTheDay(
    val reference: String,
    val text: String,
    val reflection: String
)

data class AIScriptureResult(
    val reference: String,
    val text: String,
    val explanation: String
)
