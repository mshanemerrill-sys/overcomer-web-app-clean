package com.example

import org.junit.Assert.*
import org.junit.Test
import kotlinx.coroutines.runBlocking
import com.example.network.*
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.MockResponse

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGenerateContentRequestSerialization() {
    val request = com.example.network.GenerateContentRequest(
        contents = listOf(
            com.example.network.Content(
                role = "user",
                parts = listOf(com.example.network.Part(text = "Hello OverComer Guide!"))
            )
        ),
        systemInstruction = com.example.network.Content(
            parts = listOf(com.example.network.Part(text = "You are a loving mentor."))
        )
    )
    val moshi = com.example.network.RetrofitClient.moshi
    val adapter = moshi.adapter(com.example.network.GenerateContentRequest::class.java)
    val jsonString = adapter.toJson(request)
    
    // Check that JSON contains "system_instruction"
    assertTrue("JSON must contain 'system_instruction'", jsonString.contains("\"system_instruction\""))
    // Check that JSON contains "contents"
    assertTrue("JSON must contain 'contents'", jsonString.contains("\"contents\""))
    // Check that JSON preserves the system instruction content
    assertTrue("JSON must preserve the system instruction text", jsonString.contains("You are a loving mentor."))
    // Check that JSON does NOT contain "systemInstruction"
    assertFalse("JSON must not contain outdated 'systemInstruction'", jsonString.contains("\"systemInstruction\""))
  }

  @Test
  fun testMockGeminiAuthenticationWithModernKey() = runBlocking {
    val server = MockWebServer()
    
    val successBody = """
      {
        "candidates": [
          {
            "content": {
              "parts": [
                {
                  "text": "Connected"
                }
              ],
              "role": "model"
            }
          }
        ]
      }
    """.trimIndent()
    
    server.enqueue(MockResponse().setResponseCode(200).setBody(successBody))
    server.start()
    
    val baseUrl = server.url("/").toString()
    val modernKey = "AQ.this_is_a_modern_auth_api_key_format_12345"
    
    val testRequest = GenerateContentRequest(
        contents = listOf(
            Content(role = "user", parts = listOf(Part(text = "Test connection")))
        )
    )
    
    // 1. Verify that the error parser parses API_KEY_INVALID correctly
    val errorBodyInvalidKey = """
      {
        "error": {
          "code": 400,
          "message": "API key not valid. Please pass a valid API key.",
          "status": "INVALID_ARGUMENT",
          "details": [
            {
              "@type": "type.googleapis.com/google.rpc.ErrorInfo",
              "reason": "API_KEY_INVALID"
            }
          ]
        }
      }
    """.trimIndent()
    
    val exception1 = parseGeminiError(400, errorBodyInvalidKey, modernKey)
    assertTrue("Should map to InvalidApiKeyException", exception1 is GeminiException.InvalidApiKeyException)
    assertFalse("Error display must not contain raw API Key", exception1.message!!.contains(modernKey))
    
    // 2. Verify that standard HTTP 400 without API_KEY_INVALID maps to SchemaOrBadRequestException
    val errorBodySchemaError = """
      {
        "error": {
          "code": 400,
          "message": "Invalid JSON payload received. Unknown name 'systemInstruction': Cannot find field.",
          "status": "INVALID_ARGUMENT"
        }
      }
    """.trimIndent()
    
    val exception2 = parseGeminiError(400, errorBodySchemaError, modernKey)
    assertTrue("Should map to SchemaOrBadRequestException", exception2 is GeminiException.SchemaOrBadRequestException)
    
    // 3. Verify that key redacting works beautifully
    val rawErrorMessage = "The key AQ.this_is_a_modern_auth_api_key_format_12345 failed to authenticate."
    val redacted = sanitizeErrorMessage(rawErrorMessage, modernKey)
    assertEquals("The key [REDACTED] failed to authenticate.", redacted)
    
    // 4. Verify that HTTP 401/403 maps to InvalidApiKeyException
    val exception3 = parseGeminiError(403, "Forbidden", modernKey)
    assertTrue("Should map to InvalidApiKeyException on 401/403", exception3 is GeminiException.InvalidApiKeyException)
    
    // 5. Verify that HTTP 429 maps to RateLimitException
    val exception4 = parseGeminiError(429, "Too Many Requests", modernKey)
    assertTrue("Should map to RateLimitException on 429", exception4 is GeminiException.RateLimitException)
    
    // 6. Verify that HTTP 404 maps to UnsupportedModelException
    val exception5 = parseGeminiError(404, "Model not found", modernKey)
    assertTrue("Should map to UnsupportedModelException on 404", exception5 is GeminiException.UnsupportedModelException)
    
    server.shutdown()
  }

  @Test
  fun testProductionRequestPathWithMockWebServer() = runBlocking {
    val server = MockWebServer()
    
    val successBody = """
      {
        "candidates": [
          {
            "content": {
              "parts": [
                {
                  "text": "Connected"
                }
              ],
              "role": "model"
            }
          }
        ]
      }
    """.trimIndent()
    
    server.enqueue(MockResponse().setResponseCode(200).setBody(successBody))
    server.start()
    
    val dummyKey = "AQ.DUMMY_internal-test_value-123456789"
    val testRequest = GenerateContentRequest(
        contents = listOf(
            Content(role = "user", parts = listOf(Part(text = "Test connection")))
        )
    )
    
    val baseUrl = server.url("/").toString()
    
    // Call safeCallGemini with the mocked baseUrl
    val response = safeCallGemini(dummyKey, testRequest, baseUrl = baseUrl)
    assertNotNull(response)
    
    val recordedRequest = server.takeRequest()
    assertEquals(dummyKey, recordedRequest.getHeader("x-goog-api-key"))
    assertNull(recordedRequest.getHeader("Authorization"))
    assertNull(recordedRequest.requestUrl?.queryParameter("key"))
    assertTrue(recordedRequest.path!!.contains("gemini-3.5-flash:generateContent"))
    
    server.shutdown()
  }
}
