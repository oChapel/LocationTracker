package ua.com.foxminded.locationtrackera.background.jobs;

public interface UploadWorkModel {

    void setLocationsUploader();

    void enqueueRequest();
}
