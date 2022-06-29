package ua.com.foxminded.locationtrackera.ui.auth.registration;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerTestComponent;
import ua.com.foxminded.locationtrackera.ui.MapsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MapsRegistrationFragmentTest extends RegistrationInstrumentedTest {

    private NavController navController;

    @Before
    public void setUp() {
        final ActivityScenario<MapsActivity> activityScenario = ActivityScenario.launch(MapsActivity.class);
        activityScenario.onActivity(activity -> {
            App.component = DaggerTestComponent.create();
            navController = ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapsHostContainerView))
                    .getNavController();
            navController.navigate(R.id.mapsRegistrationFragment);
        });
    }

    @Override
    public void testErrors_InvalidCreds() {
        super.testErrors_InvalidCreds();
    }

    @Override
    public void testErrors_CheckDynamicErrors() {
        super.testErrors_CheckDynamicErrors();
    }

    @Override
    public void testRegistration_Success() {
        super.testRegistration_Success();
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsFragment);
    }

    @Override
    public void testRegistration_Failure() {
        super.testRegistration_Failure();
    }

    @Override
    public void testRegistration_UserAlreadyExists() {
        super.testRegistration_UserAlreadyExists();
    }

    @Test
    public void testNavigation_ToMapsLoginFragment() {
        onView(withId(R.id.register_log_in_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsLoginFragment);
    }
}
