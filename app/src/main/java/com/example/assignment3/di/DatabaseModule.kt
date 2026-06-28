package com.example.assignment3.di

import android.content.Context
import androidx.room.Room
import com.example.assignment3.data.database.CustomerDao
import com.example.assignment3.data.database.CustomerDatabase
import com.example.assignment3.data.database.IfscDao
import com.example.assignment3.data.database.IfscDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CustomerDatabase {
        return Room.databaseBuilder(
            context,
            CustomerDatabase::class.java,
            "customer_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideIfscDatabase(
        @ApplicationContext context: Context
    ): IfscDatabase {
        return Room.databaseBuilder(
            context,
            IfscDatabase::class.java,
            "ifsc_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideCustomerDao(
        database: CustomerDatabase
    ): CustomerDao {
        return database.customerDao()
    }

    @Provides
    @Singleton
    fun provideIfscDao(
        database: IfscDatabase
    ): IfscDao {
        return database.ifscDao()
    }
}