package com.example.assignment3.network

import com.example.assignment3.data.model.Users
import retrofit2.http.GET


interface UserApi {
    @GET("users")
    suspend fun getUsers(): Users
}