package com.example.assignment3.data.model

data class Users(
    val limit: Int,
    val skip: Int,
    val total: Int,
    val users: List<User>
)