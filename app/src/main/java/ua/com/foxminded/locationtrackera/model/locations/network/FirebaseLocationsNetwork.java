package ua.com.foxminded.locationtrackera.model.locations.network;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.core.Single;
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
                        return new Result.Error(task.getException());
                    }
                }
                return new Result.Success<>(null);
            }
            return new Result.Success<>(null);
        });
    }

    private Task<Void> sendToFirebase(UserLocation userLocation, String uid) {
        return firestore.collection("Users")
                .document(uid)
                .collection("User Locations")
                .document()
                .set(userLocation);
    }
}
