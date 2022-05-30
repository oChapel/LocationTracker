package ua.com.foxminded.locationtrackera.model.locations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.model.locations.dao.LocationsDao;
import ua.com.foxminded.locationtrackera.model.locations.network.LocationsNetwork;
import ua.com.foxminded.locationtrackera.util.Result;

@RunWith(MockitoJUnitRunner.class)
public class LocationRepositoryTest {

    @Mock
    LocationsDao localDataSource;
    @Mock
    LocationsNetwork remoteDataSource;
    private LocationRepository repository;

    @Before
    public void setUp() {
        this.repository = new LocationRepositoryImpl(localDataSource, remoteDataSource);
    }

    private void verifyNoMore() {
        verifyNoMoreInteractions(localDataSource, remoteDataSource);
    }

    @Test
    public void test_saveLocation() {
        doNothing().when(localDataSource).saveLocation(any(UserLocation.class));
        repository.saveLocation(mock(UserLocation.class));

        verify(localDataSource, times(1)).saveLocation(any(UserLocation.class));
        verifyNoMore();
    }

    @Test
    public void test_getAllLocations() {
        final List<UserLocation> locationList = new ArrayList<>();
        when(localDataSource.getAllLocations()).thenReturn(locationList);

        assertEquals(locationList, repository.getAllLocations());
        verify(localDataSource, times(1)).getAllLocations();
        verifyNoMore();
    }

    @Test
    public void test_deleteLocationsFromDb() {
        doNothing().when(localDataSource).deleteAllLocation();
        repository.deleteLocationsFromDb();

        verify(localDataSource, times(1)).deleteAllLocation();
        verifyNoMore();
    }

    @Test
    public void sendLocationsTest_Success() {
        when(remoteDataSource.sendLocations(anyList())).thenReturn(Single.just(new Result.Success<>(null)));

        repository.sendLocations(new ArrayList<>())
                .test()
                .assertValue(result -> result instanceof Result.Success);

        verify(remoteDataSource, times(1)).sendLocations(anyList());
        verifyNoMore();
    }

    @Test
    public void sendLocationsTest_Failure() {
        when(remoteDataSource.sendLocations(anyList()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));

        repository.sendLocations(new ArrayList<>())
                .test()
                .assertValue(result -> result instanceof Result.Error);

        verify(remoteDataSource, times(1)).sendLocations(anyList());
        verifyNoMore();
    }

    @Test
    public void retrieveLocationsTest_Success() {
        final List<UserLocation> locationList = new ArrayList<>();
        when(remoteDataSource.retrieveLocations(anyDouble(), anyDouble()))
                .thenReturn(Single.just(new Result.Success<>(locationList)));

        repository.retrieveLocations(10.05, 10.6)
                .test()
                .assertValue(result -> ((Result.Success<List<UserLocation>>) result).getData() == locationList);

        verify(remoteDataSource, times(1)).retrieveLocations(anyDouble(), anyDouble());
        verifyNoMore();
    }

    @Test
    public void retrieveLocationsTest_Failure() {
        when(remoteDataSource.retrieveLocations(anyDouble(), anyDouble()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));

        repository.retrieveLocations(10.05, 10.6)
                .test()
                .assertValue(result -> result instanceof Result.Error);

        verify(remoteDataSource, times(1)).retrieveLocations(anyDouble(), anyDouble());
        verifyNoMore();
    }
}
