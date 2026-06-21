package com.swapily.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swapily.app.data.model.Report
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReportsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllReports(): Flow<List<Report>> = callbackFlow {
        val listener = firestore.collection("reports")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val reports = snapshot?.toObjects(Report::class.java) ?: emptyList()
                trySend(reports)
            }
        awaitClose { listener.remove() }
    }

    suspend fun resolveReport(reportId: String): Result<Unit> {
        return try {
            firestore.collection("reports").document(reportId)
                .update("status", "RESOLVED").await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun dismissReport(reportId: String): Result<Unit> {
        return try {
            firestore.collection("reports").document(reportId)
                .update("status", "DISMISSED").await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteReportedProduct(productId: String): Result<Unit> {
        return try {
            firestore.collection("products").document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun blockReportedUser(userId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("isBlocked", true).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
