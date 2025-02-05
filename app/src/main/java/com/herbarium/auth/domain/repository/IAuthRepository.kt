package com.herbarium.auth.domain.repository

import com.herbarium.auth.domain.model.User

interface IAuthRepository {
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signInWithGoogle(): Boolean
    suspend fun signOut(): Boolean
}