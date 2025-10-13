package com.teamsx.i230610_i230040

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen4 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen4)

        val back = findViewById<ImageView>(R.id.leftarrowlogo)
        back.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_home)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        val go = findViewById<RelativeLayout>(R.id.r1)
        go.setOnClickListener {
            val intent = Intent(this, socialhomescreenchat::class.java)
            startActivity(intent)
        }

        val go2 = findViewById<RelativeLayout>(R.id.r2)
        go2.setOnClickListener {
            val intent = Intent(this, socialhomescreenchat::class.java)
            startActivity(intent)
        }

        val go3 = findViewById<RelativeLayout>(R.id.r3)
        go3.setOnClickListener {
            val intent = Intent(this, socialhomescreenchat::class.java)
            startActivity(intent)
        }

        val go4 = findViewById<RelativeLayout>(R.id.r4)
        go4.setOnClickListener {
            val intent = Intent(this, socialhomescreenchat::class.java)
            startActivity(intent)
        }

        val go5 = findViewById<RelativeLayout>(R.id.r5)
        go5.setOnClickListener {
            val intent = Intent(this, socialhomescreenchat::class.java)
            startActivity(intent)
        }

        val go6 = findViewById<RelativeLayout>(R.id.r6)
        go6.setOnClickListener {
            val intent = Intent(this, socialhomescreenchat::class.java)
            startActivity(intent)
        }

        val camerabtn = findViewById<TextView>(R.id.cameraoption)
        camerabtn.setOnClickListener {
            // Open the default gallery / photos app
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"   // tell system we want to view images
            }
            startActivity(intent)
        }
    }
}
