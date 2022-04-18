package ua.com.foxminded.locationtrackera.ui.auth.reset;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.mvi.MviViewModel;
import ua.com.foxminded.locationtrackera.ui.auth.Credentials;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

public class ResetPasswordViewModel extends MviViewModel<ResetPasswordScreenState, ResetPasswordScreenEffect>
        implements ResetPasswordContract.ViewModel {

    private final PublishSubject<Credentials> credsSupplier = PublishSubject.create();
    private final AuthNetwork authNetwork;

    public ResetPasswordViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        if (event == Lifecycle.Event.ON_CREATE) {
            setUpLoginChain();
        }
    }

    private void setUpLoginChain() {
        addTillDestroy(
                credsSupplier.doOnNext(c -> setState(new ResetPasswordScreenState.ResetPasswordProgress(true)))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMapSingle(creds -> {
                    if (creds.isEmailValid()) {
                        return authNetwork.resetPassword(creds.email);
                    } else {
                        return Single.just(new Result.Error(new Throwable("Email is invalid")));
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    setState(new ResetPasswordScreenState.ResetPasswordProgress(false));
                    if (result.isSuccessful()) {
                        setAction(new ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.successful_reset));
                    } else {
                        if (result.toString().contains("Email is invalid")) {
                            setState(new ResetPasswordScreenState.ResetPasswordError(R.string.invalid_email));
                        } else {
                            setAction(new ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.reset_failed));
                        }
                    }
                }, error -> {
                    error.printStackTrace();
                    setState(new ResetPasswordScreenState.ResetPasswordProgress(false));
                    setAction(new ResetPasswordScreenEffect.ResetPasswordShowStatus(R.string.reset_failed));
                })
        );
    }

    public void resetPassword(String email) {
        credsSupplier.onNext(new Credentials(email));
    }
}
