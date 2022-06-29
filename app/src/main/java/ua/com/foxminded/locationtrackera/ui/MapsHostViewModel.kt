package ua.com.foxminded.locationtrackera.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork

class MapsHostViewModel(private val authNetwork: AuthNetwork) : ViewModel() {

    private val isUserLoggedInStatus = MutableLiveData<Boolean>()

    fun checkUserLoggedIn() {
        isUserLoggedInStatus.value = authNetwork.isUserLoggedIn()
    }

    fun getUserLoggedInStatus(): LiveData<Boolean> = isUserLoggedInStatus
}
