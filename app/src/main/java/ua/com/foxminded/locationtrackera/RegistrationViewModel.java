package ua.com.foxminded.locationtrackera;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationViewModel extends ViewModel {

    private final MutableLiveData<Integer> usernameErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> emailErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> passwordErrorStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> registerProgress = new MutableLiveData<>();

    private final FirebaseAuth firebaseAuth;

    public RegistrationViewModel(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void registerUser(String username, String email, String password) {
        //TODO: launch in a separate asynchronous job
        if (isUserNameValid(username) && isEmailValid(email) && isPasswordValid(password)) {
            registerProgress.setValue(0);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            User user = new User(username, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            registerProgress.setValue(R.string.successful_registration);
                                        } else {
                                            registerProgress.setValue(R.string.registration_failed);
                                        }
                                    });
                        } else {
                            registerProgress.setValue(R.string.registration_failed);
                        }
                    });
        }
    }

    public void loginDataChanged(String username, String email, String password) {
        if (!isUserNameValid(username)) {
            usernameErrorStatus.setValue(R.string.empty_field);
        } else {
            usernameErrorStatus.setValue(null);
        }
        if (!isEmailValid(email)) {
            emailErrorStatus.setValue(R.string.invalid_email);
        } else {
            emailErrorStatus.setValue(null);
        }
        if (!isPasswordValid(password)) {
            passwordErrorStatus.setValue(R.string.invalid_password);
        } else {
            passwordErrorStatus.setValue(null);
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public LiveData<Integer> getUsernameErrorStatus() {
        return usernameErrorStatus;
    }

    public LiveData<Integer> getEmailErrorStatus() {
        return emailErrorStatus;
    }

    public LiveData<Integer> getPasswordErrorStatus() {
        return passwordErrorStatus;
    }

    public LiveData<Integer> getRegisterProgress() {
        return registerProgress;
    }
}