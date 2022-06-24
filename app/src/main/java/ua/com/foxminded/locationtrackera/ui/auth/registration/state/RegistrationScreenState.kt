package ua.com.foxminded.locationtrackera.ui.auth.registration.state

import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationContract
import ua.com.foxminded.locationtrackera.mvi.states.ScreenState

abstract class RegistrationScreenState(
    val isProgressVisible: Boolean = false,
    val usernameError: Int = 0,
    val emailError: Int = 0,
    val passwordError: Int = 0
) : ScreenState<RegistrationContract.View>() {

    override fun visit(screen: RegistrationContract.View) {
        screen.setProgressVisibility(isProgressVisible)
        screen.showErrors(usernameError, emailError, passwordError)
    }

    class RegistrationProgress(isProgressVisible: Boolean) :
        RegistrationScreenState(isProgressVisible)

    class RegistrationError(usernameError: Int, emailError: Int, passwordError: Int) :
        RegistrationScreenState(false, usernameError, emailError, passwordError)
}