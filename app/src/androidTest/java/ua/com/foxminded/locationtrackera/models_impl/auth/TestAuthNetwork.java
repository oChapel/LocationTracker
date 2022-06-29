package ua.com.foxminded.locationtrackera.models_impl.auth;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.models.util.Result;

public class TestAuthNetwork implements AuthNetwork {

    @NotNull
    @Override
    public Single<Result<Void>> firebaseRegister(
            @NotNull String username, @NotNull String email, @NotNull String password
    ) {

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

    @NotNull
    @Override
    public Single<Result<Void>> firebaseLogin(@NotNull String email, @NotNull String password) {

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

    @NotNull
    @Override
    public Single<Result<Void>> resetPassword(@NotNull String email) {

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
