package com.example.instagram.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.CommentData
import com.example.instagram.models.CommentsItem
import com.example.instagram.models.UserX
import com.example.instagram.garbage.other.MyPreference
import com.example.instagram.garbage.other.NoRippleInteractionSource
import com.example.instagram.garbage.other.currentUser
import com.example.instagram.pagination.CommentsPagingSource
import com.example.instagram.pagination.FollowingPostsPagingSource
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val myPreference: MyPreference,
    private val followingPostsPagingSource: FollowingPostsPagingSource,
    private val commentsPagingSourceFactory: CommentsPagingSourceFactory,
    val noRippleInteractionSource: NoRippleInteractionSource
) : ViewModel() {

    private val _getCommentAddedResult =
        MutableStateFlow<Resource<String>>(Resource.Initial())
    val getCommentAddedResult: MutableStateFlow<Resource<String>> get() = _getCommentAddedResult

    private val _getCurrentUserResult =
        MutableStateFlow<Resource<UserX>>(Resource.Initial())
    val getCurrentUserResult: MutableStateFlow<Resource<UserX>> get() = _getCurrentUserResult

    var comments: List<CommentsItem?> by mutableStateOf(listOf())
        private set

    var postId: String by mutableStateOf("ahmed")

    val followingPostsListPager = Pager(PagingConfig(pageSize = 5)) {
        followingPostsPagingSource
    }.flow.cachedIn(viewModelScope)

    fun likeOrUnlikePost(postId: String) {
        viewModelScope.launch {
            async { apiServices.likePost(postId) }
        }
    }

    val commentsPager = Pager(PagingConfig(pageSize = 15)) {
        commentsPagingSourceFactory.create(postId)
    }.flow.cachedIn(viewModelScope)

    fun likeOrUnlikeComment(commentId: String) {
        viewModelScope.launch {
            async { apiServices.likeComment(commentId) }
        }
    }

    fun commentOnPost(postId: String, commentData: CommentData) {
        viewModelScope.launch {
            try {
                _getCommentAddedResult.value = Resource.Loading()
                val response = async { apiServices.commentOnPost(postId, commentData) }
                when {
                    response.await().isSuccessful -> {
                        _getCommentAddedResult.value = Resource.Success("")
                        comments =
                            comments.plus(response.await().body()?.comment).toMutableStateList()
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getCommentAddedResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getCommentAddedResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _getCurrentUserResult.value = Resource.Loading()
                val response = async { apiServices.getCurrentUser() }
                when {
                    response.await().isSuccessful -> {
                        _getCurrentUserResult.value =
                            Resource.Success(response.await().body()!!)
                        currentUser = response.await().body()
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        _getCurrentUserResult.value = Resource.Error(errorMessage)
                    }
                }
            } catch (e: Exception) {
                _getCurrentUserResult.value =
                    Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

@AssistedFactory
interface CommentsPagingSourceFactory {
    fun create(postId: String): CommentsPagingSource
}