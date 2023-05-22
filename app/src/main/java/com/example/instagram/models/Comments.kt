package com.example.instagram.models

data class Comments(
	val comments: List<CommentsItem?>? = null
)

data class Comment(
	val comment : CommentsItem? = null
)

data class CommentsItem(
	val postedBy: PostedBy? = null,
	val createdAt: String? = null,
	val comment: String? = null,
	val _id: String? = null,
	val liked: Boolean? = null,
	val likes: List<String?>? = null
)


