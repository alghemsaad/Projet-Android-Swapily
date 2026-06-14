package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.User
import com.swapily.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import kotlinx.coroutines.tasks.await
import com.swapily.app.data.model.Review
import com.google.firebase.messaging.FirebaseMessaging

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

    fun updateFcmToken() {
        val uid = repository.getCurrentUserUid() ?: return
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                viewModelScope.launch {
                    repository.updateFcmToken(uid, token)
                }
            }
        }
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
                updateFcmToken()
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
                updateFcmToken()
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
                updateFcmToken()
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
                updateFcmToken()
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

    suspend fun getOtherUserProfile(uid: String): User? {
        return repository.getUserProfile(uid).getOrNull()
    }

    fun submitReview(toUserId: String, rating: Int, comment: String) {
        val currentUserId = repository.getCurrentUserUid() ?: return
        val currentUserName = _profileUser.value?.name ?: "Anonymous"
        
        viewModelScope.launch {
            _loading.value = true
            val review = com.swapily.app.data.model.Review(
                fromUserId = currentUserId,
                fromUserName = currentUserName,
                toUserId = toUserId,
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            
            // Add review to a "reviews" collection
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            try {
                firestore.collection("reviews").add(review).await()
                
                // Update target user's rating (simple logic for now)
                val targetUserDoc = firestore.collection("users").document(toUserId).get().await()
                val targetUser = targetUserDoc.toObject(User::class.java)
                if (targetUser != null) {
                    val newReviewsCount = targetUser.reviewsCount + 1
                    val newRating = (targetUser.rating * targetUser.reviewsCount + rating) / newReviewsCount
                    val updatedTargetUser = targetUser.copy(
                        reviewsCount = newReviewsCount,
                        rating = newRating
                    )
                    firestore.collection("users").document(toUserId).set(updatedTargetUser).await()
                }
                
                _success.value = true // Reuse success for review submission
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to submit review"
            } finally {
                _loading.value = false
            }
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

    fun logout() {
        repository.logout()
        _profileUser.value = null
        _success.value = false
    }

    suspend fun fetchUserReviews(userId: String): List<Review> {
        return repository.getUserReviews(userId).getOrDefault(emptyList())
    }
}