package ua.com.foxminded.locationtrackera.ui.auth.reset;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

import ua.com.foxminded.locationtrackera.CustomItemMatchers;
import ua.com.foxminded.locationtrackera.R;

public class ResetPasswordInstrumentedTest {

    @Test
    public void testError_InvalidEmail() {
        onView(withId(R.id.reset_password_btn)).perform(click());

        onView(withId(R.id.reset_password_progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.reset_password_layout_email)).check(matches(
                CustomItemMatchers.hasTextInputLayoutErrorText("Not a valid email")
        ));
    }

    private void performResetPasswordsActions() {
        onView(withId(R.id.reset_password_btn)).perform(click());
        onView(withId(R.id.reset_password_progress_bar)).check(matches(isDisplayed()));

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.reset_password_progress_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testReset_Success() {
        onView(withId(R.id.reset_password_edit_text_email)).perform(
                typeText("ResetSuccessTest@ukr.net"), closeSoftKeyboard()
        );
        performResetPasswordsActions();

        onView(withText(R.string.successful_reset))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Check your email to reset your password")));
    }

    @Test
    public void testReset_Failure() {
        onView(withId(R.id.reset_password_edit_text_email)).perform(
                typeText("ResetFailureTest@ukr.net"), closeSoftKeyboard()
        );
        performResetPasswordsActions();

        onView(withText(R.string.reset_failed))
                .inRoot(new CustomItemMatchers.ToastMatcher())
                .check(matches(isDisplayed()))
                .check(matches(withText("Failed to reset password. Please check your email or internet connection")));
    }

}
