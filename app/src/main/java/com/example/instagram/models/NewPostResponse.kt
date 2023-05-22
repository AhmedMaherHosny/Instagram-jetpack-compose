package com.example.instagram.models

data class NewPostResponse(
	val postedBy: String? = null,
	val createdAt: String? = null,
	val comments: List<Any?>? = null,
	val hashtags: List<Any?>? = null,
	val imageOrVideoUrl: List<String?>? = null,
	val caption: String? = null,
	val location: Location? = null,
	val _id: String? = null,
	val likes: List<Any?>? = null,
	val tags: List<Any?>? = null,
	val updatedAt: String? = null
)

