package com.example.instagram.viewmodels

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.NewPostResponse
import com.example.instagram.other.convertImageBitmapsToFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val apiServices: ApiServices
) : ViewModel() {
    private val _getPostCreatedResult =
        MutableStateFlow<Resource<NewPostResponse>>(Resource.Initial())
    val getPostCreatedResult: MutableStateFlow<Resource<NewPostResponse>> get() = _getPostCreatedResult

    fun createNewPost(imagesBitmaps: List<ImageBitmap>, captionOfThePost: String) {
        viewModelScope.launch {
            val imageFiles = async { convertImageBitmapsToFiles(imagesBitmaps) }
            val images = mutableListOf<MultipartBody.Part>().apply {
                imageFiles.await().forEachIndexed { index, file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val part =
                        MultipartBody.Part.createFormData("images", file.name, requestFile)
                    add(part)
                }
            }.toList()
            val caption =
                captionOfThePost.toRequestBody("text/plain".toMediaTypeOrNull())
            try {
                _getPostCreatedResult.value = Resource.Loading()
                val response = async { apiServices.createPost(images, caption) }
                when {
                    response.await().isSuccessful -> {
                        _getPostCreatedResult.value = Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getPostCreatedResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getPostCreatedResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }

        }
    }
}