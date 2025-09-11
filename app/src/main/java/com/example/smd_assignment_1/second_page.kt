package com.example.smd_assignment_1

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class second_page : AppCompatActivity() {
    private var passwordvisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_page)
        var profilecircle = findViewById<ImageView>(R.id.whitecircle)
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

    }
}