// VIDEO CALL ACTIVITY - UPDATED FOR AGORA SDK 4.x
package com.teamsx.i230610_i230040

import android.os.Bundle
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class VideoCallActivity : AppCompatActivity() {

    private lateinit var localVideoView: FrameLayout
    private lateinit var remoteVideoView: FrameLayout
    private lateinit var callDurationText: TextView
    private lateinit var callStatusText: TextView
    private lateinit var controlsLayout: LinearLayout
    private lateinit var endCallButton: ImageView
    private lateinit var toggleAudioButton: ImageView
    private lateinit var toggleVideoButton: ImageView
    private lateinit var switchCameraButton: ImageView

    private var agoraEngine: RtcEngine? = null
    private var channelName: String = ""
    private var appId: String = ""
    private var token: String = ""
    private var otherUserName: String = ""
    private var currentUserId: String = ""

    private var isAudioEnabled = true
    private var isVideoEnabled = true
    private var isCameraFront = true

    private var callStartTime: Long = 0
    private var remoteUid: Int = 0
    private var timerThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.videocall)

        // Get intent data
        channelName = intent.getStringExtra("channelName") ?: return
        appId = intent.getStringExtra("appId") ?: return
        token = intent.getStringExtra("token") ?: ""
        otherUserName = intent.getStringExtra("otherUserName") ?: "User"
        currentUserId = intent.getStringExtra("currentUserId") ?: ""

        // Initialize views
        localVideoView = findViewById(R.id.local_video_view)
        remoteVideoView = findViewById(R.id.remote_video_view)
        callDurationText = findViewById(R.id.call_duration_text)
        callStatusText = findViewById(R.id.call_status_text)
        controlsLayout = findViewById(R.id.controls_layout)
        endCallButton = findViewById(R.id.end_call_button)
        toggleAudioButton = findViewById(R.id.toggle_audio_button)
        toggleVideoButton = findViewById(R.id.toggle_video_button)
        switchCameraButton = findViewById(R.id.switch_camera_button)

        // Setup video call
        setupVideoCall()

        // Setup controls
        setupControls()

        // Start call duration timer
        startCallDurationTimer()

        // Handle back press with AndroidX
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                endVideoCall()
            }
        })
    }

    private fun setupVideoCall() {
        try {
            // Create RtcEngine with config
            val config = RtcEngineConfig()
            config.mContext = this
            config.mAppId = appId
            config.mEventHandler = mtvRtcEngineEventHandler()

            agoraEngine = RtcEngine.create(config)

            // Enable audio and video
            agoraEngine?.enableAudio()
            agoraEngine?.enableVideo()

            // Setup local video - CORRECT METHOD FOR SDK 4.x
            val localSurfaceView = SurfaceView(this)
            localVideoView.addView(localSurfaceView)
            agoraEngine?.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))

            // Start preview
            agoraEngine?.startPreview()

            // Create channel media options
            val options = ChannelMediaOptions()
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            options.autoSubscribeAudio = true
            options.autoSubscribeVideo = true
            options.publishMicrophoneTrack = true
            options.publishCameraTrack = true

            // Join channel - CORRECT METHOD FOR SDK 4.x
            val uid = currentUserId.hashCode()
            val result = agoraEngine?.joinChannel(token, channelName, uid, options)

            if (result != 0) {
                android.util.Log.e("VideoCall", "Join channel failed: $result")
                Toast.makeText(this, "Failed to join channel: $result", Toast.LENGTH_SHORT).show()
            }

            callStatusText.text = "Connecting..."
            callStartTime = System.currentTimeMillis()

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to setup video: ${e.message}", Toast.LENGTH_SHORT).show()
            android.util.Log.e("VideoCall", "Setup error", e)
            e.printStackTrace()
        }
    }

    private fun mtvRtcEngineEventHandler(): IRtcEngineEventHandler {
        return object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                super.onUserJoined(uid, elapsed)
                remoteUid = uid
                android.util.Log.d("VideoCall", "User joined: $uid")
                runOnUiThread {
                    callStatusText.text = "Connected with $otherUserName"
                    setupRemoteVideo(uid)
                    Toast.makeText(this@VideoCallActivity, "$otherUserName joined", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                super.onUserOffline(uid, reason)
                android.util.Log.d("VideoCall", "User offline: $uid")
                if (uid == remoteUid) {
                    runOnUiThread {
                        callStatusText.text = "$otherUserName disconnected"
                        Toast.makeText(this@VideoCallActivity, "$otherUserName left the call", Toast.LENGTH_SHORT).show()
                        remoteVideoView.removeAllViews()
                    }
                }
            }

            override fun onError(err: Int) {
                super.onError(err)
                android.util.Log.e("VideoCall", "Error: $err")
                runOnUiThread {
                    Toast.makeText(this@VideoCallActivity, "Call error: $err", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onConnectionStateChanged(state: Int, reason: Int) {
                super.onConnectionStateChanged(state, reason)
                android.util.Log.d("VideoCall", "Connection state: $state, reason: $reason")
            }
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        try {
            // Remove any existing views
            remoteVideoView.removeAllViews()

            val remoteSurfaceView = SurfaceView(this)
            remoteVideoView.addView(remoteSurfaceView)

            // Setup remote video - CORRECT METHOD FOR SDK 4.x
            agoraEngine?.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))

            android.util.Log.d("RemoteVideo", "Remote video setup for uid: $uid")
        } catch (e: Exception) {
            android.util.Log.e("RemoteVideo", "Error setting up remote video", e)
            e.printStackTrace()
        }
    }

    private fun setupControls() {
        endCallButton.setOnClickListener {
            endVideoCall()
        }

        toggleAudioButton.setOnClickListener {
            isAudioEnabled = !isAudioEnabled
            agoraEngine?.muteLocalAudioStream(!isAudioEnabled)
            toggleAudioButton.setImageResource(
                if (isAudioEnabled) android.R.drawable.ic_btn_speak_now
                else android.R.drawable.ic_menu_close_clear_cancel
            )
            Toast.makeText(this, if (isAudioEnabled) "Audio ON" else "Audio OFF", Toast.LENGTH_SHORT).show()
        }

        toggleVideoButton.setOnClickListener {
            isVideoEnabled = !isVideoEnabled
            agoraEngine?.muteLocalVideoStream(!isVideoEnabled)
            toggleVideoButton.setImageResource(
                if (isVideoEnabled) android.R.drawable.ic_media_play
                else android.R.drawable.ic_media_pause
            )
            Toast.makeText(this, if (isVideoEnabled) "Video ON" else "Video OFF", Toast.LENGTH_SHORT).show()
        }

        switchCameraButton.setOnClickListener {
            isCameraFront = !isCameraFront
            agoraEngine?.switchCamera()
            Toast.makeText(this, "Camera switched", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCallDurationTimer() {
        timerThread = Thread {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    Thread.sleep(1000)
                    val duration = (System.currentTimeMillis() - callStartTime) / 1000
                    val minutes = duration / 60
                    val seconds = duration % 60
                    runOnUiThread {
                        callDurationText.text = String.format("%02d:%02d", minutes, seconds)
                    }
                }
            } catch (e: InterruptedException) {
                android.util.Log.d("Timer", "Timer interrupted")
            }
        }
        timerThread?.start()
    }

    private fun endVideoCall() {
        try {
            // Stop timer
            timerThread?.interrupt()

            // Leave channel
            agoraEngine?.leaveChannel()
            agoraEngine?.stopPreview()

            // Destroy engine
            RtcEngine.destroy()
            agoraEngine = null

            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            android.util.Log.e("EndCall", "Error", e)
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Stop timer
            timerThread?.interrupt()

            // Clean up Agora resources
            agoraEngine?.leaveChannel()
            agoraEngine?.stopPreview()

            // Destroy engine
            Thread.sleep(500)
            RtcEngine.destroy()
            agoraEngine = null
        } catch (e: Exception) {
            android.util.Log.e("VideoCallDestroy", "Error", e)
            e.printStackTrace()
        }
    }
}