package com.teamsx.i230610_i230040

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class socialhomescreen4 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private val usersList = mutableListOf<Pair<String, UserProfile>>() // Pair<uid, UserProfile>

    // Class-level variable for logged-in user's UID
    private val currentUserId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socialhomescreen4)

        database = FirebaseDatabase.getInstance().reference.child("users")
        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val back = findViewById<ImageView>(R.id.leftarrowlogo)
        back.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra(HomeActivity.EXTRA_START_DEST, R.id.nav_home)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        loadUsers()
    }

    private fun loadUsers() {
        if (currentUserId.isEmpty()) return  // safety check

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (userSnap in snapshot.children) {
                    val uid = userSnap.key ?: continue
                    if (uid == currentUserId) continue // skip self

                    val profile = userSnap.getValue(UserProfile::class.java) ?: continue
                    usersList.add(Pair(uid, profile))
                }

                recyclerView.adapter = UserAdapter(usersList) { clickedUid, username ->
                    // now we can safely use currentUserId and clickedUid
                    val chatId = if (currentUserId < clickedUid) "$currentUserId$clickedUid" else "$clickedUid$currentUserId"
                    val intent = Intent(this@socialhomescreen4, socialhomescreenchat::class.java)
                    intent.putExtra("chatId", chatId)
                    intent.putExtra("otherUserName", username)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
