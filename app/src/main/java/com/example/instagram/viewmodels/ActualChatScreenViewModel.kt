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
import com.example.instagram.other.currentUser
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.net.URISyntaxException
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

    private val _allMessages = mutableStateListOf<Message>()
    val allMessages: List<Message> = _allMessages

    var messageInserted = mutableStateOf(false)
        private set
    var messagesLoadedFirstTime = mutableStateOf(false)
        private set

    var isTyping = mutableStateOf(false)
        private set

    private lateinit var mSocket: Socket
    private lateinit var ctid: String

    init {
        connectToSocket()
        listenToNewMsgs()
        listenToTyping()

        savedStateHandle.get<String>("receiverId")?.let {
            getChatHeader(it)
        }
        savedStateHandle.get<String>("chatId")?.let {
            ctid = it
            mSocket.emit("joinRoom", it)
            getAllMessages(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectTheSocket()
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
            val data = mapOf(
                "chatId" to chatId,
                "senderId" to currentUser!!._id,
                "content" to content,
            )
            mSocket.emit("message", data)
        }
    }

    private fun connectToSocket() {
        val options = IO.Options()
        options.forceNew = true
        try {
            mSocket = IO.socket("http://192.168.1.23:5000", options)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun listenToNewMsgs() {
        mSocket.on("message") {
            Timber.e(messageInserted.value.toString())
            messageInserted.value = false
            val gson = Gson()
            val message = gson.fromJson(it[0].toString(), Message::class.java)
            _allMessages.add(message) // need to fix scroll to new msg
            messageInserted.value = true
            isTyping.value = false
        }
    }

    private fun listenToTyping() {
        mSocket.on("typing") {
            val gson = Gson()
            val senderId: String? = gson.fromJson(it[0].toString(), String::class.java)
            isTyping.value = senderId != null
        }
    }

    fun typingFunctionality(isTyping: Boolean) {
        if (isTyping) {
            mSocket.emit("typing", JSONObject().apply {
                put("chatId", ctid)
                put("senderId", currentUser!!._id)
            })
            return
        }
        mSocket.emit("typing", JSONObject().apply {
            put("chatId", ctid)
            put("senderId", "")
        })
    }

    private fun disconnectTheSocket() {
        mSocket.disconnect()
    }


}