// ENHANCED CHAT ACTIVITY WITH VOICE & VIDEO CALLS
package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import androidx.activity.OnBackPressedCallback

class socialhomescreenchat : AppCompatActivity() {

    // Chat Views
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var voiceCallButton: ImageView
    private lateinit var videoCallButton: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()

    // Agora Properties
    private var agoraEngine: RtcEngine? = null
    private val AGORA_APP_ID = "3807f3b08fb643758a944aa185607ae0" // Replace with your Agora App ID
    private val AGORA_TEMP_TOKEN = ""

    private var isCallActive = false
    private var callType = "NONE" // VOICE, VIDEO, or NONE
    private var channelName: String = ""

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
        channelName = chatId

        // Initialize views
        recyclerView = findViewById(R.id.messagesRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        voiceCallButton = findViewById(R.id.call) // Reusing for voice call

        // Initialize video call button (add to layout if needed)
        try {
            videoCallButton = findViewById(R.id.videoCall)
        } catch (e: Exception) {
            android.util.Log.d("VideoCallButton", "Video call button not found in layout")
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
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
            if (isCallActive) {
                showEndCallDialog()
            } else {
                val intent = Intent(this, socialhomescreen4::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
        }

        // Send message button
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }

        // Voice call button
        voiceCallButton.setOnClickListener {
            if (!isCallActive) {
                initiateVoiceCall()
            } else if (callType == "VOICE") {
                endCall()
            } else {
                Toast.makeText(this, "Video call in progress", Toast.LENGTH_SHORT).show()
            }
        }

        // Video call button (if exists)
        try {
            videoCallButton.setOnClickListener {
                if (!isCallActive) {
                    initiateVideoCall()
                } else if (callType == "VIDEO") {
                    endCall()
                } else {
                    Toast.makeText(this, "Voice call in progress", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            android.util.Log.d("VideoCallButton", "Video button setup skipped")
        }

        // Load messages
        loadMessagesRealTime()

        // Initialize Agora Engine
        setupAgoraEngine()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isCallActive) {
                    showEndCallDialog()
                } else {
                    val intent = Intent(this@socialhomescreenchat, socialhomescreen4::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    // ============= AGORA SETUP =============
    private fun setupAgoraEngine() {
        try {
            agoraEngine = RtcEngine.create(this, AGORA_APP_ID, mtvRtcEngineEventHandler())
        } catch (e: Exception) {
            Toast.makeText(this, "Agora initialization failed: ${e.message}", Toast.LENGTH_SHORT).show()
            android.util.Log.e("AgoraSetup", "Error", e)
        }
    }

    private fun mtvRtcEngineEventHandler(): IRtcEngineEventHandler {
        return object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                super.onUserJoined(uid, elapsed)
                android.util.Log.d("Agora", "User joined: $uid")
                Toast.makeText(this@socialhomescreenchat, "$otherUserName joined the call", Toast.LENGTH_SHORT).show()
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                super.onUserOffline(uid, reason)
                android.util.Log.d("Agora", "User offline: $uid")
                if (isCallActive) {
                    Toast.makeText(this@socialhomescreenchat, "$otherUserName left the call", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(err: Int) {
                super.onError(err)
                android.util.Log.e("Agora", "Error: $err")
                Toast.makeText(this@socialhomescreenchat, "Call error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ============= VOICE CALL =============
    private fun initiateVoiceCall() {
        if (agoraEngine == null) {
            Toast.makeText(this, "Agora engine not ready", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Disable video for voice call
            agoraEngine?.disableVideo()
            agoraEngine?.enableAudio()

            val options = ChannelMediaOptions()
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            options.autoSubscribeAudio = true
            options.autoSubscribeVideo = false

            val uid = currentUserId.hashCode()
            agoraEngine?.joinChannel(AGORA_TEMP_TOKEN, channelName, uid, options)

            isCallActive = true
            callType = "VOICE"
            voiceCallButton.setImageResource(android.R.drawable.ic_media_pause)

            sendCallNotification("voice")
            Toast.makeText(this, "Voice call started with $otherUserName", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start voice call: ${e.message}", Toast.LENGTH_SHORT).show()
            android.util.Log.e("VoiceCall", "Error", e)
        }
    }

    // ============= VIDEO CALL =============
    private fun initiateVideoCall() {
        if (agoraEngine == null) {
            Toast.makeText(this, "Agora engine not ready", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Enable both audio and video
            agoraEngine?.enableAudio()
            agoraEngine?.enableVideo()

            val options = ChannelMediaOptions()
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            options.autoSubscribeAudio = true
            options.autoSubscribeVideo = true

            val uid = currentUserId.hashCode()
            agoraEngine?.joinChannel(AGORA_TEMP_TOKEN, channelName, uid, options)

            isCallActive = true
            callType = "VIDEO"

            // Start preview and open video call activity
            agoraEngine?.startPreview()
            openVideoCallScreen()

            sendCallNotification("video")
            Toast.makeText(this, "Video call started with $otherUserName", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start video call: ${e.message}", Toast.LENGTH_SHORT).show()
            android.util.Log.e("VideoCall", "Error", e)
        }
    }

    private fun openVideoCallScreen() {
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra("channelName", channelName)
        intent.putExtra("appId", AGORA_APP_ID)
        intent.putExtra("token", AGORA_TEMP_TOKEN)
        intent.putExtra("otherUserName", otherUserName)
        intent.putExtra("currentUserId", currentUserId)
        startActivityForResult(intent, VIDEO_CALL_REQUEST_CODE)
    }

    // ============= END CALL =============
    private fun endCall() {
        try {
            agoraEngine?.leaveChannel()
            agoraEngine?.stopPreview()

            isCallActive = false
            callType = "NONE"
            voiceCallButton.setImageResource(android.R.drawable.ic_media_play)

            Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            android.util.Log.e("EndCall", "Error", e)
        }
    }

    private fun showEndCallDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("End Call")
            .setMessage("A $callType call is in progress. End it before leaving?")
            .setPositiveButton("End Call") { _, _ ->
                endCall()
                val intent = Intent(this, socialhomescreen4::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendCallNotification(callType: String) {
        val callNotification = mapOf(
            "type" to "call_initiated",
            "callType" to callType,
            "from" to currentUserId,
            "fromName" to currentUsername,
            "timestamp" to System.currentTimeMillis(),
            "chatId" to chatId
        )

        database.child("callNotifications").push().setValue(callNotification)
    }

    // ============= MESSAGING FUNCTIONS (UNCHANGED) =============
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
        val timeDifference = (currentTime - message.timestamp) / 1000 / 60
        return message.senderId == currentUserId && timeDifference <= 5 && !message.isDeleted
    }

    private fun canDeleteMessage(message: Message): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDifference = (currentTime - message.timestamp) / 1000 / 60
        return message.senderId == currentUserId && timeDifference <= 5 && !message.isDeleted
    }

    private fun editMessage(message: Message) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_CALL_REQUEST_CODE && resultCode == RESULT_OK) {
            endCall()
        }
    }

    // ============= CLEANUP =============
    override fun onDestroy() {
        super.onDestroy()
        if (isCallActive) {
            endCall()
        }
        RtcEngine.destroy()
        agoraEngine = null
    }

    companion object {
        private const val VIDEO_CALL_REQUEST_CODE = 102
    }
}