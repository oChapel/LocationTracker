package ua.com.foxminded.locationtrackera.ui.auth.reset.state

import ua.com.foxminded.locationtrackera.mvi.states.ScreenState
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordContract

sealed class ResetPasswordScreenState(
    val isProgressVisible: Boolean = false,
    val emailError: Int = 0
) : ScreenState<ResetPasswordContract.View>() {

    override fun visit(screen: ResetPasswordContract.View) {
        screen.setProgressVisibility(isProgressVisible)
        screen.showEmailError(emailError)
    }

    class ResetPasswordProgress(isProgressVisible: Boolean) :
        ResetPasswordScreenState(isProgressVisible)

    class ResetPasswordError(emailError: Int) : ResetPasswordScreenState(false, emailError)
}
