package ua.com.foxminded.locationtrackera.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private LoginViewModel loginViewModel;
    private LoginFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(LoginViewModel.class);
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

        loginViewModel.getEmailErrorStatus().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null) {
                binding.loginLayoutEmail.setError(null);
            } else {
                binding.loginLayoutEmail.setError(getString(integer));
            }
        });

        loginViewModel.getPasswordErrorStatus().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null) {
                binding.loginLayoutPassword.setError(null);
            } else {
                binding.loginLayoutPassword.setError(getString(integer));
            }
        });

        loginViewModel.getLoginProgress().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                setUpProgressBarVisibility(true);
            } else if (integer == 1) {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                Navigation
                        .findNavController(binding.getRoot())
                        .navigate(R.id.nav_from_loginFragment_to_trackerFragment);
            } else if (integer == R.string.login_failed) {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), integer, Toast.LENGTH_SHORT).show();
            }
        });

        binding.loginBtn.setOnClickListener(this);
        binding.signUpTxt.setOnClickListener(this);
        binding.forgotPasswordTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.loginBtn) {
            loginViewModel.login(
                    binding.loginEditTextEmail.getText().toString().trim(),
                    binding.loginEditTextPassword.getText().toString().trim()
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

    private void setUpProgressBarVisibility(boolean isVisible) {
        final ProgressBar progressBar = binding.loginProgressBar;
        final float progressTargetAlpha = isVisible ? 1F : 0F;
        final int shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (progressTargetAlpha != progressBar.getAlpha()) {
            progressBar.animate().alpha(progressTargetAlpha)
                    .setDuration(shortAnimationDuration)
                    .withEndAction(isVisible ? null : () -> progressBar.setVisibility(View.INVISIBLE))
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
