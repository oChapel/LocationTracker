package ua.com.foxminded.locationtrackera.ui.auth.login;

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
import ua.com.foxminded.locationtrackera.models.auth.AuthNetwork;
import ua.com.foxminded.locationtrackera.models.util.Result;
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.login.state.LoginScreenState;

@RunWith(MockitoJUnitRunner.class)
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public TrampolineSchedulerRule schedulerRule = new TrampolineSchedulerRule();

    @Mock
    AuthNetwork authNetwork;
    @Mock
    Observer<LoginScreenState> stateObserver;
    @Mock
    Observer<LoginScreenEffect> effectObserver;
    private LoginViewModel model;
    private ArgumentCaptor<LoginScreenState> stateCaptor;
    private ArgumentCaptor<LoginScreenEffect> actionCaptor;

    @Before
    public void setUp() {
        this.model = new LoginViewModel(authNetwork);
        model.getStateObservable().observeForever(stateObserver);
        model.getEffectObservable().observeForever(effectObserver);

        stateCaptor = ArgumentCaptor.forClass(LoginScreenState.class);
        actionCaptor = ArgumentCaptor.forClass(LoginScreenEffect.class);
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

    private void checkErrorsAndStateCount(int emailError, int passwordError) {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        int loadingCounter = 0;
        int errorCounter = 0;
        for (LoginScreenState value : stateCaptor.getAllValues()) {
            if (value instanceof LoginScreenState.LoginProgress) {
                loadingCounter += 1;
            } else if (value instanceof LoginScreenState.LoginError) {
                errorCounter += 1;
                assertEquals(emailError, value.getEmailError());
                assertEquals(passwordError, value.getPasswordError());
                assertFalse(value.isProgressVisible());
            }
        }
        assertEquals(1, loadingCounter);
        assertEquals(1, errorCounter);
        verifyNoMore();
    }

    private void checkAuthStateCount() {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(authNetwork, times(1)).firebaseLogin(anyString(), anyString());
        for (LoginScreenState value : stateCaptor.getAllValues()) {
            assertTrue(value instanceof LoginScreenState.LoginProgress);
        }
        verifyNoMore();
    }

    private void checkFailedLogin() {
        model.login("kraken@ukr.net", "123");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkAuthStateCount();
        assertTrue(actionCaptor.getValue() instanceof LoginScreenEffect.LoginFailed);
    }

    @Test
    public void loginTest_InvalidEmail() {
        model.login("", "123");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(R.string.invalid_email, 0);
    }

    @Test
    public void loginTest_InvalidPassword() {
        model.login("kraken@ukr.net", "");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(0, R.string.enter_password);
    }

    @Test
    public void loginTest_InvalidEmailAndPassword() {
        model.login("", "");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkErrorsAndStateCount(R.string.invalid_email, R.string.enter_password);
    }

    @Test
    public void loginTest_ThrowError() {
        when(authNetwork.firebaseLogin(anyString(), anyString()))
                .thenThrow(new RuntimeException("some error"));
        checkFailedLogin();
    }

    @Test
    public void loginTest_AuthUnsuccessful() {
        when(authNetwork.firebaseLogin(anyString(), anyString()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        checkFailedLogin();
    }

    @Test
    public void loginTest_AuthSuccessful() {
        when(authNetwork.firebaseLogin(anyString(), anyString()))
                .thenReturn(Single.just(new Result.Success<>(null)));
        model.login("kraken@ukr.net", "123");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkAuthStateCount();
        assertTrue(actionCaptor.getValue() instanceof LoginScreenEffect.LoginSuccessful);
    }
}
