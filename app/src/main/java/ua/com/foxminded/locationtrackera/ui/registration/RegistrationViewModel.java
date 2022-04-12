package ua.com.foxminded.locationtrackera.ui.registration;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.data.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.data.auth.AuthConstants;

public class RegistrationViewModel extends ViewModel {

    private final MutableLiveData<Integer> usernameErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> registerProgress = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final AuthNetwork authNetwork;

    public RegistrationViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void registerUser(String username, String email, String password) {
        if (isUserNameValid(username) && isEmailValid(email) && isPasswordValid(password)) {
            registerProgress.setValue(AuthConstants.REGISTRATION_IN_PROGRESS);
            compositeDisposable.add(authNetwork.firebaseRegister(username, email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccessful()) {
                            registerProgress.setValue(AuthConstants.REGISTRATION_SUCCESSFUL);
                        } else {
                            registerProgress.setValue(AuthConstants.REGISTRATION_FAILED);
                        }
                    }, error -> {
                        error.printStackTrace();
                        registerProgress.setValue(AuthConstants.REGISTRATION_FAILED);
                    })
            );
        }
    }

    public void registrationDataChanged(String username, String email, String password) {
        if (!isUserNameValid(username)) {
            usernameErrorStatus.setValue(R.string.empty_field);
        } else {
            usernameErrorStatus.setValue(null);
        }
        if (!isEmailValid(email)) {
            emailErrorStatus.setValue(R.string.invalid_email);
        } else {
            emailErrorStatus.setValue(null);
        }
        if (!isPasswordValid(password)) {
            passwordErrorStatus.setValue(R.string.invalid_password);
        } else {
            passwordErrorStatus.setValue(null);
        }
    }

    private boolean isUserNameValid(String username) {
        return !TextUtils.isEmpty(username);
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.trim().length() > 5;
    }

    public LiveData<Integer> getUsernameErrorStatus() {
        return usernameErrorStatus;
    }

    public LiveData<Integer> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    public LiveData<Integer> getPasswordErrorStatus() {
        return passwordErrorStatus;
    }

    public LiveData<Integer> getRegisterProgress() {
        return registerProgress;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}