package ua.com.foxminded.locationtrackera.models_impl.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.auth.User
import ua.com.foxminded.locationtrackera.models.util.Result

class FirebaseAuthNetwork(private val firebaseAuth: FirebaseAuth) : AuthNetwork {

    override suspend fun firebaseRegister(
        username: String, email: String, password: String
    ): Result<Void?> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            try {
                FirebaseFirestore.getInstance()
                    .collection(COLLECTION_PATH_USERS)
                    .document(firebaseAuth.currentUser!!.uid)
                    .set(User(username, email))
                    .await()
                Result.Success(null)
            } catch (e: Exception) {
                Result.Error(e)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun firebaseLogin(email: String, password: String): Result<Void?> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Void?> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun isUserLoggedIn() = firebaseAuth.currentUser != null

    companion object {
        private const val COLLECTION_PATH_USERS = "Users"
    }
}
