package com.example.assignment3.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.assignment3.data.model.UserStatus
import com.example.assignment3.databinding.FragmentCameraBinding
import com.example.assignment3.viewmodel.UserViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var customerId: Int = -1
    private lateinit var customerViewModel: UserViewModel

    private var imageCapture: ImageCapture? = null
    private var lensFacing = CameraSelector.LENS_FACING_FRONT // Default to front camera for selfie
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerId = requireArguments().getInt(ARG_CUSTOMER_ID, -1)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        startCamera()
        setupListeners()
    }

    private fun setupListeners() {
        binding.captureButton.setOnClickListener {
            takePhoto()
        }

        binding.switchCamera.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            startCamera()
        }

        binding.closeCamera.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Select camera lens
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(requireContext(), "Failed to open camera: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Show processing overlay
        binding.loadingOverlay.visibility = View.VISIBLE

        // Create unique output file to hold the selfie
        val photoFile = File(
            requireContext().filesDir,
            "kyc_selfie_${customerId}_${System.currentTimeMillis()}.jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    binding.loadingOverlay.visibility = View.GONE
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(requireContext(), "Failed to capture selfie. Please try again.", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "Selfie saved successfully: ${photoFile.absolutePath}")

                    // Update customer in Room database to COMPLETED and set captured selfie file path
                    val customersList = customerViewModel.user.value ?: emptyList()
                    val targetCustomer = customersList.find { it.id == customerId }

                    if (targetCustomer != null) {
                        val updatedCustomer = targetCustomer.copy(
                            status = UserStatus.COMPLETED,
                            image = photoFile.absolutePath
                        )
                        
                        // Update Room
                        customerViewModel.insertCustomers(listOf(updatedCustomer))
                        
                        // Refresh LiveData
                        customerViewModel.getAllCustomers()

                        Toast.makeText(requireContext(), "KYC Verified Successfully!", Toast.LENGTH_LONG).show()

                        // Hide overlay and pop back
                        binding.loadingOverlay.visibility = View.GONE
                        parentFragmentManager.popBackStack()
                    } else {
                        binding.loadingOverlay.visibility = View.GONE
                        Toast.makeText(requireContext(), "Customer not found.", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        _binding = null
    }

    companion object {
        private const val TAG = "CameraFragment"
        private const val ARG_CUSTOMER_ID = "customer_id"

        fun newInstance(customerId: Int): CameraFragment {
            return CameraFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CUSTOMER_ID, customerId)
                }
            }
        }
    }
}
