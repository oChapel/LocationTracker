package ua.com.foxminded.locationtrackera.ui.auth.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.auth.AuthErrorConstants;
import ua.com.foxminded.locationtrackera.ui.auth.Credentials;
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

public class LoginViewModel extends MviViewModel<LoginScreenState, LoginScreenEffect> implements LoginContract.ViewModel {

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
                        .doOnNext(c -> setState(new LoginScreenState.LoginProgress(true)))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .flatMapSingle(creds -> {
                            if (creds.isEmailValid() && creds.isPasswordValid()) {
                                return authNetwork.firebaseLogin(creds.email, creds.password);
                            } else {
                                return Single.just(new Result.Error(
                                        new Throwable(AuthErrorConstants.INVALID_EMAIL_PASSWORD + creds.getLoginErrorCode())
                                ));
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            setState(new LoginScreenState.LoginProgress(false));
                            if (result.isSuccessful()) {
                                setAction(new LoginScreenEffect.LoginSuccessful());
                            } else {
                                if (result.toString().contains(AuthErrorConstants.ERROR_CODE_1)) {
                                    setState(new LoginScreenState.LoginError(R.string.invalid_email, R.string.enter_password));
                                } else if (result.toString().contains(AuthErrorConstants.ERROR_CODE_2)) {
                                    setState(new LoginScreenState.LoginError(R.string.invalid_email, 0));
                                } else if (result.toString().contains(AuthErrorConstants.ERROR_CODE_3)) {
                                    setState(new LoginScreenState.LoginError(0, R.string.enter_password));
                                } else {
                                    setAction(new LoginScreenEffect.LoginFailed());
                                }
                            }
                        }, error -> {
                            error.printStackTrace();
                            setState(new LoginScreenState.LoginProgress(false));
                            setAction(new LoginScreenEffect.LoginFailed());
                        })
        );
    }

    @Override
    public void login(String email, String password) {
        credsSupplier.onNext(new Credentials(email, password));
    }
}
