package ua.com.foxminded.locationtrackera.ui.registration;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.data.FirebaseAuthNetwork;

public class RegistrationViewModel extends ViewModel {

    private final MutableLiveData<Integer> usernameErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final FirebaseAuthNetwork authNetwork;

    public RegistrationViewModel(FirebaseAuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void registerUser(String username, String email, String password) {
        if (isUserNameValid(username) && isEmailValid(email) && isPasswordValid(password)) {
            compositeDisposable.add(Observable.fromCallable(() -> {
                        authNetwork.firebaseRegister(username, email, password);
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe()
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
        return username != null && !username.trim().isEmpty();
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
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
        return authNetwork.getRegisterProgress();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}