package com.herbarium.auth.domain.repository

interface IAuthRepository {
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signInWithGoogle(): Boolean
    suspend fun signOut(): Boolean
}