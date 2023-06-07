package com.example.instagram.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.AppUser
import com.example.instagram.models.GetAllMessagesResponse
import com.example.instagram.models.Message
import com.example.instagram.other.NoRippleInteractionSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ActualChatScreenViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val noRippleInteractionSource: NoRippleInteractionSource,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _getChatHeaderResult =
        MutableStateFlow<Resource<AppUser>>(Resource.Initial())
    val getChatHeaderResult: MutableStateFlow<Resource<AppUser>> get() = _getChatHeaderResult

    private val _getAllMessagesResult =
        MutableStateFlow<Resource<GetAllMessagesResponse>>(Resource.Initial())
    val getAllMessagesResult: MutableStateFlow<Resource<GetAllMessagesResponse>> get() = _getAllMessagesResult

    private val _getAddMessageResult =
        MutableStateFlow<Resource<Message>>(Resource.Initial())
    val getAddMessageResult: MutableStateFlow<Resource<Message>> get() = _getAddMessageResult

    private val _allMessages = mutableStateListOf<Message>()
    val allMessages : List<Message> = _allMessages

    var messageInserted = mutableStateOf(false)
        private set
    var messagesLoadedFirstTime = mutableStateOf(false)
        private set

    init {
        savedStateHandle.get<String>("receiverId")?.let {
            getChatHeader(it)
        }
        savedStateHandle.get<String>("chatId")?.let {
            getAllMessages(it)
        }
    }

    private fun getChatHeader(id: String) {
        viewModelScope.launch {
            try {
                _getChatHeaderResult.value = Resource.Loading()
                val response = async { apiServices.getChatHeaderById(id) }
                when {
                    response.await().isSuccessful -> {
                        _getChatHeaderResult.value =
                            Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getChatHeaderResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getChatHeaderResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun getAllMessages(id: String) {
        viewModelScope.launch {
            try {
                _getAllMessagesResult.value = Resource.Loading()
                val response = async { apiServices.getAllMessagesByChatId(id) }
                when {
                    response.await().isSuccessful -> {
                        _getAllMessagesResult.value =
                            Resource.Success(response.await().body()!!)
                        _allMessages.addAll(response.await().body()!!.message)
                        messagesLoadedFirstTime.value = true
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getAllMessagesResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getAllMessagesResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun addMessage(chatId: String, content: String) {
        viewModelScope.launch {
            try {
                messageInserted.value = false
                _getAddMessageResult.value = Resource.Loading()
                val response = async { apiServices.addMessage(chatId, content) }
                when {
                    response.await().isSuccessful -> {
                        _getAddMessageResult.value =
                            Resource.Success(response.await().body()?.message!!)
                        _allMessages.add(response.await().body()?.message!!)
                        messageInserted.value = true
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getAddMessageResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getAddMessageResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


}