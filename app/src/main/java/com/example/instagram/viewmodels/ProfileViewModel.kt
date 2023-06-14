package com.example.instagram.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.GetChatResponse
import com.example.instagram.models.GetChatResponsex
import com.example.instagram.models.ProfileResponse
import com.example.instagram.other.MyPreference
import com.example.instagram.other.NoRippleInteractionSource
import com.example.instagram.pagination.ProfilePostsPagingSource
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val apiServices: ApiServices,
    val myPreference: MyPreference,
    val noRippleInteractionSource: NoRippleInteractionSource,
    private val profilePostsPagingSourceFactory: ProfilePostsPagingSourceFactory
) : ViewModel() {
    private val _getProfileResult =
        MutableStateFlow<Resource<ProfileResponse>>(Resource.Initial())
    val getProfileResult: MutableStateFlow<Resource<ProfileResponse>> get() = _getProfileResult

    private var _getChatResult =
        MutableStateFlow<Resource<GetChatResponsex>>(Resource.Initial())
    val getChatResult: MutableStateFlow<Resource<GetChatResponsex>> get() = _getChatResult
    var profileId: String by mutableStateOf("ahmed")

    fun getProfile(userId: String) {
        viewModelScope.launch {
            try {
                _getProfileResult.value = Resource.Loading()
                val response = async { apiServices.getProfileById(userId) }
                when {
                    response.await().isSuccessful -> {
                        _getProfileResult.value =
                            Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getProfileResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getProfileResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            val response = async { apiServices.followUser(userId) }
            if (response.await().isSuccessful) {
                myPreference.setToken(response.await().body()?.user?.token!!)
            }
        }
    }

    fun unFollowUser(userId: String) {
        viewModelScope.launch {
            val response = async { apiServices.unFollowUser(userId) }
            if (response.await().isSuccessful) {
                myPreference.setToken(response.await().body()?.user?.token!!)
            }
        }
    }

    val profilePostsPager = Pager(PagingConfig(pageSize = 5)) {
        profilePostsPagingSourceFactory.create(profileId)
    }.flow.cachedIn(viewModelScope)

    fun getChat(userId: String) {
        viewModelScope.launch {
            try {
                _getChatResult.value = Resource.Loading()
                val response = async { apiServices.getChat(userId) }
                when {
                    response.await().isSuccessful -> {
                        _getChatResult.value =
                            Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getChatResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getChatResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun clearGetChatResult(){
        _getChatResult = MutableStateFlow(Resource.Initial())
    }


}

@AssistedFactory
interface ProfilePostsPagingSourceFactory {
    fun create(profileId: String): ProfilePostsPagingSource
}