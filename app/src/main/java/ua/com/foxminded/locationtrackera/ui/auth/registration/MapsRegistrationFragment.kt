package ua.com.foxminded.locationtrackera.ui.auth.registration

import android.view.View
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.util.SafeNavigation

class MapsRegistrationFragment : RegistrationFragment() {

    override fun onClick(view: View) {
        when {
            view === binding?.registerBtn -> super.onClick(view)
            view === binding?.registerLogInTxt -> SafeNavigation.navigate(
                binding?.root, R.id.mapsRegistrationFragment,
                R.id.nav_from_mapsRegistrationFragment_to_mapsLoginFragment
            )
        }
    }

    override fun proceedToNextScreen() {
        SafeNavigation.navigate(
            binding?.root, R.id.mapsRegistrationFragment,
            R.id.nav_from_mapsRegistrationFragment_to_mapsFragment
        )
    }
}
