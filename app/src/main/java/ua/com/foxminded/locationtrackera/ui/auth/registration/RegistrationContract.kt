package ua.com.foxminded.locationtrackera.ui.auth.registration

import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState
import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract

class RegistrationContract {
    interface ViewModel :
        FragmentContract.ViewModel<RegistrationScreenState, RegistrationScreenEffect> {
        fun registerUser(username: String?, email: String?, password: String?)
        fun registrationDataChanged(username: String?, email: String?, password: String?)
    }

    interface View : FragmentContract.View {
        fun setProgressVisibility(isProgressVisible: Boolean)
        fun showErrors(usernameError: Int, emailError: Int, passwordError: Int)
        fun proceedToNextScreen()
        fun showFailureToastMessage(resId: Int)
    }

    interface Host : FragmentContract.Host
}