package ua.com.foxminded.locationtrackera.ui.auth.reset;

import static org.junit.Assert.assertEquals;
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
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordViewModelTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public TrampolineSchedulerRule schedulerRule = new TrampolineSchedulerRule();

    @Mock
    AuthNetwork authNetwork;
    @Mock
    Observer<ResetPasswordScreenState> stateObserver;
    @Mock
    Observer<ResetPasswordScreenEffect> effectObserver;
    private ResetPasswordViewModel model;
    private ArgumentCaptor<ResetPasswordScreenState> stateCaptor;
    private ArgumentCaptor<ResetPasswordScreenEffect> actionCaptor;

    @Before
    public void setUp() {
        this.model = new ResetPasswordViewModel(authNetwork);
        model.getStateObservable().observeForever(stateObserver);
        model.getEffectObservable().observeForever(effectObserver);

        stateCaptor = ArgumentCaptor.forClass(ResetPasswordScreenState.class);
        actionCaptor = ArgumentCaptor.forClass(ResetPasswordScreenEffect.class);
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

    private void checkResetStateCount() {
        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        verify(effectObserver, times(1)).onChanged(actionCaptor.capture());
        verify(authNetwork, times(1)).resetPassword(anyString());
        for (ResetPasswordScreenState value : stateCaptor.getAllValues()) {
            assertTrue(value instanceof ResetPasswordScreenState.ResetPasswordProgress);
        }
        verifyNoMore();
    }

    private void checkFailedResetPassword() {
        model.resetPassword("kraken@ukr.net");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkResetStateCount();
        assertEquals(
                R.string.reset_failed,
                ((ResetPasswordScreenEffect.ResetPasswordShowStatus) actionCaptor.getValue()).getIdStringResource()
        );
    }

    @Test
    public void resetTest_EmailInvalid() {
        model.resetPassword("");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(stateObserver, times(2)).onChanged(stateCaptor.capture());
        int loadingCounter = 0;
        int errorCounter = 0;
        for (ResetPasswordScreenState value : stateCaptor.getAllValues()) {
            if (value instanceof ResetPasswordScreenState.ResetPasswordProgress) {
                loadingCounter += 1;
            } else if (value instanceof ResetPasswordScreenState.ResetPasswordError) {
                errorCounter += 1;
                assertEquals(R.string.invalid_email, value.getEmailError());
            }
        }
        assertEquals(1, loadingCounter);
        assertEquals(1, errorCounter);
        verifyNoMore();
    }

    @Test
    public void resetTest_ThrowError() {
        when(authNetwork.resetPassword(anyString()))
                .thenThrow(new RuntimeException("some error"));
        checkFailedResetPassword();
    }

    @Test
    public void resetTest_ResetUnsuccessful() {
        when(authNetwork.resetPassword(anyString()))
                .thenReturn(Single.just(new Result.Error<>(new Throwable("some error"))));
        checkFailedResetPassword();
    }

    @Test
    public void resetTest_ResetSuccessful() {
        when(authNetwork.resetPassword(anyString()))
                .thenReturn(Single.just(new Result.Success<>(null)));
        model.resetPassword("kraken@ukr.net");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkResetStateCount();
        assertEquals(
                R.string.successful_reset,
                ((ResetPasswordScreenEffect.ResetPasswordShowStatus) actionCaptor.getValue()).getIdStringResource()
        );
    }
}
