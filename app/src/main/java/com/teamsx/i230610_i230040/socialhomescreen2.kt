package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class socialhomescreen2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreen2)
        var bottomnavhome = findViewById<ImageView>(R.id.bottomnavhome)
        var bottomnavsearch = findViewById<ImageView>(R.id.bottomnavsearch)
        var bottomnavcreate = findViewById<ImageView>(R.id.bottomnavcreate)
        var bottomnavlike = findViewById<ImageView>(R.id.bottomnavlike)
        var bottomnavprofile = findViewById<ImageView>(R.id.bottomnavicon)
        bottomnavhome.setOnClickListener {
            val Intent = Intent(this, socialhomescreen1::class.java)
            startActivity(Intent)
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
        val searchField = findViewById<EditText>(R.id.searchfield)

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { // user pressed enter/done
                if (searchField.text.toString().trim().equals("internshala", ignoreCase = true)) {
                    startActivity(Intent(this, socialhomescreen3::class.java))
                }
                true // consume
            } else {
                false
            }


        }
    }
}