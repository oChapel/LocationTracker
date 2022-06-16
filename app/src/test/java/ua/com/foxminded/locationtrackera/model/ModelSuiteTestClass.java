package ua.com.foxminded.locationtrackera.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ua.com.foxminded.locationtrackera.model.locations.LocationRepositoryTest;
import ua.com.foxminded.locationtrackera.model.usecase.SendLocationsUseCaseTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LocationRepositoryTest.class,
        SendLocationsUseCaseTest.class
})
public class ModelSuiteTestClass {
}
