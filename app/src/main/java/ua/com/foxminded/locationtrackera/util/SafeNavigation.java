package ua.com.foxminded.locationtrackera.util;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SafeNavigation {

    public static void navigate(View view, @IdRes int destination, @IdRes int action) {
        final NavController controller = Navigation.findNavController(view);
        if (controller.getCurrentDestination().getId() == destination) {
            controller.navigate(action);
        }
    }
}
