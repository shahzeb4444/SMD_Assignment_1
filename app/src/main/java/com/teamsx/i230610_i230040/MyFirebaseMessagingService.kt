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
        private const val CHANNEL_ID = "default"
        private const val CHANNEL_NAME = "General Notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // Save token to Firebase Realtime Database
        saveFCMTokenToDatabase(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")

        // Check if message contains a notification payload
        message.notification?.let {
            Log.d(TAG, "Notification Title: ${it.title}")
            Log.d(TAG, "Notification Body: ${it.body}")
        }

        // Check if message contains a data payload
        message.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${message.data}")

            val notificationType = message.data["type"]
            handleNotification(notificationType, message)
        }
    }

    private fun handleNotification(type: String?, message: RemoteMessage) {
        when (type) {
            "new_message" -> showNewMessageNotification(message)
            "follow_request" -> showFollowRequestNotification(message)
            "screenshot_detected" -> showScreenshotNotification(message)
            else -> showDefaultNotification(message)
        }
    }

    private fun showNewMessageNotification(message: RemoteMessage) {
        val title = message.notification?.title ?: "New Message"
        val body = message.notification?.body ?: "You have a new message"
        val chatId = message.data["chatId"] ?: ""
        val senderId = message.data["senderId"] ?: ""
        val senderName = message.data["senderName"] ?: ""

        val intent = Intent(this, socialhomescreenchat::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("otherUserName", senderName)
            putExtra("otherUserId", senderId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        showNotification(
            title = title,
            message = body,
            intent = intent,
            notificationId = chatId.hashCode()
        )
    }

    private fun showFollowRequestNotification(message: RemoteMessage) {
        val title = message.notification?.title ?: "New Follow Request"
        val body = message.notification?.body ?: "Someone wants to follow you"

        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_notifications)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        showNotification(
            title = title,
            message = body,
            intent = intent,
            notificationId = System.currentTimeMillis().toInt()
        )
    }

    private fun showScreenshotNotification(message: RemoteMessage) {
        val title = message.notification?.title ?: "Screenshot Alert"
        val body = message.notification?.body ?: "Someone took a screenshot"
        val chatId = message.data["chatId"] ?: ""
        val userId = message.data["userId"] ?: ""
        val userName = message.data["userName"] ?: ""

        val intent = Intent(this, socialhomescreenchat::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("otherUserName", userName)
            putExtra("otherUserId", userId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        showNotification(
            title = title,
            message = body,
            intent = intent,
            notificationId = chatId.hashCode(),
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    private fun showDefaultNotification(message: RemoteMessage) {
        val title = message.notification?.title ?: "Socially"
        val body = message.notification?.body ?: "You have a new notification"

        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        showNotification(
            title = title,
            message = body,
            intent = intent,
            notificationId = System.currentTimeMillis().toInt()
        )
    }

    private fun showNotification(
        title: String,
        message: String,
        intent: Intent,
        notificationId: Int,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for general app notifications"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun saveFCMTokenToDatabase(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("fcmTokens").child(userId).setValue(token)
                .addOnSuccessListener {
                    Log.d(TAG, "FCM token saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to save FCM token", e)
                }
        }
    }
}