package ua.com.foxminded.locationtrackera.ui.splash

import android.view.View
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.util.SafeNavigation

class MapsWelcomeFragment : WelcomeFragment() {

    override fun onClick(view: View) {
        when {
            view === binding.welcomeRegisterBtn -> SafeNavigation.navigate(
                binding.root, R.id.mapsWelcomeFragment,
                R.id.nav_from_mapsWelcomeFragment_to_mapsRegistrationFragment
            )
            view === binding.welcomeLoginTxt -> SafeNavigation.navigate(
                binding.root, R.id.mapsWelcomeFragment,
                R.id.nav_from_mapsWelcomeFragment_to_mapsLoginFragment
            )
        }
    }
}