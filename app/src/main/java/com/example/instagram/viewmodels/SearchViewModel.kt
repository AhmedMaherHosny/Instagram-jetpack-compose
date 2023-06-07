package com.example.instagram.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.models.SearchResponse
import com.example.instagram.models.UserX
import com.example.instagram.other.NoRippleInteractionSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val noRippleInteractionSource: NoRippleInteractionSource,
) : ViewModel() {
    private var _initialized = false

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun onSearchTextChange(q: String) {
        _searchText.value = q
    }

    private val _users = MutableStateFlow(SearchResponse().users)

    @OptIn(FlowPreview::class)
    val users = searchText
        .onEach { if (_initialized) _isSearching.update { true } }
        .debounce(500L)
        .combine(_users) { q, users ->
            if (q.trim().isBlank()) {
                _initialized = true
                users
            } else {
                getUsersFromApi(q.trim())
            }
        }
        .onEach { if (_initialized) _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _users.value
        )

    private suspend fun getUsersFromApi(q: String): List<UserX?> {
        try {
            val response = apiServices.searchUser(q)
            if (response.isSuccessful) {
                return response.body()?.users ?: emptyList()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = JSONObject(errorBody!!).getString("message")
                Timber.e(errorMessage)
            }
        } catch (e: Exception) {
            Timber.e("Unknown error occurred")
        }
        return emptyList()
    }

}