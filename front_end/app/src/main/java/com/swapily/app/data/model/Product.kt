package com.swapily.app.data.model

data class Product(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val wantedCategory: String = "",
    val estimatedValue: Double = 0.0,
    val lookingFor: String = "",
    val condition: String = "", // ex: "LIKE NEW", "USED"
    val location: String = "",
    val images: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isAvailable: Boolean = true
)
