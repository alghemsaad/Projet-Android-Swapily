package com.swapily.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swapily.app.data.model.AppNotification
import com.swapily.app.data.model.Product
import com.swapily.app.data.model.Swap
import com.swapily.app.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // ── Dashboard Stats ───────────────────────────────────────────────

    suspend fun getTotalUsersCount(): Int {
        return try {
            firestore.collection("users").get().await().size()
        } catch (e: Exception) { 0 }
    }

    suspend fun getTotalProductsCount(): Int {
        return try {
            firestore.collection("products").get().await().size()
        } catch (e: Exception) { 0 }
    }

    suspend fun getTotalSwapsCount(): Int {
        return try {
            firestore.collection("swaps").get().await().size()
        } catch (e: Exception) { 0 }
    }

    suspend fun getPendingSwapsCount(): Int {
        return try {
            firestore.collection("swaps")
                .whereEqualTo("status", "PENDING")
                .get().await().size()
        } catch (e: Exception) { 0 }
    }

    suspend fun getTotalReportsCount(): Int {
        return try {
            firestore.collection("reports").get().await().size()
        } catch (e: Exception) { 0 }
    }

    data class RecentActivity(
        val type: String,
        val description: String,
        val timestamp: Long
    )

    suspend fun getRecentActivities(): List<RecentActivity> {
        return try {
            val activities = mutableListOf<RecentActivity>()

            // Recent users
            val recentUsers = firestore.collection("users")
                .orderBy("uid", Query.Direction.DESCENDING)
                .limit(3).get().await()
            for (doc in recentUsers) {
                val user = doc.toObject(User::class.java)
                activities.add(RecentActivity("user", "New user: ${user.name}", System.currentTimeMillis()))
            }

            // Recent swaps
            val recentSwaps = firestore.collection("swaps")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3).get().await()
            for (doc in recentSwaps) {
                val swap = doc.toObject(Swap::class.java)
                activities.add(RecentActivity("swap", "Swap ${swap.status.lowercase()} by ${swap.senderName}", swap.timestamp))
            }

            // Recent products
            val recentProducts = firestore.collection("products")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3).get().await()
            for (doc in recentProducts) {
                val product = doc.toObject(Product::class.java)
                activities.add(RecentActivity("product", "New product: ${product.title}", product.timestamp))
            }

            activities.sortedByDescending { it.timestamp }.take(10)
        } catch (e: Exception) { emptyList() }
    }

    // ── Users Management ──────────────────────────────────────────────

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val users = snapshot?.toObjects(User::class.java) ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    suspend fun blockUser(userId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("isBlocked", true).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun unblockUser(userId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("isBlocked", false).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserProductCount(userId: String): Int {
        return try {
            firestore.collection("products")
                .whereEqualTo("userId", userId)
                .get().await().size()
        } catch (e: Exception) { 0 }
    }

    suspend fun getUserSwapCount(userId: String): Int {
        return try {
            val sender = firestore.collection("swaps")
                .whereEqualTo("senderId", userId).get().await().size()
            val receiver = firestore.collection("swaps")
                .whereEqualTo("receiverId", userId).get().await().size()
            sender + receiver
        } catch (e: Exception) { 0 }
    }

    // ── Products Management ───────────────────────────────────────────

    fun getAllProductsAdmin(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    suspend fun deleteProductAdmin(productId: String): Result<Unit> {
        return try {
            firestore.collection("products").document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun archiveProduct(productId: String): Result<Unit> {
        return try {
            firestore.collection("products").document(productId)
                .update("status", "archived").await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // ── Swaps Management ──────────────────────────────────────────────

    fun getAllSwapsAdmin(): Flow<List<Swap>> = callbackFlow {
        val listener = firestore.collection("swaps")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val swaps = snapshot?.toObjects(Swap::class.java) ?: emptyList()
                trySend(swaps)
            }
        awaitClose { listener.remove() }
    }

    // ── Notifications ─────────────────────────────────────────────────

    suspend fun sendNotificationToAll(title: String, message: String): Result<Unit> {
        return try {
            val notification = hashMapOf(
                "title" to title,
                "message" to message,
                "targetType" to "ALL",
                "targetUserId" to "",
                "timestamp" to System.currentTimeMillis(),
                "sentBy" to "ADMIN"
            )
            firestore.collection("notifications").add(notification).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun sendNotificationToUser(userId: String, title: String, message: String): Result<Unit> {
        return try {
            val notification = hashMapOf(
                "title" to title,
                "message" to message,
                "targetType" to "USER",
                "targetUserId" to userId,
                "timestamp" to System.currentTimeMillis(),
                "sentBy" to "ADMIN"
            )
            firestore.collection("notifications").add(notification).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    // ── User-facing Notifications ─────────────────────────────────────

    fun getUserNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = firestore.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val all = snapshot?.toObjects(AppNotification::class.java) ?: emptyList()
                // Show notifications targeted to ALL or specifically to this user
                val filtered = all.filter {
                    it.targetType == "ALL" || it.targetUserId == userId
                }
                trySend(filtered)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            firestore.collection("notifications").document(notificationId)
                .update("read", true).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUnreadNotificationCount(userId: String): Int {
        return try {
            val all = firestore.collection("notifications").get().await()
                .toObjects(AppNotification::class.java)
            all.count { !it.read && (it.targetType == "ALL" || it.targetUserId == userId) }
        } catch (e: Exception) { 0 }
    }
}
