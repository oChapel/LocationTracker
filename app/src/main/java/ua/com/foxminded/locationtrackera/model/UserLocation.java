package ua.com.foxminded.locationtrackera.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {

    public final GeoPoint geoPoint;
    @ServerTimestamp
    public final Date timestamp;
    public final String locationId;

    public UserLocation(GeoPoint geoPoint, Date timestamp, String locationId) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.locationId = locationId;
    }
}
