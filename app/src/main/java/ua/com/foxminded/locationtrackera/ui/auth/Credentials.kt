package ua.com.foxminded.locationtrackera.ui.auth

import android.text.TextUtils
import androidx.core.util.PatternsCompat
import ua.com.foxminded.locationtrackera.util.Utils

class Credentials {
    val username: String?
    val email: String?
    val password: String?

    constructor(email: String?) {
        username = null
        this.email = email
        password = null
    }

    constructor(email: String?, password: String?) {
        username = null
        this.email = email
        this.password = password
    }

    constructor(username: String?, email: String?, password: String?) {
        this.username = username
        this.email = email
        this.password = password
    }

    val isEmailValid: Boolean
        get() = !TextUtils.isEmpty(email) && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()

    val isPasswordValid: Boolean
        get() = !TextUtils.isEmpty(password)

    val isRegistrationPasswordValid: Boolean
        get() = isPasswordValid && Utils.hasMoreThanFiveChars(password)

    val isUsernameValid: Boolean
        get() = !TextUtils.isEmpty(username)

    val loginErrorCode: Int
        get() = if (!isEmailValid && !isPasswordValid) {
            1
        } else if (!isEmailValid && isPasswordValid) {
            2
        } else if (isEmailValid && !isPasswordValid) {
            3
        } else {
            0
        }
}
