package ua.com.foxminded.locationtrackera.data.source;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ua.com.foxminded.locationtrackera.model.auth.UserLocation;

public class FirebaseLocationsNetwork implements LocationsNetwork {

    private final FirebaseFirestore firestore;

    public FirebaseLocationsNetwork(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void saveLocation(UserLocation userLocation) {
        if (userLocation != null) {
            final DocumentReference locationRef = firestore
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("User Locations")
                    .document();
            locationRef
                    .set(userLocation);
        }
    }

    @Override
    public List<UserLocation> getAll() {
        return null;
    }
}
