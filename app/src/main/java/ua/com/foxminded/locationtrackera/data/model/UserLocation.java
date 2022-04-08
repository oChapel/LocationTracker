package ua.com.foxminded.locationtrackera.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@Entity
public class UserLocation {

    @PrimaryKey
    @NonNull
    public String locationId;
    public double latitude;
    public double longitude;
    public double date;

    public UserLocation(double latitude, double longitude, double date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
}
