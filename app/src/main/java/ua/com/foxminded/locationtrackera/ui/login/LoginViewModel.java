package ua.com.foxminded.locationtrackera.ui.login;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.data.auth.AuthConstants;
import ua.com.foxminded.locationtrackera.data.auth.AuthNetwork;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> loginProgress = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final AuthNetwork authNetwork;

    public LoginViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void login(String email, String password) {
        loginProgress.setValue(AuthConstants.LOGIN_IN_PROGRESS);
        if (isEmailValid(email) && isPasswordValid(password)) {
            compositeDisposable.add(authNetwork.firebaseLogin(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccessful()) {
                            loginProgress.setValue(AuthConstants.LOGIN_SUCCESSFUL);
                        } else {
                            loginProgress.setValue(AuthConstants.LOGIN_FAILED);
                        }
                    }, error -> {
                        error.printStackTrace();
                        loginProgress.setValue(AuthConstants.LOGIN_FAILED);
                    })
            );
        }
    }

    private boolean isEmailValid(String email) {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErrorStatus.setValue(null);
            return true;
        }
        emailErrorStatus.setValue(R.string.invalid_email);
        return false;
    }

    private boolean isPasswordValid(String password) {
        if (!TextUtils.isEmpty(password)) {
            passwordErrorStatus.setValue(null);
            return true;
        }
        passwordErrorStatus.setValue(R.string.enter_password);
        return false;
    }

    public LiveData<Integer> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    public LiveData<Integer> getPasswordErrorStatus() {
        return passwordErrorStatus;
    }

    public LiveData<Integer> getLoginProgress() {
        return loginProgress;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
