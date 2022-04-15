package ua.com.foxminded.locationtrackera.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public class MainActivity extends AppCompatActivity implements LoginContract.Host {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
