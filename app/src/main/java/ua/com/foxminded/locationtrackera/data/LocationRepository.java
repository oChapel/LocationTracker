package ua.com.foxminded.locationtrackera.data;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import ua.com.foxminded.locationtrackera.data.decorator.FirebaseDataSourceDecorator;
import ua.com.foxminded.locationtrackera.di.DataComponent;
import ua.com.foxminded.locationtrackera.model.auth.UserLocation;
import ua.com.foxminded.locationtrackera.services.LocationServiceContract;

//TODO: demo
public class LocationRepository implements LocationServiceContract.Repository {

    //private final DataComponent component = DaggerDataComponent.create();
    private final LocationDataSource dataSource;

    private List<UserLocation> locationList;

    @Inject
    LocationDataSource localDataSource;

    public LocationRepository() {
        //component.inject(this);
        dataSource = new FirebaseDataSourceDecorator(localDataSource);
    }

    @Override
    public void saveLocation(UserLocation userLocation) {
        dataSource.saveLocation(userLocation);
    }

    @Override
    public List<UserLocation> getAll() {
        return locationList;
    }
}
