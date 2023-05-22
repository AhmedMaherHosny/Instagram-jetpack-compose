package com.example.instagram.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.AppUser
import com.example.instagram.models.LoginData
import com.example.instagram.other.MyPreference
import com.example.instagram.other.NoRippleInteractionSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val myPreference: MyPreference,
    val noRippleInteractionSource: NoRippleInteractionSource
) : ViewModel() {
    var loginData: LoginData? = null
    private val _validationError = MutableSharedFlow<String>()
    val validationError: SharedFlow<String>
        get() = _validationError
    private val _loginResult = MutableStateFlow<Resource<AppUser>>(Resource.Initial())
    val loginResult: MutableStateFlow<Resource<AppUser>> get() = _loginResult

    fun login() {
        viewModelScope.launch {
            try {
                _loginResult.value = Resource.Loading()
                val response = async { apiServices.login(loginData!!) }
                when {
                    response.await().isSuccessful -> {
                        myPreference.setToken(response.await().body()?.user?.token!!)
                        _loginResult.value = Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        emitError(errorMessage)
                        _loginResult.value = Resource.Error(response.await().message())
                    }
                }
            } catch (e: Exception) {
                _loginResult.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun emitError(validationError: String) {
        viewModelScope.launch {
            _validationError.emit(validationError)
        }
    }
}