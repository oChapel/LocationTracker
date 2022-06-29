package ua.com.foxminded.locationtrackera.models.auth

import ua.com.foxminded.locationtrackera.models.util.Result

interface AuthNetwork {

    suspend fun firebaseRegister(username: String, email: String, password: String): Result<Void?>
    suspend fun firebaseLogin(email: String, password: String): Result<Void?>
    suspend fun resetPassword(email: String): Result<Void?>
    fun logout()
    fun isUserLoggedIn(): Boolean
}
