package ua.com.foxminded.locationtrackera.ui.auth.login;

import org.junit.Test;

import ua.com.foxminded.locationtrackera.CustomItemMatchers;
import ua.com.foxminded.locationtrackera.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class LoginInstrumentedTest {

    @Test
    public void testErrors_InvalidCreds() {
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.login_layout_email)).check(matches(
                CustomItemMatchers.hasTextInputLayoutErrorText("Not a valid email")
        ));

        onView(withId(R.id.login_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.login_layout_password)).check(matches(
                CustomItemMatchers.hasTextInputLayoutErrorText("Please enter password")
        ));
    }

    @Test
    public void testErrors_InvalidEmail() {
        onView(withId(R.id.login_edit_text_password)).perform(
                typeText("123"), closeSoftKeyboard()
        );
        onView(withId(R.id.login_btn)).perform(click());

        onView(withId(R.id.login_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.login_layout_email)).check(matches(
                CustomItemMatchers.hasTextInputLayoutErrorText("Not a valid email")
        ));
    }

    @Test
    public void testErrors_InvalidPassword() {
        onView(withId(R.id.login_edit_text_email)).perform(
                typeText("kraken@ukr.net"), closeSoftKeyboard()
        );
        onView(withId(R.id.login_btn)).perform(click());

        onView(withId(R.id.login_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.login_layout_password)).check(matches(
                CustomItemMatchers.hasTextInputLayoutErrorText("Please enter password")
        ));
    }

    @Test
    public void testLogin_Success() {
        onView(withId(R.id.login_edit_text_email)).perform(
                typeText("LoginSuccessTest@ukr.net"), closeSoftKeyboard()
        );
        onView(withId(R.id.login_edit_text_password)).perform(
                typeText("654321"), closeSoftKeyboard()
        );
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.login_progress_bar)).check(matches(isDisplayed()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLogin_Failure() {
        onView(withId(R.id.login_edit_text_email)).perform(
                typeText("LoginErrorTest@ukr.net"), closeSoftKeyboard()
        );
        onView(withId(R.id.login_edit_text_password)).perform(
                typeText("123456"), closeSoftKeyboard()
        );
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.login_progress_bar)).check(matches(isDisplayed()));

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.login_progress_bar)).check(matches(not(isDisplayed())));
        onView(withText(R.string.login_failed))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Failed to login. Please check your email and password")));
    }
}
