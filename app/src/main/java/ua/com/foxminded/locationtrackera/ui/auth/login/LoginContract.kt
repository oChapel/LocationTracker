package ua.com.foxminded.locationtrackera.ui.auth.login

import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState
import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract

class LoginContract {
    interface ViewModel : FragmentContract.ViewModel<LoginScreenState, LoginScreenEffect> {
        fun login(email: String?, password: String?)
    }

    interface View : FragmentContract.View {
        fun setProgressVisibility(isProgressVisible: Boolean)
        fun proceedToNextScreen()
        fun showFailureToastMessage()
        fun showEmailAndPasswordError(emailError: Int, passwordError: Int)
    }

    interface Host : FragmentContract.Host
}