package ua.com.foxminded.locationtrackera.ui.auth.registration

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.util.Result
import ua.com.foxminded.locationtrackera.mvi.MviViewModel
import ua.com.foxminded.locationtrackera.ui.auth.AuthErrorConstants
import ua.com.foxminded.locationtrackera.ui.auth.Credentials
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState

class RegistrationViewModel(private val authNetwork: AuthNetwork) : MviViewModel<
        RegistrationScreenState,
        RegistrationScreenEffect>(),
    RegistrationContract.ViewModel {

    private val registerFlow = MutableSharedFlow<Credentials>(extraBufferCapacity = 1)
    private var launch: Job? = null
    private var errorCreds: Credentials? = null

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            launch = viewModelScope.launch {
                registerFlow
                    .onEach { setState(RegistrationScreenState.RegistrationProgress(true)) }
                    .flowOn(Dispatchers.Main)
                    .map { creds ->
                        if (creds.isUsernameValid() && creds.isEmailValid() && creds.isRegistrationPasswordValid()) {
                            return@map authNetwork.firebaseRegister(
                                creds.username!!, creds.email!!, creds.password!!
                            )
                        } else {
                            errorCreds = creds
                            return@map Result.Error(
                                Throwable(AuthErrorConstants.INVALID_USERNAME_EMAIL_PASSWORD)
                            )
                        }
                    }
                    .flowOn(Dispatchers.IO)
                    .catch { error ->
                        error.printStackTrace()
                        setState(RegistrationScreenState.RegistrationProgress(false))
                        setAction(RegistrationScreenEffect.RegistrationFailed(R.string.registration_failed))
                    }
                    .collect { result ->
                        setState(RegistrationScreenState.RegistrationProgress(false))
                        if (result.isSuccessful) {
                            setAction(RegistrationScreenEffect.RegistrationSuccessful())
                        } else {
                            if (result.toString()
                                    .contains(AuthErrorConstants.INVALID_USERNAME_EMAIL_PASSWORD)
                            ) {
                                setState(getErrorState(errorCreds))
                            } else {
                                if ((result as Result.Error<Void?>).error is FirebaseAuthUserCollisionException) {
                                    setAction(RegistrationScreenEffect.RegistrationFailed(R.string.user_already_exists))
                                } else {
                                    setAction(RegistrationScreenEffect.RegistrationFailed(R.string.registration_failed))
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun getErrorState(creds: Credentials?): RegistrationScreenState {
        var usernameError = 0
        var emailError = 0
        var passwordError = 0
        creds?.let {
            usernameError = if (creds.isUsernameValid()) 0 else R.string.empty_field
            emailError = if (creds.isEmailValid()) 0 else R.string.invalid_email
            passwordError = if (creds.isRegistrationPasswordValid()) 0 else R.string.invalid_password
        }
        return RegistrationScreenState.RegistrationError(usernameError, emailError, passwordError)
    }

    override fun registerUser(username: String?, email: String?, password: String?) {
        registerFlow.tryEmit(Credentials(username, email, password))
    }

    override fun registrationDataChanged(username: String?, email: String?, password: String?) {
        setState(getErrorState(Credentials(username, email, password)))
    }

    override fun onCleared() {
        launch = null
        super.onCleared()
    }
}
