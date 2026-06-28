package com.example.assignment3.data.repositories.remote

import android.util.Log
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.model.UserStatus
import com.example.assignment3.network.UserApi
import javax.inject.Inject

class UsersRepository @Inject constructor(private val api: UserApi){

    private val ifscList = listOf(
        "HDFC0CAGSBK",
        "SBIN0000001",
        "ICIC0000001",
        "PUNB0244200",
        "UTIB0000001"
    )

    val nationalityMap = mapOf(
        "United States" to "American",
        "India" to "Indian",
        "Japan" to "Japanese",
        "Canada" to "Canadian",
        "Australia" to "Australian"
    )


    suspend fun getUsers(): List<Customer>{
        return try {
            val users = api.getUsers().users
            users.map{
                val ifsc = ifscList[(it.id - 1) % ifscList.size]
                val balance = 5000 + (it.id * 10000)
                Customer(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                image = it.image,
                birthDate = it.birthDate,
                gender = it.gender,
                address = it.address.address,
                email = it.email,
                phone = it.phone,
                iban = it.bank.iban,
                ifsc = ifsc,
                balance = balance.toDouble(),
                status = UserStatus.PENDING,
                nationality = nationalityMap[it.address.country] ?: it.address.country
                )
            }
        } catch (e: Exception)
        {
            Log.d("API ERROR",e.message.toString())
            emptyList<Customer>()
        }
    }
}