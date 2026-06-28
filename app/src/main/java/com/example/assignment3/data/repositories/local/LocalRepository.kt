package com.example.assignment3.data.repositories.local

import com.example.assignment3.data.database.CustomerDao
import com.example.assignment3.data.database.IfscDao
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.model.IfscDetails
import com.example.assignment3.data.model.UserStatus
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private val ifscDao: IfscDao
) {
    //Customers Local
    suspend fun insertCustomers(customers: List<Customer>) {
        customerDao.insertCustomers(customers)
    }

    suspend fun getAllCustomers(): List<Customer> {
        return customerDao.getAllCustomers()
    }

    suspend fun getCustomersByStatus(status: UserStatus): List<Customer> {
        return customerDao.getCustomersByStatus(status)
    }

    suspend fun getCustomerByFirstName(name: String): List<Customer> {
        return customerDao.getCustomerByFirstName(name)
    }

    suspend fun getCustomerByIfsc(ifsc: String): List<Customer> {
        return customerDao.getCustomerByIfsc(ifsc)
    }

    //IFSC local

    suspend fun insertIfscDetails(details: List<IfscDetails>) {
        ifscDao.insertIfscDetails(details)
    }

    suspend fun getAllIfscDetails(): List<IfscDetails> {
        return ifscDao.getAllIfscDetails()
    }

    suspend fun getIfscByCode(ifsc: String): IfscDetails? {
        return ifscDao.getIfscByCode(ifsc)
    }

}