package ua.com.foxminded.locationtrackera.ui.auth.registration;

import android.view.KeyEvent;

import org.junit.Test;

import ua.com.foxminded.locationtrackera.CustomItemMatchers;
import ua.com.foxminded.locationtrackera.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class RegistrationInstrumentedTest {

    private void checkInputLayoutErrors(
            boolean hasUserNameError, boolean hasEmailError, boolean hasPasswordError
    ) {
        onView(withId(R.id.register_progress_bar)).check(matches(not(isDisplayed())));
        if (hasUserNameError) {
            onView(withId(R.id.register_layout_user_name)).check(matches(
                    CustomItemMatchers.hasTextInputLayoutErrorText("This field cannot be empty")
            ));
        } else {
            onView(withId(R.id.register_layout_user_name)).check(matches(
                    not(CustomItemMatchers.hasTextInputLayoutErrorText("This field cannot be empty"))
            ));
        }

        if (hasEmailError) {
            onView(withId(R.id.register_layout_email)).check(matches(
                    CustomItemMatchers.hasTextInputLayoutErrorText("Not a valid email")
            ));
        } else {
            onView(withId(R.id.register_layout_email)).check(matches(
                    not(CustomItemMatchers.hasTextInputLayoutErrorText("Not a valid email"))
            ));
        }

        if (hasPasswordError) {
            onView(withId(R.id.register_layout_password)).check(matches(
                    CustomItemMatchers.hasTextInputLayoutErrorText("Password must be >5 characters")
            ));
        } else {
            onView(withId(R.id.register_layout_password)).check(matches(
                    not(CustomItemMatchers.hasTextInputLayoutErrorText("Password must be >5 characters"))
            ));
        }
    }

    @Test
    public void testErrors_InvalidCreds() {
        onView(withId(R.id.register_btn)).perform(click());
        checkInputLayoutErrors(true, true, true);
    }

    @Test
    public void testErrors_CheckDynamicErrors() {
        onView(withId(R.id.register_edit_text_user_name)).perform(
                typeText("UserName"), closeSoftKeyboard()
        );
        checkInputLayoutErrors(false, true, true);

        onView(withId(R.id.register_edit_text_user_email)).perform(
                typeText("kraken@ukr.net"), closeSoftKeyboard()
        );
        checkInputLayoutErrors(false, false, true);

        onView(withId(R.id.register_edit_text_password)).perform(
                typeText("123456"), closeSoftKeyboard()
        );
        checkInputLayoutErrors(false, false, false);

        onView(withId(R.id.register_edit_text_user_name)).perform(clearText());
        checkInputLayoutErrors(true, false, false);

        onView(withId(R.id.register_edit_text_user_email)).perform(replaceText("kraken"));
        checkInputLayoutErrors(true, true, false);

        onView(withId(R.id.register_edit_text_password)).perform(pressKey(KeyEvent.KEYCODE_DEL));
        checkInputLayoutErrors(true, true, true);
    }

    private void performRegistrationActions() {
        onView(withId(R.id.register_edit_text_user_email)).perform(
                typeText("kraken@ukr.net"), closeSoftKeyboard()
        );
        onView(withId(R.id.register_edit_text_password)).perform(
                typeText("123456"), closeSoftKeyboard()
        );
        onView(withId(R.id.register_btn)).perform(click());
        onView(withId(R.id.register_progress_bar)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegistration_Success() {
        onView(withId(R.id.register_edit_text_user_name)).perform(typeText("RegisterSuccessTest"));
        performRegistrationActions();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegistration_Failure() {
        onView(withId(R.id.register_edit_text_user_name)).perform(typeText("RegisterFailureTest"));
        performRegistrationActions();

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.register_progress_bar)).check(matches(not(isDisplayed())));
        onView(withText(R.string.registration_failed))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Failed to register. Try again")));
    }

    @Test
    public void testRegistration_UserAlreadyExists() {
        onView(withId(R.id.register_edit_text_user_name)).perform(typeText("UserAlreadyExistsTest"));
        performRegistrationActions();

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.register_progress_bar)).check(matches(not(isDisplayed())));
        onView(withText(R.string.user_already_exists))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("User with such email already exists")));
    }
}
