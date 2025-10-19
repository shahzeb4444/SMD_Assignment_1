package com.teamsx.i230610_i230040

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    private lateinit var storiesRecyclerView: RecyclerView
    private lateinit var storiesAdapter: StoriesAdapter
    private val storyGroups = mutableListOf<StoryGroup>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupExistingListeners(view)
        setupStoriesRecyclerView(view)
        loadStories()
    }

    override fun onResume() {
        super.onResume()
        // Reload stories when returning to fragment
        loadStories()
    }

    private fun setupExistingListeners(view: View) {
        val messages = view.findViewById<ImageView>(R.id.messangerlogo)
        val cameraicon = view.findViewById<ImageView>(R.id.cameralogo)
        val otheruserprofile = view.findViewById<ImageView>(R.id.mainprofile)

        cameraicon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply { type = "image/*" }
            startActivity(intent)
        }

        messages.setOnClickListener {
            startActivity(Intent(requireContext(), socialhomescreen4::class.java))
        }

        otheruserprofile.setOnClickListener {
            navigateToUserProfile("demo_user_id", "joshua_l")
        }
    }

    private fun setupStoriesRecyclerView(view: View) {
        storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView)
        storiesRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val currentUserId = auth.currentUser?.uid ?: ""

        storiesAdapter = StoriesAdapter(
            stories = storyGroups,
            currentUserId = currentUserId,
            onStoryClick = { storyGroup ->
                openStoryViewer(storyGroup)
            },
            onAddStoryClick = {
                openCreateStory()
            }
        )

        storiesRecyclerView.adapter = storiesAdapter
    }

    private fun loadStories() {
        val currentUserId = auth.currentUser?.uid ?: return

        // Get list of users current user is following
        db.child("follows").child(currentUserId).child("following")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(followingSnapshot: DataSnapshot) {
                    val followingIds = followingSnapshot.children.mapNotNull { it.key }.toMutableList()

                    // Add current user to the list to show their own stories
                    followingIds.add(0, currentUserId)

                    loadStoriesForUsers(followingIds, currentUserId)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error silently or show a message
                }
            })
    }

    private fun loadStoriesForUsers(userIds: List<String>, currentUserId: String) {
        storyGroups.clear()
        storiesAdapter.notifyDataSetChanged() // Clear existing items immediately

        val tempStoryGroups = mutableListOf<StoryGroup>()
        val storiesRef = db.child("stories")
        var processedCount = 0

        if (userIds.isEmpty()) {
            return
        }

        for (userId in userIds) {
            storiesRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userStories = mutableListOf<Story>()

                    for (storySnap in snapshot.children) {
                        val story = storySnap.getValue(Story::class.java)
                        if (story != null && story.isValid()) {
                            userStories.add(story)
                        }
                    }

                    // Sort stories by timestamp (oldest first)
                    userStories.sortBy { it.timestamp }

                    // Check if user has unviewed stories
                    val hasUnviewed = userStories.any { !it.isViewedBy(currentUserId) }

                    // Get user info
                    db.child("users").child(userId).get()
                        .addOnSuccessListener { userSnapshot ->
                            val userProfile = userSnapshot.getValue(UserProfile::class.java)

                            // Always add current user (even with no stories)
                            if (userId == currentUserId) {
                                val storyGroup = StoryGroup(
                                    userId = userId,
                                    username = userProfile?.username ?: "You",
                                    userPhotoBase64 = userProfile?.photoBase64,
                                    stories = userStories,
                                    hasUnviewedStories = false
                                )
                                synchronized(tempStoryGroups) {
                                    // Remove any existing entry for this user (prevent duplicates)
                                    tempStoryGroups.removeAll { it.userId == userId }
                                    tempStoryGroups.add(storyGroup)
                                }
                            } else if (userStories.isNotEmpty()) {
                                // Only add other users if they have stories
                                val storyGroup = StoryGroup(
                                    userId = userId,
                                    username = userProfile?.username ?: "User",
                                    userPhotoBase64 = userProfile?.photoBase64,
                                    stories = userStories,
                                    hasUnviewedStories = hasUnviewed
                                )
                                synchronized(tempStoryGroups) {
                                    // Remove any existing entry for this user (prevent duplicates)
                                    tempStoryGroups.removeAll { it.userId == userId }
                                    tempStoryGroups.add(storyGroup)
                                }
                            }

                            processedCount++
                            if (processedCount == userIds.size) {
                                // All users processed, update adapter ONCE
                                updateStoriesUI(tempStoryGroups, currentUserId)
                            }
                        }
                        .addOnFailureListener {
                            processedCount++
                            if (processedCount == userIds.size) {
                                updateStoriesUI(tempStoryGroups, currentUserId)
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    processedCount++
                    if (processedCount == userIds.size) {
                        updateStoriesUI(tempStoryGroups, currentUserId)
                    }
                }
            })
        }
    }

    private fun updateStoriesUI(tempStoryGroups: List<StoryGroup>, currentUserId: String) {
        // Sort: Current user first, then unviewed stories, then viewed stories
        val sorted = tempStoryGroups.sortedWith(compareBy(
            { it.userId != currentUserId }, // Current user first
            { !it.hasUnviewedStories },     // Unviewed stories next
            { it.username }                  // Then alphabetically
        ))

        storyGroups.clear()
        storyGroups.addAll(sorted)
        storiesAdapter.notifyDataSetChanged()
    }

    private fun openStoryViewer(storyGroup: StoryGroup) {
        val currentUserId = auth.currentUser?.uid ?: return

        if (storyGroup.userId == currentUserId) {
            // Open own story viewer
            val intent = Intent(requireContext(), socialhomescreen14::class.java).apply {
                putExtra("user_id", storyGroup.userId)
                putExtra("username", storyGroup.username)
            }
            startActivity(intent)
        } else {
            // Open other user's story viewer
            val intent = Intent(requireContext(), socialhomescreen12::class.java).apply {
                putExtra("user_id", storyGroup.userId)
                putExtra("username", storyGroup.username)
                putExtra("start_index", 0)
            }
            startActivity(intent)
        }
    }

    private fun openCreateStory() {
        startActivity(Intent(requireContext(), socialhomescreen15::class.java))
    }

    private fun navigateToUserProfile(userId: String, username: String) {
        val bundle = Bundle().apply {
            putString("user_id", userId)
            putString("username", username)
        }
        findNavController().navigate(R.id.other_user_profile, bundle)
    }
}