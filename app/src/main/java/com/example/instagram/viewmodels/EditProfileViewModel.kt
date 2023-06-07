package com.example.instagram.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagram.api.ApiServices
import com.example.instagram.api.Resource
import com.example.instagram.models.AppUser
import com.example.instagram.other.NoRippleInteractionSource
import com.example.instagram.other.convertImageBitmapToFile
import com.example.instagram.other.imageBitmapTempProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val noRippleInteractionSource: NoRippleInteractionSource
) : ViewModel() {
    private val _updateResult = MutableStateFlow<Resource<AppUser>>(Resource.Initial())
    val updateResult: MutableStateFlow<Resource<AppUser>> get() = _updateResult

    private val _validationError = MutableSharedFlow<String>()
    val validationError: SharedFlow<String>
        get() = _validationError

    fun update(usernameP: String) {
        viewModelScope.launch {
            try {
                val imageFile = async { convertImageBitmapToFile(imageBitmapTempProfile!!) }
                val imageRequestBody =
                    imageFile.await().asRequestBody("image/*".toMediaTypeOrNull())
                val image =
                    MultipartBody.Part.createFormData(
                        "image",
                        imageFile.await().name,
                        imageRequestBody
                    )
                val username = usernameP.toRequestBody("text/plain".toMediaTypeOrNull())

                _updateResult.value = Resource.Loading()
                val response = async { apiServices.editProfile(image, username) }
                when {
                    response.await().isSuccessful -> {
                        _updateResult.value = Resource.Success(response.await().body()!!)
                    }

                    else -> {
                        val errorBody = response.await().errorBody()?.string()
                        val errorMessage = JSONObject(errorBody!!).getString("message")
                        emitError(errorMessage)
                        _updateResult.value = Resource.Error(response.await().message())
                    }
                }
            } catch (e: Exception) {
                _updateResult.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun emitError(errorMessage: String) {
        viewModelScope.launch {
            _validationError.emit(errorMessage)
        }
    }

}