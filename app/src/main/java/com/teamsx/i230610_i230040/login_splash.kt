package com.teamsx.i230610_i230040

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class login_splash : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    // Notification permission launcher for Android 13+
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifications disabled. You won't receive alerts.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_splash)

        val lgnbtn = findViewById<Button>(R.id.loginbutton)
        val switchaccount = findViewById<TextView>(R.id.switchaccounts)
        val profilename = findViewById<TextView>(R.id.profilename)
        val profilepic = findViewById<ImageView>(R.id.profilepic)

        // 1) If not logged in, go to login
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, mainlogin::class.java))
            finish()
            return
        }

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Register FCM token
        FCMTokenManager.registerFCMToken(this)

        // 2) Load /users/{uid} once and fill UI
        db.child("users").child(user.uid).get()
            .addOnSuccessListener { snap ->
                val profile = snap.getValue(UserProfile::class.java)
                profilename.text = profile?.username ?: "user"

                // decode Base64 photo if present
                val b64 = profile?.photoBase64
                if (!b64.isNullOrEmpty()) {
                    try {
                        val bytes = Base64.decode(b64, Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        profilepic.setImageBitmap(bmp)
                    } catch (_: Exception) { /* ignore bad data */ }
                } else {
                    // optional: set a placeholder drawable
                    profilepic.setImageResource(R.drawable.ic_launcher_background)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Couldn’t load profile.", Toast.LENGTH_SHORT).show()
            }

        // 3) Buttons
        lgnbtn.setOnClickListener {
            // CHANGED: go to HomeActivity (BottomNav) instead of socialhomescreen1
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_home)
            }
            startActivity(intent)
            finish()
        }
        switchaccount.setOnClickListener {
            // Sign out then go to login
            auth.signOut()
            FCMTokenManager.unregisterFCMToken() // Remove FCM token on logout
            startActivity(Intent(this, mainlogin::class.java))
            finish()
        }

        val logoutText = findViewById<TextView>(R.id.logout)
        logoutText.setOnClickListener {
            auth.signOut()
            FCMTokenManager.unregisterFCMToken() // Remove FCM token on logout
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, mainlogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
