package ua.com.foxminded.locationtrackera.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserLocation {

    @PrimaryKey(autoGenerate = true)
    public int locationId;
    public double latitude;
    public double longitude;
    public double date;

    public UserLocation(double latitude, double longitude, double date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
}
