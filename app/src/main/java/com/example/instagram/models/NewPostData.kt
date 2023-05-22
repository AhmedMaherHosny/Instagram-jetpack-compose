package com.example.instagram.models

import java.io.File

data class NewPostData(
    val files: List<File>,
    val caption: String? = null,
    val listOfTaggedPeople: List<String?>? = listOf(),
    val location: Location? = null
)
