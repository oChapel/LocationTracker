package ua.com.foxminded.locationtrackera.ui.auth.reset;

import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;

import org.junit.Before;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerTestComponent;
import ua.com.foxminded.locationtrackera.ui.MapsActivity;

public class MapsResetPasswordFragmentTest extends ResetPasswordInstrumentedTest {

    @Before
    public void setUp() {
        final ActivityScenario<MapsActivity> activityScenario = ActivityScenario.launch(MapsActivity.class);
        activityScenario.onActivity(activity -> {
            App.component = DaggerTestComponent.create();
            ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapsHostContainerView))
                    .getNavController()
                    .navigate(R.id.mapsResetPasswordFragment);
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
