package com.example.assignment3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @PrimaryKey
    val id: Int,
    val firstName: String,
    val lastName: String,
    val image: String,
    val birthDate: String,
    val gender: String,
    val address: String,
    val email: String,
    val phone: String,
    val iban: String,
    val ifsc: String,
    val balance: Double,
    val status: UserStatus,
    val nationality: String
)
