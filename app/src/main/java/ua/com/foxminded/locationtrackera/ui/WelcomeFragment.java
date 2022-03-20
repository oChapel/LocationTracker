package ua.com.foxminded.locationtrackera.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.databinding.WelcomeFragmentBinding;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private WelcomeFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container,
                             @Nullable  Bundle savedInstanceState) {
        binding = WelcomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.welcomeRegisterBtn.setOnClickListener(this);
        binding.welcomeLoginTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.welcomeRegisterBtn) {
            Navigation
                    .findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_welcomeFragment_to_registrationFragment);
        } else if (view == binding.welcomeLoginTxt) {
            Navigation
                    .findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_welcomeFragment_to_loginFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
