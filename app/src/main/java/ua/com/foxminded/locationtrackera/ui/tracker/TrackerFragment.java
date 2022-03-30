package ua.com.foxminded.locationtrackera.ui.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.TrackerFragmentBinding;

public class TrackerFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int GOOGLE_API_AVAILABILITY_REQUEST_CODE = 101;

    private TrackerViewModel viewModel;
    private GoogleMap googleMap;
    private Marker marker;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    setUpTracker();
                } else {
                    Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> enableGpsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> checkPermissionGranted()
            );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(App.getInstance()))
                .get(TrackerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return TrackerFragmentBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        checkGoogleServicesAvailability();
        checkGpsEnabled();

        viewModel.getLocationData().observe(getViewLifecycleOwner(), location -> {
                    final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
        );
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (marker != null) {
            marker.remove();
        }

        final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker = googleMap.addMarker(
                new MarkerOptions()
                        .position(currentLatLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        );

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
    }

    private void checkGoogleServicesAvailability() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (available == ConnectionResult.SUCCESS) {
            return;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(getActivity(), available, GOOGLE_API_AVAILABILITY_REQUEST_CODE)
                    .show();
        } else {
            Toast.makeText(getContext(), R.string.common_google_play_services_unknown_issue, Toast.LENGTH_LONG).show();
        }
    }

    private void checkGpsEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertDialogMessageNoGps();
        } else {
            checkPermissionGranted();
        }
    }

    private void checkPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setUpTracker();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void showAlertDialogMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.no_gps_alert_message)
                .setCancelable(false)
                .setPositiveButton(R.string.settings, (dialogInterface, i) ->
                        enableGpsLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    private void setUpTracker() {
        googleMap.setMyLocationEnabled(true);
        viewModel.setUpLocationServices();
        viewModel.startLocationUpdates();
    }
}