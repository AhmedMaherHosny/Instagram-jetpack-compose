package com.example.instagram.models

data class FollowingPosts(
	val followingPosts: List<FollowingPostsItem?>? = null
)

data class FollowingPostsItem(
	val postedBy: PostedBy? = null,
	val createdAt: String? = null,
	val comments: List<String?>? = null,
	val imageOrVideoUrl: List<String?>? = null,
	val caption: String? = null,
	val location: Location? = null,
	val _id: String? = null,
	val liked: Boolean? = null,
	val likes: List<String?>? = null,
	val tags: List<String?>? = null,
	val updatedAt: String? = null
)

data class PostedBy(
	val _id: String? = null,
	val avatar: String? = null,
	val username: String? = null
)

data class Location(
	val coordinates: List<Any?>? = null,
	val type: String? = null
)