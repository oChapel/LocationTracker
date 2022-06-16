package ua.com.foxminded.locationtrackera.ui.auth.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

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

@RunWith(AndroidJUnit4ClassRunner.class)
public class MapsLoginFragmentTest extends LoginInstrumentedTest {

    private NavController navController;

    @Before
    public void setUp() {
        final ActivityScenario<MapsActivity> activityScenario = ActivityScenario.launch(MapsActivity.class);
        activityScenario.onActivity(activity -> {
            App.setAppComponent(DaggerTestComponent.create());
            navController = ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapsHostContainerView))
                    .getNavController();
            navController.navigate(R.id.mapsLoginFragment);
        });
    }

    @Override
    public void testErrors_InvalidCreds() {
        super.testErrors_InvalidCreds();
    }

    @Override
    public void testErrors_InvalidEmail() {
        super.testErrors_InvalidEmail();
    }

    @Override
    public void testErrors_InvalidPassword() {
        super.testErrors_InvalidPassword();
    }

    @Override
    public void testLogin_Success() {
        super.testLogin_Success();
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsFragment);
    }

    @Override
    public void testLogin_Failure() {
        super.testLogin_Failure();
    }

    @Test
    public void testNavigation_ToMapsResetPasswordFragment() {
        onView(withId(R.id.forgot_password_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsResetPasswordFragment);
    }

    @Test
    public void testNavigation_ToMapsRegistrationFragment() {
        onView(withId(R.id.sign_up_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsRegistrationFragment);
    }
}
