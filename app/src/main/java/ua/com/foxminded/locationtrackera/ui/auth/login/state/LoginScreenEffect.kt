package ua.com.foxminded.locationtrackera.ui.auth.login.state

import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract

sealed class LoginScreenEffect : AbstractEffect<LoginContract.View>() {

    class LoginSuccessful : LoginScreenEffect() {
        override fun handle(screen: LoginContract.View) {
            screen.proceedToNextScreen()
        }
    }

    class LoginFailed : LoginScreenEffect() {
        override fun handle(screen: LoginContract.View) {
            screen.showFailureToastMessage()
        }
    }
}
