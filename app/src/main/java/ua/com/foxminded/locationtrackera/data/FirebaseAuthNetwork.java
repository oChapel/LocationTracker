package ua.com.foxminded.locationtrackera.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import ua.com.foxminded.locationtrackera.data.model.User;

public class FirebaseAuthNetwork {

    private static final int REGISTRATION_IN_PROGRESS = 100;
    private static final int REGISTRATION_SUCCESSFUL = 101;
    private static final int REGISTRATION_FAILED = 102;

    private static final int LOGIN_IN_PROGRESS = 200;
    private static final int LOGIN_SUCCESSFUL = 201;
    private static final int LOGIN_FAILED = 202;

    private static final int RESET_IN_PROGRESS = 300;
    private static final int RESET_SUCCESSFUL = 301;
    private static final int RESET_FAILED = 302;

    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<Integer> registerProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> loginProgress = new MutableLiveData<>();
    private final MutableLiveData<Integer> resetProgress = new MutableLiveData<>();

    public FirebaseAuthNetwork(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void firebaseRegister(String username, String email, String password) {
        registerProgress.postValue(REGISTRATION_IN_PROGRESS);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(username, email);
                        FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .set(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        registerProgress.postValue(REGISTRATION_SUCCESSFUL);
                                    } else {
                                        registerProgress.postValue(REGISTRATION_FAILED);
                                    }
                                });
                    } else {
                        registerProgress.postValue(REGISTRATION_FAILED);
                    }
                });
    }

    public void firebaseLogin(String email, String password) {
        loginProgress.postValue(LOGIN_IN_PROGRESS);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginProgress.postValue(LOGIN_SUCCESSFUL);
                    } else {
                        loginProgress.postValue(LOGIN_FAILED);
                    }
                });
    }

    public void resetPassword(String email) {
        resetProgress.postValue(RESET_IN_PROGRESS);
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resetProgress.postValue(RESET_SUCCESSFUL);
                    } else {
                        resetProgress.postValue(RESET_FAILED);
                    }
                });
    }

    public void firebaseLogout() {
        firebaseAuth.signOut();
    }

    public LiveData<Integer> getRegisterProgress() {
        return registerProgress;
    }

    public LiveData<Integer> getLoginProgress() {
        return loginProgress;
    }

    public LiveData<Integer> getResetProgress() {
        return resetProgress;
    }
}
