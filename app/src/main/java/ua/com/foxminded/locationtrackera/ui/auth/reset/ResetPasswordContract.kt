package ua.com.foxminded.locationtrackera.ui.auth.reset

import ua.com.foxminded.locationtrackera.mvi.fragments.FragmentContract
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState

class ResetPasswordContract {
    interface ViewModel : FragmentContract.ViewModel<
            ResetPasswordScreenState,
            ResetPasswordScreenEffect> {
        fun resetPassword(email: String?)
    }

    interface View : FragmentContract.View {
        fun setProgressVisibility(isProgressVisible: Boolean)
        fun showToastMessage(idStringResource: Int)
        fun showEmailError(emailError: Int)
    }

    interface Host : FragmentContract.Host
}
