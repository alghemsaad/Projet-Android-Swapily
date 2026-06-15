package com.swapily.app.data.model

data class MatchResult(
    val myProduct: Product,
    val otherProduct: Product,
    val score: Int,
    val otherUserId: String,
    val otherUserName: String = "User"
)
