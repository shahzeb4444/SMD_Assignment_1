package com.shahzebrizwan.i230610

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen15 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen15)
        val cancelicon = findViewById<ImageView>(R.id.g1)
        val yourstory = findViewById<ImageView>(R.id.yourstory)
        val closefriends = findViewById<ImageView>(R.id.closefriends)
        closefriends.setOnClickListener {
            val intent = Intent(this, socialhomescreen1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        yourstory.setOnClickListener {
            val intent = Intent(this, socialhomescreen1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        cancelicon.setOnClickListener {
            val intent = Intent(this, socialhomescreen17::class.java)
            startActivity(intent)
            finish()
        }
    }
}