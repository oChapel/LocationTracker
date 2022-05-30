package ua.com.foxminded.locationtrackera.ui.maps;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.FragmentMapsBinding;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.mvi.HostedFragment;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.maps.dialog.MapsDialogFragment;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState;

public class MapsFragment extends HostedFragment<
        MapsContract.View,
        MapsScreenState,
        MapsScreenEffect,
        MapsContract.ViewModel,
        MapsContract.Host> implements MapsContract.View, OnMapReadyCallback {

    private final List<Marker> markerList = new ArrayList<>();
    private FragmentMapsBinding binding;
    private GoogleMap googleMap;
    private long startTimePoint;

    @Override
    protected MapsContract.ViewModel createModel() {
        return new ViewModelProvider(this, new AuthViewModelFactory()).get(MapsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.mapsToolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.timeline);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.maps_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getModel().onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        getModel().retrieveDefaultLocations();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.maps_menu_item_logout) {
            getModel().logout();
        } else if (item.getItemId() == R.id.maps_menu_timeline) {
            getDatePicker(0, R.string.select_start_date).show(getChildFragmentManager(), "date_picker_0");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void proceedToSplashScreen() {
        Toast.makeText(getContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.nav_from_mapsFragment_to_mapsWelcomeFragment);
    }

    @Override
    public void placeLocationMarkers(List<UserLocation> locationList) {
        if (!markerList.isEmpty()) {
            for (Marker marker : markerList) {
                marker.remove();
            }
        }

        markerList.clear();

        for (UserLocation userLocation : locationList) {
            final LatLng location = new LatLng(userLocation.latitude, userLocation.longitude);
            final Marker marker = googleMap.addMarker(new MarkerOptions().position(location));
            markerList.add(marker);
        }

        zoomCamera();
    }

    @Override
    public void showToastMessage(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialogFragment() {
        final MapsDialogFragment dialog = new MapsDialogFragment();
        dialog.show(getChildFragmentManager(), "logout_dialog");
    }

    public void doPositiveButton() {
        getModel().logout();
    }

    private MaterialDatePicker<Long> getDatePicker(int code, int resId) {
        final MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(resId)
                .build();

        datePicker.addOnNegativeButtonClickListener(click -> datePicker.dismiss());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            int stringRes;
            if (code == 0) {
                stringRes = R.string.select_start_time;
            } else {
                stringRes = R.string.select_end_time;
            }
            getTimePicker(code, stringRes, new Date(selection)).show(getChildFragmentManager(), "time_picker_" + code);
        });

        return datePicker;
    }

    private MaterialTimePicker getTimePicker(int code, int resId, Date selectedDate) {
        final MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText(resId)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

        timePicker.addOnNegativeButtonClickListener(click -> timePicker.dismiss());
        timePicker.addOnPositiveButtonClickListener(click -> {
            final Calendar calendar = Calendar.getInstance();

            calendar.setTime(selectedDate);
            calendar.set(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE), timePicker.getHour(), timePicker.getMinute()
            );

            if (code == 0) {
                startTimePoint = calendar.getTimeInMillis();
                getDatePicker(1, R.string.select_end_date).show(getChildFragmentManager(), "date_picker_1");
            } else if (code == 1) {
                getModel().retrieveLocationsByDate(startTimePoint, calendar.getTimeInMillis());
            }
        });

        return timePicker;
    }

    private void zoomCamera() {
        final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            boundsBuilder.include(marker.getPosition());
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                boundsBuilder.build(),
                120)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}