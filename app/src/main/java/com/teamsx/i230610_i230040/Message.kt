// 1. MESSAGE DATA CLASS
package com.teamsx.i230610_i230040

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val senderUsername: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L
)