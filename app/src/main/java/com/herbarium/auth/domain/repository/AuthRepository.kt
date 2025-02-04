package com.herbarium.auth.domain.repository

import com.herbarium.auth.domain.model.User

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): User?
}