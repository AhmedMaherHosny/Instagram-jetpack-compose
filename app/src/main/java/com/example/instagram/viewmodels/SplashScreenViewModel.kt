package com.example.instagram.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.instagram.api.ApiServices
import com.example.instagram.other.MyPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val apiServices: ApiServices,
    val myPreference: MyPreference
) : ViewModel() {
 // TODO : send req to get the currentUser
}