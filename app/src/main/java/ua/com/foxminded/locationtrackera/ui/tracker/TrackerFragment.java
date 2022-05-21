package ua.com.foxminded.locationtrackera.ui.tracker;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.background.LocationService;
import ua.com.foxminded.locationtrackera.databinding.FragmentTrackerBinding;
import ua.com.foxminded.locationtrackera.model.gps.GpsStatusConstants;
import ua.com.foxminded.locationtrackera.mvi.HostedFragment;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.tracker.dialog.TrackerDialogFragment;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenEffect;
import ua.com.foxminded.locationtrackera.ui.tracker.state.TrackerScreenState;

public class TrackerFragment extends HostedFragment<
        TrackerContract.View,
        TrackerScreenState,
        TrackerScreenEffect,
        TrackerContract.ViewModel,
        TrackerContract.Host> implements TrackerContract.View {

    private static final int GOOGLE_API_AVAILABILITY_REQUEST_CODE = 101;

    private FragmentTrackerBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startService();
                } else {
                    Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected TrackerContract.ViewModel createModel() {
        return new ViewModelProvider(this,
                new AuthViewModelFactory()).get(TrackerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTrackerBinding.inflate(inflater, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tracker_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkGoogleServicesAvailability();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_logout) {
            getModel().logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void proceedToSplashScreen() {
        stopService();
        Toast.makeText(getContext(), R.string.logged_out, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.nav_from_trackerFragment_to_welcomeFragment);
    }

    @Override
    public void changeGpsStatus(int gpsStatus) {
        if (gpsStatus == GpsStatusConstants.FIX_ACQUIRED) {
            binding.gpsStatus.setText(R.string.enabled);
            binding.gpsStatus.setTextColor(getResources().getColor(R.color.green_500));
        } else if (gpsStatus == GpsStatusConstants.FIX_NOT_ACQUIRED) {
            binding.gpsStatus.setText(R.string.disabled);
            binding.gpsStatus.setTextColor(getResources().getColor(R.color.red_500));
        } else if (gpsStatus == GpsStatusConstants.CONNECTING) {
            binding.gpsStatus.setText(R.string.connecting);
            binding.gpsStatus.setTextColor(getResources().getColor(R.color.yellow_700));
        }
    }

    @Override
    public void changeServiceStatus(boolean isEnabled) {
        if (isEnabled) {
            binding.serviceStatus.setText(R.string.enabled);
            binding.serviceStatus.setTextColor(getResources().getColor(R.color.green_500));
        } else {
            binding.serviceStatus.setText(R.string.disabled);
            binding.serviceStatus.setTextColor(getResources().getColor(R.color.red_500));
        }

    }

    @Override
    public void showDialogFragment(int argType, int message, int negativeButton, int positiveButton) {
        final TrackerDialogFragment dialog
                = TrackerDialogFragment.newInstance(argType, message, negativeButton, positiveButton);
        dialog.show(getChildFragmentManager(), "logout_dialog");
    }

    public void doPositiveButton(int code) {
        getModel().setDialogResponse(code);
    }

    public void doNegativeButton(int code) {
        if (code == 0) {
            getModel().setDialogResponse(1);
        }
    }

    private void checkGoogleServicesAvailability() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());
        if (available == ConnectionResult.SUCCESS) {
            checkPermissionGranted();
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(requireActivity(), available, GOOGLE_API_AVAILABILITY_REQUEST_CODE)
                    .show();
        } else {
            Toast.makeText(getContext(), R.string.common_google_play_services_unknown_issue, Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startService();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void startService() {
        if (!isLocationServiceRunning()) {
            getModel().setSharedPreferencesServiceFlag(true);
            final Intent serviceIntent = new Intent(getContext(), LocationService.class);
            ContextCompat.startForegroundService(requireContext(), serviceIntent);
        }
    }

    private void stopService() {
        if (isLocationServiceRunning()) {
            getModel().setSharedPreferencesServiceFlag(false);
            final Intent serviceIntent = new Intent(getContext(), LocationService.class);
            requireActivity().stopService(serviceIntent);
        }
    }

    private boolean isLocationServiceRunning() {
        final ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}