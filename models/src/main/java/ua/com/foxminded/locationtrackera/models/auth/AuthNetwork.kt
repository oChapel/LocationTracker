package ua.com.foxminded.locationtrackera.models.auth

import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.util.Result

interface AuthNetwork {

    val isUserLoggedIn: Boolean
    fun firebaseRegister(username: String, email: String, password: String): Single<Result<Void?>>
    fun firebaseLogin(email: String, password: String): Single<Result<Void?>>
    fun resetPassword(email: String): Single<Result<Void?>>
    fun logout()
}