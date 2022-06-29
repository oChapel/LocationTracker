package ua.com.foxminded.locationtrackera.background.jobs

import android.content.Context
import androidx.work.*

class DefaultUploadWorkModel(private val context: Context) : UploadWorkModel {

    private var request: WorkRequest

    init {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        request = OneTimeWorkRequest.Builder(LocationsUploader::class.java)
            .setConstraints(constraints)
            .build()
    }

    override fun enqueueRequest() {
        WorkManager.getInstance(context).enqueue(request)
    }
}