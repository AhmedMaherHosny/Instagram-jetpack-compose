package com.example.instagram.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.GetAllChatsResponse
import com.example.instagram.other.NoRippleInteractionSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val noRippleInteractionSource: NoRippleInteractionSource
) : ViewModel() {
    private val _getChatsResult =
        MutableStateFlow<Resource<GetAllChatsResponse>>(Resource.Initial())
    val getChatsResult: MutableStateFlow<Resource<GetAllChatsResponse>> get() = _getChatsResult

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            try {
                _getChatsResult.value = Resource.Loading()
                val response = async { apiServices.getAllChats() }
                when {
                    response.await().isSuccessful -> {
                        _getChatsResult.value =
                            Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getChatsResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getChatsResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}