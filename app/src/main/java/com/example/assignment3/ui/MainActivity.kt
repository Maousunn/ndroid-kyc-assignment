package com.example.assignment3.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.assignment3.R
import com.example.assignment3.databinding.ActivityMainBinding
import com.example.assignment3.viewmodel.IfscViewModel
import com.example.assignment3.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var customerViewModel: UserViewModel
    lateinit var ifscViewModel: IfscViewModel
    private lateinit var binding: ActivityMainBinding

    private var isSeedingPending = false
    private var isIfscSeedingPending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        ifscViewModel = ViewModelProvider(this)[IfscViewModel::class.java]

        // Load cached database data first
        customerViewModel.getAllCustomers()
        ifscViewModel.getAllIfscDetails()

        customerViewModel.user.observe(this) { list ->
            if (list.isEmpty() && !isSeedingPending) {
                isSeedingPending = true
                Log.d("MainActivity", "Customer database is empty, fetching from remote API...")
                customerViewModel.getUsers()
            } else if (isSeedingPending) {
                isSeedingPending = false
                Log.d("MainActivity", "API fetched ${list.size} customers, caching in Room database...")
                customerViewModel.insertCustomers(list)
                // Trigger a refresh from the database to make Room the source of truth
                customerViewModel.getAllCustomers()
            }
        }

        ifscViewModel.ifsc.observe(this) { list ->
            if (list.isEmpty() && !isIfscSeedingPending) {
                isIfscSeedingPending = true
                Log.d("MainActivity", "IFSC database is empty, fetching details from Razorpay...")
                ifscViewModel.getIfscDetails()
            } else if (isIfscSeedingPending) {
                isIfscSeedingPending = false
                Log.d("MainActivity", "Fetched ${list.size} IFSC details, caching in Room...")
                ifscViewModel.insertIfscDetails(list)
                ifscViewModel.getAllIfscDetails()
            }
        }

        // Set up the initial Explore fragment
        if (savedInstanceState == null) {
            navigateTo(ExploreFragment(), addToBackStack = false)
        }
    }

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}