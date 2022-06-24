package ua.com.foxminded.locationtrackera.ui.auth.reset

import androidx.lifecycle.Lifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.util.Result
import ua.com.foxminded.locationtrackera.ui.auth.AuthErrorConstants
import ua.com.foxminded.locationtrackera.ui.auth.Credentials
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState
import ua.com.foxminded.locationtrackera.mvi.MviViewModel

class ResetPasswordViewModel(private val authNetwork: AuthNetwork) : MviViewModel<
        ResetPasswordScreenState,
        ResetPasswordScreenEffect>(),
    ResetPasswordContract.ViewModel {

    private val credsSupplier: PublishSubject<Credentials> = PublishSubject.create()

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            setUpResetPasswordChain()
        }
    }

    private fun setUpResetPasswordChain() {
        addTillDestroy(
            credsSupplier.observeOn(Schedulers.io())
                .flatMapSingle { creds: Credentials ->
                    if (creds.isEmailValid) {
                        postState(ResetPasswordScreenState.ResetPasswordProgress(true))
                        return@flatMapSingle authNetwork.resetPassword(creds.email!!)
                    } else {
                        return@flatMapSingle Single.just(
                            Result.Error(Throwable(AuthErrorConstants.INVALID_EMAIL))
                        )
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: Result<Void?> ->
                    setState(ResetPasswordScreenState.ResetPasswordProgress(false))
                    if (result.isSuccessful) {
                        setAction(ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.successful_reset))
                    } else {
                        if (result.toString().contains(AuthErrorConstants.INVALID_EMAIL)) {
                            setState(ResetPasswordScreenState.ResetPasswordError(R.string.invalid_email))
                        } else {
                            setAction(ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.reset_failed))
                        }
                    }
                }) { error: Throwable ->
                    error.printStackTrace()
                    setState(ResetPasswordScreenState.ResetPasswordProgress(false))
                    setAction(ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.reset_failed))
                }
        )
    }

    override fun resetPassword(email: String?) {
        credsSupplier.onNext(Credentials(email))
    }
}