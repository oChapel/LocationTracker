package ua.com.foxminded.locationtrackera.models.locations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserLocation(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var date: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var locationId: Int = 0
}
