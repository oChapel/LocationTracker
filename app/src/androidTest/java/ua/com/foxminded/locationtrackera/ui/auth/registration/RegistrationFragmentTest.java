package ua.com.foxminded.locationtrackera.ui.auth.registration;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.CustomItemMatchers;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerTestComponent;
import ua.com.foxminded.locationtrackera.ui.TrackerActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RegistrationFragmentTest extends RegistrationInstrumentedTest {

    private NavController navController;

    @Before
    public void setUp() {
        final ActivityScenario<TrackerActivity> activityScenario = ActivityScenario.launch(TrackerActivity.class);
        activityScenario.onActivity(activity -> {
            App.setAppComponent(DaggerTestComponent.create());
            navController = ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.trackerHostContainerView))
                    .getNavController();
            navController.navigate(R.id.registrationFragment);
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
        onView(withText(R.string.successful_registration))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Registration is successful")));
        assertEquals(navController.getCurrentDestination().getId(), R.id.trackerFragment);
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
    public void testNavigation_ToLoginFragment() {
        onView(withId(R.id.register_log_in_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.loginFragment);
    }
}
