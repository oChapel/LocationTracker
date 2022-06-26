package ua.com.foxminded.locationtrackera.ui.auth.reset

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
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState

class ResetPasswordViewModel(private val authNetwork: AuthNetwork) : MviViewModel<
        ResetPasswordScreenState,
        ResetPasswordScreenEffect>(),
    ResetPasswordContract.ViewModel {

    private val resetFlow = MutableSharedFlow<Credentials>(extraBufferCapacity = 1)
    private var launch: Job? = null

    override fun onStateChanged(event: Lifecycle.Event) {
        super.onStateChanged(event)
        if (event == Lifecycle.Event.ON_CREATE) {
            launch = viewModelScope.launch {
                resetFlow
                    .map { creds ->
                        if (creds.isEmailValid) {
                            postState(ResetPasswordScreenState.ResetPasswordProgress(true))
                            return@map authNetwork.resetPassword(creds.email!!)
                        } else {
                            return@map Result.Error(Throwable(AuthErrorConstants.INVALID_EMAIL))
                        }
                    }
                    .flowOn(Dispatchers.IO)
                    .catch { error ->
                        error.printStackTrace()
                        setState(ResetPasswordScreenState.ResetPasswordProgress(false))
                        setAction(ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.reset_failed))
                    }
                    .collect { result ->
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
                    }
            }
        }
    }

    override fun resetPassword(email: String?) {
        resetFlow.tryEmit(Credentials(email))
    }
}