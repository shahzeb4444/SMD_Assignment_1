package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen1)
        var bottomnavsearch = findViewById<ImageView>(R.id.bottomnavsearch)
        var bottomnavhome = findViewById<ImageView>(R.id.bottomnavhome)
        var bottomnavcreate = findViewById<ImageView>(R.id.bottomnavcreate)
        var bottomnavlike = findViewById<ImageView>(R.id.bottomnavlike)
        var bottomnavprofile = findViewById<ImageView>(R.id.bottomnavicon)
        var userprofile = findViewById<ImageView>(R.id.p1)
        var otherprofile1 = findViewById<ImageView>(R.id.p2)
        var otherprofile2 = findViewById<ImageView>(R.id.p3)
        var otherprofile3 = findViewById<ImageView>(R.id.p4)
        var otherprofile4 = findViewById<ImageView>(R.id.p5)
        var otherprofile5 = findViewById<ImageView>(R.id.p6)
        val messages = findViewById<ImageView>(R.id.messangerlogo)
        val cameraicon = findViewById<ImageView>(R.id.cameralogo)
        cameraicon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"   // tell system we want to view images
            }
            startActivity(intent)
        }
        messages.setOnClickListener {
            val intent = Intent(this, socialhomescreen4::class.java)
            startActivity(intent)
        }

        otherprofile1.setOnClickListener {
            val intent = Intent(this, socialhomescreen12::class.java)
            startActivity(intent)
        }
        otherprofile2.setOnClickListener {
            val intent = Intent(this, socialhomescreen12::class.java)
            startActivity(intent)
        }
        otherprofile3.setOnClickListener {
            val intent = Intent(this, socialhomescreen12::class.java)
            startActivity(intent)
        }
        otherprofile4.setOnClickListener {
            val intent = Intent(this, socialhomescreen12::class.java)
            startActivity(intent)
        }
        otherprofile5.setOnClickListener {
            val intent = Intent(this, socialhomescreen12::class.java)
            startActivity(intent)
        }
        userprofile.setOnClickListener {
            val intent = Intent(this, socialhomescreen14::class.java)
            startActivity(intent)
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
        bottomnavprofile.setOnClickListener {
            val intent = Intent(this, socialhomescreen8::class.java)
            startActivity(intent)
            finish()
        }
        val otheruserprofile = findViewById<ImageView>(R.id.mainprofile)
        otheruserprofile.setOnClickListener {
            val intent = Intent(this, socialhomescreen10::class.java)
            startActivity(intent)

        }

    }
}