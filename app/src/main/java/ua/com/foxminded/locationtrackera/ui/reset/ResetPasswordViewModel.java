package ua.com.foxminded.locationtrackera.ui.reset;

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

public class ResetPasswordViewModel extends ViewModel {

    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> resetProgress = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final AuthNetwork authNetwork;

    public ResetPasswordViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void resetPassword(String email) {
        if (isEmailIsValid(email)) {
            resetProgress.setValue(AuthConstants.RESET_IN_PROGRESS);
            compositeDisposable.add(authNetwork.resetPassword(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccessful()) {
                            resetProgress.setValue(AuthConstants.RESET_SUCCESSFUL);
                        } else {
                            resetProgress.setValue(AuthConstants.RESET_FAILED);
                        }
                    }, error -> {
                        error.printStackTrace();
                        resetProgress.setValue(AuthConstants.RESET_FAILED);
                    })
            );
        }
    }

    private boolean isEmailIsValid(String email) {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
