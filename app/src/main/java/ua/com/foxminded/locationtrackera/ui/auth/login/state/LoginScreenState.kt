package ua.com.foxminded.locationtrackera.ui.auth.login.state

import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract
import ua.com.foxminded.locationtrackera.mvi.states.ScreenState

abstract class LoginScreenState(
    val isProgressVisible: Boolean = false,
    val emailError: Int = 0,
    val passwordError: Int = 0
) : ScreenState<LoginContract.View>() {

    override fun visit(screen: LoginContract.View) {
        screen.setProgressVisibility(isProgressVisible)
        screen.showEmailAndPasswordError(emailError, passwordError)
    }

    class LoginProgress(isProgressVisible: Boolean) : LoginScreenState(isProgressVisible)

    class LoginError(emailError: Int, passwordError: Int) :
        LoginScreenState(false, emailError, passwordError)
}