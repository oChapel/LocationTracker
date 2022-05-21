package ua.com.foxminded.locationtrackera.ui.auth.login;

import android.view.View;
import android.widget.Toast;

import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;

public class MapsLoginFragment extends LoginFragment {

    @Override
    public void onClick(View view) {
        if (view == binding.loginBtn) {
            super.onClick(view);
        } else if (view == binding.signUpTxt) {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_mapsLoginFragment_to_mapsRegistrationFragment);
        } else if (view == binding.forgotPasswordTxt) {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_mapsLoginFragment_to_resetPasswordFragment);
        }
    }

    @Override
    public void proceedToNextScreen() {
        Toast.makeText(requireContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.nav_from_mapsLoginFragment_to_mapsFragment);
    }
}
