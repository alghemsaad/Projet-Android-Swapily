package com.swapily.app.data.repository

import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.swapily.app.data.model.User
import kotlinx.coroutines.tasks.await

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
}