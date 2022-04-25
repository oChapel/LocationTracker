package ua.com.foxminded.locationtrackera.model.service;

import ua.com.foxminded.locationtrackera.services.LocationServiceContract;
import ua.com.foxminded.locationtrackera.services.StatusListener;

public class Cache implements LocationServiceContract.Cache {

    private StatusListener listener;

    @Override
    public void setGpsStatus(int status) {
        listener.onGpsStatusChanged(status);
    }

    @Override
    public void serviceStatusChanged(boolean isRunning) {
        listener.onServiceStatusChanged(isRunning);
    }

    @Override
    public void setStatusListener(StatusListener listener) {
        this.listener = listener;
    }
}
