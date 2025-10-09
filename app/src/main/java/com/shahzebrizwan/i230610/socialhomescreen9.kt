package com.shahzebrizwan.i230610

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen9 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen9)
        var bottomnavsearch = findViewById<ImageView>(R.id.bottomnavsearch)
        var bottomnavhome = findViewById<ImageView>(R.id.bottomnavhome)
        var bottomnavcreate = findViewById<ImageView>(R.id.bottomnavcreate)
        var bottomnavlike = findViewById<ImageView>(R.id.bottomnavlike)
        var bottomnavprofile = findViewById<ImageView>(R.id.bottomnavicon)
        var unfollowbtn = findViewById<ImageView>(R.id.feedbutton1)
        bottomnavhome.setOnClickListener {
            val intent = Intent(this, socialhomescreen2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        bottomnavsearch.setOnClickListener {
            val intent = Intent(this, socialhomescreen2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        bottomnavcreate.setOnClickListener {
            val intent = Intent(this, socialhomescreen17::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
        bottomnavlike.setOnClickListener {
            val intent = Intent(this, socialhomescreen6::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        bottomnavprofile.setOnClickListener {
            val intent = Intent(this, socialhomescreen8::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        unfollowbtn.setOnClickListener {
            val intent = Intent(this, socialhomescreen10::class.java)
            startActivity(intent)
            finish()
        }
        val backarrow = findViewById<ImageView>(R.id.leftarrow)
        backarrow.setOnClickListener {
            val intent = Intent(this, socialhomescreen1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}