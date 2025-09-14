package com.example.smd_assignment_1

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class socialhomescreen11 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen11)
        val canceltext = findViewById<TextView>(R.id.canceltext)
        val switchprofile = findViewById<TextView>(R.id.changeprofilephoto)
        val donetext = findViewById<TextView>(R.id.donetext)
        donetext.setOnClickListener {
            val intent = Intent(this, socialhomescreen8::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        switchprofile.setOnClickListener {
            val intent = Intent(this, socialhomescreen16::class.java)
            startActivity(intent)
        }
        canceltext.setOnClickListener {
            val intent = Intent(this, socialhomescreen8::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}