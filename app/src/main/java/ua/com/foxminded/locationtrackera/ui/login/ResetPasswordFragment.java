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

import ua.com.foxminded.locationtrackera.databinding.ResetPasswordFragmentBinding;

public class ResetPasswordFragment extends Fragment implements View.OnClickListener {

    private ResetPasswordViewModel viewModel;
    private ResetPasswordFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(ResetPasswordViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ResetPasswordFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getEmailErrorStatus().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null) {
                binding.resetPasswordLayoutEmail.setError(null);
            } else {
                binding.resetPasswordLayoutEmail.setError(getString(integer));
            }
        });

        viewModel.getResetProgress().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                setUpProgressBarVisibility(true);
            } else {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), integer, Toast.LENGTH_LONG).show();
            }
        });

        binding.resetPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        viewModel.resetPassword(binding.resetPasswordEditTextEmail.getText().toString().trim());
    }

    private void setUpProgressBarVisibility(boolean isVisible) {
        final ProgressBar progressBar = binding.resetPasswordProgressBar;
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
