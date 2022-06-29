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

    fun isEmailValid() = !TextUtils.isEmpty(email) && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid() = !TextUtils.isEmpty(password)

    fun isRegistrationPasswordValid() = isPasswordValid() && Utils.hasMoreThanFiveChars(password)

    fun isUsernameValid() = !TextUtils.isEmpty(username)

    fun loginErrorCode() = when {
        !isEmailValid() && !isPasswordValid() -> AuthErrorConstants.ERROR_CODE_1
        !isEmailValid() && isPasswordValid() -> AuthErrorConstants.ERROR_CODE_2
        isEmailValid() && !isPasswordValid() -> AuthErrorConstants.ERROR_CODE_3
        else -> AuthErrorConstants.ERROR_CODE_UNKNOWN
    }
}
