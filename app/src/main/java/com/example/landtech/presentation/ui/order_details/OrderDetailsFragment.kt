package com.example.landtech.presentation.ui.order_details

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.landtech.databinding.FragmentOrderDetailsBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayoutMediator

class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private val args: OrderDetailsFragmentArgs by navArgs()
    private val viewModel: OrderDetailsViewModel by activityViewModels()

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    viewModel.setImage(fileUri)
                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationStartCallback: LocationCallback
    private lateinit var locationEndCallback: LocationCallback
    private lateinit var locationTripEndCallback: LocationCallback

    // This will store current location info
    private var currentLocation: Location? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        viewModel.setOrder(args.order)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OrderDetailsViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Техника"
                1 -> "Работа"
                2 -> "Прием/возврат запчастей"
                else -> ""
            }
        }.attach()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        locationStartCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    location?.let {
                        viewModel.setStartLocation(it)
                        Toast.makeText(
                            requireContext(),
                            "Location=(${it.latitude}, ${it.longitude})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                fusedLocationProviderClient.removeLocationUpdates(locationStartCallback)
            }
        }

        locationEndCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLocation = location
                    currentLocation?.let {
                        viewModel.setEndLocation(it)
                        Toast.makeText(
                            requireContext(),
                            "Location=(${it.latitude}, ${it.longitude})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                fusedLocationProviderClient.removeLocationUpdates(locationEndCallback)
            }
        }

        locationTripEndCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLocation = location
                    currentLocation?.let {
                        viewModel.setEndTrip(it)
                        Toast.makeText(
                            requireContext(),
                            "Окончание поездки зафиксирована!" +
                                    "\nЛокация=(${it.latitude}, ${it.longitude})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                fusedLocationProviderClient.removeLocationUpdates(locationTripEndCallback)
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@OrderDetailsFragment.viewModel
            startTripBtn.setOnClickListener {
                getLastLocation(locationStartCallback)
            }

            arrivalBtn.setOnClickListener {
                if (this@OrderDetailsFragment.viewModel.startLocationIsSet()) {
                    getLastLocation(locationEndCallback)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Начальная локация не зафиксирована!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            startWorkBtn.setOnClickListener {
                this@OrderDetailsFragment.viewModel.setWorkStart()
                Toast.makeText(
                    requireContext(),
                    "Начала работы зафиксирована!",
                    Toast.LENGTH_LONG
                ).show()
            }

            endWorkBtn.setOnClickListener {
                this@OrderDetailsFragment.viewModel.setWorkEnd()
            }

            endTripBtn.setOnClickListener {
                getLastLocation(locationTripEndCallback)
            }

            addImageBtn.setOnClickListener {
                ImagePicker.with(this@OrderDetailsFragment)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
        }
    }

    private fun getLastLocation(locationCallback: LocationCallback) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}