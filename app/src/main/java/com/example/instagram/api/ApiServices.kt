package com.example.instagram.api

import com.example.instagram.models.*
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
    @POST("/api/auth/register")
    suspend fun register(@Body registerData: RegisterData): Response<JsonElement>

    @POST("/api/auth/login")
    suspend fun login(@Body loginData: LoginData): Response<AppUser>

    @GET("/api/posts/explore/get-following-posts")
    suspend fun getFollowingPosts(@Query("page") page: Int = 1): Response<FollowingPosts>

    @PATCH("/api/posts/like/{id}")
    suspend fun likePost(@Path("id") id: String): Response<JsonElement>

    @GET("/api/posts/comments/{id}")
    suspend fun getCommentsByPostId(
        @Path("id") id: String,
        @Query("page") page: Int = 1
    ): Response<Comments>

    @PATCH("/api/posts/like/comments/{id}")
    suspend fun likeComment(@Path("id") id: String): Response<JsonElement>

    @PATCH("/api/posts/{id}")
    suspend fun commentOnPost(
        @Path("id") id: String,
        @Body commentData: CommentData
    ): Response<Comment>

    @GET("/api/profile/{id}")
    suspend fun getProfileById(@Path("id") id: String): Response<ProfileResponse>

    @PATCH("/api/users/follow-user/{id}")
    suspend fun followUser(@Path("id") id: String): Response<AppUser>

    @PATCH("/api/users/unfollow-user/{id}")
    suspend fun unFollowUser(@Path("id") id: String): Response<AppUser>

    @GET("/api/users/current-user")
    suspend fun getCurrentUser(): Response<UserX>

    @GET("/api/profile/{id}/posts")
    suspend fun getProfilePosts(
        @Path("id") id: String,
        @Query("page") page: Int = 1
    ): Response<ProfilePostsResponse>

    @GET("/api/users/search")
    suspend fun searchUser(@Query("q") q: String): Response<SearchResponse>

    @Multipart
    @POST("/api/posts")
    suspend fun createPost(
        @Part images: List<MultipartBody.Part>,
        @Part("caption") caption: RequestBody
    ): Response<NewPostResponse>

    @Multipart
    @PUT("/api/profile/edit")
    suspend fun editProfile(
        @Part image: MultipartBody.Part,
        @Part("username") username: RequestBody
    ): Response<AppUser>
}