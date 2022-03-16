package ua.com.foxminded.locationtrackera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ua.com.foxminded.locationtrackera.databinding.LoginActivityBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}