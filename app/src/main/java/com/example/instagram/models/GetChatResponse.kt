package com.example.instagram.models

data class GetChatResponsex(
	val chat: Chatx? = null
)

data class Chatx(
	val createdAt: String? = null,
	val members: List<String?>? = null,
	val __v: Int? = null,
	val _id: String? = null,
	val updatedAt: String? = null
)

