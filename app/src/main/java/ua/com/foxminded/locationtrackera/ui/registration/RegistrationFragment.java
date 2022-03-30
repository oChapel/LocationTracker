package ua.com.foxminded.locationtrackera.ui.registration;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.RegistrationFragmentBinding;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private RegistrationViewModel registrationViewModel;
    private RegistrationFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrationViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(RegistrationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = RegistrationFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registrationViewModel.getUsernameErrorStatus().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null) {
                binding.registerLayoutUserName.setError(null);
            } else {
                binding.registerLayoutUserName.setError(getString(integer));
            }
        });
        registrationViewModel.getEmailErrorStatus().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null) {
                binding.registerLayoutEmail.setError(null);
            } else {
                binding.registerLayoutEmail.setError(getString(integer));
            }
        });
        registrationViewModel.getPasswordErrorStatus().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null) {
                binding.registerLayoutPassword.setError(null);
            } else {
                binding.registerLayoutPassword.setError(getString(integer));
            }
        });
        registrationViewModel.getRegisterProgress().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                setUpProgressBarVisibility(true);
            } else if (integer == R.string.successful_registration) {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), integer, Toast.LENGTH_SHORT).show();
                Navigation
                        .findNavController(binding.getRoot())
                        .navigate(R.id.nav_from_registrationFragment_to_loginFragment);
            } else if (integer == R.string.registration_failed) {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), integer, Toast.LENGTH_LONG).show();
            }
        });

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
                registrationViewModel.registrationDataChanged(
                        binding.registerEditTextUserName.getText().toString().trim(),
                        binding.registerEditTextUserEmail.getText().toString().trim(),
                        binding.registerEditTextPassword.getText().toString().trim()
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
            registrationViewModel.registerUser(
                    binding.registerEditTextUserName.getText().toString().trim(),
                    binding.registerEditTextUserEmail.getText().toString().trim(),
                    binding.registerEditTextPassword.getText().toString().trim()
            );
        } else if (view == binding.registerLogInTxt) {
            Navigation
                    .findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_registrationFragment_to_loginFragment);
        }
    }

    private void setUpProgressBarVisibility(boolean isVisible) {
        final ProgressBar progressBar = binding.registerProgressBar;
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
