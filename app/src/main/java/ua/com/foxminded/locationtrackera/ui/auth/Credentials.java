package ua.com.foxminded.locationtrackera.ui.auth;

import android.text.TextUtils;
import android.util.Patterns;

import ua.com.foxminded.locationtrackera.util.Utils;

public class Credentials {
    public final String username;
    public final String email;
    public final String password;

    public Credentials(String email) {
        this.username = null;
        this.email = email;
        this.password = null;
    }

    public Credentials(String email, String password) {
        this.username = null;
        this.email = email;
        this.password = password;
    }

    public Credentials(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public boolean isEmailValid() {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid() {
        return !TextUtils.isEmpty(password);
    }

    public boolean isRegistrationPasswordValid() {
        return isPasswordValid() && Utils.hasMoreThanFiveChars(password);
    }

    public boolean isUsernameValid() {
        return !TextUtils.isEmpty(username);
    }

    public int getLoginErrorCode() {
        if (!isEmailValid() && !isPasswordValid()) {
            return 1;
        } else if (!isEmailValid() && isPasswordValid()) {
            return 2;
        } else if (isEmailValid() && !isPasswordValid()) {
            return 3;
        } else {
            return 0;
        }
    }
}
