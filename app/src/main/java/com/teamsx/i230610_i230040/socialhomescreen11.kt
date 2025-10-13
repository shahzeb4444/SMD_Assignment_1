package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen11 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen11)

        val canceltext = findViewById<TextView>(R.id.canceltext)
        val switchprofile = findViewById<TextView>(R.id.changeprofilephoto)
        val donetext = findViewById<TextView>(R.id.donetext)

        donetext.setOnClickListener {
            // Go back to Profile tab in HomeActivity
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_profile)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
            finish()
        }

        switchprofile.setOnClickListener {
            // Keep existing behavior: open the change-profile-photo screen
            startActivity(Intent(this, socialhomescreen16::class.java))
        }

        canceltext.setOnClickListener {
            // Also return to Profile tab
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_profile)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
            finish()
        }
    }
}
