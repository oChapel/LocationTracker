package ua.com.foxminded.locationtrackera.ui;

import javax.inject.Inject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public class TrackerActivity extends AppCompatActivity implements LoginContract.Host {

    @Inject
    AuthNetwork authNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        App.getComponent().inject(this);

        if (authNetwork.isUserLoggedIn()) {
            ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.trackerFragmentContainerView))
                    .getNavController()
                    .navigate(R.id.trackerFragment);
        }
    }
}
