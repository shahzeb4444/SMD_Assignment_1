package com.teamsx.i230610_i230040

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.tasks.await

object NotificationHelper {

    private const val TAG = "NotificationHelper"
    private const val PC_IP = "172.20.10.13" // Your PC's WiFi IP
    private const val EMULATOR_IP = "10.0.2.2"
    private const val PORT = 3000

    // Automatically detect if running on emulator or real device
    private fun isEmulator(): Boolean {
        val fingerprint = android.os.Build.FINGERPRINT.lowercase()
        val model = android.os.Build.MODEL.lowercase()
        return fingerprint.contains("generic") ||
               fingerprint.contains("emulator") ||
               model.contains("sdk") ||
               model.contains("emulator")
    }

    private fun getServerUrl(): String {
        val ip = if (isEmulator()) EMULATOR_IP else PC_IP
        val url = "http://$ip:$PORT/send"
        Log.d(TAG, "Using server URL: $url (isEmulator: ${isEmulator()})")
        return url
    }

    /**
     * Send notification for new message
     */
    suspend fun sendNewMessageNotification(
        recipientUserId: String,
        senderName: String,
        messageText: String?,
        isMedia: Boolean,
        chatId: String,
        senderId: String
    ) {
        Log.d(TAG, "🔔 sendNewMessageNotification called")
        Log.d(TAG, "   Recipient: $recipientUserId")
        Log.d(TAG, "   Sender: $senderName")
        Log.d(TAG, "   Message: ${messageText?.take(50)}")

        val fcmToken = getFCMToken(recipientUserId)
        if (fcmToken == null) {
            Log.e(TAG, "❌ No FCM token found for user $recipientUserId - notification NOT sent")
            return
        }

        Log.d(TAG, "✅ Found FCM token: ${fcmToken.take(20)}...")

        val payload = JSONObject().apply {
            put("token", fcmToken)
            put("type", "new_message")
            put("data", JSONObject().apply {
                put("senderName", senderName)
                put("messageText", messageText ?: "")
                put("isMedia", isMedia)
                put("chatId", chatId)
                put("senderId", senderId)
            })
        }

        sendNotificationRequest(payload)
    }

    /**
     * Send notification for follow request
     */
    suspend fun sendFollowRequestNotification(
        recipientUserId: String,
        fromUsername: String,
        fromUserId: String
    ) {
        Log.d(TAG, "👥 sendFollowRequestNotification called")
        Log.d(TAG, "   Recipient: $recipientUserId")
        Log.d(TAG, "   From: $fromUsername")

        val fcmToken = getFCMToken(recipientUserId)
        if (fcmToken == null) {
            Log.e(TAG, "❌ No FCM token found for user $recipientUserId - notification NOT sent")
            return
        }

        Log.d(TAG, "✅ Found FCM token: ${fcmToken.take(20)}...")

        val payload = JSONObject().apply {
            put("token", fcmToken)
            put("type", "follow_request")
            put("data", JSONObject().apply {
                put("fromUsername", fromUsername)
                put("fromUserId", fromUserId)
            })
        }

        sendNotificationRequest(payload)
    }

    /**
     * Send notification for screenshot detection
     */
    suspend fun sendScreenshotDetectedNotification(
        recipientUserId: String,
        userName: String,
        userId: String,
        chatId: String
    ) {
        val fcmToken = getFCMToken(recipientUserId) ?: return

        val payload = JSONObject().apply {
            put("token", fcmToken)
            put("type", "screenshot_detected")
            put("data", JSONObject().apply {
                put("userName", userName)
                put("userId", userId)
                put("chatId", chatId)
            })
        }

        sendNotificationRequest(payload)
    }

    /**
     * Get FCM token from Firebase Realtime Database
     */
    private suspend fun getFCMToken(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val database = FirebaseDatabase.getInstance().reference
            val snapshot = database.child("fcmTokens").child(userId).child("token").get().await()
            val token = snapshot.getValue(String::class.java)
            if (token.isNullOrEmpty()) {
                Log.w(TAG, "No FCM token found for user $userId")
                return@withContext null
            }
            return@withContext token
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching FCM token for user $userId", e)
            return@withContext null
        }
    }

    /**
     * Send HTTP request to FCM server
     */
    private suspend fun sendNotificationRequest(payload: JSONObject) = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val serverUrl = getServerUrl()
            Log.d(TAG, "📡 Sending notification to: $serverUrl")
            Log.d(TAG, "📦 Payload: $payload")

            val url = URL(serverUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "📨 Server response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG, "✅ Notification sent successfully!")
                Log.d(TAG, "📄 Response: $response")
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error details"
                Log.e(TAG, "❌ Failed to send notification. Response code: $responseCode")
                Log.e(TAG, "❌ Error response: $errorResponse")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception sending notification", e)
            Log.e(TAG, "❌ Error message: ${e.message}")
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
    }
}