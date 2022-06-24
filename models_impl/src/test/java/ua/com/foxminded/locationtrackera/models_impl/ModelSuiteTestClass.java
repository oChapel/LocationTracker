package ua.com.foxminded.locationtrackera.models_impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ua.com.foxminded.locationtrackera.models_impl.locations.LocationRepositoryTest;
import ua.com.foxminded.locationtrackera.models_impl.usecase.SendLocationsUseCaseTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LocationRepositoryTest.class,
        SendLocationsUseCaseTest.class
})
public class ModelSuiteTestClass {
}
