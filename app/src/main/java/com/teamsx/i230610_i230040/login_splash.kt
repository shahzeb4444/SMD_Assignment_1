package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class login_splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_splash)
        var lgnbtn = findViewById<Button>(R.id.loginbutton)
        var switchaccount = findViewById<TextView>(R.id.switchaccounts)
        var signup = findViewById<TextView>(R.id.signup)
        lgnbtn.setOnClickListener {
            val intent = Intent(this, socialhomescreen1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        switchaccount.setOnClickListener {
            val intent = Intent(this, mainlogin::class.java)
            startActivity(intent)

        }
        signup.setOnClickListener {
            val intent = Intent(this, second_page::class.java)
            startActivity(intent)
        }
    }
}