package com.example.assignment3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IfscDetails(
    @PrimaryKey
    val IFSC: String,
    val BANK: String,
    val BRANCH: String,
    val CITY: String,
    val STATE: String,
    val MICR: String?
)