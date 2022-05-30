package ua.com.foxminded.locationtrackera.ui.auth.registration;

import android.view.View;

import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;

public class MapsRegistrationFragment extends RegistrationFragment {

    @Override
    public void onClick(View view) {
        if (view == binding.registerBtn) {
            super.onClick(view);
        } else if (view == binding.registerLogInTxt) {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_mapsRegistrationFragment_to_mapsLoginFragment);
        }
    }

    @Override
    public void proceedToNextScreen() {
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.nav_from_mapsRegistrationFragment_to_mapsFragment);
    }
}
