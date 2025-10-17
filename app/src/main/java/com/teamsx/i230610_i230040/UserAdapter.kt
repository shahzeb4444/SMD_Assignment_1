package com.teamsx.i230610_i230040

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of users in socialhomescreen4.
 * Uses UserProfile to fetch username, and UID is stored separately for chat.
 */
class UserAdapter(
    private val users: List<Pair<String, UserProfile>>, // Pair<uid, UserProfile>
    private val onClick: (uid: String, username: String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: RelativeLayout = itemView.findViewById(R.id.r1) // your RelativeLayout in item layout
        val usernameText: TextView = itemView.findViewById(R.id.boldtext1) // username TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_dynamic, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val (uid, profile) = users[position]
        holder.usernameText.text = profile.username

        holder.container.setOnClickListener {
            onClick(uid, profile.username)
        }
    }

    override fun getItemCount(): Int = users.size
}
