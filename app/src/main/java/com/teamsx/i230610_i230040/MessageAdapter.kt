// 4. MESSAGE ADAPTER
package com.teamsx.i230610_i230040

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import android.widget.FrameLayout

class MessageAdapter(
    private val messages: List<Message>,
    private val currentUserId: String,
    private val onMessageAction: (Message, String) -> Unit
) : RecyclerView.Adapter<MessageViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(if (viewType == 1) R.layout.item_message_sent else R.layout.item_message_received, parent, false)
        return MessageViewHolder(view, onMessageAction)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size
}

class MessageViewHolder(itemView: android.view.View, private val onMessageAction: (Message, String) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private val messageText: MaterialTextView = itemView.findViewById(R.id.messageText)
    private val timestampText: MaterialTextView = itemView.findViewById(R.id.timestampText)
    private val messageContainer: FrameLayout = itemView.findViewById(R.id.messageContainer)

    fun bind(message: Message) {
        messageText.text = if (message.isDeleted) message.text else message.text

        val editedLabel = if (message.isEdited) " (edited)" else ""
        timestampText.text = formatTime(message.timestamp) + editedLabel

        messageContainer.setOnLongClickListener {
            val currentTime = System.currentTimeMillis()
            val timeDifference = (currentTime - message.timestamp) / 1000 / 60 // minutes

            if (timeDifference <= 5 && !message.isDeleted) {
                showMessageMenu(it, message)
                true
            } else {
                false
            }
        }
    }

    private fun showMessageMenu(view: android.view.View, message: Message) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.message_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_message -> {
                    onMessageAction(message, "EDIT")
                    true
                }
                R.id.delete_message -> {
                    onMessageAction(message, "DELETE")
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}