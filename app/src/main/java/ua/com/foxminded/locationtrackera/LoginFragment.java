package ua.com.foxminded.locationtrackera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ua.com.foxminded.locationtrackera.databinding.LoginFragmentBinding;
import ua.com.foxminded.locationtrackera.ui.login.LoginViewModel;
import ua.com.foxminded.locationtrackera.ui.login.LoginViewModelFactory;

public class LoginFragment extends Fragment implements View.OnClickListener {

    //private LoginViewModel loginViewModel;
    private LoginFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*loginViewModel = new ViewModelProvider(this,
                new LoginViewModelFactory()).get(LoginViewModel.class);*/
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

        binding.signInBtn.setOnClickListener(this);
        binding.signUpTxt.setOnClickListener(this);
        binding.forgotPasswordTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.signInBtn) {
            Toast.makeText(getContext(), "Button clicked!", Toast.LENGTH_SHORT).show();
        } else if (view == binding.signUpTxt) {
            Navigation
                    .findNavController(binding.getRoot())
                    .navigate(R.id.nav_from_loginFragment_to_registrationFragment);
        } else if (view == binding.forgotPasswordTxt) {
            Toast.makeText(getContext(), "Boy, I'm sorry for that(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
