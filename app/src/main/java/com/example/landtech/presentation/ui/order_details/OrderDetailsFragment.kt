package com.example.landtech.presentation.ui.order_details

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.landtech.R
import com.example.landtech.databinding.FragmentOrderDetailsBinding
import com.example.landtech.domain.models.ScreenStatus
import com.example.landtech.domain.services.LocationTrackingService
import com.example.landtech.domain.utils.isServiceRunning
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private val args: OrderDetailsFragmentArgs by navArgs()
    private val viewModel: OrderDetailsViewModel by activityViewModels()

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationTripEndCallback: LocationCallback

    // This will store current location info
    private var currentLocation: Location? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleOnBackPressedOrderDetails()
                }
            })

    }

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

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.orders_details_appbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        saveOrder()
                        true
                    }

                    else -> handleOnBackPressedOrderDetails()
                }
            }
        }, viewLifecycleOwner)

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

        val receiverStartLocation = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val startLocationLatitude = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
                val startLocationLongitude = intent?.getDoubleExtra("lng", 0.0) ?: 0.0

                viewModel.setStartLocation(startLocationLatitude, startLocationLongitude)
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                receiverStartLocation,
                IntentFilter(LocationTrackingService.BROADCAST_START_LOCATION)
            )

        val receiverEndLocation = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val startLocationLatitude = intent?.getDoubleExtra("startLat", 0.0) ?: 0.0
                val startLocationLongitude = intent?.getDoubleExtra("startLng", 0.0) ?: 0.0
                val endLocationLatitude = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
                val endLocationLongitude = intent?.getDoubleExtra("lng", 0.0) ?: 0.0
                val distance = intent?.getDoubleExtra("distance", 0.0) ?: 0.0

                viewModel.setStartLocation(startLocationLatitude, startLocationLongitude)
                viewModel.setEndLocation(endLocationLatitude, endLocationLongitude, distance)
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                receiverEndLocation,
                IntentFilter(LocationTrackingService.BROADCAST_END_LOCATION)
            )

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

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
            this@OrderDetailsFragment.viewModel.isMainUser.observe(viewLifecycleOwner) {
                setEnabled(it, this@OrderDetailsFragment.viewModel.isInEngineersList.value)
            }

            this@OrderDetailsFragment.viewModel.isInEngineersList.observe(viewLifecycleOwner) {
                setEnabled(this@OrderDetailsFragment.viewModel.isMainUser.value, it)
            }

            this@OrderDetailsFragment.viewModel.worksHaveEnded.observe(viewLifecycleOwner) {
                if (it) setWorksEnded()
            }

            this@OrderDetailsFragment.viewModel.screenStatus.observe(viewLifecycleOwner) {
                if (it == ScreenStatus.READY) {
                    constraintLayout.isEnabled = true
                    progressBar.visibility = View.INVISIBLE
                } else if (it == ScreenStatus.LOADING) {
                    constraintLayout.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
            }
            this@OrderDetailsFragment.viewModel.locationOrderId.observe(viewLifecycleOwner) {}
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@OrderDetailsFragment.viewModel
            startTripBtn.setOnClickListener {
                if (isServiceRunning(
                        requireActivity().applicationContext,
                        LocationTrackingService::class.java
                    )
                ) {
                    Toast.makeText(requireContext(), "Сервис уже работает!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(requireContext(), "Отслеживаем локацию", Toast.LENGTH_SHORT)
                        .show()
                    Intent(
                        requireActivity().applicationContext,
                        LocationTrackingService::class.java
                    ).apply {
                        action = LocationTrackingService.ACTION_START
                        putExtra("orderId", args.order.id)
                        requireActivity().startService(this)
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            voiceDefinitionBtn.setOnClickListener {
//                val language = "ru-RU"
//
//                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//                intent.putExtra(
//                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, language
//                )
//                intent.putExtra(
//                    RecognizerIntent.EXTRA_LANGUAGE, language
//                )
//
//                startSpeechToText.launch(intent)
                this@OrderDetailsFragment.viewModel.order.value?.id?.let {
                    findNavController().navigate(
                        OrderDetailsFragmentDirections.actionOrderDetailsFragmentToAudioRecordFragment(
                            it
                        )
                    )
                }
            }
            arrivalBtn.setOnClickListener {
                if (this@OrderDetailsFragment.viewModel.startLocationIsSet()) {
                    Intent(
                        requireActivity().applicationContext,
                        LocationTrackingService::class.java
                    ).apply {
                        action = LocationTrackingService.ACTION_STOP
                        putExtra("orderId", args.order.id)
                        requireActivity().startService(this)
                    }
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
                    "Начало работы зафиксировано!",
                    Toast.LENGTH_LONG
                ).show()
            }

            endWorkBtn.setOnClickListener {
                if (this@OrderDetailsFragment.viewModel.startWorkIsSet()) {
                    this@OrderDetailsFragment.viewModel.setWorkEnd()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Начало работы не зафиксировано!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            signWorks.setOnClickListener {
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToSignatureFragment())
            }

            endAllWorks.setOnClickListener {
                this@OrderDetailsFragment.viewModel.setAllWorksEnd()
            }

            endTripBtn.setOnClickListener {
                getLastLocation(locationTripEndCallback)
            }

            addImageBtn.setOnClickListener {
                this@OrderDetailsFragment.viewModel.order.value?.id?.let { orderId ->
                    findNavController().navigate(
                        OrderDetailsFragmentDirections.actionOrderDetailsFragmentToImagesFragment(
                            orderId
                        )
                    )
                }
            }

            addAutoBtn.setOnClickListener {
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionOrderDetailsFragmentToExploitationObjectSelectFragment(
                        false
                    )
                )
            }
        }
    }

    private fun setEnabled(isMainUser: Boolean?, isInEngineersList: Boolean?) {
        val isInEngineers = isInEngineersList ?: false

        isMainUser?.let {
            binding.apply {
                startTripBtn.isEnabled = it || isInEngineers
                arrivalBtn.isEnabled = it || isInEngineers
                startWorkBtn.isEnabled = it || isInEngineers
                endWorkBtn.isEnabled = it || isInEngineers
                endAllWorks.isEnabled = it
                voiceDefinitionBtn.isEnabled = it
                endTripBtn.isEnabled = it
                addAutoBtn.isEnabled = it
                addImageBtn.isEnabled = it
            }
        }
    }

    private fun setWorksEnded() {
        binding.apply {
            startTripBtn.isEnabled = false
            arrivalBtn.isEnabled = false
            startWorkBtn.isEnabled = false
            endWorkBtn.isEnabled = false
            endAllWorks.isEnabled = false
            endTripBtn.isEnabled = false
            addAutoBtn.isEnabled = false
            addImageBtn.isEnabled = false
        }
    }

    private fun handleOnBackPressedOrderDetails(): Boolean {
        if (viewModel.isModified) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Сохранить изменения?")
                setMessage("Заказ был изменен. Хотите сохранить изменения?")
                setPositiveButton("Да") { _, _ ->
                    saveOrder()
                    viewModel.clearAddedUsedParts()
                    findNavController().navigateUp()
                }
                setNegativeButton("Нет") { _, _ ->
                    viewModel.reset()
                    viewModel.clearAddedUsedParts()
                    findNavController().navigateUp()
                }
                show()
            }
        } else {
            viewModel.clearAddedUsedParts()
            findNavController().navigateUp()
        }

        return true
    }

    private fun getLastLocation(locationCallback: LocationCallback) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.screenStatusLoading()
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun saveOrder() {
        val result = viewModel.saveOrder()
        if (result)
            Toast.makeText(
                requireContext(),
                "Заказ сохранен!",
                Toast.LENGTH_LONG
            ).show()
        else {
            Toast.makeText(
                requireContext(),
                "Ошибка заполнения таблицы возвращенных запчастей!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}