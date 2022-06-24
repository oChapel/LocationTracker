package ua.com.foxminded.locationtrackera.ui.auth.reset.state

import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordContract
import ua.com.foxminded.locationtrackera.mvi.states.AbstractEffect

abstract class ResetPasswordScreenEffect : AbstractEffect<ResetPasswordContract.View>() {

    class ResetPasswordShowStatus(val idStringResource: Int) : ResetPasswordScreenEffect() {
        override fun handle(screen: ResetPasswordContract.View) {
            screen.showToastMessage(idStringResource)
        }
    }
}