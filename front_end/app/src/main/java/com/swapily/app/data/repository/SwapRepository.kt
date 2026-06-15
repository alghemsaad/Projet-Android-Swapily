package com.swapily.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swapily.app.data.model.Swap
import com.swapily.app.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SwapRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun proposeSwap(swap: Swap): Result<String> {
        return try {
            val docRef = firestore.collection("swaps").document()
            val swapWithId = swap.copy(id = docRef.id)
            docRef.set(swapWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllUserSwaps(userId: String): Flow<List<Swap>> = callbackFlow {
        val listener = firestore.collection("swaps")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val swaps = snapshot?.toObjects(Swap::class.java) ?: emptyList()
                val userSwaps = swaps.filter { it.senderId == userId || it.receiverId == userId }
                trySend(userSwaps.sortedByDescending { it.timestamp })
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateSwapStatus(swapId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("swaps").document(swapId).update(
                "status", status,
                "read", true
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Accepts a swap atomically:
     * 1. Updates swaps/{swapId}.status = "ACCEPTED"
     * 2. Sets acceptedAt = current timestamp
     * 3. Deletes both products from Firestore (swap history retains titles/images)
     *
     * Uses Firestore batch write to ensure all-or-nothing consistency.
     */
    suspend fun acceptSwapRequest(
        swapId: String,
        senderProductId: String,
        receiverProductId: String
    ): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val now = System.currentTimeMillis()

            // 1. Update swap document
            val swapRef = firestore.collection("swaps").document(swapId)
            batch.update(swapRef, "status", "ACCEPTED", "acceptedAt", now, "read", true)

            // 2. Delete sender product
            val senderProductRef = firestore.collection("products").document(senderProductId)
            batch.delete(senderProductRef)

            // 3. Delete receiver product
            val receiverProductRef = firestore.collection("products").document(receiverProductId)
            batch.delete(receiverProductRef)

            // Commit all writes atomically
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Returns accepted swaps for a user as a real-time Flow.
     * Used by Swaps History / Archived Swaps.
     */
    fun getSwapHistory(userId: String): Flow<List<Swap>> = callbackFlow {
        val listener = firestore.collection("swaps")
            .whereEqualTo("status", "ACCEPTED")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val swaps = snapshot?.toObjects(Swap::class.java) ?: emptyList()
                val userSwaps = swaps.filter { it.senderId == userId || it.receiverId == userId }
                trySend(userSwaps.sortedByDescending { it.acceptedAt })
            }
        awaitClose { listener.remove() }
    }

    fun getSwapMessages(swapId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("swaps").document(swapId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(swapId: String, message: Message): Result<Unit> {
        return try {
            val docRef = firestore.collection("swaps").document(swapId).collection("messages").document()
            val msgWithId = message.copy(id = docRef.id)
            docRef.set(msgWithId).await()
            
            firestore.collection("swaps").document(swapId).update(
                "lastMessage", message.text,
                "lastSenderId", message.senderId,
                "read", false,
                "timestamp", System.currentTimeMillis()
            ).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markSwapAsRead(swapId: String): Result<Unit> {
        return try {
            firestore.collection("swaps").document(swapId).update("read", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSwapsCount(userId: String): Int {
        return try {
            val senderSwaps = firestore.collection("swaps")
                .whereEqualTo("senderId", userId)
                .get().await()
            val receiverSwaps = firestore.collection("swaps")
                .whereEqualTo("receiverId", userId)
                .get().await()
            
            // Note: This counts total participation (pending, accepted, etc.)
            senderSwaps.size() + receiverSwaps.size()
        } catch (e: Exception) {
            0
        }
    }
}
