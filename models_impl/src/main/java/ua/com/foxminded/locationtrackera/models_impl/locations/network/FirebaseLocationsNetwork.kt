package ua.com.foxminded.locationtrackera.models_impl.locations.network

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.rxjava3.core.Single
import ua.com.foxminded.locationtrackera.models.locations.UserLocation
import ua.com.foxminded.locationtrackera.models.util.Result
import java.util.*
import java.util.concurrent.ExecutionException

class FirebaseLocationsNetwork(private val firebaseAuth: FirebaseAuth) :
    ua.com.foxminded.locationtrackera.models.locations.network.LocationsNetwork {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun sendLocations(locationList: List<UserLocation>): Single<Result<Void?>> =
        Single.fromCallable {
            if (locationList.isNotEmpty()) {
                for (i in locationList.indices) {
                    val task = sendToFirebase(
                        locationList[i],
                        firebaseAuth.currentUser!!.uid
                    )

                    try {
                        Tasks.await(task)
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }

                    if (!task.isSuccessful) {
                        return@fromCallable Result.Error(task.exception)
                    }
                }
                return@fromCallable Result.Success(null)
            }
            return@fromCallable Result.Success(null)
        }


    override fun retrieveLocations(
        fromTime: Long,
        toTime: Long
    ): Single<Result<List<UserLocation>>> = Single.fromCallable {
        val locationsList: MutableList<UserLocation> = ArrayList()
        val task = retrieveFromFirebase(
            firebaseAuth.currentUser!!.uid, fromTime, toTime
        )

        try {
            Tasks.await(task)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        if (!task.isSuccessful) {
            return@fromCallable Result.Error<List<UserLocation>>(task.exception)
        }

        if (!task.result.isEmpty) {
            for (ds in task.result.documents) {
                ds.toObject(UserLocation::class.java)?.let { locationsList.add(it) }
            }
        }
        return@fromCallable Result.Success<List<UserLocation>>(locationsList)
    }

    private fun sendToFirebase(userLocation: UserLocation?, uid: String): Task<Void> =
        firestore.collection(COLLECTION_PATH_USERS)
            .document(uid)
            .collection(COLLECTION_PATH_USER_LOCATIONS)
            .document()
            .set(userLocation!!)

    private fun retrieveFromFirebase(
        uid: String,
        startDate: Long,
        endDate: Long
    ): Task<QuerySnapshot> = firestore.collection(COLLECTION_PATH_USERS)
        .document(uid)
        .collection(COLLECTION_PATH_USER_LOCATIONS)
        .whereGreaterThanOrEqualTo(FIELD_DATE, startDate)
        .whereLessThanOrEqualTo(FIELD_DATE, endDate)
        .get()


    companion object {
        private const val COLLECTION_PATH_USERS = "Users"
        private const val COLLECTION_PATH_USER_LOCATIONS = "User Locations"
        private const val FIELD_DATE = "date"
    }
}