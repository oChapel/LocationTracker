package ua.com.foxminded.locationtrackera;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ua.com.foxminded.locationtrackera.ui.auth.login.LoginTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.maps.MapsTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerTestSuiteClass;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTestSuiteClass.class,
        RegistrationTestSuiteClass.class,
        ResetPasswordTestSuiteClass.class,
        TrackerTestSuiteClass.class,
        MapsTestSuiteClass.class
})
public class TestSuiteClass {
}
