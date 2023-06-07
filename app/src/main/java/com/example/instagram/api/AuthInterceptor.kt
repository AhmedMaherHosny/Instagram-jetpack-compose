package com.example.instagram.api

import com.example.instagram.garbage.other.MyPreference
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val myPreference: MyPreference) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("token", myPreference.getToken())
            .build()
        return chain.proceed(newRequest)
    }
}
