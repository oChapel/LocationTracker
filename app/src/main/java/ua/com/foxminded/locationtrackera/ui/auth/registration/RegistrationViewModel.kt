package ua.com.foxminded.locationtrackera.ui.auth.registration

import androidx.lifecycle.Lifecycle
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.ui.auth.AuthErrorConstants
import ua.com.foxminded.locationtrackera.ui.auth.Credentials
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState
import ua.com.foxminded.locationtrackera.mvi.MviViewModel
import ua.com.foxminded.locationtrackera.models.util.Result

class RegistrationViewModel(private val authNetwork: AuthNetwork) : MviViewModel<
        RegistrationScreenState,
        RegistrationScreenEffect>(),
    RegistrationContract.ViewModel {

    private val credsSupplier: PublishSubject<Credentials> = PublishSubject.create()

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            setUpRegistrationChain()
        }
    }

    private fun setUpRegistrationChain() {
        addTillDestroy(
            credsSupplier.observeOn(Schedulers.io())
                .flatMapSingle { creds: Credentials ->
                    if (creds.isUsernameValid && creds.isEmailValid && creds.isRegistrationPasswordValid) {
                        postState(RegistrationScreenState.RegistrationProgress(true))
                        return@flatMapSingle authNetwork.firebaseRegister(
                            creds.username!!, creds.email!!, creds.password!!
                        )
                    } else {
                        postState(getErrorState(creds))
                        return@flatMapSingle Single.just(
                            Result.Error(Throwable(AuthErrorConstants.INVALID_USERNAME_EMAIL_PASSWORD))
                        )
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: Result<Void?> ->
                    if (result.isSuccessful) {
                        setState(RegistrationScreenState.RegistrationProgress(false))
                        setAction(RegistrationScreenEffect.RegistrationSuccessful())
                    } else {
                        if (!result.toString()
                                .contains(AuthErrorConstants.INVALID_USERNAME_EMAIL_PASSWORD)
                        ) {
                            setState(RegistrationScreenState.RegistrationProgress(false))
                            if ((result as Result.Error<Void?>).error is FirebaseAuthUserCollisionException) {
                                setAction(RegistrationScreenEffect.RegistrationFailed(R.string.user_already_exists))
                            } else {
                                setAction(RegistrationScreenEffect.RegistrationFailed(R.string.registration_failed))
                            }
                        }
                    }
                }) { error: Throwable ->
                    error.printStackTrace()
                    setState(RegistrationScreenState.RegistrationProgress(false))
                    setAction(RegistrationScreenEffect.RegistrationFailed(R.string.registration_failed))
                }
        )
    }

    private fun getErrorState(creds: Credentials): RegistrationScreenState {
        val usernameError: Int = if (creds.isUsernameValid) 0 else R.string.empty_field
        val emailError: Int = if (creds.isEmailValid) 0 else R.string.invalid_email
        val passwordError: Int =
            if (creds.isRegistrationPasswordValid) 0 else R.string.invalid_password
        return RegistrationScreenState.RegistrationError(usernameError, emailError, passwordError)
    }

    override fun registerUser(username: String?, email: String?, password: String?) {
        credsSupplier.onNext(Credentials(username, email, password))
    }

    override fun registrationDataChanged(username: String?, email: String?, password: String?) {
        setState(getErrorState(Credentials(username, email, password)))
    }

}