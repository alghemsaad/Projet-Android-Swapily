package com.swapily.app.data.model

data class Review(
    val id: String = "",
    val fromUserId: String = "",
    val fromUserName: String = "",
    val toUserId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
