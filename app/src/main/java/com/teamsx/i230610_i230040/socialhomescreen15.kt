package com.teamsx.i230610_i230040

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
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_home)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
            finish()
        }

        yourstory.setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_home)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
            finish()
        }

        cancelicon.setOnClickListener {
            val intent = Intent(this, socialhomescreen17::class.java)
            startActivity(intent)
            finish()
        }
    }
}
