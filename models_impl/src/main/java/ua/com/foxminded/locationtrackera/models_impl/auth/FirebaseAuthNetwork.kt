package ua.com.foxminded.locationtrackera.models_impl.auth

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork
import ua.com.foxminded.locationtrackera.models.auth.User
import ua.com.foxminded.locationtrackera.models.util.Result
import java.util.concurrent.ExecutionException

class FirebaseAuthNetwork(private val firebaseAuth: FirebaseAuth) : AuthNetwork {

    override fun firebaseRegister(
        username: String, email: String, password: String
    ): Single<Result<Void?>> {
        return Single.fromCallable {
            val task = firebaseAuth.createUserWithEmailAndPassword(email, password)

            try {
                Tasks.await(task)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            if (task.isSuccessful) {
                val task2 = FirebaseFirestore.getInstance()
                    .collection(COLLECTION_PATH_USERS)
                    .document(firebaseAuth.currentUser!!.uid)
                    .set(User(username, email))

                try {
                    Tasks.await(task2)
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }

                if (task.isSuccessful) {
                    return@fromCallable Result.Success<Void?>(null)
                } else {
                    return@fromCallable Result.Error<Void?>(task2.exception)
                }
            } else {
                return@fromCallable Result.Error<Void?>(task.exception)
            }
        }
    }

    override fun firebaseLogin(email: String, password: String): Single<Result<Void?>> {
        return Single.fromCallable {
            val task = firebaseAuth.signInWithEmailAndPassword(email, password)

            try {
                Tasks.await(task)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            if (task.isSuccessful) {
                return@fromCallable Result.Success<Void?>(null)
            } else {
                return@fromCallable Result.Error<Void?>(task.exception)
            }
        }
    }

    override fun resetPassword(email: String): Single<Result<Void?>> {
        return Single.fromCallable {
            val task =
                firebaseAuth.sendPasswordResetEmail(email)

            try {
                Tasks.await(task)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            if (task.isSuccessful) {
                return@fromCallable Result.Success<Void?>(null)
            } else {
                return@fromCallable Result.Error<Void?>(task.exception)
            }
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    companion object {
        private const val COLLECTION_PATH_USERS = "Users"
    }
}
