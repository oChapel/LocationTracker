package ua.com.foxminded.locationtrackera.models_impl.locations.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ua.com.foxminded.locationtrackera.models.locations.UserLocation

@Database(entities = [UserLocation::class], version = 3, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun userLocationDao(): UserLocationDao

    companion object {
        private var instance: LocationDatabase? = null

        @Synchronized
        fun getInstance(context: Context): LocationDatabase {
            return instance
                ?: Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}