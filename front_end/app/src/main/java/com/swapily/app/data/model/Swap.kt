package com.swapily.app.data.model

data class Swap(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderImage: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverImage: String = "",
    val senderProductId: String = "",
    val senderProductTitle: String = "",
    val senderProductImage: String = "",
    val receiverProductId: String = "",
    val receiverProductTitle: String = "",
    val receiverProductImage: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, COMPLETED
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
