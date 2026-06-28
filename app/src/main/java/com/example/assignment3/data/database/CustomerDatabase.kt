package com.example.assignment3.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.assignment3.data.model.Customer

@Database(entities = [Customer::class], version = 1, exportSchema = false)
abstract class CustomerDatabase: RoomDatabase() {
    abstract fun customerDao(): CustomerDao
}