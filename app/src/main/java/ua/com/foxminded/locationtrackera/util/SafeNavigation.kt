package ua.com.foxminded.locationtrackera.util

import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.Navigation.findNavController

object SafeNavigation {

    fun navigate(view: View, @IdRes destination: Int, @IdRes action: Int) {
        val controller = findNavController(view)
        if (controller.currentDestination!!.id == destination) {
            controller.navigate(action)
        }
    }
}