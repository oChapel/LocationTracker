package ua.com.foxminded.locationtrackera.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import ua.com.foxminded.locationtrackera.App
import ua.com.foxminded.locationtrackera.R
import ua.com.foxminded.locationtrackera.di.DaggerAppComponent
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract

class MapsActivity : AppCompatActivity(), LoginContract.Host {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        App.component = DaggerAppComponent.create()
        val viewModel = ViewModelProvider(this, AuthViewModelFactory()).get(
            MapsHostViewModel::class.java
        )
        viewModel.getUserLoggedInStatus().observe(this) {
            if (it) {
                navigateToMainScreen()
            }
        }
        viewModel.checkUserLoggedIn()
    }

    private fun navigateToMainScreen() {
        (supportFragmentManager.findFragmentById(R.id.mapsHostContainerView) as NavHostFragment)
            .navController
            .navigate(R.id.mapsFragment)
    }
}
