package com.example.smd_assignment_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class socialhomescreen8 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen8)
        var bottomnavsearch = findViewById<ImageView>(R.id.bottomnavsearch)
        var bottomnavhome = findViewById<ImageView>(R.id.bottomnavhome)
        var bottomnavcreate = findViewById<ImageView>(R.id.bottomnavcreate)
        var bottomnavlike = findViewById<ImageView>(R.id.bottomnavlike)
        var highlight1 = findViewById<ImageView>(R.id.circle2)
        var highlight2 = findViewById<ImageView>(R.id.circle3)
        var highlight3 = findViewById<ImageView>(R.id.circle4)
        val editprofilebtn = findViewById<Button>(R.id.editprofilebtn)
        editprofilebtn.setOnClickListener {
            val Intent = Intent(this, socialhomescreen11::class.java)
            startActivity(Intent)
        }

        highlight1.setOnClickListener {
            val Intent = Intent(this, socialhomescreen13::class.java)
            startActivity(Intent)
        }
        highlight2.setOnClickListener {
            val Intent = Intent(this, socialhomescreen13::class.java)
            startActivity(Intent)
        }
        highlight3.setOnClickListener {
            val Intent = Intent(this, socialhomescreen13::class.java)
            startActivity(Intent)
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
        bottomnavlike.setOnClickListener {
            val intent = Intent(this, socialhomescreen6::class.java)
            startActivity(intent)
            finish()
        }

    }
}