package ua.com.foxminded.locationtrackera.ui.auth.login;

import android.view.View;
import android.widget.Toast;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.util.SafeNavigation;

public class MapsLoginFragment extends LoginFragment {

    @Override
    public void onClick(View view) {
        if (view == binding.loginBtn) {
            super.onClick(view);
        } else if (view == binding.signUpTxt) {
            SafeNavigation.navigate(binding.getRoot(), R.id.mapsLoginFragment,
                    R.id.nav_from_mapsLoginFragment_to_mapsRegistrationFragment);
        } else if (view == binding.forgotPasswordTxt) {
            SafeNavigation.navigate(binding.getRoot(), R.id.mapsLoginFragment,
                    R.id.nav_from_mapsLoginFragment_to_resetPasswordFragment);
        }
    }

    @Override
    public void proceedToNextScreen() {
        Toast.makeText(requireContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
        SafeNavigation.navigate(binding.getRoot(), R.id.mapsLoginFragment,
                R.id.nav_from_mapsLoginFragment_to_mapsFragment);
    }
}
