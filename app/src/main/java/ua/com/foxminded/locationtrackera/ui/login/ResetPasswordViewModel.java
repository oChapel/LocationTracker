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

public class ResetPasswordViewModel extends ViewModel {

    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final FirebaseAuthNetwork authNetwork;

    public ResetPasswordViewModel(FirebaseAuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void resetPassword(String email) {
        if (isEmailIsValid(email)) {
            compositeDisposable.add(Observable.just(email)
                    .doOnNext(authNetwork::resetPassword)
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
        return authNetwork.getResetProgress();
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
