package com.example.assignment3.di

import com.example.assignment3.network.IfscApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object IfscModule {

    @Provides
    @Singleton
    @IfscRetrofit
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ifsc.razorpay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun ifscApi(@IfscRetrofit retrofit: Retrofit): IfscApi {
        return retrofit.create(IfscApi::class.java)
    }
}