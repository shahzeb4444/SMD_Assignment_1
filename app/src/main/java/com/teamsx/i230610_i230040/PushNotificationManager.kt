package com.teamsx.i230610_i230040

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object PushNotificationManager {

    private const val TAG = "PushNotificationManager"
    private const val WORKER_URL = "https://fcm-v1-notifier.shahzebrizwan05.workers.dev/" // Replace with your worker URL

    private val client = OkHttpClient()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    /**
     * Initialize FCM and request notification permission
     */
    fun initialize(context: Context, onTokenReceived: (String) -> Unit = {}) {
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Notification permission not granted")
                return
            }
        }

        // Get FCM token
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(TAG, "FCM Token: $token")
                onTokenReceived(token)

                // Save token to database
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    saveFcmToken(userId, token)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get FCM token", e)
            }
    }

    /**
     * Save FCM token to database
     */
    private fun saveFcmToken(userId: String, token: String) {
        db.child("users").child(userId).child("fcmToken").setValue(token)
            .addOnSuccessListener {
                Log.d(TAG, "FCM token saved for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save FCM token", e)
            }
    }

    /**
     * Send notification for new message
     */
    fun sendNewMessageNotification(
        recipientUserId: String,
        senderName: String,
        messageText: String,
        chatId: String,
        otherUserName: String
    ) {
        db.child("users").child(recipientUserId).child("fcmToken").get()
            .addOnSuccessListener { snapshot ->
                val token = snapshot.getValue(String::class.java)
                if (token != null) {
                    sendNotification(
                        token = token,
                        title = "New message from $senderName",
                        body = messageText,
                        data = mapOf(
                            "type" to "new_message",
                            "chatId" to chatId,
                            "otherUserName" to otherUserName,
                            "otherUserId" to recipientUserId
                        )
                    )
                }
            }
    }

    /**
     * Send notification for follow request
     */
    fun sendFollowRequestNotification(
        recipientUserId: String,
        senderName: String
    ) {
        db.child("users").child(recipientUserId).child("fcmToken").get()
            .addOnSuccessListener { snapshot ->
                val token = snapshot.getValue(String::class.java)
                if (token != null) {
                    sendNotification(
                        token = token,
                        title = "New Follow Request",
                        body = "$senderName wants to follow you",
                        data = mapOf(
                            "type" to "follow_request",
                            "fromUserId" to auth.currentUser?.uid.orEmpty()
                        )
                    )
                }
            }
    }

    /**
     * Send notification for screenshot alert
     */
    fun sendScreenshotAlertNotification(
        recipientUserId: String,
        screenshotTakerName: String,
        chatId: String
    ) {
        db.child("users").child(recipientUserId).child("fcmToken").get()
            .addOnSuccessListener { snapshot ->
                val token = snapshot.getValue(String::class.java)
                if (token != null) {
                    sendNotification(
                        token = token,
                        title = "Screenshot Alert",
                        body = "$screenshotTakerName took a screenshot of your chat",
                        data = mapOf(
                            "type" to "screenshot_alert",
                            "chatId" to chatId,
                            "fromUserId" to auth.currentUser?.uid.orEmpty()
                        )
                    )
                }
            }
    }

    /**
     * Send notification via Cloudflare Worker
     */
    private fun sendNotification(
        token: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ) {
        val json = JSONObject().apply {
            put("token", token)
            put("title", title)
            put("body", body)
            put("data", JSONObject(data))
        }

        val requestBody = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(WORKER_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to send notification", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Notification sent successfully")
                } else {
                    Log.e(TAG, "Failed to send notification: ${response.code}")
                }
            }
        })
    }
}