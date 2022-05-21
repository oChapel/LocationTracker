package ua.com.foxminded.locationtrackera.ui.auth.reset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import ua.com.foxminded.locationtrackera.databinding.FragmentResetPasswordBinding;
import ua.com.foxminded.locationtrackera.mvi.HostedFragment;
import ua.com.foxminded.locationtrackera.ui.auth.AuthViewModelFactory;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenEffect;
import ua.com.foxminded.locationtrackera.ui.auth.reset.state.ResetPasswordScreenState;
import ua.com.foxminded.locationtrackera.util.Utils;

public class ResetPasswordFragment extends HostedFragment<
        ResetPasswordContract.View,
        ResetPasswordScreenState,
        ResetPasswordScreenEffect,
        ResetPasswordContract.ViewModel,
        ResetPasswordContract.Host>
        implements ResetPasswordContract.View, View.OnClickListener {

    private FragmentResetPasswordBinding binding;

    @Override
    protected ResetPasswordContract.ViewModel createModel() {
        return new ViewModelProvider(this,
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

        binding.resetPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        getModel().resetPassword(Utils.getTextFromEditText(binding.resetPasswordEditTextEmail));
    }

    @Override
    public void setProgressVisibility(boolean isProgressVisible) {
        setUpProgressBarVisibility(isProgressVisible);
    }

    @Override
    public void showToastMessage(int idStringResource) {
        Toast.makeText(getContext(), idStringResource, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEmailError(int emailError) {
        if (emailError == 0) {
            binding.resetPasswordLayoutEmail.setError(null);
        } else {
            binding.resetPasswordLayoutEmail.setError(getString(emailError));
        }
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
