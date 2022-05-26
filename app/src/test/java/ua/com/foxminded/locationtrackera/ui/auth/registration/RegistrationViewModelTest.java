package ua.com.foxminded.locationtrackera.ui.auth.registration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.TrampolineSchedulerRule;
import ua.com.foxminded.locationtrackera.model.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState;
import ua.com.foxminded.locationtrackera.util.Result;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public TrampolineSchedulerRule schedulerRule = new TrampolineSchedulerRule();

    @Mock
    AuthNetwork authNetwork;
    @Mock
    Observer<RegistrationScreenState> stateObserver;
    @Mock
    Observer<RegistrationScreenEffect> effectObserver;
    private RegistrationViewModel model;
    private ArgumentCaptor<RegistrationScreenState> stateCaptor;
    private ArgumentCaptor<RegistrationScreenEffect> actionCaptor;

    @Before
    public void setUp() {
        this.model = new RegistrationViewModel(authNetwork);
        model.getStateObservable().observeForever(stateObserver);
        model.getEffectObservable().observeForever(effectObserver);

        stateCaptor = ArgumentCaptor.forClass(RegistrationScreenState.class);
        actionCaptor = ArgumentCaptor.forClass(RegistrationScreenEffect.class);
        model.onStateChanged(null, Lifecycle.Event.ON_CREATE);
    }

    @After
    public void tearDown() {
        model.getStateObservable().removeObserver(stateObserver);
        model.getEffectObservable().removeObserver(effectObserver);
    }

    private void verifyNoMore() {
        verifyNoMoreInteractions(stateObserver, effectObserver);
    }

    private void checkErrorsAndStateCount(int usernameError, int emailError, int passwordError) {
        verify(stateObserver, times(3)).onChanged(stateCaptor.capture());
        int loadingCounter = 0;
        int errorCounter = 0;
        for (RegistrationScreenState value : stateCaptor.getAllValues()) {
            if (value instanceof RegistrationScreenState.RegistrationProgress) {
                loadingCounter += 1;
            } else if (value instanceof RegistrationScreenState.RegistrationError) {
                errorCounter += 1;
                assertEquals(usernameError, value.usernameError);
                assertEquals(emailError, value.emailError);
                assertEquals(passwordError, value.passwordError);
                assertFalse(value.isProgressVisible);
            }
        }
        assertEquals(2, loadingCounter);
        assertEquals(1, errorCounter);
        verifyNoMore();
    }

    private void checkRegistrationStateCount() {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        for (RegistrationScreenState value : stateCaptor.getAllValues()) {
            assertTrue(value instanceof RegistrationScreenState.RegistrationProgress);
        }
        verifyNoMore();
    }

    @Test
    public void registrationTest_InvalidUsername() {
        model.registerUser("", "kraken@ukr.net", "123456");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(R.string.empty_field, 0, 0);
    }

    @Test
    public void registrationTest_InvalidEmail() {
        model.registerUser("Kraken", "", "123456");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(0, R.string.invalid_email, 0);
    }

    @Test
    public void registrationTest_InvalidPassword() {
        model.registerUser("Kraken", "kraken@ukr.net", "123");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(0, 0, R.string.invalid_password);
    }

    @Test
    public void registrationTest_InvalidUsernameEmailAndPassword() {
        model.registerUser("", "", "123");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(R.string.empty_field, R.string.invalid_email, R.string.invalid_password);
    }

    @Test
    public void registrationTest_RegistrationUnsuccessful() {
        when(authNetwork.firebaseRegister(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        model.registerUser("Kraken", "kraken@ukr.net", "123456");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkRegistrationStateCount();
        assertTrue(actionCaptor.getValue() instanceof RegistrationScreenEffect.RegistrationFailed);
    }

    @Test
    public void registrationTest_RegistrationSuccessful() {
        when(authNetwork.firebaseRegister(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(new Result.Success<>(null)));
        model.registerUser("Kraken", "kraken@ukr.net", "123456");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkRegistrationStateCount();
        assertTrue(actionCaptor.getValue() instanceof RegistrationScreenEffect.RegistrationSuccessful);
    }
}
