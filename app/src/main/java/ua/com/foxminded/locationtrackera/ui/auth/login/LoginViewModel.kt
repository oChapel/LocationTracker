package ua.com.foxminded.locationtrackera.ui.auth.login

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
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
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState

class LoginViewModel(private val authNetwork: AuthNetwork) : MviViewModel<
        LoginScreenState,
        LoginScreenEffect>(),
    LoginContract.ViewModel {

    private val loginFlow = MutableSharedFlow<Credentials>(extraBufferCapacity = 1)
    private var launch: Job? = null

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            launch = viewModelScope.launch {
                loginFlow
                    .onEach { setState(LoginScreenState.LoginProgress(true)) }
                    .flowOn(Dispatchers.Main)
                    .map { creds ->
                        if (creds.isEmailValid() && creds.isPasswordValid()) {
                            return@map authNetwork.firebaseLogin(creds.email!!, creds.password!!)
                        } else {
                            return@map Result.Error(
                                Throwable(AuthErrorConstants.INVALID_EMAIL_PASSWORD + creds.loginErrorCode())
                            )
                        }
                    }
                    .flowOn(Dispatchers.IO)
                    .catch { error ->
                        error.printStackTrace()
                        setState(LoginScreenState.LoginProgress(false))
                        setAction(LoginScreenEffect.LoginFailed())
                    }
                    .collect { result ->
                        setState(LoginScreenState.LoginProgress(false))
                        if (result.isSuccessful) {
                            setAction(LoginScreenEffect.LoginSuccessful())
                        } else {
                            when {
                                result.toString().contains(AuthErrorConstants.ERROR_CODE_1) -> {
                                    setState(LoginScreenState.LoginError(
                                            R.string.invalid_email, R.string.enter_password
                                    ))
                                }
                                result.toString().contains(AuthErrorConstants.ERROR_CODE_2) ->
                                    setState(LoginScreenState.LoginError(R.string.invalid_email, 0))
                                result.toString().contains(AuthErrorConstants.ERROR_CODE_3) ->
                                    setState(LoginScreenState.LoginError(0, R.string.enter_password))
                                else -> setAction(LoginScreenEffect.LoginFailed())
                            }
                        }
                    }
            }
        }
    }

    override fun login(email: String?, password: String?) {
        loginFlow.tryEmit(Credentials(email, password))
    }

    override fun onCleared() {
        launch = null
        super.onCleared()
    }
}
