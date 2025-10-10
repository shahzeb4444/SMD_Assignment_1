package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class mainlogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainlogin)
        var signup = findViewById<TextView>(R.id.signup)
        var loginbtn = findViewById<Button>(R.id.loginbutton)
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        signup.setOnClickListener {
            val intent = Intent(this, second_page::class.java)
            startActivity(intent)
        }
        loginbtn.setOnClickListener {
            val intent = Intent(this, socialhomescreen1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}