package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.example.ui.OverComerViewModel
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.MockResponse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OverComer", appName)
  }

  @Test
  fun testCustomApiKeyLifecycleAndVerificationStates() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = OverComerViewModel(app)
    
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
    
    val testBaseUrl = server.url("/").toString()
    
    val keyA = "AQ.KEY_A_FOR_TEST_123456"
    val keyB = "AQ.KEY_B_FOR_TEST_987654"
    
    val keyAFingerprint = viewModel.getSha256Fingerprint(keyA)
    val keyBFingerprint = viewModel.getSha256Fingerprint(keyB)
    
    assertNotEquals(keyAFingerprint, keyBFingerprint)
    
    // 1. Verify that Test Connection uses the current draft value and does not use fallback
    var testASuccess = false
    var testAMsg = ""
    viewModel.testCustomApiKey(keyA, baseUrl = testBaseUrl) { success, msg ->
      testASuccess = success
      testAMsg = msg
    }
    
    // Poll until the state machine transitions away from "testing" or times out (5 seconds max)
    var attempts = 0
    while (viewModel.customApiKeyStatus.value == "testing" && attempts < 100) {
      delay(50)
      org.robolectric.shadows.ShadowLooper.idleMainLooper()
      attempts++
    }
    
    assertTrue("Test connection with Key A should succeed", testASuccess)
    assertEquals("verified", viewModel.customApiKeyStatus.value)
    assertEquals(keyAFingerprint, viewModel.verifiedApiKeyFingerprint.value)
    
    val recordedRequest = server.takeRequest()
    assertEquals(keyA, recordedRequest.getHeader("x-goog-api-key"))
    
    // 2. Verify that entering Key B (or invoking onKeyInputChanged) invalidates Key A's verified status
    viewModel.onKeyInputChanged(keyB)
    assertEquals("unverified", viewModel.customApiKeyStatus.value)
    
    // 3. Verify that saving Key B sets status to unverified and clears Key A's fingerprint
    viewModel.saveCustomApiKey(keyB)
    assertEquals("unverified", viewModel.customApiKeyStatus.value)
    assertEquals("", viewModel.verifiedApiKeyFingerprint.value)
    
    // 4. Verify that a failed test cannot leave a green verified status visible, and resets fingerprint
    server.enqueue(MockResponse().setResponseCode(400).setBody("""
      {
        "error": {
          "code": 400,
          "message": "API key not valid",
          "status": "INVALID_ARGUMENT",
          "details": [
            {
              "@type": "type.googleapis.com/google.rpc.ErrorInfo",
              "reason": "API_KEY_INVALID"
            }
          ]
        }
      }
    """.trimIndent()))
    
    var testBSuccess = true
    viewModel.testCustomApiKey(keyB, baseUrl = testBaseUrl) { success, _ ->
      testBSuccess = success
    }
    
    // Poll until the state machine transitions away from "testing" or times out (5 seconds max)
    var failedAttempts = 0
    while (viewModel.customApiKeyStatus.value == "testing" && failedAttempts < 100) {
      delay(50)
      org.robolectric.shadows.ShadowLooper.idleMainLooper()
      failedAttempts++
    }
    
    assertFalse("Test connection with invalid Key B should fail", testBSuccess)
    assertEquals("failed", viewModel.customApiKeyStatus.value)
    assertEquals("", viewModel.verifiedApiKeyFingerprint.value)
    
    server.shutdown()
  }
}
