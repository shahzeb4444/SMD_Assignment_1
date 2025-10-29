package com.teamsx.i230610_i230040

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

object FCMTokenManager {

    private const val TAG = "FCMTokenManager"

    /**
     * Get and save FCM token to Firebase Database
     * Call this when user logs in
     */
    fun registerFCMToken(context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "User not logged in, cannot register FCM token")
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, "FCM Token: $token")

            // Save to Firebase Database
            saveFCMToken(userId, token)
        }
    }

    /**
     * Save FCM token to Firebase Realtime Database
     */
    private fun saveFCMToken(userId: String, token: String) {
        val database = FirebaseDatabase.getInstance().reference

        val tokenData = mapOf(
            "token" to token,
            "updatedAt" to System.currentTimeMillis(),
            "platform" to "android"
        )

        database.child("fcmTokens").child(userId).setValue(tokenData)
            .addOnSuccessListener {
                Log.d(TAG, "FCM token saved successfully for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save FCM token for user: $userId", e)
            }
    }

    /**
     * Remove FCM token on logout
     */
    fun unregisterFCMToken() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val database = FirebaseDatabase.getInstance().reference
        database.child("fcmTokens").child(userId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "FCM token removed successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to remove FCM token", e)
            }
    }
}