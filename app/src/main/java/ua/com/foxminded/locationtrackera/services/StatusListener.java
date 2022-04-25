package ua.com.foxminded.locationtrackera.services;

public interface StatusListener {

    void onGpsStatusChanged(int gpsStatus);

    void onServiceStatusChanged(boolean isEnabled);
}
