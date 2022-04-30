package ua.com.foxminded.locationtrackera.model.locations.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ua.com.foxminded.locationtrackera.model.locations.UserLocation;

@Database(entities = UserLocation.class, version = 2, exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {

    private static LocationDatabase instance;

    public abstract UserLocationDao userLocationDao();

    public static synchronized LocationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LocationDatabase.class, "location_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
