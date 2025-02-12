package com.herbarium.auth.data.source

import com.herbarium.auth.domain.repository.IAuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: Auth
) : IAuthRepository {
    override suspend fun signUp(email: String, password: String): Boolean {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signInWithGoogle(): Boolean {
        return try {
            auth.signInWith(Google)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun signOut(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUser(): UserInfo? {
        return auth.currentUserOrNull()
    }

    fun getCurrentUserUid(): String {
        return auth.currentUserOrNull()?.id?: ""
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUserOrNull()!=null
    }
}