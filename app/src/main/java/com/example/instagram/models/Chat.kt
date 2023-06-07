package com.example.instagram.models

data class Chat(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val latestMessage: LatestMessage?,
    val members: List<UserX>,
    val updatedAt: String
)