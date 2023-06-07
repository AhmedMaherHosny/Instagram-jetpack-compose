package com.example.instagram.models

data class MessageHold(
    val message : Message
)

data class Message(
    val __v: Int,
    val _id: String,
    val chatId: String,
    val content: String,
    val createdAt: String,
    val isDelivered: Boolean,
    val isRead: Boolean,
    val senderId: String,
    val updatedAt: String
)