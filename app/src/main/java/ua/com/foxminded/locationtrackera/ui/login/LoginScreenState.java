package ua.com.foxminded.locationtrackera.ui.login;


import ua.com.foxminded.locationtrackera.mvi.ScreenState;

public class LoginScreenState extends ScreenState<LoginContract.View> {

    private static final int LOGIN_IN_PROGRESS = 1;
    private static final int LOGIN_SUCCESSFUL = 2;
    private static final int LOGIN_FAILED = 3;
    private static final int LOGIN_ERROR = 4;

    private final int action;
    private final int emailError;
    private final int passwordError;

    public LoginScreenState(int action) {
        this.action = action;
        this.emailError = -1;
        this.passwordError = -1;
    }

    public LoginScreenState(int action, int emailError, int passwordError) {
        this.action = action;
        this.emailError = emailError;
        this.passwordError = passwordError;
    }

    public static LoginScreenState createLoginInProgressState() {
        return new LoginScreenState(LOGIN_IN_PROGRESS);
    }

    public static LoginScreenState createLoginSuccessState() {
        return new LoginScreenState(LOGIN_SUCCESSFUL);
    }

    public static LoginScreenState createLoginFailureState() {
        return new LoginScreenState(LOGIN_FAILED);
    }

    public static LoginScreenState createLoginErrorState(int emailError, int passwordError) {
        return new LoginScreenState(LOGIN_ERROR, emailError, passwordError);
    }

    @Override
    public void visit(LoginContract.View loginScreen) {
        if (LOGIN_IN_PROGRESS == action) {
            loginScreen.showProgress();
        } else if (LOGIN_SUCCESSFUL == action) {
            loginScreen.proceedToNextScreen();
        } else if (LOGIN_FAILED == action) {
            loginScreen.showFailureToastMessage();
        } else if (LOGIN_ERROR == action) {
            loginScreen.showEmailAndPasswordError(emailError, passwordError);
        }
    }
}
