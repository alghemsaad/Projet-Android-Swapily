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

    /**
     * Returns ALL products (including swapped) as a real-time Flow.
     * Consumers filter by status as needed.
     */
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

    /**
     * Returns only products with status == "available" in real-time.
     * Used by Discover/Home.
     */
    fun getAvailableProducts(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("status", "available")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java)
                    ?.sortedByDescending { it.timestamp }
                    ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Returns current user's available products in real-time.
     * Used by My Products section.
     */
    fun getMyAvailableProducts(userId: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "available")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java)
                    ?.sortedByDescending { it.timestamp }
                    ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Returns any user's available products in real-time.
     * Used by Public/View User profile.
     */
    fun getUserAvailableProducts(userId: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "available")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java)
                    ?.sortedByDescending { it.timestamp }
                    ?: emptyList()
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
            firestore.collection("products").document(productId).update(
                mapOf(
                    "isAvailable" to false,
                    "status" to "swapped"
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSmartMatches(currentUserId: String): List<com.swapily.app.data.model.MatchResult> {
        return try {
            val allProductsSnapshot = firestore.collection("products")
                .get().await()
            val allProducts = allProductsSnapshot.toObjects(Product::class.java)
                .filter { it.status == "available" } // Only available products in smart matches

            val myProducts = allProducts.filter { it.userId == currentUserId }
            val otherProducts = allProducts.filter { it.userId != currentUserId }

            val matches = mutableListOf<com.swapily.app.data.model.MatchResult>()

            for (myProduct in myProducts) {
                for (otherProduct in otherProducts) {
                    var score = 0
                    
                    val myLookingFor = myProduct.lookingFor.lowercase().trim()
                    val otherTitle = otherProduct.title.lowercase().trim()
                    val otherLookingFor = otherProduct.lookingFor.lowercase().trim()
                    val myTitle = myProduct.title.lowercase().trim()

                    // 1. Perfect Cross Match
                    val myWantsHim = (myLookingFor.isNotEmpty() && otherTitle.contains(myLookingFor)) || 
                                     (myProduct.wantedCategory.isNotEmpty() && myProduct.wantedCategory.equals(otherProduct.category, ignoreCase = true))
                    
                    val heWantsMe = (otherLookingFor.isNotEmpty() && myTitle.contains(otherLookingFor)) ||
                                     (otherProduct.wantedCategory.isNotEmpty() && otherProduct.wantedCategory.equals(myProduct.category, ignoreCase = true))

                    if (myWantsHim && heWantsMe) {
                        score = 100
                    } else if (myWantsHim) {
                        score = 80
                    } else if (heWantsMe) {
                        score = 60
                    } else if (myProduct.category.equals(otherProduct.category, ignoreCase = true)) {
                        score = 40
                    } else {
                        score = (20..35).random() // Always give a small score to show SOMETHING
                    }

                    // Fetch owner name
                    val ownerDoc = firestore.collection("users").document(otherProduct.userId).get().await()
                    val ownerName = ownerDoc.getString("name") ?: "User"
                    
                    matches.add(
                        com.swapily.app.data.model.MatchResult(
                            myProduct = myProduct,
                            otherProduct = otherProduct,
                            score = score,
                            otherUserId = otherProduct.userId,
                            otherUserName = ownerName
                        )
                    )
                }
            }
            
            // If I have NO products, just suggest some random available products as "Discovery" matches
            if (myProducts.isEmpty() && otherProducts.isNotEmpty()) {
                otherProducts.take(5).forEach { otherProduct ->
                    val ownerDoc = firestore.collection("users").document(otherProduct.userId).get().await()
                    matches.add(
                        com.swapily.app.data.model.MatchResult(
                            myProduct = Product(title = "Your future item"), // Placeholder
                            otherProduct = otherProduct,
                            score = (30..50).random(),
                            otherUserId = otherProduct.userId,
                            otherUserName = ownerDoc.getString("name") ?: "User"
                        )
                    )
                }
            }

            matches.sortedByDescending { it.score }.distinctBy { it.otherProduct.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
