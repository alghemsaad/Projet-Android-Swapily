package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _success =
        MutableStateFlow(false)

    val success =
        _success.asStateFlow()

    private val _error =
        MutableStateFlow("")

    val error =
        _error.asStateFlow()

    private val _loading =
        MutableStateFlow(false)

    val loading =
        _loading.asStateFlow()

    private val _registerSuccess =
        MutableStateFlow(false)

    val registerSuccess =
        _registerSuccess.asStateFlow()

    fun resetRegisterSuccess() {
        _registerSuccess.value = false
    }

    fun register(
        name: String,
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _loading.value = true

            val result =
                repository.register(
                    name,
                    email,
                    password
                )

            _loading.value = false

            result.onSuccess {

                _registerSuccess.value = true

            }.onFailure {

                _error.value =
                    it.message ?: "Error"
            }
        }
    }

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _loading.value = true

            val result =
                repository.login(
                    email,
                    password
                )

            _loading.value = false

            result.onSuccess {

                _success.value = true

            }.onFailure {

                _error.value =
                    it.message ?: "Error"
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.signInWithGoogle(idToken)
            _loading.value = false
            result.onSuccess {
                _success.value = true
            }.onFailure {
                _error.value = it.message ?: "Google Sign In Error"
            }
        }
    }

    fun setGoogleError(message: String) {
        _error.value = message
    }

    fun signInWithFacebook(accessToken: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.signInWithFacebook(accessToken)
            _loading.value = false
            result.onSuccess {
                _success.value = true
            }.onFailure {
                _error.value = it.message ?: "Facebook Sign In Error"
            }
        }
    }
}