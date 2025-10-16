package com.teamsx.i230610_i230040

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OtherUserProfileFragment : Fragment() {

    companion object {
        const val ARG_USER_ID = "user_id"
        const val ARG_USERNAME = "username"

        fun newInstance(userId: String, username: String): OtherUserProfileFragment {
            val fragment = OtherUserProfileFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    private val db by lazy { FirebaseDatabase.getInstance().reference }

    private lateinit var profileImageView: ShapeableImageView
    private lateinit var usernameTextView: TextView
    private lateinit var fullNameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var postsCountTextView: TextView
    private lateinit var followersCountTextView: TextView
    private lateinit var followingCountTextView: TextView
    private lateinit var followButton: ImageView
    private lateinit var backArrow: ImageView

    private var userId: String? = null
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_USER_ID)
            username = it.getString(ARG_USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_other_user_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        profileImageView = view.findViewById(R.id.profileicon)
        usernameTextView = view.findViewById(R.id.jacobtext)
        fullNameTextView = view.findViewById(R.id.profileusername)
        bioTextView = view.findViewById(R.id.d1)
        postsCountTextView = view.findViewById(R.id.noposts)
        followersCountTextView = view.findViewById(R.id.nofollowers)
        followingCountTextView = view.findViewById(R.id.nofollowing)
        followButton = view.findViewById(R.id.feedbutton1)
        backArrow = view.findViewById(R.id.leftarrow)

        // Back button navigation
        backArrow.setOnClickListener {
            // Navigate back or to home
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Follow button click
        followButton.setOnClickListener {
            // TODO: Implement follow/unfollow functionality
            Toast.makeText(requireContext(), "Follow functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        // Story highlights listeners
        listOf(
            view.findViewById<ImageView>(R.id.circle2),
            view.findViewById<ImageView>(R.id.circle2_2),
            view.findViewById<ImageView>(R.id.circle3),
            view.findViewById<ImageView>(R.id.circle_4),
            view.findViewById<ImageView>(R.id.circle4)
        ).forEach { imageView ->
            imageView?.setOnClickListener {
                Toast.makeText(requireContext(), "Story highlights coming soon", Toast.LENGTH_SHORT).show()
            }
        }

        // Load user profile
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = userId
        if (uid == null) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        db.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                if (profile != null) {
                    displayProfile(profile)
                } else {
                    Toast.makeText(requireContext(), "Profile not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load profile: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayProfile(profile: UserProfile) {
        // Set username
        usernameTextView.text = profile.username

        // Set full name
        val fullName = "${profile.firstName} ${profile.lastName}".trim()
        fullNameTextView.text = if (fullName.isNotEmpty()) fullName else "User"

        // Set bio
        bioTextView.text = if (profile.bio.isNotEmpty()) profile.bio else "No bio yet"

        // Set counts
        postsCountTextView.text = profile.postsCount.toString()
        followersCountTextView.text = formatCount(profile.followersCount)
        followingCountTextView.text = profile.followingCount.toString()

        // Load profile photo using ImageUtils
        if (!profile.photoBase64.isNullOrEmpty()) {
            val bitmap = ImageUtils.loadBase64ImageOptimized(profile.photoBase64, 300)
            if (bitmap != null) {
                val circularBitmap = ImageUtils.getCircularBitmap(bitmap, 300)
                profileImageView.setImageBitmap(circularBitmap)
            } else {
                setDefaultProfileImage()
            }
        } else {
            setDefaultProfileImage()
        }
    }

    private fun setDefaultProfileImage() {
        profileImageView.setImageResource(R.drawable.profile_login_splash)
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1000000 -> String.format("%.1fM", count / 1000000.0)
            count >= 1000 -> String.format("%.1fK", count / 1000.0)
            else -> count.toString()
        }
    }
}