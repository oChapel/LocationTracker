package ua.com.foxminded.locationtrackera.ui.registration;

import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.model.User;

public class RegistrationViewModel extends ViewModel {

    private static final int REGISTRATION_IN_PROGRESS = 100;
    private static final int REGISTRATION_SUCCESSFUL = 101;
    private static final int REGISTRATION_FAILED = 102;

    private final MutableLiveData<Integer> usernameErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> registerProgress = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public RegistrationViewModel() {
    }

    public void registerUser(String username, String email, String password) {
        if (isUserNameValid(username) && isEmailValid(email) && isPasswordValid(password)) {
            registerProgress.setValue(REGISTRATION_IN_PROGRESS);
            compositeDisposable.add(Observable.fromCallable(() -> {
                        firebaseRegister(username, email, password);
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

    private void firebaseRegister(String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(username, email);
                        FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        handler.post(() -> registerProgress
                                                .setValue(REGISTRATION_SUCCESSFUL));
                                    } else {
                                        handler.post(() -> registerProgress
                                                .setValue(REGISTRATION_FAILED));
                                    }
                                });
                    } else {
                        handler.post(() -> registerProgress.setValue(REGISTRATION_FAILED));
                    }
                });
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