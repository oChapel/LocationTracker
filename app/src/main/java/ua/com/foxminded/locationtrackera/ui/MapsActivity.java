package ua.com.foxminded.locationtrackera.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ua.com.foxminded.locationtrackera.R;
import ua.com.foxminded.locationtrackera.ui.auth.login.LoginContract;

public class MapsActivity extends AppCompatActivity implements LoginContract.Host {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }
}