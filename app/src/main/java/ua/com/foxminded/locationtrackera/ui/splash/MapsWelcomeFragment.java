package ua.com.foxminded.locationtrackera.ui.splash;

import android.view.View;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.util.SafeNavigation;

public class MapsWelcomeFragment extends WelcomeFragment {

    @Override
    public void onClick(View view) {
        if (view == binding.welcomeRegisterBtn) {
            SafeNavigation.navigate(binding.getRoot(), R.id.mapsWelcomeFragment,
                    R.id.nav_from_mapsWelcomeFragment_to_mapsRegistrationFragment);
        } else if (view == binding.welcomeLoginTxt) {
            SafeNavigation.navigate(binding.getRoot(), R.id.mapsWelcomeFragment,
                    R.id.nav_from_mapsWelcomeFragment_to_mapsLoginFragment);
        }
    }
}
