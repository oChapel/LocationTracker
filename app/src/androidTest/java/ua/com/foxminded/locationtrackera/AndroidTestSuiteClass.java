package ua.com.foxminded.locationtrackera;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ua.com.foxminded.locationtrackera.ui.auth.login.LoginAndroidTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.auth.registration.RegistrationAndroidTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.auth.reset.ResetPasswordAndroidTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.maps.MapsFragmentTest;
import ua.com.foxminded.locationtrackera.ui.splash.SplashAndroidTestSuiteClass;
import ua.com.foxminded.locationtrackera.ui.tracker.TrackerFragmentTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SplashAndroidTestSuiteClass.class,
        LoginAndroidTestSuiteClass.class,
        RegistrationAndroidTestSuiteClass.class,
        ResetPasswordAndroidTestSuiteClass.class,
        TrackerFragmentTest.class,
        MapsFragmentTest.class
})
public class AndroidTestSuiteClass {
}
