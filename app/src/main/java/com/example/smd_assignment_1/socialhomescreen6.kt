package com.example.smd_assignment_1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class socialhomescreen6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen6)
        var bottomnavhome = findViewById<ImageView>(R.id.bottomnavhome)
        var bottomnavsearch = findViewById<ImageView>(R.id.bottomnavsearch)
        var bottomnavcreate = findViewById<ImageView>(R.id.bottomnavcreate)
        var bottomnavprofile = findViewById<ImageView>(R.id.bottomnavicon)
        var youtext = findViewById<TextView>(R.id.Youtext)
        youtext.setOnClickListener {
            val intent = Intent(this, socialhomescreen7::class.java)
            startActivity(intent)
            finish()
        }
        bottomnavhome.setOnClickListener {
            val Intent = Intent(this, socialhomescreen1::class.java)
            startActivity(Intent)
            finish()
        }
        bottomnavsearch.setOnClickListener {
            val intent = Intent(this, socialhomescreen2::class.java)
            startActivity(intent)
            finish()
        }
        bottomnavcreate.setOnClickListener {
            val intent = Intent(this, socialhomescreen17::class.java)
            startActivity(intent)

        }
        bottomnavprofile.setOnClickListener {
            val intent = Intent(this, socialhomescreen8::class.java)
            startActivity(intent)
            finish()
        }
    }
}