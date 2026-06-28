package com.example.assignment3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment3.data.model.IfscDetails
import com.example.assignment3.data.repositories.local.LocalRepository
import com.example.assignment3.data.repositories.remote.IfscRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IfscViewModel @Inject constructor(
    val ifscRepository: IfscRepository,
    private val localRepository: LocalRepository
): ViewModel() {

    private val _ifsc = MutableLiveData<List<IfscDetails>>()
    val ifsc: LiveData<List<IfscDetails>> = _ifsc

    fun getIfscDetails() {
        viewModelScope.launch {
            _ifsc.value = ifscRepository.getIfscDetails()
        }
    }

    fun insertIfscDetails(details: List<IfscDetails>) {
        viewModelScope.launch {
            localRepository.insertIfscDetails(details)
        }
    }

    fun getAllIfscDetails() {
        viewModelScope.launch {
            _ifsc.value = localRepository.getAllIfscDetails()
        }
    }

    fun getIfscByCode(ifscCode: String) {
        viewModelScope.launch {
            _ifsc.value = localRepository.getIfscByCode(ifscCode)?.let {
                listOf(it)
            } ?: emptyList()
        }
    }

}