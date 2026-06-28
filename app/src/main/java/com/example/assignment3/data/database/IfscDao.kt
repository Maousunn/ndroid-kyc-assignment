package com.example.assignment3.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.assignment3.data.model.IfscDetails

@Dao
interface IfscDao {

    @Upsert
    suspend fun insertIfscDetails(details: List<IfscDetails>)

    @Query("SELECT * FROM IfscDetails")
    suspend fun getAllIfscDetails(): List<IfscDetails>

    @Query("SELECT * FROM IfscDetails WHERE IFSC = :ifsc")
    suspend fun getIfscByCode(ifsc: String): IfscDetails?
}