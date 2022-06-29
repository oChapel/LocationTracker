package ua.com.foxminded.locationtrackera.models_impl.usecase;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.models.locations.LocationRepository;
import ua.com.foxminded.locationtrackera.models.usecase.SendLocationsUseCase;
import ua.com.foxminded.locationtrackera.models.util.Result;

@RunWith(MockitoJUnitRunner.class)
public class SendLocationsUseCaseTest {

    @Mock
    LocationRepository repository;
    private SendLocationsUseCase useCase;

    @Before
    public void setUp() {
        this.useCase = new SendLocationsUseCaseImpl(repository);
    }

    private void verifyExecuteTestInteractions() {
        when(repository.getAllLocations()).thenReturn(new ArrayList<>());
        useCase.execute().test()
                .assertComplete()
                .assertNoErrors();

        verify(repository, times(1)).getAllLocations();
        verify(repository, times(1)).sendLocations(anyList());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void executeTest_Success() {
        when(repository.sendLocations(anyList())).thenReturn(Single.just(new Result.Success<>(null)));
        verifyExecuteTestInteractions();
    }

    @Test
    public void executeTest_Failure() {
        when(repository.sendLocations(anyList()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        verifyExecuteTestInteractions();
    }

    @Test
    public void executeTest_ThrowError() {
        when(repository.sendLocations(anyList()))
                .thenThrow(new RuntimeException("some error"));
        verifyExecuteTestInteractions();
    }
}
