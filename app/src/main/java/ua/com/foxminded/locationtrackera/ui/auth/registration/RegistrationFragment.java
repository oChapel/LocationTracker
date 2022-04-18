package ua.com.foxminded.locationtrackera.ui.auth.registration;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import ua.com.foxminded.locationtrackera.databinding.FragmentRegistrationBinding;
import ua.com.foxminded.locationtrackera.mvi.HostedFragment;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.registration.state.RegistrationScreenState;
import ua.com.foxminded.locationtrackera.util.Utils;

public class RegistrationFragment extends HostedFragment<
        RegistrationContract.View,
        RegistrationScreenState,
        RegistrationScreenEffect,
        RegistrationContract.ViewModel,
        RegistrationContract.Host>
        implements RegistrationContract.View, View.OnClickListener {

    private FragmentRegistrationBinding binding;

    @Override
    protected RegistrationContract.ViewModel createModel() {
        return new ViewModelProvider(this,
                new AuthViewModelFactory()).get(RegistrationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerBtn.setOnClickListener(this);
        binding.registerLogInTxt.setOnClickListener(this);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                getModel().registrationDataChanged(
                        Utils.getTextFromEditText(binding.registerEditTextUserName),
                        Utils.getTextFromEditText(binding.registerEditTextUserEmail),
                        Utils.getTextFromEditText(binding.registerEditTextPassword)
                );
            }
        };
        binding.registerEditTextUserName.addTextChangedListener(watcher);
        binding.registerEditTextUserEmail.addTextChangedListener(watcher);
        binding.registerEditTextPassword.addTextChangedListener(watcher);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.registerBtn) {
            getModel().registerUser(
                    Utils.getTextFromEditText(binding.registerEditTextUserName),
                    Utils.getTextFromEditText(binding.registerEditTextUserEmail),
                    Utils.getTextFromEditText(binding.registerEditTextPassword)
            );
        } else if (view == binding.registerLogInTxt) {
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_registrationFragment_to_loginFragment);
        }
    }

    @Override
    public void setProgressVisibility(boolean isProgressVisible) {
        setUpProgressBarVisibility(isProgressVisible);
    }

    @Override
    public void showErrors(int usernameError, int emailError, int passwordError) {
        if (usernameError == 0) {
            binding.registerLayoutUserName.setError(null);
        } else {
            binding.registerLayoutUserName.setError(getString(usernameError));
        }
        if (emailError == 0) {
            binding.registerLayoutEmail.setError(null);
        } else {
            binding.registerLayoutEmail.setError(getString(emailError));
        }
        if (passwordError == 0) {
            binding.registerLayoutPassword.setError(null);
        } else {
            binding.registerLayoutPassword.setError(getString(passwordError));
        }
    }

    @Override
    public void proceedToNextScreen() {
        Toast.makeText(getContext(), R.string.successful_registration, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.nav_from_registrationFragment_to_loginFragment);
    }

    @Override
    public void showFailureToastMessage() {
        Toast.makeText(getContext(), R.string.registration_failed, Toast.LENGTH_LONG).show();
    }

    private void setUpProgressBarVisibility(boolean isVisible) {
        final ProgressBar progressBar = binding.registerProgressBar;
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
