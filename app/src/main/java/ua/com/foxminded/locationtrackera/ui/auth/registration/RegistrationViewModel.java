package ua.com.foxminded.locationtrackera.ui.auth.registration;

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
import ua.com.foxminded.locationtrackera.ui.auth.Credentials;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

public class RegistrationViewModel extends MviViewModel<RegistrationScreenState, RegistrationScreenEffect>
        implements RegistrationContract.ViewModel {

    private final PublishSubject<Credentials> credsSupplier = PublishSubject.create();
    private final AuthNetwork authNetwork;

    public RegistrationViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        if (event == Lifecycle.Event.ON_CREATE) {
            setUpLoginChain();
        }
    }

    private void setUpLoginChain() {
        addTillDestroy(
                credsSupplier.doOnNext(c -> setState(new RegistrationScreenState.RegistrationProgress(true)))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .flatMapSingle(creds -> {
                            if (creds.isUsernameValid() && creds.isEmailValid() && creds.isRegistrationPasswordValid()) {
                                return authNetwork.firebaseRegister(creds.username, creds.email, creds.password);
                            } else {
                                postState(getErrorState(creds));
                                return Single.just(new Result.Error(new Throwable("Username or email or password invalid")));
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> setState(new RegistrationScreenState.RegistrationProgress(false)))
                        .subscribe(result -> {
                            if (result.isSuccessful()) {
                                setAction(new RegistrationScreenEffect.RegistrationSuccessful());
                            } else {
                                if (!result.toString().contains("Username or email or password invalid")) {
                                    setAction(new RegistrationScreenEffect.RegistrationFailed());
                                }
                            }
                        }, error -> {
                            error.printStackTrace();
                            setAction(new RegistrationScreenEffect.RegistrationFailed());
                        })
        );
    }

    private RegistrationScreenState getErrorState(Credentials creds) {
        int usernameError, emailError, passwordError;
        if (creds.isUsernameValid()) {

            usernameError = -1;
        } else {
            usernameError = R.string.empty_field;
        }
        if (creds.isEmailValid()) {
            emailError = -1;
        } else {
            emailError =  R.string.invalid_email;
        }
        if (creds.isRegistrationPasswordValid()) {
            passwordError = -1;
        } else {
            passwordError = R.string.invalid_password;
        }
        return new RegistrationScreenState.RegistrationError(usernameError, emailError, passwordError);
    }

    public void registerUser(String username, String email, String password) {
        credsSupplier.onNext(new Credentials(username, email, password));
    }

    public void registrationDataChanged(String username, String email, String password) {
        setState(getErrorState(new Credentials(username, email, password)));
    }
}