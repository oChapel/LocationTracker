package ua.com.foxminded.locationtrackera.ui.login;

import android.text.TextUtils;
import android.util.Patterns;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.data.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;

public class LoginViewModel extends MviViewModel<LoginScreenState> implements LoginContract.ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final AuthNetwork authNetwork;

    private int emailError = -1;
    private int passwordError = -1;

    public LoginViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void login(String email, String password) {
        if (isEmailValid(email) && isPasswordValid(password)) {
            setState(LoginScreenState.createLoginInProgressState());
            compositeDisposable.add(authNetwork.firebaseLogin(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccessful()) {
                            setState(LoginScreenState.createLoginSuccessState());
                        } else {
                            setState(LoginScreenState.createLoginFailureState());
                        }
                    }, error -> {
                        error.printStackTrace();
                        setState(LoginScreenState.createLoginFailureState());
                    })
            );
        } else {
            setState(LoginScreenState.createLoginErrorState(emailError, passwordError));
        }
    }

    private boolean isEmailValid(String email) {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = -1;
            return true;
        }
        emailError = R.string.invalid_email;
        return false;
    }

    private boolean isPasswordValid(String password) {
        if (!TextUtils.isEmpty(password)) {
            passwordError = -1;
            return true;
        }
        passwordError = R.string.enter_password;
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
