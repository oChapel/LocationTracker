package ua.com.foxminded.locationtrackera.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public class MapsActivity extends AppCompatActivity implements LoginContract.Host {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final MapsHostViewModel viewModel
                = new ViewModelProvider(this, new AuthViewModelFactory()).get(MapsHostViewModel.class);
        viewModel.getIsUserLoggedInStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                navigateToMainScreen();
            }
        });

        viewModel.checkUserLoggedIn();
    }

    private void navigateToMainScreen() {
        ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mapsHostContainerView))
                .getNavController()
                .navigate(R.id.mapsFragment);
    }
}
