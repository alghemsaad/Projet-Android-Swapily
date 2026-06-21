package com.swapily.app.data.model

data class Report(
    val id: String = "",
    val reportedBy: String = "",
    val reporterName: String = "",
    val targetType: String = "", // "PRODUCT" or "USER"
    val targetId: String = "",
    val reason: String = "",
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val createdAt: Long = System.currentTimeMillis()
)
