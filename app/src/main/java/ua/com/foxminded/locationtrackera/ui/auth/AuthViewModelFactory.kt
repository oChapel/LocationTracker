package ua.com.foxminded.locationtrackera.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.di.AppComponent
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.bus.TrackerCache
import ua.com.foxminded.locationtrackera.models.gps.GpsSource
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository
import ua.com.foxminded.locationtrackera.models.shared_preferences.SharedPreferencesModel
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase
import ua.com.foxminded.locationtrackera.ui.MapsHostViewModel
import ua.com.foxminded.locationtrackera.ui.TrackerHostViewModel
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginViewModel
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationViewModel
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordViewModel
import ua.com.foxminded.locationtrackera.ui.maps.MapsViewModel
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerViewModel
import javax.inject.Inject

class AuthViewModelFactory : NewInstanceFactory() {

    private val component: AppComponent = App.component

    @Inject
    lateinit var authNetwork: AuthNetwork

    @Inject
    lateinit var cache: TrackerCache

    @Inject
    lateinit var repository: LocationRepository

    @Inject
    lateinit var sendLocationsUseCase: SendLocationsUseCase

    @Inject
    lateinit var sharedPreferencesModel: SharedPreferencesModel

    @Inject
    lateinit var gpsSource: GpsSource

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        component.inject(this)
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(authNetwork) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authNetwork) as T
        } else if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            return ResetPasswordViewModel(authNetwork) as T
        } else if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            return TrackerViewModel(
                authNetwork, cache, repository,
                sendLocationsUseCase, sharedPreferencesModel, gpsSource
            ) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(authNetwork, repository) as T
        } else if (modelClass.isAssignableFrom(MapsHostViewModel::class.java)) {
            return MapsHostViewModel(authNetwork) as T
        } else if (modelClass.isAssignableFrom(TrackerHostViewModel::class.java)) {
            return TrackerHostViewModel(authNetwork) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}