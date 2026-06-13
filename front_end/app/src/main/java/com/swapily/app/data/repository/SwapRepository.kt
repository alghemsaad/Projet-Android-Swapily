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
            firestore.collection("swaps").document(swapId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
                "timestamp", System.currentTimeMillis()
            ).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
