package ua.com.foxminded.locationtrackera.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.data.FirebaseAuthNetwork;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final FirebaseAuthNetwork authNetwork;

    public LoginViewModel(FirebaseAuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void login(String email, String password) {
        if (isEmailValid(email) && isPasswordValid(password)) {
            compositeDisposable.add(Observable.fromCallable(() -> {
                        authNetwork.firebaseLogin(email, password);
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            );
        }
    }

    private boolean isEmailValid(String email) {
        if (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty()) {
            emailErrorStatus.setValue(null);
            return true;
        }
        emailErrorStatus.setValue(R.string.invalid_email);
        return false;
    }

    private boolean isPasswordValid(String password) {
        if (password != null && !password.trim().isEmpty()) {
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
        return authNetwork.getLoginProgress();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
