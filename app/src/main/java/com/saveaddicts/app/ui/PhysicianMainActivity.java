package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.adapters.AppointmentAdapter;
import com.saveaddicts.app.adapters.PatientListAdapter;
import com.saveaddicts.app.models.Appointment;
import com.saveaddicts.app.models.AppointmentStatus;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

import java.util.List;

public class PhysicianMainActivity extends AppCompatActivity {

    private UserProfile currentPhysician;

    private TextView tvPhysicianName, tvAvatarInitials;
    private Button btnTabAppointments, btnTabRecovery;
    private View viewPhysicianAppointments, viewPhysicianRecovery;

    // Appointments Tab (FR9)
    private RecyclerView rvPhysicianAppointments;
    private AppointmentAdapter appointmentAdapter;
    private TextView tvEmptyAppointments;

    // Recovery Tab (FR8)
    private RecyclerView rvPatientsList;
    private PatientListAdapter patientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_main);

        currentPhysician = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentPhysician == null || currentPhysician.getRole() != UserRole.PHYSICIAN) {
            Toast.makeText(this, "Access restricted to licensed Physicians.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupNavigation();
        setupAppointmentsTab();
        setupRecoveryTab();
        loadAllData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllData();
    }

    private void initViews() {
        tvPhysicianName = findViewById(R.id.tvPhysicianName);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        btnTabAppointments = findViewById(R.id.btnTabAppointments);
        btnTabRecovery = findViewById(R.id.btnTabRecovery);
        viewPhysicianAppointments = findViewById(R.id.viewPhysicianAppointments);
        viewPhysicianRecovery = findViewById(R.id.viewPhysicianRecovery);

        rvPhysicianAppointments = findViewById(R.id.rvPhysicianAppointments);
        tvEmptyAppointments = findViewById(R.id.tvEmptyAppointments);
        rvPatientsList = findViewById(R.id.rvPatientsList);

        tvPhysicianName.setText(currentPhysician.getPseudonym());
        tvAvatarInitials.setText("MD");

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("End your clinical physician session?")
                    .setPositiveButton("Sign Out", (dialog, which) -> {
                        RepositoryLocator.getAuthRepository().logout();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupNavigation() {
        btnTabAppointments.setOnClickListener(v -> showTab(true));
        btnTabRecovery.setOnClickListener(v -> showTab(false));
    }

    private void showTab(boolean isAppointments) {
        viewPhysicianAppointments.setVisibility(isAppointments ? View.VISIBLE : View.GONE);
        viewPhysicianRecovery.setVisibility(isAppointments ? View.GONE : View.VISIBLE);

        if (isAppointments) {
            btnTabAppointments.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.accent_teal));
            btnTabAppointments.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnTabRecovery.setBackgroundTintList(null);
            btnTabRecovery.setTextColor(ContextCompat.getColor(this, R.color.primary_navy));
        } else {
            btnTabRecovery.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.accent_teal));
            btnTabRecovery.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnTabAppointments.setBackgroundTintList(null);
            btnTabAppointments.setTextColor(ContextCompat.getColor(this, R.color.primary_navy));
        }
    }

    private void setupAppointmentsTab() {
        rvPhysicianAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(UserRole.PHYSICIAN, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onConfirm(Appointment appointment) {
                RepositoryLocator.getAppointmentRepository().updateAppointmentStatus(appointment.getId(), AppointmentStatus.CONFIRMED, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(PhysicianMainActivity.this, "Appointment confirmed.", Toast.LENGTH_SHORT).show();
                        loadAppointments();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(PhysicianMainActivity.this, "Failed to confirm: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel(Appointment appointment) {
                new AlertDialog.Builder(PhysicianMainActivity.this)
                        .setTitle("Cancel Appointment")
                        .setMessage("Cancel consultation with patient " + appointment.getPatientPseudonym() + "?")
                        .setPositiveButton("Cancel Consultation", (dialog, which) -> {
                            RepositoryLocator.getAppointmentRepository().cancelAppointment(appointment.getId(), "Clinically rescheduled/cancelled by physician", new Callback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(PhysicianMainActivity.this, "Consultation cancelled.", Toast.LENGTH_SHORT).show();
                                    loadAppointments();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Toast.makeText(PhysicianMainActivity.this, "Failed to cancel: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Back", null)
                        .show();
            }
        });
        rvPhysicianAppointments.setAdapter(appointmentAdapter);
    }

    private void setupRecoveryTab() {
        rvPatientsList.setLayoutManager(new LinearLayoutManager(this));
        patientAdapter = new PatientListAdapter(patient -> {
            Intent intent = new Intent(PhysicianMainActivity.this, UpdateRecoveryActivity.class);
            intent.putExtra("patient", patient);
            startActivity(intent);
        });
        rvPatientsList.setAdapter(patientAdapter);
    }

    private void loadAppointments() {
        RepositoryLocator.getAppointmentRepository().getAppointmentsForPhysician(currentPhysician.getUid(), new Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> list) {
                appointmentAdapter.setItems(list);
                tvEmptyAppointments.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PhysicianMainActivity.this, "Error loading appointments: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPatients() {
        RepositoryLocator.getUserRepository().getUsersByRole(UserRole.PATIENT, new Callback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> list) {
                patientAdapter.setItems(list);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PhysicianMainActivity.this, "Error loading patients: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllData() {
        loadAppointments();
        loadPatients();
    }
}
