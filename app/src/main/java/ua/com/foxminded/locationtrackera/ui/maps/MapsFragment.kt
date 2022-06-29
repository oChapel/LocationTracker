package ua.com.foxminded.locationtrackera.ui.maps

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.databinding.FragmentMapsBinding
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.mvi.fragments.HostedFragment
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState
import ua.com.foxminded.locationtrackera.util.SafeNavigation
import java.util.*

class MapsFragment : HostedFragment<
        MapsContract.View,
        MapsScreenState,
        MapsScreenEffect,
        MapsContract.ViewModel,
        MapsContract.Host>(),
    MapsContract.View, OnMapReadyCallback {

    private var binding: FragmentMapsBinding? = null

    private val markerList: MutableList<Marker> = ArrayList()
    private var googleMap: GoogleMap? = null
    private var startTimePoint: Long = 0

    override fun createModel(): MapsContract.ViewModel =
        ViewModelProvider(this, AuthViewModelFactory()).get(MapsViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.mapsToolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.timeline)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.maps_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().moveTaskToBack(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        model?.retrieveDefaultLocations()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps_menu_item_logout -> model?.logout()
            R.id.maps_menu_timeline -> getDatePicker(0, R.string.select_start_date).show(
                childFragmentManager, "date_picker_0"
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun proceedToSplashScreen() {
        Toast.makeText(context, R.string.logged_out, Toast.LENGTH_SHORT).show()
        SafeNavigation.navigate(
            binding?.root, R.id.mapsFragment,
            R.id.nav_from_mapsFragment_to_mapsWelcomeFragment
        )
    }

    override fun placeLocationMarkers(locationList: List<UserLocation>) {
        if (markerList.isNotEmpty()) {
            for (marker in markerList) {
                marker.remove()
            }
        }
        markerList.clear()
        for (userLocation in locationList) {
            val location = LatLng(userLocation.latitude, userLocation.longitude)
            val marker: Marker? = googleMap?.addMarker(MarkerOptions().position(location))
            marker?.let { markerList.add(it) }
        }
        zoomCamera()
    }

    override fun showToastMessage(resId: Int) =
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()

    private fun getDatePicker(code: Int, resId: Int): MaterialDatePicker<Long> {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resId)
            .build()
        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener { selection: Long ->
            val stringRes: Int =
                if (code == 0) R.string.select_start_time else R.string.select_end_time
            getTimePicker(code, stringRes, Date(selection))
                .show(childFragmentManager, "time_picker_$code")
        }
        return picker
    }

    private fun getTimePicker(code: Int, resId: Int, selectedDate: Date): MaterialTimePicker {
        val picker = MaterialTimePicker.Builder()
            .setTitleText(resId)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()
        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate
            calendar.set(
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DATE],
                picker.hour, picker.minute
            )
            if (code == 0) {
                startTimePoint = calendar.timeInMillis
                getDatePicker(1, R.string.select_end_date).show(
                    childFragmentManager, "date_picker_1"
                )
            } else if (code == 1) {
                model?.retrieveLocationsByDate(startTimePoint, calendar.timeInMillis)
            }
        }
        return picker
    }

    private fun zoomCamera() {
        val boundsBuilder: LatLngBounds.Builder = LatLngBounds.Builder()
        for (marker in markerList) {
            boundsBuilder.include(marker.position)
        }
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 120)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
