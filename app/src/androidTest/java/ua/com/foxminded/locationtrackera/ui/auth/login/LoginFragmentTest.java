package ua.com.foxminded.locationtrackera.ui.auth.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

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

@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginFragmentTest extends LoginInstrumentedTest {

    private NavController navController;

    @Before
    public void setUp() {
        final ActivityScenario<TrackerActivity> activityScenario = ActivityScenario.launch(TrackerActivity.class);
        activityScenario.onActivity(activity -> {
            App.setAppComponent(DaggerTestComponent.create());
            navController = ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.trackerHostContainerView))
                    .getNavController();
            navController.navigate(R.id.loginFragment);
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

        onView(withText(R.string.successful_login))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Welcome back!")));
        assertEquals(navController.getCurrentDestination().getId(), R.id.trackerFragment);
    }

    @Override
    public void testLogin_Failure() {
        super.testLogin_Failure();
    }

    @Test
    public void testNavigation_ToResetPasswordFragment() {
        onView(withId(R.id.forgot_password_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.resetPasswordFragment);
    }

    @Test
    public void testNavigation_ToRegistrationFragment() {
        onView(withId(R.id.sign_up_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.registrationFragment);
    }
}
