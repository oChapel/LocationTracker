package ua.com.foxminded.locationtrackera.ui.login;

import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.R;

public class LoginViewModel extends ViewModel {

    private static final int LOGIN_IN_PROGRESS = 100;
    private static final int LOGIN_SUCCESSFUL = 101;
    private static final int LOGIN_FAILED = 102;

    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> loginProgress = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public LoginViewModel() {
    }

    public void login(String email, String password) {
        if (isEmailValid(email) && isPasswordValid(password)) {
            loginProgress.setValue(LOGIN_IN_PROGRESS);
            compositeDisposable.add(Observable.fromCallable(() -> {
                        firebaseLogin(email, password);
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            );
        }
    }

    private void firebaseLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handler.post(() -> loginProgress.setValue(LOGIN_SUCCESSFUL));
                    } else {
                        handler.post(() -> loginProgress.setValue(LOGIN_FAILED));
                    }
                });
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
