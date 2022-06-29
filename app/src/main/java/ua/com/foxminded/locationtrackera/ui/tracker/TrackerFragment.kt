package ua.com.foxminded.locationtrackera.ui.tracker

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.background.LocationService
import ua.com.foxminded.locationtrackera.databinding.FragmentTrackerBinding
import ua.com.foxminded.locationtrackera.models_impl.gps.GpsStatusConstants
import ua.com.foxminded.locationtrackera.mvi.fragments.HostedFragment
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.TrackerDialogArgTypes
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.TrackerDialogFragment
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState
import ua.com.foxminded.locationtrackera.util.SafeNavigation

class TrackerFragment : HostedFragment<
        TrackerContract.View,
        TrackerScreenState,
        TrackerScreenEffect,
        TrackerContract.ViewModel,
        TrackerContract.Host>(),
    TrackerContract.View, View.OnClickListener {

    private var binding: FragmentTrackerBinding? = null

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                checkBackgroundLocationPermissionGranted()
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }

    override fun createModel(): TrackerContract.ViewModel =
        ViewModelProvider(this, AuthViewModelFactory()).get(TrackerViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackerBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tracker_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
        binding?.trackerStartStopBtn?.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_logout) model?.logout()
        return super.onOptionsItemSelected(item)
    }

    override fun proceedToSplashScreen() {
        stopService()
        Toast.makeText(context, R.string.logged_out, Toast.LENGTH_SHORT).show()
        SafeNavigation.navigate(
            binding?.root, R.id.trackerFragment,
            R.id.nav_from_trackerFragment_to_welcomeFragment
        )
    }

    override fun changeGpsStatus(gpsStatus: Int) {
        when (gpsStatus) {
            GpsStatusConstants.FIX_ACQUIRED -> {
                binding?.gpsStatus?.setText(R.string.enabled)
                binding?.gpsStatus?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.green_500)
                )
            }
            GpsStatusConstants.FIX_NOT_ACQUIRED -> {
                binding?.gpsStatus?.setText(R.string.disabled)
                binding?.gpsStatus?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red_500)
                )
            }
            GpsStatusConstants.ACTIVE -> {
                binding?.gpsStatus?.setText(R.string.connecting)
                binding?.gpsStatus?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.yellow_700)
                )
            }
            GpsStatusConstants.NO_PERMISSION -> {
                binding?.gpsStatus?.setText(R.string.no_permission)
            }
        }
    }

    override fun changeServiceStatus(isEnabled: Boolean) {
        if (isEnabled) {
            binding?.serviceStatus?.setText(R.string.enabled)
            binding?.serviceStatus?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.green_500)
            )
            binding?.trackerStartStopBtn?.setText(R.string.stop_service)
        } else {
            binding?.serviceStatus?.setText(R.string.disabled)
            binding?.serviceStatus?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.red_500)
            )
            binding?.trackerStartStopBtn?.setText(R.string.start_service)
        }
    }

    override fun showDialogFragment(
        argType: Int, message: Int, negativeButton: Int, positiveButton: Int
    ) {
        TrackerDialogFragment.newInstance(argType, message, negativeButton, positiveButton)
            .show(childFragmentManager, TrackerDialogFragment.TAG)
    }

    override fun onClick(view: View) {
        if (view === binding?.trackerStartStopBtn) {
            if (isLocationServiceRunning) {
                stopService()
            } else {
                checkGoogleServicesAvailability()
            }
        }
    }

    fun doPositiveButton(code: Int) = model?.setDialogResponse(code)

    fun doNegativeButton(code: Int) {
        if (code == TrackerDialogArgTypes.CODE_0) {
            model?.setDialogResponse(TrackerDialogArgTypes.CODE_1)
        }
    }

    private fun checkGoogleServicesAvailability() {
        val available: Int =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        when {
            available == ConnectionResult.SUCCESS -> checkPermissionGranted()
            GoogleApiAvailability.getInstance().isUserResolvableError(available) ->
                GoogleApiAvailability.getInstance()
                    .getErrorDialog(
                        requireActivity(), available,
                        GOOGLE_API_AVAILABILITY_REQUEST_CODE
                    )
                    ?.show()
            else -> Toast.makeText(
                context, R.string.common_google_play_services_unknown_issue, Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkBackgroundLocationPermissionGranted()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun checkBackgroundLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            startService()
            return
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startService()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun startService() {
        if (!isLocationServiceRunning) {
            model?.setSharedPreferencesServiceFlag(true)
            ContextCompat.startForegroundService(
                requireContext(), LocationService.getIntent(requireContext())
            )
        }
    }

    private fun stopService() {
        if (isLocationServiceRunning) {
            model?.setSharedPreferencesServiceFlag(false)
            requireActivity().stopService(LocationService.getIntent(requireContext()))
        }
    }

    @Suppress("deprecation")
    private val isLocationServiceRunning: Boolean
        get() {
            val manager: ActivityManager =
                requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationService::class.java.name == service.service.className) return true
            }
            return false
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val GOOGLE_API_AVAILABILITY_REQUEST_CODE = 101
    }
}
