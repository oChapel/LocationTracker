package ua.com.foxminded.locationtrackera.ui.splash;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.com.foxminded.locationtrackera.R;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MapsWelcomeFragmentTest {

    private TestNavHostController navController;

    @Before
    public void setUp() {
        navController = new TestNavHostController(ApplicationProvider.getApplicationContext());

        final FragmentScenario<MapsWelcomeFragment> welcomeScenario =
                FragmentScenario.launchInContainer(MapsWelcomeFragment.class);
        welcomeScenario.onFragment(fragment -> {
            navController.setGraph(R.navigation.maps_navigation_graph);
            Navigation.setViewNavController(fragment.requireView(), navController);
        });
    }

    @Test
    public void testNavigation_ToMapsLoginFragment() {
        onView(withId(R.id.welcome_login_txt)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsLoginFragment);
    }

    @Test
    public void testNavigation_ToMapsRegistrationFragment() {
        onView(withId(R.id.welcome_register_btn)).perform(click());
        assertEquals(navController.getCurrentDestination().getId(), R.id.mapsRegistrationFragment);
    }
}
