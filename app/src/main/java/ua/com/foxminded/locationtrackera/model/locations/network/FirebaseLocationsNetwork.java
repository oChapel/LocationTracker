package ua.com.foxminded.locationtrackera.model.locations.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.model.auth.FirebaseNetworkConstants;
import ua.com.foxminded.locationtrackera.model.locations.UserLocation;
import ua.com.foxminded.locationtrackera.util.Result;

public class FirebaseLocationsNetwork implements LocationsNetwork {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;

    public FirebaseLocationsNetwork(FirebaseAuth firebaseAuth) {
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public Single<Result<Void>> sendLocations(List<UserLocation> locationList) {
        return Single.fromCallable(() -> {
            if (!locationList.isEmpty()) {
                for (int i = 0; i < locationList.size(); i++) {
                    final Task<Void> task = sendToFirebase(
                            locationList.get(i),
                            firebaseAuth.getCurrentUser().getUid()
                    );

                    try {
                        Tasks.await(task);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (!task.isSuccessful()) {
                        return new Result.Error<>(task.getException());
                    }
                }
                return new Result.Success<>(null);
            }
            return new Result.Success<>(null);
        });
    }

    @Override
    public Single<Result<List<UserLocation>>> retrieveLocations(double startDate, double endDate) {
        return Single.fromCallable(() -> {
            final List<UserLocation> locationsList = new ArrayList<>();
            final Task<QuerySnapshot> task = retrieveFromFirebase(
                    firebaseAuth.getCurrentUser().getUid(), startDate, endDate
            );

            try {
                Tasks.await(task);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (!task.isSuccessful()) {
                return new Result.Error<>(task.getException());
            }

            if (!task.getResult().isEmpty()) {
                for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                    locationsList.add(ds.toObject(UserLocation.class));
                }
            }
            return new Result.Success<>(locationsList);
        });
    }

    private Task<Void> sendToFirebase(UserLocation userLocation, String uid) {
        return firestore.collection(FirebaseNetworkConstants.COLLECTION_PATH_USERS)
                .document(uid)
                .collection(FirebaseNetworkConstants.COLLECTION_PATH_USER_LOCATIONS)
                .document()
                .set(userLocation);
    }

    private Task<QuerySnapshot> retrieveFromFirebase(String uid, double startDate, double endDate) {
        return firestore.collection(FirebaseNetworkConstants.COLLECTION_PATH_USERS)
                .document(uid)
                .collection(FirebaseNetworkConstants.COLLECTION_PATH_USER_LOCATIONS)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get();
    }
}
