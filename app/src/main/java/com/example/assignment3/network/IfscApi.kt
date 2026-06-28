package com.example.assignment3.network

import com.example.assignment3.data.model.IfscDetails
import retrofit2.http.GET
import retrofit2.http.Path

interface IfscApi {
    @GET("{ifsc}")
    suspend fun getIfscDetails(
        @Path("ifsc") ifsc: String
    ): IfscDetails
}