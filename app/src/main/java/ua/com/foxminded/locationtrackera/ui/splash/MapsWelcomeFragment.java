package ua.com.foxminded.locationtrackera.ui.splash;

import android.view.View;

import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.ui.splash.WelcomeFragment;

public class MapsWelcomeFragment extends WelcomeFragment {

    @Override
    public void onClick(View view) {
        if (view == binding.welcomeRegisterBtn) {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_mapsWelcomeFragment_to_mapsRegistrationFragment);
        } else if (view == binding.welcomeLoginTxt) {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_mapsWelcomeFragment_to_mapsLoginFragment);
        }
    }
}
