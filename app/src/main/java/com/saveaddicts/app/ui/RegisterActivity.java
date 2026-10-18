package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.saveaddicts.app.R;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etPseudonym, etEmail, etPassword, etConfirmPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvErrorMessage, tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPseudonym = findViewById(R.id.etPseudonym);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgRole = findViewById(R.id.rgRole);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String pseudonym = etPseudonym.getText() != null ? etPseudonym.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (pseudonym.length() < 3) {
            showError("Please enter a pseudonym of at least 3 characters.");
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Registration always produces a PATIENT account.
        // Privileged roles (Psychiatrist, Physician, Admin) must be provisioned
        // by a system administrator via the Firebase Console — not self-assigned.
        final UserRole selectedRole = UserRole.PATIENT;

        setLoading(true);
        tvErrorMessage.setVisibility(View.GONE);

        RepositoryLocator.getAuthRepository().register(pseudonym, email, password, selectedRole, new Callback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile user) {
                setLoading(false);
                // Patients always proceed to substance selection (FR2)
                Intent intent = new Intent(RegisterActivity.this, SubstanceSelectionActivity.class);
                startActivity(intent);
                finishAffinity();
            }

            @Override
            public void onError(Exception exception) {
                setLoading(false);
                showError(exception.getMessage() != null ? exception.getMessage() : "Registration failed.");
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
    }

    private void showError(String msg) {
        tvErrorMessage.setText(msg);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
