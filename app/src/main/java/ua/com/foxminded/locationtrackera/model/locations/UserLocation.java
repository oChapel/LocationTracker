package ua.com.foxminded.locationtrackera.model.locations;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class UserLocation {

    @PrimaryKey(autoGenerate = true)
    public int locationId;
    public double latitude;
    public double longitude;
    public long date;

    public UserLocation(double latitude, double longitude, long date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    @Ignore
    public UserLocation() {
    }
}
