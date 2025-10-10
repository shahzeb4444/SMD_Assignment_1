package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class second_page : AppCompatActivity() {
    private var passwordvisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_page)
        var createaccount = findViewById<Button>(R.id.createaccountbutton)
        var profilecircle = findViewById<ImageView>(R.id.whitecircle)
        val backicon = findViewById<ImageView>(R.id.leftarrow)
        backicon.setOnClickListener {
            val intent = Intent(this, login_splash::class.java)
            startActivity(intent)
            finish()
        }
        profilecircle.setOnClickListener {
            // Open the default gallery / photos app
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"   // tell system we want to view images
            }
            startActivity(intent)
        }

        val passwordtext = findViewById<EditText>(R.id.passwordtextfield)
        val eyeview = findViewById<ImageView>(R.id.eyeopen)
        eyeview.setOnClickListener {
            passwordvisible = !passwordvisible

            if (passwordvisible) {
                // Show password
                passwordtext.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeview.setImageResource(R.drawable.eye_slash)
            } else {
                // Hide password
                passwordtext.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeview.setImageResource(R.drawable.eye)
            }

            // Move cursor to end after changing inputType
            passwordtext.setSelection(passwordtext.text.length)
        }
        createaccount.setOnClickListener {
            val intent = Intent(this, mainlogin::class.java)
            startActivity(intent)
            finish()
        }

    }
}