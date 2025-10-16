// 3. UPDATED CHAT ACTIVITY WITH MESSAGING
package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class socialhomescreenchat : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()

    private lateinit var chatId: String
    private lateinit var otherUserName: String
    private val currentUserId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    private val currentUsername: String by lazy { FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_socialhomescreenchat)

        // Get chat data from intent
        chatId = intent.getStringExtra("chatId") ?: return
        otherUserName = intent.getStringExtra("otherUserName") ?: "User"

        // Initialize views
        recyclerView = findViewById(R.id.messagesRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Start from bottom
        }
        messageAdapter = MessageAdapter(messagesList, currentUserId) { message, action ->
            handleMessageAction(message, action)
        }
        recyclerView.adapter = messageAdapter

        // Database reference
        database = FirebaseDatabase.getInstance().reference.child("messages").child(chatId)

        // Back button
        val backButton = findViewById<ImageView>(R.id.cameralogo)
        backButton.setOnClickListener {
            val intent = Intent(this, socialhomescreen4::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        // Send button
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }

        // Load messages in real-time
        loadMessagesRealTime()
    }

    private fun sendMessage(text: String) {
        val messageId = database.push().key ?: return
        val timestamp = System.currentTimeMillis()

        val message = Message(
            messageId = messageId,
            senderId = currentUserId,
            senderUsername = currentUsername,
            text = text,
            timestamp = timestamp,
            isEdited = false,
            isDeleted = false
        )

        database.child(messageId).setValue(message)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                android.util.Log.e("SendMessage", "Error: ", exception)
            }
    }

    private fun loadMessagesRealTime() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                messagesList.add(message)
                messageAdapter.notifyItemInserted(messagesList.size - 1)
                recyclerView.scrollToPosition(messagesList.size - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedMessage = snapshot.getValue(Message::class.java) ?: return
                val index = messagesList.indexOfFirst { it.messageId == updatedMessage.messageId }
                if (index != -1) {
                    messagesList[index] = updatedMessage
                    messageAdapter.notifyItemChanged(index)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@socialhomescreenchat, "Error loading messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleMessageAction(message: Message, action: String) {
        when (action) {
            "EDIT" -> if (canEditMessage(message)) editMessage(message)
            "DELETE" -> if (canDeleteMessage(message)) deleteMessage(message)
        }
    }

    private fun canEditMessage(message: Message): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDifference = (currentTime - message.timestamp) / 1000 / 60 // in minutes
        return message.senderId == currentUserId && timeDifference <= 5 && !message.isDeleted
    }

    private fun canDeleteMessage(message: Message): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDifference = (currentTime - message.timestamp) / 1000 / 60 // in minutes
        return message.senderId == currentUserId && timeDifference <= 5 && !message.isDeleted
    }

    private fun editMessage(message: Message) {
        // Show edit dialog
        val editDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        val editText = EditText(this)
        editText.setText(message.text)

        editDialog.setTitle("Edit Message")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty() && newText != message.text) {
                    val updatedMessage = message.copy(
                        text = newText,
                        isEdited = true
                    )
                    database.child(message.messageId).setValue(updatedMessage)
                        .addOnSuccessListener {
                            Toast.makeText(this@socialhomescreenchat, "Message edited", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMessage(message: Message) {
        val updatedMessage = message.copy(
            isDeleted = true,
            text = "[This message was deleted]",
            deletedAt = System.currentTimeMillis()
        )

        database.child(message.messageId).setValue(updatedMessage)
            .addOnSuccessListener {
                Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
            }
    }
}