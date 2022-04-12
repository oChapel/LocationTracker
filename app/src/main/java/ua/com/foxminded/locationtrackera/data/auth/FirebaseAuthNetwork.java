package ua.com.foxminded.locationtrackera.data.auth;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.rxjava3.core.Single;

import ua.com.foxminded.locationtrackera.data.model.User;

public class FirebaseAuthNetwork implements AuthNetwork {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthNetwork(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public Single<Task<AuthResult>> firebaseRegister(String username, String email, String password) {
        return Single.fromCallable(() -> firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .set(new User(username, email));
                    }
                })
        );
    }

    @Override
    public Single<Task<AuthResult>> firebaseLogin(String email, String password) {
        return Single.fromCallable(() -> firebaseAuth.signInWithEmailAndPassword(email, password));
    }

    @Override
    public Single<Task<Void>> resetPassword(String email) {
        return Single.fromCallable(() -> firebaseAuth.sendPasswordResetEmail(email));
    }

    @Override
    public void firebaseLogout() {
        firebaseAuth.signOut();
    }
}
