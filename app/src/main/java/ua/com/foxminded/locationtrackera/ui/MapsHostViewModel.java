package ua.com.foxminded.locationtrackera.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;

public class MapsHostViewModel extends ViewModel {

    private final AuthNetwork authNetwork;
    private final MutableLiveData<Boolean> isUserLoggedInStatus = new MutableLiveData<>();

    public MapsHostViewModel(AuthNetwork authNetwork) {
        this.authNetwork = authNetwork;
    }

    public void checkUserLoggedIn() {
        isUserLoggedInStatus.setValue(authNetwork.isUserLoggedIn());
    }

    public LiveData<Boolean> getIsUserLoggedInStatus() {
        return isUserLoggedInStatus;
    }
}
