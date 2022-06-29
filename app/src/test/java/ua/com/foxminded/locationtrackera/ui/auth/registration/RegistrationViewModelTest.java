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

import com.google.firebase.auth.FirebaseAuthUserCollisionException;

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
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.models.util.Result;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState;

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
        model.onStateChanged(Lifecycle.Event.ON_CREATE);
    }

    @After
    public void tearDown() {
        model.getStateObservable().removeObserver(stateObserver);
        model.getEffectObservable().removeObserver(effectObserver);
    }

    private void verifyNoMore() {
        verifyNoMoreInteractions(stateObserver, effectObserver);
        verifyNoMoreInteractions(authNetwork);
    }

    private void checkErrorsAndStateCount(int usernameError, int emailError, int passwordError) {
        verify(stateObserver, times(1)).onChanged(stateCaptor.capture());
        int errorCounter = 0;
        for (RegistrationScreenState value : stateCaptor.getAllValues()) {
             if (value instanceof RegistrationScreenState.RegistrationError) {
                errorCounter += 1;
                checkErrors(value, usernameError, emailError, passwordError);
            }
        }
        assertEquals(1, errorCounter);
        verifyNoMore();
    }

    private void checkErrors(
            RegistrationScreenState value, int usernameError, int emailError, int passwordError
    ) {
        assertEquals(usernameError, value.getUsernameError());
        assertEquals(emailError, value.getEmailError());
        assertEquals(passwordError, value.getPasswordError());
        assertFalse(value.isProgressVisible());
    }

    private void checkRegistrationStateCount() {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(authNetwork, times(1)).firebaseRegister(anyString(), anyString(), anyString());
        for (RegistrationScreenState value : stateCaptor.getAllValues()) {
            assertTrue(value instanceof RegistrationScreenState.RegistrationProgress);
        }
        verifyNoMore();
    }

    private void checkFailedRegistration() {
        model.registerUser("Kraken", "kraken@ukr.net", "123456");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkRegistrationStateCount();
        assertTrue(actionCaptor.getValue() instanceof RegistrationScreenEffect.RegistrationFailed);
        assertEquals(
                R.string.registration_failed,
                ((RegistrationScreenEffect.RegistrationFailed) actionCaptor.getValue()).getStringResId()
        );
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
    public void registrationTest_InvalidCreds() {
        model.registerUser("", "", "123");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(R.string.empty_field, R.string.invalid_email, R.string.invalid_password);
    }

    @Test
    public void registrationTest_ThrowError() {
        when(authNetwork.firebaseRegister(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("some error"));
        checkFailedRegistration();
    }

    @Test
    public void registrationTest_RegistrationUnsuccessful() {
        when(authNetwork.firebaseRegister(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        checkFailedRegistration();
    }

    @Test
    public void registrationTest_UserAlreadyExists() {
        when(authNetwork.firebaseRegister(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(new Result.Error<>(new FirebaseAuthUserCollisionException("s1", "s2"))));
        model.registerUser("Kraken", "kraken@ukr.net", "123456");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkRegistrationStateCount();
        assertTrue(actionCaptor.getValue() instanceof RegistrationScreenEffect.RegistrationFailed);
        assertEquals(
                R.string.user_already_exists,
                ((RegistrationScreenEffect.RegistrationFailed) actionCaptor.getValue()).getStringResId()
        );
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

    @Test
    public void dataChangedTest_ValidCreds() {
        model.registrationDataChanged("Kraken", "kraken@ukr.net", "123456");

        verify(stateObserver, times(1)).onChanged(stateCaptor.capture());
        assertTrue(stateCaptor.getValue() instanceof RegistrationScreenState.RegistrationError);
        checkErrors(stateCaptor.getValue(), 0, 0, 0);
        verifyNoMore();
    }

    @Test
    public void dataChangedTest_InvalidCreds() {
        model.registrationDataChanged("", "", "");

        verify(stateObserver, times(1)).onChanged(stateCaptor.capture());
        assertTrue(stateCaptor.getValue() instanceof RegistrationScreenState.RegistrationError);
        checkErrors(stateCaptor.getValue(), R.string.empty_field, R.string.invalid_email, R.string.invalid_password);
        verifyNoMore();
    }
}
