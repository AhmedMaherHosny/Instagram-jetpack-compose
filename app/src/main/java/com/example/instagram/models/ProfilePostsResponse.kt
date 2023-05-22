package com.example.instagram.models

data class ProfilePostsResponse(
	val profilePosts: List<ProfilePostsItem?>? = null
)

data class ProfilePostsItem(
	val firstImageUrl: String? = null,
	val id: String? = null
)

