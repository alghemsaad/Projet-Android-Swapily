package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.User
import com.swapily.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri

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

    private val _profileUser = MutableStateFlow<User?>(null)
    val profileUser = _profileUser.asStateFlow()

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

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess = _updateSuccess.asStateFlow()

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.updateUserProfile(user)
            _loading.value = false
            result.onSuccess {
                _profileUser.value = user
                _updateSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Error updating profile"
            }
        }
    }

    fun fetchUserProfile() {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                _loading.value = true
                val result = repository.getUserProfile(uid)
                _loading.value = false
                result.onSuccess {
                    _profileUser.value = it
                }.onFailure {
                    _error.value = it.message ?: "Error fetching profile"
                }
            }
        } else {
            _error.value = "User not logged in"
        }
    }

    fun uploadImage(uri: Uri) {
        val uid = repository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                _loading.value = true
                val result = repository.uploadProfileImage(uri, uid)
                _loading.value = false
                result.onSuccess { url ->
                    val updatedUser = _profileUser.value?.copy(image = url)
                    if (updatedUser != null) {
                        updateUserProfile(updatedUser)
                    }
                }.onFailure {
                    _error.value = it.message ?: "Error uploading image"
                }
            }
        }
    }
}