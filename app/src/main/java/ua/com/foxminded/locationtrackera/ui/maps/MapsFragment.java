package ua.com.foxminded.locationtrackera.ui.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
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

import java.util.ArrayList;
import java.util.List;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.FragmentMapsBinding;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.mvi.HostedFragment;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenEffect;
import ua.com.foxminded.locationtrackera.ui.maps.state.MapsScreenState;

public class MapsFragment extends HostedFragment<
        MapsContract.View,
        MapsScreenState,
        MapsScreenEffect,
        MapsContract.ViewModel,
        MapsContract.Host> implements MapsContract.View, OnMapReadyCallback {

    private static final double DEFAULT_LOCATIONS_RETRIEVING_TIME_HOURS = 12;

    private final List<Marker> markerList = new ArrayList<>();
    private FragmentMapsBinding binding;
    private GoogleMap googleMap;
    private MaterialDatePicker<Pair<Long, Long>> picker;

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

        picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.select_date_range)
                .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                .build();

        picker.addOnNegativeButtonClickListener(click -> picker.dismiss());
        picker.addOnPositiveButtonClickListener(selection ->
                getModel().retrieveLocationsByDate(selection.first, selection.second));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        getModel().retrieveLocationsByDate(
                System.currentTimeMillis() - DEFAULT_LOCATIONS_RETRIEVING_TIME_HOURS * 3600,
                System.currentTimeMillis()
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.maps_menu_item_logout) {
            getModel().logout();
        } else if (item.getItemId() == R.id.maps_menu_timeline) {
            picker.show(getChildFragmentManager(), "date_picker");
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