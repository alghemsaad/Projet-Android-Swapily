package com.swapily.app.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swapily.app.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun uploadProductImages(uris: List<Uri>): Result<List<String>> {
        val uploadedUrls = mutableListOf<String>()
        
        for (uri in uris) {
            val result = uploadToCloudinary(uri)
            if (result.isSuccess) {
                result.getOrNull()?.let { uploadedUrls.add(it) }
            } else {
                return Result.failure(result.exceptionOrNull() ?: Exception("Upload failed"))
            }
        }
        return Result.success(uploadedUrls)
    }

    private suspend fun uploadToCloudinary(uri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(uri)
                .option("folder", "products")
                .option("upload_preset", "swapily_preset")
                .option("unsigned", true)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        continuation.resume(if (url != null) Result.success(url) else Result.failure(Exception("URL null")))
                    }
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                }).dispatch()
        }
    }

    suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            val docRef = firestore.collection("products").document()
            val productWithId = product.copy(id = docRef.id)
            docRef.set(productWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            firestore.collection("products").document(product.id).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            firestore.collection("products").document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markProductAsSwapped(productId: String): Result<Unit> {
        return try {
            firestore.collection("products").document(productId).update("isAvailable", false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
