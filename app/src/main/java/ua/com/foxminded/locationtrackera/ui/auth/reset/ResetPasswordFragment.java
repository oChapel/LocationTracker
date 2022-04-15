package ua.com.foxminded.locationtrackera.ui.auth.reset;

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

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.FragmentResetPasswordBinding;
import ua.com.foxminded.locationtrackera.model.auth.AuthConstants;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.util.Utils;

public class ResetPasswordFragment extends Fragment implements View.OnClickListener {

    private ResetPasswordViewModel viewModel;
    private FragmentResetPasswordBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this,
                new AuthViewModelFactory()).get(ResetPasswordViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
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
            if (integer == AuthConstants.RESET_IN_PROGRESS) {
                setUpProgressBarVisibility(true);
            } else if (integer == AuthConstants.RESET_SUCCESSFUL) {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), R.string.successful_reset, Toast.LENGTH_LONG).show();
            } else if (integer == AuthConstants.RESET_FAILED) {
                setUpProgressBarVisibility(false);
                Toast.makeText(getContext(), R.string.reset_failed, Toast.LENGTH_LONG).show();
            }
        });

        binding.resetPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        viewModel.resetPassword(Utils.getTextFromEditText(binding.resetPasswordEditTextEmail));
    }

    private void setUpProgressBarVisibility(boolean isVisible) {
        final ProgressBar progressBar = binding.resetPasswordProgressBar;
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
