package com.saveaddicts.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.saveaddicts.app.R;
import com.saveaddicts.app.models.HealthStatus;
import com.saveaddicts.app.models.RecoveryRecord;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;
import com.saveaddicts.app.security.AccessControl;
import com.saveaddicts.app.ui.components.OptimismGraphView;

import java.util.ArrayList;
import java.util.List;

public class UpdateRecoveryActivity extends AppCompatActivity {

    private UserProfile patient;
    private UserProfile currentPhysician;

    private TextView tvPatientHeader, tvSubstanceHeader, tvScoreValue;
    private OptimismGraphView physicianGraphView;
    private SeekBar sbOptimismScore;
    private Spinner spHealthStatus;
    private TextInputEditText etClinicalNotes;
    private Button btnSaveAssessment;
    private ProgressBar progressBar;

    private int selectedScore = 75;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recovery);

        patient = (UserProfile) getIntent().getSerializableExtra("patient");
        currentPhysician = RepositoryLocator.getAuthRepository().getCurrentUser();

        // Enforce Clinical Role Authorization
        if (currentPhysician == null || !AccessControl.canModifyRecoveryRecord(currentPhysician.getRole())) {
            Toast.makeText(this, "Security Violation: Clinical assessments can only be updated by a Physician.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (patient == null) {
            Toast.makeText(this, "Patient record not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupForm();
        loadExistingRecoveryRecords();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvPatientHeader = findViewById(R.id.tvPatientHeader);
        tvSubstanceHeader = findViewById(R.id.tvSubstanceHeader);
        tvScoreValue = findViewById(R.id.tvScoreValue);
        physicianGraphView = findViewById(R.id.physicianGraphView);
        sbOptimismScore = findViewById(R.id.sbOptimismScore);
        spHealthStatus = findViewById(R.id.spHealthStatus);
        etClinicalNotes = findViewById(R.id.etClinicalNotes);
        btnSaveAssessment = findViewById(R.id.btnSaveAssessment);
        progressBar = findViewById(R.id.progressBar);

        tvPatientHeader.setText("Patient: " + patient.getPseudonym());
        tvSubstanceHeader.setText("Substance: " + patient.getSubstancePreference().getDisplayName());
    }

    private void setupForm() {
        // Optimism Score SeekBar
        sbOptimismScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedScore = progress;
                tvScoreValue.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Health Status Spinner
        List<String> statusLabels = new ArrayList<>();
        for (HealthStatus s : HealthStatus.values()) {
            statusLabels.add(s.getLabel());
        }
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statusLabels);
        spHealthStatus.setAdapter(statusAdapter);

        btnSaveAssessment.setOnClickListener(v -> saveClinicalRecord());
    }

    private void loadExistingRecoveryRecords() {
        RepositoryLocator.getRecoveryRepository().getRecoveryRecordsForPatient(patient.getUid(), new Callback<List<RecoveryRecord>>() {
            @Override
            public void onSuccess(List<RecoveryRecord> list) {
                physicianGraphView.setRecords(list);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(UpdateRecoveryActivity.this, "Could not load prior records: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveClinicalRecord() {
        String notes = etClinicalNotes.getText() != null ? etClinicalNotes.getText().toString().trim() : "";
        if (notes.isEmpty()) {
            Toast.makeText(this, "Please enter clinical evaluation notes.", Toast.LENGTH_SHORT).show();
            return;
        }

        int statusPos = spHealthStatus.getSelectedItemPosition();
        HealthStatus selectedStatus = HealthStatus.values()[Math.max(0, statusPos)];

        RecoveryRecord record = new RecoveryRecord(
                null,
                patient.getUid(),
                currentPhysician.getUid(),
                currentPhysician.getPseudonym(),
                selectedScore,
                selectedStatus,
                notes,
                System.currentTimeMillis()
        );

        setLoading(true);

        RepositoryLocator.getRecoveryRepository().addRecoveryRecord(record, new Callback<RecoveryRecord>() {
            @Override
            public void onSuccess(RecoveryRecord result) {
                setLoading(false);
                Toast.makeText(UpdateRecoveryActivity.this, "Clinical recovery record saved successfully!", Toast.LENGTH_LONG).show();
                loadExistingRecoveryRecords();
                finish();
            }

            @Override
            public void onError(Exception exception) {
                setLoading(false);
                Toast.makeText(UpdateRecoveryActivity.this, "Failed to save record: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSaveAssessment.setEnabled(!loading);
    }
}
