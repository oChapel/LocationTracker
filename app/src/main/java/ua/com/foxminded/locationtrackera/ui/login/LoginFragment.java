package ua.com.foxminded.locationtrackera.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.LoginFragmentBinding;
import ua.com.foxminded.locationtrackera.mvi.HostedFragment;
import ua.com.foxminded.locationtrackera.ui.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.util.Utils;

public class LoginFragment extends HostedFragment<LoginScreenState, LoginContract.ViewModel, LoginContract.Host>
        implements LoginContract.View, View.OnClickListener {

    private LoginFragmentBinding binding;

    @Override
    protected LoginContract.ViewModel createModel() {
        return new ViewModelProvider(this,
                new AuthViewModelFactory()).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginBtn.setOnClickListener(this);
        binding.signUpTxt.setOnClickListener(this);
        binding.forgotPasswordTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.loginBtn) {
            getModel().login(
                    Utils.getTextFromEditText(binding.loginEditTextEmail),
                    Utils.getTextFromEditText(binding.loginEditTextPassword)
            );
        } else if (view == binding.signUpTxt) {
            Navigation
                    .findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_loginFragment_to_registrationFragment);
        } else if (view == binding.forgotPasswordTxt) {
            Navigation
                    .findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_loginFragment_to_resetPasswordFragment);
        }
    }

    @Override
    public void showProgress() {
        resetErrors();
        setUpProgressBarVisibility(true);
    }

    @Override
    public void proceedToNextScreen() {
        setUpProgressBarVisibility(false);
        Toast.makeText(getContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
        Navigation
                .findNavController(binding.getRoot())
                .navigate(R.id.nav_from_loginFragment_to_trackerFragment);
    }

    @Override
    public void showFailureToastMessage() {
        setUpProgressBarVisibility(false);
        Toast.makeText(getContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmailAndPasswordError(int emailError, int passwordError) {
        if (emailError == -1) {
            binding.loginLayoutEmail.setError(null);
        } else {
            binding.loginLayoutEmail.setError(getString(emailError));
        }
        if (passwordError == -1) {
            binding.loginLayoutPassword.setError(null);
        } else {
            binding.loginLayoutPassword.setError(getString(passwordError));
        }
    }

    private void resetErrors() {
        binding.loginLayoutEmail.setError(null);
        binding.loginLayoutPassword.setError(null);
    }

    private void setUpProgressBarVisibility(boolean isVisible) {
        final ProgressBar progressBar = binding.loginProgressBar;
        final float progressTargetAlpha = isVisible ? 1F : 0F;
        final int shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (progressTargetAlpha != progressBar.getAlpha()) {
            progressBar.animate().alpha(progressTargetAlpha)
                    .setDuration(shortAnimationDuration)
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
