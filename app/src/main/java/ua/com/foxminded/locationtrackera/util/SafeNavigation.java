package ua.com.foxminded.locationtrackera.util;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

public class SafeNavigation {

    public static void navigate(View view, String className, @IdRes int action) {
        final NavController controller = Navigation.findNavController(view);
        final String destination = ((FragmentNavigator.Destination) controller.getCurrentDestination()).getClassName();
        if (destination.equals(className)) {
            controller.navigate(action);
        }
    }
}
