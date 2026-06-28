package com.example.assignment3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.repositories.local.LocalRepository
import com.example.assignment3.data.repositories.remote.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _user = MutableLiveData<List<Customer>>()
    val user: LiveData<List<Customer>> = _user

    fun getUsers() {
        viewModelScope.launch {
            _user.value = usersRepository.getUsers()
        }
    }

    fun insertCustomers(customers: List<Customer>) {
        viewModelScope.launch {
            localRepository.insertCustomers(customers)
        }
    }

    fun getAllCustomers() {
        viewModelScope.launch {
            _user.value = localRepository.getAllCustomers()
        }
    }
}