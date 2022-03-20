package ua.com.foxminded.locationtrackera.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import ua.com.foxminded.locationtrackera.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> loginProgress = new MutableLiveData<>();

    private final FirebaseAuth firebaseAuth;

    public LoginViewModel(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void login(String email, String password) {
        //TODO: launch in a separate asynchronous job
        if (isEmailValid(email) && isPasswordValid(password)) {
            loginProgress.setValue(0);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            loginProgress.setValue(1);
                        } else {
                            loginProgress.setValue(R.string.login_failed);
                        }
                    });
        }
    }

    private boolean isEmailValid(String email) {
        if (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty()) {
            emailErrorStatus.setValue(null);
            return true;
        }
        emailErrorStatus.setValue(R.string.invalid_email);
        return false;
    }

    private boolean isPasswordValid(String password) {
        if (password != null && !password.trim().isEmpty()) {
            passwordErrorStatus.setValue(null);
            return true;
        }
        passwordErrorStatus.setValue(R.string.enter_password);
        return false;
    }

    public LiveData<Integer> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    public LiveData<Integer> getPasswordErrorStatus() {
        return passwordErrorStatus;
    }

    public LiveData<Integer> getLoginProgress() {
        return loginProgress;
    }
}
