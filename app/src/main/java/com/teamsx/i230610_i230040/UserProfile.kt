package com.teamsx.i230610_i230040

/**
 * User profile shape stored under /users/{uid} in Realtime Database.
 * Defaults are required so Firebase can deserialize it.
 */
data class UserProfile(
    val username: String = "",          // store LOWERCASE
    val firstName: String = "",
    val lastName: String = "",
    val dob: String = "",               // e.g., "2003-05-12" (string is fine)
    val email: String = "",
    val photoBase64: String? = null,    // optional; null if no photo picked
    val isProfileComplete: Boolean = true
) {
    /** Use for updateChildren() fan-out writes. */
    fun toMap(): Map<String, Any?> = mapOf(
        "username" to username,
        "firstName" to firstName,
        "lastName" to lastName,
        "dob" to dob,
        "email" to email,
        "photoBase64" to photoBase64,
        "isProfileComplete" to isProfileComplete
    )

    companion object {
        /** Normalize before saving so usernames are unique and searchable. */
        fun normalizeUsername(raw: String) = raw.trim().lowercase()
    }
}
