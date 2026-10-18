package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.saveaddicts.app.R;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvErrorMessage, tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Pre-fill patient email by default for convenience
        etEmail.setText("patient@saveaddicts.org");
        etPassword.setText("password123");
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Quick evaluation logins
        findViewById(R.id.btnQuickPatient).setOnClickListener(v -> {
            etEmail.setText("patient@saveaddicts.org");
            etPassword.setText("password123");
            attemptLogin();
        });

        findViewById(R.id.btnQuickPsychiatrist).setOnClickListener(v -> {
            etEmail.setText("psychiatrist@saveaddicts.org");
            etPassword.setText("password123");
            attemptLogin();
        });

        findViewById(R.id.btnQuickPhysician).setOnClickListener(v -> {
            etEmail.setText("physician@saveaddicts.org");
            etPassword.setText("password123");
            attemptLogin();
        });

        findViewById(R.id.btnQuickAdmin).setOnClickListener(v -> {
            etEmail.setText("admin@saveaddicts.org");
            etPassword.setText("password123");
            attemptLogin();
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty()) {
            showError("Please enter your email address.");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }

        setLoading(true);
        tvErrorMessage.setVisibility(View.GONE);

        RepositoryLocator.getAuthRepository().login(email, password, new Callback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile user) {
                setLoading(false);
                navigateToDashboard(user);
            }

            @Override
            public void onError(Exception exception) {
                setLoading(false);
                showError(exception.getMessage() != null ? exception.getMessage() : "Authentication failed.");
            }
        });
    }

    private void navigateToDashboard(UserProfile user) {
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

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }

    private void showError(String msg) {
        tvErrorMessage.setText(msg);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
