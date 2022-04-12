package ua.com.foxminded.locationtrackera.data.auth;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import io.reactivex.rxjava3.core.Single;

public interface AuthNetwork {

    Single<Task<AuthResult>> firebaseRegister(String username, String email, String password);

    Single<Task<AuthResult>> firebaseLogin(String email, String password);

    Single<Task<Void>> resetPassword(String email);

    void firebaseLogout();
}
