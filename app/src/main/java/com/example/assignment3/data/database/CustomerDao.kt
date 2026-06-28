package com.example.assignment3.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.model.UserStatus

@Dao
interface CustomerDao {

    @Upsert
    suspend fun insertCustomers(customers: List<Customer>)

    @Query("SELECT * FROM Customer")
    suspend fun getAllCustomers(): List<Customer>

    @Query("SELECT * FROM Customer WHERE status = :status")
    suspend fun getCustomersByStatus(status: UserStatus): List<Customer>

    @Query("SELECT * FROM Customer WHERE firstName LIKE '%' || :name || '%'")
    suspend fun getCustomerByFirstName(name: String): List<Customer>

    @Query("SELECT * FROM Customer WHERE ifsc = :ifsc")
    suspend fun getCustomerByIfsc(ifsc: String): List<Customer>
}