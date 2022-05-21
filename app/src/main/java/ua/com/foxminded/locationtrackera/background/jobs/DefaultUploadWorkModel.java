package ua.com.foxminded.locationtrackera.background.jobs;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class DefaultUploadWorkModel implements UploadWorkModel {

    private final Context context;
    private WorkRequest request;

    public DefaultUploadWorkModel(Context appContext) {
        this.context = appContext;
    }

    @Override
    public void setLocationsUploader() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        request = new OneTimeWorkRequest
                .Builder(LocationsUploader.class)
                .setConstraints(constraints)
                .build();
    }

    @Override
    public void enqueueRequest() {
        WorkManager.getInstance(context).enqueue(request);
    }
}
