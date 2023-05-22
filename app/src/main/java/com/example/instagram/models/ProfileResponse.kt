package com.example.instagram.models

data class ProfileResponse(
    val numberOfFollowers: Int? = null,
    val isFollowing: Boolean? = null,
    val postList: List<PostListItem?>? = null,
    val numberOfFollowing: Int? = null,
    val bio: String? = null,
    val _id: String? = null,
    val avatar: String? = null,
    val username: String? = null,
    val isAdmin: Boolean? = null,
    val isCurrentUser: Boolean? = null,
)

data class PostListItem(
    val imageOrVideoUrl: List<String?>? = null,
    val _id: String? = null
)

