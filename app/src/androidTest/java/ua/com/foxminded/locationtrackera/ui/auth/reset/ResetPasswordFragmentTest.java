package ua.com.foxminded.locationtrackera.ui.auth.reset;

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ResetPasswordFragmentTest extends ResetPasswordInstrumentedTest {

    @Before
    public void setUp() {
        final ActivityScenario<TrackerActivity> activityScenario = ActivityScenario.launch(TrackerActivity.class);
        activityScenario.onActivity(activity -> {
            App.setAppComponent(DaggerTestComponent.create());
            ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.trackerHostContainerView))
                    .getNavController()
                    .navigate(R.id.resetPasswordFragment);
        });
    }

    @Override
    public void testError_InvalidEmail() {
        super.testError_InvalidEmail();
    }

    @Override
    public void testReset_Success() {
        super.testReset_Success();
    }

    @Override
    public void testReset_Failure() {
        super.testReset_Failure();
    }
}
