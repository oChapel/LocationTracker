package ua.com.foxminded.locationtrackera.ui.auth.login

import android.view.View
import android.widget.Toast
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.util.SafeNavigation

class MapsLoginFragment : LoginFragment() {
    override fun onClick(view: View) {
        when {
            view === binding.loginBtn -> super.onClick(view)
            view === binding.signUpTxt -> SafeNavigation.navigate(
                binding.root, R.id.mapsLoginFragment,
                R.id.nav_from_mapsLoginFragment_to_mapsRegistrationFragment
            )
            view === binding.forgotPasswordTxt -> SafeNavigation.navigate(
                binding.root, R.id.mapsLoginFragment,
                R.id.nav_from_mapsLoginFragment_to_resetPasswordFragment
            )
        }
    }

    override fun proceedToNextScreen() {
        Toast.makeText(requireContext(), R.string.successful_login, Toast.LENGTH_SHORT).show()
        SafeNavigation.navigate(
            binding.root, R.id.mapsLoginFragment,
            R.id.nav_from_mapsLoginFragment_to_mapsFragment
        )
    }
}
