package ua.com.foxminded.locationtrackera.model.auth;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.util.Result;

public interface AuthNetwork {

    Single<Result<Void>> firebaseRegister(String username, String email, String password);

    Single<Result<Void>> firebaseLogin(String email, String password);

    Single<Result<Void>> resetPassword(String email);

    boolean isUserLoggedIn();

    void logout();
}
