package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreenchat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreenchat)
        val backicon = findViewById<ImageView>(R.id.cameralogo)
        backicon.setOnClickListener {

                val intent = Intent(this, socialhomescreen4::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
        }
        val callicon = findViewById<ImageView>(R.id.call)
        callicon.setOnClickListener {
            val intent = Intent(this, socialhomescreen5::class.java)
            startActivity(intent)
        }
    }
}