package ua.com.foxminded.locationtrackera.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerAppComponent;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public class TrackerActivity extends AppCompatActivity implements LoginContract.Host {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        App.setAppComponent(DaggerAppComponent.create());

        final TrackerHostViewModel viewModel
                = new ViewModelProvider(this, new AuthViewModelFactory()).get(TrackerHostViewModel.class);
        viewModel.getIsUserLoggedInStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                navigateToMainScreen();
            }
        });

        viewModel.checkUserLoggedIn();
    }

    private void navigateToMainScreen() {
        ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.trackerHostContainerView))
                .getNavController()
                .navigate(R.id.trackerFragment);
    }
}
