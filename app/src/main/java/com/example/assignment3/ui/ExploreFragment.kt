package com.example.assignment3.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.model.UserStatus
import com.example.assignment3.databinding.FragmentExploreBinding
import com.example.assignment3.viewmodel.UserViewModel

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var customerViewModel: UserViewModel
    private lateinit var adapter: CustomerAdapter

    private var allCustomers = emptyList<Customer>()
    private var currentTab = 0 // 0 = PENDING, 1 = VERIFIED
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Refresh local cache data every time we return to this screen
        customerViewModel.getAllCustomers()
    }

    private fun setupRecyclerView() {
        adapter = CustomerAdapter(
            onCustomerClick = { customer ->
                // Navigate to details screen
                val detailsFragment = ProductDetailsFragment.newInstance(customer.id)
                (activity as? MainActivity)?.navigateTo(detailsFragment)
            },
            onKycClick = { customer ->
                // Navigate to details screen directly to complete KYC
                val detailsFragment = ProductDetailsFragment.newInstance(customer.id)
                (activity as? MainActivity)?.navigateTo(detailsFragment)
            }
        )

        // Using 2-column grid layout for beautiful cards
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        // Search text watcher
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString() ?: ""
                filterAndDisplay()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Tab selection listener
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                filterAndDisplay()
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        binding.progressBar.visibility = View.VISIBLE
        customerViewModel.user.observe(viewLifecycleOwner) { list ->
            binding.progressBar.visibility = View.GONE
            allCustomers = list
            filterAndDisplay()
        }
    }

    private fun filterAndDisplay() {
        val filteredList = allCustomers.filter { customer ->
            // Filter by Tab
            val matchesTab = if (currentTab == 0) {
                customer.status == UserStatus.PENDING
            } else {
                customer.status == UserStatus.COMPLETED
            }

            // Filter by Search Query (First Name, Last Name, or IBAN)
            val matchesSearch = if (searchQuery.isEmpty()) {
                true
            } else {
                val fullName = "${customer.firstName} ${customer.lastName}".lowercase()
                fullName.contains(searchQuery.lowercase()) ||
                        customer.iban.contains(searchQuery, ignoreCase = true)
            }

            matchesTab && matchesSearch
        }

        adapter.submitList(filteredList)

        // Show/hide empty state
        binding.emptyStateView.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
