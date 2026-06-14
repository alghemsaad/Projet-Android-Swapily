package com.swapily.app.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val location: String = "",
    val bio: String = "",
    val publicProfile: Boolean = true,
    val showLocation: Boolean = true,
    val swapsCount: Int = 0,
    val reviewsCount: Int = 0,
    val rating: Double = 0.0,
    val favorites: List<String> = emptyList(),
    val fcmToken: String = ""
)