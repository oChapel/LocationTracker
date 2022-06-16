package ua.com.foxminded.locationtrackera.ui.auth.registration;

import android.view.View;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.util.SafeNavigation;

public class MapsRegistrationFragment extends RegistrationFragment {

    @Override
    public void onClick(View view) {
        if (view == binding.registerBtn) {
            super.onClick(view);
        } else if (view == binding.registerLogInTxt) {
            SafeNavigation.navigate(binding.getRoot(), R.id.mapsRegistrationFragment,
                    R.id.nav_from_mapsRegistrationFragment_to_mapsLoginFragment);
        }
    }

    @Override
    public void proceedToNextScreen() {
        SafeNavigation.navigate(binding.getRoot(), R.id.mapsRegistrationFragment,
                R.id.nav_from_mapsRegistrationFragment_to_mapsFragment);
    }
}
