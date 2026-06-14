package com.swapily.app.data.repository

import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.swapily.app.data.model.User
import kotlinx.coroutines.tasks.await
import android.net.Uri
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google sign in failed")
            val uid = firebaseUser.uid

            // Vérifier si l'utilisateur existe déjà dans Firestore, sinon le créer
            val userDoc = firestore.collection("users").document(uid).get().await()
            if (!userDoc.exists()) {
                val user = User(
                    uid = uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    image = firebaseUser.photoUrl?.toString() ?: ""
                )
                firestore.collection("users").document(uid).set(user).await()
            }

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithFacebook(accessToken: String): Result<String> {
        return try {
            val credential = FacebookAuthProvider.getCredential(accessToken)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Facebook sign in failed")
            val uid = firebaseUser.uid

            // Vérifier si l'utilisateur existe déjà dans Firestore, sinon le créer
            val userDoc = firestore.collection("users").document(uid).get().await()
            if (!userDoc.exists()) {
                val user = User(
                    uid = uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    image = firebaseUser.photoUrl?.toString() ?: ""
                )
                firestore.collection("users").document(uid).set(user).await()
            }

            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<String> {

        return try {

            val result = auth
                .createUserWithEmailAndPassword(
                    email,
                    password
                ).await()

            val uid = result.user?.uid ?: ""

            val user = User(
                uid = uid,
                name = name,
                email = email,
                image = ""
            )

            firestore
                .collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(uid)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {

        return try {

            val result = auth
                .signInWithEmailAndPassword(
                    email,
                    password
                ).await()

            Result.success(
                result.user?.uid ?: ""
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            val user = userDoc.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFcmToken(uid: String, token: String): Result<Unit> {
        return try {
            firestore.collection("users").document(uid).update("fcmToken", token).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(uri: Uri, uid: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                MediaManager.get().upload(uri)
                    .option("folder", "profile_images")
                    // On ne force pas le public_id pour que Cloudinary génère une URL unique à chaque fois
                    .option("upload_preset", "swapily_preset")
                    .option("unsigned", true)
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {}
                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val url = resultData["secure_url"] as? String
                            if (url != null) {
                                if (continuation.isActive) continuation.resume(Result.success(url))
                            } else {
                                if (continuation.isActive) continuation.resume(Result.failure(Exception("Cloudinary URL missing")))
                            }
                        }
                        override fun onError(requestId: String, error: ErrorInfo) {
                            val errorMsg = error.description ?: "Unknown Cloudinary Error"
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Cloudinary: $errorMsg")))
                        }
                        override fun onReschedule(requestId: String, error: ErrorInfo) {}
                    }).dispatch()
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
        }
    }

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserReviews(userId: String): Result<List<com.swapily.app.data.model.Review>> {
        return try {
            val snapshot = firestore.collection("reviews")
                .whereEqualTo("toUserId", userId)
                .get().await()
            val reviews = snapshot.toObjects(com.swapily.app.data.model.Review::class.java)
            val sortedReviews = reviews.sortedByDescending { it.timestamp }
            Result.success(sortedReviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}