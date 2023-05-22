package com.example.instagram.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.RegisterData
import com.example.instagram.other.MyPreference
import com.example.instagram.other.NoRippleInteractionSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val myPreference: MyPreference,
    val noRippleInteractionSource: NoRippleInteractionSource
) : ViewModel() {
    var registerData: RegisterData? = null
    private val _validationError = MutableSharedFlow<String>()
    val validationError: SharedFlow<String>
        get() = _validationError
    private val _registerResult = MutableStateFlow<Resource<String>>(Resource.Initial())
    val registerResult: StateFlow<Resource<String>> get() = _registerResult

    fun register() {
        if (validateForm(registerData!!)) {
            viewModelScope.launch {
                try {
                    _registerResult.value = Resource.Loading()
                    val response = async { apiServices.register(registerData!!) }
                    val jsonObject = response.await().body()?.asJsonObject
                    val email = jsonObject?.get("email")?.asString
                    when {
                        response.await().isSuccessful -> {
                            _registerResult.value =
                                Resource.Success(email!!)
                        }

                        else -> {
                            _registerResult.value = Resource.Error(response.await().message())
                        }
                    }
                } catch (e: Exception) {
                    _registerResult.value = Resource.Error(e.message ?: "Unknown error occurred")
                }
            }
        }
    }


    private fun validateForm(registerData: RegisterData): Boolean {
        if (registerData.username.isBlank()) {
            emitError("username can't be empty")
            return false
        }
        if (registerData.username.length < 3) {
            emitError("username can't be less than 3 characters")
            return false
        }
        if (registerData.username.length > 30) {
            emitError("username can't be greater than 30 characters")
            return false
        }
        if (registerData.email.isBlank() || !registerData.email.isValidEmail()) {
            emitError("we accept only Gmail domain")
            return false
        }
        if (registerData.firstName.isBlank()) {
            emitError("firstName can't be empty")
            return false
        }
        if (registerData.firstName.length < 3) {
            emitError("firstName can't be less than 3 characters")
            return false
        }
        if (registerData.firstName.length > 30) {
            emitError("firstName can't be greater than 30 characters")
            return false
        }
        if (registerData.lastName.isBlank()) {
            emitError("lastName can't be empty")
            return false
        }
        if (registerData.lastName.length < 3) {
            emitError("lastName can't be less than 3 characters")
            return false
        }
        if (registerData.lastName.length > 30) {
            emitError("lastName can't be greater than 30 characters")
            return false
        }
        if (registerData.password.isBlank() || !registerData.password.isValidPassword()) {
            emitError("uppercase, lowercase and symbol")
            return false
        }
        if (registerData.password.length < 8) {
            emitError("password length must be not less than 8")
            return false
        }
        if (registerData.phoneNumber.isBlank() || !registerData.phoneNumber.isValidPhoneNumber()) {
            emitError("phone must start with +20 and be 12 number")
            return false
        }
        return true
    }

    private fun emitError(validationError: String) {
        viewModelScope.launch {
            _validationError.emit(validationError)
        }
    }

    private fun String.isValidEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@gmail.com$"
        return matches(emailRegex.toRegex())
    }

    private fun String.isValidPassword(): Boolean {
        val pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&+=]).+\$")
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    private fun String.isValidPhoneNumber(): Boolean {
        val prefix = "+20"
        return length == 13 && startsWith(prefix)
    }


}