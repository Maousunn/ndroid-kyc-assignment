package com.example.assignment3.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.assignment3.data.model.IfscDetails


@Database(entities = [IfscDetails::class], version = 1, exportSchema = false)
abstract class IfscDatabase: RoomDatabase() {
    abstract fun ifscDao(): IfscDao
}