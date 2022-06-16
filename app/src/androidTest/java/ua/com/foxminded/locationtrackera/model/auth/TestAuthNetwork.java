package ua.com.foxminded.locationtrackera.model.auth;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.util.Result;

public class TestAuthNetwork implements AuthNetwork {

    @Override
    public Single<Result<Void>> firebaseRegister(String username, String email, String password) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (username.contains("RegisterSuccessTest")) {
            return Single.just(new Result.Success<>(null));
        } else if (username.contains("RegisterFailureTest")) {
            return Single.just(new Result.Error<>(null));
        } else {
            return Single.just(new Result.Error<>(new FirebaseAuthUserCollisionException("s1", "s2")));
        }
    }

    @Override
    public Single<Result<Void>> firebaseLogin(String email, String password) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (email.contains("LoginSuccessTest")) {
            return Single.just(new Result.Success<>(null));
        }
        return Single.just(new Result.Error<>(null));
    }

    @Override
    public Single<Result<Void>> resetPassword(String email) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (email.contains("ResetSuccessTest")) {
            return Single.just(new Result.Success<>(null));
        }
        return Single.just(new Result.Error<>(null));
    }

    @Override
    public boolean isUserLoggedIn() {
        return false;
    }

    @Override
    public void logout() {
    }
}
