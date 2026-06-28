package com.example.assignment3.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.assignment3.R
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.model.UserStatus
import com.example.assignment3.databinding.FragmentProductDetailsBinding
import com.example.assignment3.viewmodel.IfscViewModel
import com.example.assignment3.viewmodel.UserViewModel
import java.io.File

class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var customerViewModel: UserViewModel
    private lateinit var ifscViewModel: IfscViewModel
    private var customerId: Int = -1

    // Camera Permission request contract
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(
                requireContext(),
                "Camera permission is required to capture selfie for KYC verification.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerId = requireArguments().getInt(ARG_CUSTOMER_ID, -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        ifscViewModel = ViewModelProvider(requireActivity())[IfscViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        // Observe customers
        customerViewModel.user.observe(viewLifecycleOwner) { list ->
            val customer = list.find { it.id == customerId }
            if (customer != null) {
                bindCustomerDetails(customer)
            }
        }
    }

    private fun bindCustomerDetails(customer: Customer) {
        val context = requireContext()

        // 1. Basic Info
        binding.detailName.text = "${customer.firstName} ${customer.lastName}".uppercase()
        val last4 = if (customer.iban.length >= 4) customer.iban.takeLast(4) else customer.iban
        binding.detailAccount.text = "A/C **** $last4"
        binding.detailBalance.text = String.format("Rs %,.0f", customer.balance)

        // 2. Profile Details
        binding.profileDob.text = customer.birthDate
        binding.profileNationality.text = customer.nationality
        binding.profileContact.text = "${customer.phone}\n${customer.email}"
        binding.profileAddress.text = customer.address
        binding.profileIfsc.text = customer.ifsc

        // 3. Status Badge & Selfie section
        if (customer.status == UserStatus.COMPLETED) {
            // Verified Status
            binding.detailStatusBadge.text = "KYC VERIFIED"
            binding.detailStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_verified))
            binding.detailStatusBadge.setBackgroundResource(R.drawable.bg_badge_verified)

            // Hide/Show correct elements
            binding.selfieStatusText.visibility = View.VISIBLE
            binding.actionKycButton.text = "Re-take Selfie"
            
            // Set up KYC image
            binding.selfieFrame.setBackgroundResource(0) // Remove placeholder background
            if (customer.image.startsWith("http")) {
                // If it is remote image (should not happen for verified, but fallback)
                Glide.with(context).load(customer.image).into(binding.selfieImage)
                Glide.with(context).load(customer.image).into(binding.detailAvatar)
            } else {
                val selfieFile = File(customer.image)
                Glide.with(context).load(selfieFile).into(binding.selfieImage)
                // Set the avatar to the captured selfie too
                Glide.with(context).load(selfieFile).into(binding.detailAvatar)
            }
        } else {
            // Pending Status
            binding.detailStatusBadge.text = "KYC PENDING"
            binding.detailStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_pending))
            binding.detailStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending)

            // Hide/Show correct elements
            binding.selfieStatusText.visibility = View.GONE
            binding.actionKycButton.text = "Do KYC"

            // Set up remote avatar image
            Glide.with(context)
                .load(customer.image)
                .placeholder(R.drawable.ic_avatar_placeholder)
                .into(binding.detailAvatar)

            // Reset selfie image view to empty/avatar icon
            binding.selfieImage.setImageResource(R.drawable.ic_avatar_placeholder)
            binding.selfieFrame.setBackgroundResource(R.drawable.bg_selfie_placeholder)
        }

        // 4. Resolve Bank / Branch live from database cache
        ifscViewModel.ifsc.observe(viewLifecycleOwner) { ifscList ->
            val details = ifscList.find { it.IFSC == customer.ifsc }
            if (details != null) {
                binding.profileBankBranch.text = "${details.BANK}\n${details.BRANCH}"
            } else {
                binding.profileBankBranch.text = "Resolving bank branch..."
                // Force a query to the database
                ifscViewModel.getIfscByCode(customer.ifsc)
            }
        }

        // 5. KYC action trigger
        binding.actionKycButton.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val cameraFragment = CameraFragment.newInstance(customerId)
        (activity as? MainActivity)?.navigateTo(cameraFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CUSTOMER_ID = "customer_id"

        fun newInstance(customerId: Int): ProductDetailsFragment {
            return ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CUSTOMER_ID, customerId)
                }
            }
        }
    }
}
