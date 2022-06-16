package ua.com.foxminded.locationtrackera.ui.auth.reset;

import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.runner.RunWith;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerTestComponent;
import ua.com.foxminded.locationtrackera.ui.TrackerActivity;


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
