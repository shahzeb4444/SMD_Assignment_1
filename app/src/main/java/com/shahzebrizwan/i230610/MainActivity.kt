package com.shahzebrizwan.i230610

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, login_splash::class.java)
            startActivity(intent)
            finish() // close SplashActivity so user can’t go back to it
        }, 2000) //
    }
}