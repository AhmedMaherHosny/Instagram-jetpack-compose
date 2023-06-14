package com.example.instagram.di

import android.content.Context
import com.example.instagram.api.ApiServices
import com.example.instagram.api.AuthInterceptor
import com.example.instagram.other.MyPreference
import com.example.instagram.other.NoRippleInteractionSource
import com.example.instagram.pagination.CommentsPagingSource
import com.example.instagram.pagination.CommentsPagingSource_Factory
import com.example.instagram.pagination.FollowingPostsPagingSource
import com.example.instagram.pagination.ProfilePostsPagingSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSharedPrefImpl(@ApplicationContext app: Context): MyPreference {
        return MyPreference(app)
    }

    @Provides
    @Singleton
    fun provideAuth(myPreference: MyPreference): AuthInterceptor {
        return AuthInterceptor(myPreference)
    }

    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.23:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideModelApi(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }

    @Provides
    @Singleton
    fun provideFollowingPostsPagingSource(apiServices: ApiServices): FollowingPostsPagingSource{
        return FollowingPostsPagingSource(apiServices)
    }

    @Provides
    @Singleton
    fun provideNoRippleInteractionSource(): NoRippleInteractionSource {
        return NoRippleInteractionSource()
    }
}

