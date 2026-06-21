package com.swapily.app.data.model

import com.google.firebase.firestore.DocumentId

data class AppNotification(
    @DocumentId val id: String = "",
    val title: String = "",
    val message: String = "",
    val targetType: String = "", // "ALL" or "USER"
    val targetUserId: String = "",
    val timestamp: Long = 0L,
    val sentBy: String = "ADMIN",
    val read: Boolean = false
)
