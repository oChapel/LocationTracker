package ua.com.foxminded.locationtrackera.ui.auth;

import android.text.TextUtils;
import android.util.Patterns;

public class Credentials {
    public final String email;
    public final String password;

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean isEmailValid() {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid() {
        return !TextUtils.isEmpty(password);
    }
}
