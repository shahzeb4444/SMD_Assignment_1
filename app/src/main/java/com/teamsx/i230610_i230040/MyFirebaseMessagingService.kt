package com.teamsx.i230610_i230040

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "socially_notifications"
        private const val CHANNEL_NAME = "Socially Notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // Save token to Firebase Database
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            saveFcmToken(userId, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        // Handle data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")
            handleDataPayload(message.data)
        }

        // Handle notification payload
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            showNotification(
                title = it.title ?: "Socially",
                body = it.body ?: "",
                data = message.data
            )
        }
    }

    private fun handleDataPayload(data: Map<String, String>) {
        val type = data["type"]
        val title = data["title"] ?: "Socially"
        val body = data["body"] ?: ""

        when (type) {
            "new_message" -> {
                showNotification(title, body, data, NotificationType.MESSAGE)
            }
            "follow_request" -> {
                showNotification(title, body, data, NotificationType.FOLLOW_REQUEST)
            }
            "screenshot_alert" -> {
                showNotification(title, body, data, NotificationType.SCREENSHOT)
            }
            else -> {
                showNotification(title, body, data)
            }
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String>,
        type: NotificationType = NotificationType.GENERAL
    ) {
        createNotificationChannel()

        val intent = when (type) {
            NotificationType.MESSAGE -> {
                Intent(this, socialhomescreenchat::class.java).apply {
                    data["chatId"]?.let { putExtra("chatId", it) }
                    data["otherUserName"]?.let { putExtra("otherUserName", it) }
                    data["otherUserId"]?.let { putExtra("otherUserId", it) }
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            }
            NotificationType.FOLLOW_REQUEST -> {
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_notifications)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            }
            NotificationType.SCREENSHOT -> {
                Intent(this, socialhomescreenchat::class.java).apply {
                    data["chatId"]?.let { putExtra("chatId", it) }
                    data["otherUserName"]?.let { putExtra("otherUserName", it) }
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            }
            else -> {
                Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Socially app"
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun saveFcmToken(userId: String, token: String) {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .child(userId)
            .child("fcmToken")
            .setValue(token)
            .addOnSuccessListener {
                Log.d(TAG, "FCM token saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save FCM token", e)
            }
    }

    enum class NotificationType {
        GENERAL,
        MESSAGE,
        FOLLOW_REQUEST,
        SCREENSHOT
    }
}