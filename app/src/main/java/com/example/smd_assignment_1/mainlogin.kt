package com.example.smd_assignment_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class mainlogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainlogin)
        var signup = findViewById<TextView>(R.id.signup)
        var loginbtn = findViewById<Button>(R.id.loginbutton)
        signup.setOnClickListener {
            val intent = Intent(this, second_page::class.java)
            startActivity(intent)
        }
        loginbtn.setOnClickListener {
            val intent = Intent(this, login_splash::class.java)
            startActivity(intent)
        }
    }
}