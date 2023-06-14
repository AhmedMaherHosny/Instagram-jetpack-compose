package com.example.instagram.models

data class GetAllChatsResponse(
    val chats: List<Chat?>?
)

data class GetChatResponse(
    val chat : Chat?
)