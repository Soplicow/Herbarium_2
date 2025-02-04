package com.herbarium.auth.data.dto

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.datetime.Instant

// Supabase Auth responses (simplified)
data class AuthResponse(
    val user: UserInfo?,
    val error: String?
)

data class SupabaseUser(
    val id: String,
    val email: String?,
    val aud: String,
    val lastSignInAt: Instant?
)

// TODO: Implement this