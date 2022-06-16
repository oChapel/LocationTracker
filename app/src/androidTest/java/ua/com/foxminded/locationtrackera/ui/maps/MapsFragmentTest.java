package ua.com.foxminded.locationtrackera.ui.maps;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.com.foxminded.locationtrackera.App;
import ua.com.foxminded.locationtrackera.CustomItemMatchers;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.di.DaggerTestComponent;
import ua.com.foxminded.locationtrackera.ui.MapsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MapsFragmentTest {

    private static final int WAIT_TIMEOUT_TIME = 2000;

    private NavController navController;

    @Before
    public void setUp() {
        final ActivityScenario<MapsActivity> activityScenario = ActivityScenario.launch(MapsActivity.class);
        activityScenario.onActivity(activity -> {
            App.setAppComponent(DaggerTestComponent.create());
            navController = ((NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapsHostContainerView))
                    .getNavController();
            navController.navigate(R.id.mapsFragment);
        });
    }

    @Test
    public void test_DoNothing() {
        onView(withText(R.string.no_locations_in_current_period))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("There are no locations during chosen period")));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.maps_menu_timeline)).check(matches(isClickable()));
    }

    private void performPickerActions(int fromDayOfMonth, int toDayOfMonth) {
        onView(withId(R.id.maps_menu_timeline)).perform(click());

        final UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        device.wait(Until.hasObject(By.text("Selected date")), WAIT_TIMEOUT_TIME);
        device.findObject(By.text(String.valueOf(fromDayOfMonth))).click();
        device.findObject(By.text("OK")).click();

        device.wait(Until.hasObject(By.text("OK")), WAIT_TIMEOUT_TIME);
        device.findObject(By.text("OK")).click();

        device.wait(Until.hasObject(By.text("Selected date")), WAIT_TIMEOUT_TIME);
        device.findObject(By.text(String.valueOf(toDayOfMonth))).click();
        device.findObject(By.text("OK")).click();

        device.wait(Until.hasObject(By.text("OK")), WAIT_TIMEOUT_TIME);
        device.findObject(By.text("OK")).click();
    }

    @Test
    public void testRetrieveLocations_InvalidTimePeriod() {
        performPickerActions(20, 10);

        onView(withText(R.string.invalid_time_period))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Start date must be anterior to end date")));
    }

    @Test
    public void testRetrieveLocations_NoLocations() {
        performPickerActions(11, 12);

        onView(withText(R.string.no_locations_in_current_period))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("There are no locations during chosen period")));
    }

    @Test
    public void testRetrieveLocations_LocationsReceived() {
        performPickerActions(1, 2);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO assert markers were set
    }

    @Test
    public void test_Logout() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.logout)).perform(click());

        onView(withText(R.string.logged_out))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("See you later!")));
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsWelcomeFragment);
    }

}
