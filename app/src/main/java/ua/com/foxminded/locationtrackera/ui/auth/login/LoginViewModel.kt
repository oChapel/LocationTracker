package ua.com.foxminded.locationtrackera.ui.auth.login

import androidx.lifecycle.Lifecycle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.util.Result
import ua.com.foxminded.locationtrackera.mvi.MviViewModel
import ua.com.foxminded.locationtrackera.ui.auth.AuthErrorConstants
import ua.com.foxminded.locationtrackera.ui.auth.Credentials
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState

class LoginViewModel(private val authNetwork: AuthNetwork) : MviViewModel<
        LoginScreenState,
        LoginScreenEffect>(),
    LoginContract.ViewModel {

    private val credsSupplier: PublishSubject<Credentials> = PublishSubject.create()

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            setupLoginChain()
        }
    }

    private fun setupLoginChain() {
        addTillDestroy(
            credsSupplier.observeOn(Schedulers.io())
                .flatMapSingle { creds: Credentials ->
                    if (creds.isEmailValid && creds.isPasswordValid) {
                        postState(LoginScreenState.LoginProgress(true))
                        return@flatMapSingle authNetwork.firebaseLogin(
                            creds.email!!, creds.password!!
                        )
                    } else {
                        return@flatMapSingle Single.just(
                            Result.Error(
                                Throwable(AuthErrorConstants.INVALID_EMAIL_PASSWORD + creds.loginErrorCode)
                            )
                        )
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: Result<Void?> ->
                    setState(LoginScreenState.LoginProgress(false))
                    if (result.isSuccessful) {
                        setAction(LoginScreenEffect.LoginSuccessful())
                    } else {
                        when {
                            result.toString().contains(AuthErrorConstants.ERROR_CODE_1) -> {
                                setState(
                                    LoginScreenState.LoginError(
                                        R.string.invalid_email, R.string.enter_password
                                    )
                                )
                            }
                            result.toString().contains(AuthErrorConstants.ERROR_CODE_2) ->
                                setState(LoginScreenState.LoginError(R.string.invalid_email, 0))
                            result.toString().contains(AuthErrorConstants.ERROR_CODE_3) ->
                                setState(LoginScreenState.LoginError(0, R.string.enter_password))
                            else -> setAction(LoginScreenEffect.LoginFailed())
                        }
                    }
                }) { error: Throwable ->
                    error.printStackTrace()
                    setState(LoginScreenState.LoginProgress(false))
                    setAction(LoginScreenEffect.LoginFailed())
                }
        )
    }

    override fun login(email: String?, password: String?) {
        credsSupplier.onNext(Credentials(email, password))
    }

}