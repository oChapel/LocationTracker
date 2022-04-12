package ua.com.foxminded.locationtrackera.data.auth;

import io.reactivex.rxjava3.core.Single;

import ua.com.foxminded.locationtrackera.data.Result;

public interface AuthNetwork {

    Single<Result<Void>> firebaseRegister(String username, String email, String password);

    Single<Result<Void>> firebaseLogin(String email, String password);

    Single<Result<Void>> resetPassword(String email);

    void logout();
}
