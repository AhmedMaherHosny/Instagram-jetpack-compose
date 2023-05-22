package com.example.instagram.models

data class AppUser(
    var user: UserX? = null
)

data class UserX(
	val lastName: String? = null,
	val lastIP: String? = null,
	val bio: String? = null,
	val isOnline: Boolean? = null,
	val avatar: String? = null,
	val isAdmin: Boolean? = null,
	val isEmailVerified: Boolean? = null,
	val token: String? = null,
	val firstName: String? = null,
	val createdAt: String? = null,
	val password: String? = null,
	val phoneNumber: String? = null,
	val followers: List<String?>? = null,
	val lastSeen: String? = null,
	val following: List<String?>? = null,
	val _id: String? = null,
	val email: String? = null,
	val username: String? = null,
	val updatedAt: String? = null
)

