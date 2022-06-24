package ua.com.foxminded.locationtrackera.ui.tracker;

import android.os.Build;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.CustomItemMatchers;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerTestComponent;
import ua.com.foxminded.locationtrackera.ui.TrackerActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TrackerFragmentTest {

    private static final int WAIT_TIMEOUT_TIME = 2000;
    private final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    private final Matcher<View> disabledMatcher = allOf(
            withText("disabled"), CustomItemMatchers.withTextColor(R.color.red_500)
    );
    private final Matcher<View> enabledMatcher = allOf(
            withText("enabled"), CustomItemMatchers.withTextColor(R.color.green_500)
    );
    private final Matcher<View> activeMatcher = allOf(
            withText("active"), CustomItemMatchers.withTextColor(R.color.yellow_700)
    );

    private NavController navController;

    @Before
    public void setUp() {
        final ActivityScenario<TrackerActivity> activityScenario = ActivityScenario.launch(TrackerActivity.class);
        activityScenario.onActivity(activity -> {
            App.component = DaggerTestComponent.create();
            navController = ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.trackerHostContainerView))
                    .getNavController();
            navController.navigate(R.id.trackerFragment);
        });
    }

    private void grandPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        String allowStr;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            allowStr = "Allow";
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            allowStr = "ALLOW";
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            allowStr = "Allow only while using the app";
        } else {
            allowStr = "While using the app";
        }
        device.wait(Until.hasObject(By.text(allowStr)), WAIT_TIMEOUT_TIME);
        device.findObject(By.text(allowStr)).click();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device.wait(Until.hasObject(By.text("Allow all the time")), WAIT_TIMEOUT_TIME);
            device.findObject(By.textContains("Allow all the time")).click();
        }
    }

    private void denyPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        String denyStr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            denyStr = "DENY";
        } else {
            denyStr = "Deny";
        }
        device.wait(Until.hasObject(By.text(denyStr)), WAIT_TIMEOUT_TIME);
        device.findObject(By.text(denyStr)).click();
    }

    private void checkGpsStatus() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.gps_status)).check(matches(activeMatcher));
        device.wait(Until.hasObject(By.text("Gps status: enabled")), WAIT_TIMEOUT_TIME);
        onView(withId(R.id.gps_status)).check(matches(enabledMatcher));
    }

    private void checkNotification() {
        device.openNotification();
        device.wait(Until.hasObject(By.text("Location Tracker")), WAIT_TIMEOUT_TIME);
        device.pressBack();
    }

    @Test
    public void test_DoNothing() {
        onView(withId(R.id.service_status)).check(matches(disabledMatcher));
        onView(withId(R.id.gps_status)).check(matches(withText("-")));
        onView(withId(R.id.tracker_start_stop_btn)).check(matches(isClickable()));
    }

    @Test
    public void testRequestPermission_Allowed() {
        onView(withId(R.id.tracker_start_stop_btn)).perform(click());
        grandPermission();

        checkGpsStatus();
        onView(withId(R.id.service_status)).check(matches(enabledMatcher));
    }

    @Test
    public void testRequestPermission_Denied() {
        onView(withId(R.id.tracker_start_stop_btn)).perform(click());
        denyPermission();

        onView(withId(R.id.service_status)).check(matches(disabledMatcher));
        onView(withId(R.id.gps_status)).check(matches(withText("-")));
        onView(withText(R.string.permission_denied))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("To track your location please allow the app to use GPS")));
    }

    @Test
    public void test_StartStopService() {
        onView(withId(R.id.tracker_start_stop_btn)).perform(click());
        grandPermission();

        checkGpsStatus();
        onView(withId(R.id.service_status)).check(matches(enabledMatcher));
        checkNotification();

        onView(withId(R.id.tracker_start_stop_btn)).perform(click());
        onView(withId(R.id.service_status)).check(matches(disabledMatcher));
    }

    @Test
    public void testNotification_IsClickable() {
        onView(withId(R.id.tracker_start_stop_btn)).perform(click());
        grandPermission();

        device.wait(Until.hasObject(By.text("Gps status: enabled")), WAIT_TIMEOUT_TIME);
        device.pressHome();
        device.openNotification();
        device.findObject(By.text("Location Tracker")).click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.tracker_start_stop_btn)).check(matches(isDisplayed()));
    }

    private void performLogoutTestActions() {
        onView(withId(R.id.tracker_start_stop_btn)).perform(click());
        grandPermission();

        device.wait(Until.hasObject(By.text("Gps status: enabled")), WAIT_TIMEOUT_TIME);

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.logout)).perform(click());
    }

    @Test
    public void testLogout_AssertFirstDialogFragmentAppeared() {
        performLogoutTestActions();

        onView(withText(R.string.db_not_empty_alert_message)).check(matches(isDisplayed()));
        onView(withText(R.string.delete_uppercase)).perform(click());

        onView(withText(R.string.logged_out))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("See you later!")));
        assertEquals(navController.getCurrentDestination().getId(), R.id.welcomeFragment);
    }

    @Test
    public void testLogout_AssertSecondDialogFragmentAppeared_ActionCancel() {
        performLogoutTestActions();

        onView(withText(R.string.send_uppercase)).perform(click());
        onView(withText(R.string.failed_to_send_alert_message)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).perform(click());

        assertEquals(navController.getCurrentDestination().getId(), R.id.trackerFragment);
    }

    @Test
    public void testLogout_AssertSecondDialogFragmentAppeared_ActionDelete() {
        performLogoutTestActions();

        onView(withText(R.string.send_uppercase)).perform(click());
        onView(withText(R.string.failed_to_send_alert_message)).check(matches(isDisplayed()));
        onView(withText(R.string.logout_uppercase)).perform(click());

        onView(withText(R.string.logged_out))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("See you later!")));
        assertEquals(navController.getCurrentDestination().getId(), R.id.welcomeFragment);
    }
}
