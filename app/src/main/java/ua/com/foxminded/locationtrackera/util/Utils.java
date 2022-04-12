package ua.com.foxminded.locationtrackera.util;

import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

public class Utils {

    private Utils() {
    }

    @NotNull
    public static String getTextFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }
}
