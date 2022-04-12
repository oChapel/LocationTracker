package ua.com.foxminded.locationtrackera.data.auth;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.rxjava3.core.Single;

import ua.com.foxminded.locationtrackera.data.Result;
import ua.com.foxminded.locationtrackera.data.model.User;

public class FirebaseAuthNetwork implements AuthNetwork {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthNetwork(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public Single<Result<Void>> firebaseRegister(String username, String email, String password) {
        return Single.fromCallable(() -> {
            final Task<AuthResult> task =
                    firebaseAuth.createUserWithEmailAndPassword(email, password);
            Tasks.await(task);
            if (task.isSuccessful()) {
                final Task<Void> task2 = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(firebaseAuth.getCurrentUser().getUid())
                        .set(new User(username, email));
                Tasks.await(task2);
                if (task.isSuccessful()) {
                    return new Result.Success<>(null);
                } else {
                    return new Result.Error(task2.getException());
                }
            } else {
                return new Result.Error(task.getException());
            }
        });
    }

    @Override
    public Single<Result<Void>> firebaseLogin(String email, String password) {
        return Single.fromCallable(() -> {
            final Task<AuthResult> task = firebaseAuth.signInWithEmailAndPassword(email, password);
            Tasks.await(task);
            if (task.isSuccessful()) {
                return new Result.Success<>(null);
            } else {
                return new Result.Error(task.getException());
            }
        });
    }

    @Override
    public Single<Result<Void>> resetPassword(String email) {
        return Single.fromCallable(() -> {
            final Task<Void> task = firebaseAuth.sendPasswordResetEmail(email);
            Tasks.await(task);
            if (task.isSuccessful()) {
                return new Result.Success<>(null);
            } else {
                return new Result.Error(task.getException());
            }
        });
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }
}
