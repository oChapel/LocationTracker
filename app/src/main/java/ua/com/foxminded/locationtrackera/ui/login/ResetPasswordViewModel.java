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

public class ResetPasswordViewModel extends ViewModel {

    private static final int RESET_IN_PROGRESS = 100;
    private static final int RESET_SUCCESSFUL = 101;
    private static final int RESET_FAILED = 102;

    private final MutableLiveData<Integer> resetProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ResetPasswordViewModel() {
    }

    public void resetPassword(String email) {
        if (isEmailIsValid(email)) {
            resetProgress.setValue(RESET_IN_PROGRESS);
            compositeDisposable.add(Observable.fromCallable(() -> {
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        handler.post(() -> resetProgress.setValue(RESET_SUCCESSFUL));
                                    } else {
                                        handler.post(() -> resetProgress.setValue(RESET_FAILED));
                                    }
                                });
                        return true;
                    })
                            .subscribeOn(Schedulers.io())
                            .subscribe()
            );
        }
    }

    private boolean isEmailIsValid(String email) {
        if (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty()) {
            emailErrorStatus.setValue(null);
            return true;
        }
        emailErrorStatus.setValue(R.string.invalid_email);
        return false;
    }

    public LiveData<Integer> getResetProgress() {
        return resetProgress;
    }

    public LiveData<Integer> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
