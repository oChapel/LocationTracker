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

    private final MutableLiveData<Integer> resetProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ResetPasswordViewModel() {
    }

    public void resetPassword(String email) {
        if (isEmailIsValid(email)) {
            resetProgress.setValue(0);
            compositeDisposable.add(Observable.fromCallable(() -> {
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        handler.post(() -> resetProgress.setValue(R.string.successful_reset));
                                    } else {
                                        handler.post(() -> resetProgress.setValue(R.string.reset_failed));
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
