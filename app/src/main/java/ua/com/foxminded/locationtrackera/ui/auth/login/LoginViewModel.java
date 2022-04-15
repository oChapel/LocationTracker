package ua.com.foxminded.locationtrackera.ui.auth.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.auth.Credentials;
import ua.com.foxminded.locationtrackera.util.Result;

public class LoginViewModel extends MviViewModel<LoginScreenState> implements LoginContract.ViewModel {

    private final PublishSubject<Credentials> credsSupplier = PublishSubject.create();
    private final AuthNetwork authNetwork;

    public LoginViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        if (event == Lifecycle.Event.ON_CREATE) {
            setupLoginChain();
        }
    }

    private void setupLoginChain() {
        addTillDestroy(
                credsSupplier
                        .doOnNext(c -> LoginScreenState.createLoginInProgressState())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .flatMapSingle(creds -> {
                            if (creds.isEmailValid() && creds.isPasswordValid()) {
                                return authNetwork.firebaseLogin(creds.email, creds.password);
                            } else {
                                // R.string.enter_password; R.string.invalid_email;
                                return Single.just(new Result.Error(new Throwable("Email or password is invalid")));
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccessful()) {
                                setState(LoginScreenState.createLoginSuccessState());
                            } else {
                                // TODO: set error messages
                                setState(LoginScreenState.createLoginFailureState());
                            }
                        }, error -> {
                            error.printStackTrace();
                            setState(LoginScreenState.createLoginFailureState());
                        })
        );
    }

    public void login(String email, String password) {
        credsSupplier.onNext(new Credentials(email, password));
    }
}
