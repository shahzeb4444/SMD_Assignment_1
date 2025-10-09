package com.shahzebrizwan.i230610

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen3)
        val filledcancel = findViewById<ImageView>(R.id.filledcancel)
        val cleartext = findViewById<TextView>(R.id.cleartext)
        filledcancel.setOnClickListener {
            val intent = Intent(this, socialhomescreen2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        cleartext.setOnClickListener {
            val intent = Intent(this, socialhomescreen2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}