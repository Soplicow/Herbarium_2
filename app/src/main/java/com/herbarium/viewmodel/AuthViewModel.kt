package com.herbarium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.auth.data.source.AuthRepository
import com.herbarium.auth.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

//    fun signIn(email: String, password: String) {
//        viewModelScope.launch {
//            _authState.value = AuthState.Loading
//            authRepository.signIn(email, password)
//                .onSuccess { user ->
//                    _authState.value = AuthState.Success(user)
//                }
//                .onFailure { e ->
//                    _authState.value = AuthState.Error(e.message ?: "Unknown error")
//                }
//        }
//    }

    // Similar logic for signUp and signOut
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}