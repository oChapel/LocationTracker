package ua.com.foxminded.locationtrackera.ui.auth.registration.state

import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationContract

abstract class RegistrationScreenEffect : AbstractEffect<RegistrationContract.View>() {

    class RegistrationSuccessful : RegistrationScreenEffect() {
        override fun handle(screen: RegistrationContract.View) {
            screen.proceedToNextScreen()
        }
    }

    class RegistrationFailed(val stringResId: Int) : RegistrationScreenEffect() {
        override fun handle(screen: RegistrationContract.View) {
            screen.showFailureToastMessage(stringResId)
        }
    }
}
