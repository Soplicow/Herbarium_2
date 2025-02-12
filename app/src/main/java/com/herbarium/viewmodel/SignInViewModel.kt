package com.herbarium.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.auth.data.source.AuthRepository
import com.herbarium.auth.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: Flow<String> = _email

    private val _password = MutableStateFlow("")
    val password: Flow<String> = _password

    private val _currentUser = mutableStateOf<UserInfo?>(null)
    val currentUser: State<UserInfo?> = _currentUser

    init {
        _currentUser.value = authRepository.getCurrentUser()
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignIn() {
        viewModelScope.launch {
            authRepository.signIn(
                email = _email.value,
                password = _password.value
            )
        }
    }

    fun onGoogleSingIn() {
        viewModelScope.launch {
            authRepository.signInWithGoogle()
        }
    }
}