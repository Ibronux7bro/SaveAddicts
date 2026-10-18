package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.RepositoryLocator;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthAndRoute, 1200);
    }

    private void checkAuthAndRoute() {
        UserProfile currentUser = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentUser != null) {
            routeUserByRole(currentUser);
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void routeUserByRole(UserProfile user) {
        UserRole role = user.getRole();
        Intent intent;
        if (role == UserRole.PSYCHIATRIST) {
            intent = new Intent(this, PsychiatristMainActivity.class);
        } else if (role == UserRole.PHYSICIAN) {
            intent = new Intent(this, PhysicianMainActivity.class);
        } else if (role == UserRole.ADMIN) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            intent = new Intent(this, PatientMainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
