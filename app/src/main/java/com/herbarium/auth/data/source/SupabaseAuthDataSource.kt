package com.herbarium.auth.data.source

import com.herbarium.auth.data.dto.AuthResponse
import com.herbarium.auth.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo

class SupabaseAuthDataSource(
    private val supabaseClient: SupabaseClient
) {
    suspend fun signUp(email: String, password: String): AuthResponse {
        return try {
            val result = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResponse(result, error = null)
        } catch (e: Exception) {
            AuthResponse(user = null, error = e.message)
        }
    }

    suspend fun signIn(email: String, password: String): AuthResponse {
        return try {
            val result = supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResponse(supabaseClient.auth.currentUserOrNull(), error = null)
        } catch (e: Exception) {
            AuthResponse(user = null, error = e.message)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): User? {
        return supabaseClient.auth.currentUserOrNull()?.toDomain()
    }

    // Convert SupabaseUser to domain User
    private fun UserInfo.toDomain(): User {
        return User(id = id, email = email.toString(), createdAt = createdAt.toString())
    }
}