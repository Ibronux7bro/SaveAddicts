package com.saveaddicts.app.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.saveaddicts.app.R;
import com.saveaddicts.app.models.Appointment;
import com.saveaddicts.app.models.AppointmentStatus;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner spPhysicians;
    private Button btnPickDate, btnSubmitBooking;
    private RadioGroup rgTimeSlots;
    private TextInputEditText etAppointmentNotes;
    private ProgressBar progressBar;
    private TextView tvBookingError;

    private final List<UserProfile> physicians = new ArrayList<>();
    private String selectedDateFormatted = "";
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        initViews();
        setupPhysiciansDropdown();
        setupDatePicker();
        setupSubmission();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        spPhysicians = findViewById(R.id.spPhysicians);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSubmitBooking = findViewById(R.id.btnSubmitBooking);
        rgTimeSlots = findViewById(R.id.rgTimeSlots);
        etAppointmentNotes = findViewById(R.id.etAppointmentNotes);
        progressBar = findViewById(R.id.progressBar);
        tvBookingError = findViewById(R.id.tvBookingError);

        // Default date: tomorrow
        selectedCalendar.add(Calendar.DAY_OF_YEAR, 1);
        updateDateDisplay();
    }

    private void setupPhysiciansDropdown() {
        RepositoryLocator.getUserRepository().getUsersByRole(UserRole.PHYSICIAN, new Callback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> list) {
                physicians.clear();
                if (list != null) {
                    physicians.addAll(list);
                }
                List<String> names = new ArrayList<>();
                for (UserProfile p : physicians) {
                    names.add(p.getPseudonym() + " (In-Person Clinical Consultations)");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(BookAppointmentActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names);
                spPhysicians.setAdapter(adapter);
            }

            @Override
            public void onError(Exception exception) {
                showError("Could not load physician directory: " + exception.getMessage());
            }
        });
    }

    private void setupDatePicker() {
        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH));
            // Minimum date tomorrow
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            dialog.show();
        });
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDateFormatted = sdf.format(selectedCalendar.getTime());
        btnPickDate.setText("Selected Date: " + selectedDateFormatted);
    }

    private void setupSubmission() {
        btnSubmitBooking.setOnClickListener(v -> {
            if (physicians.isEmpty()) {
                showError("No physicians available for booking.");
                return;
            }

            int physPos = spPhysicians.getSelectedItemPosition();
            if (physPos < 0 || physPos >= physicians.size()) {
                showError("Please select a consulting physician.");
                return;
            }
            UserProfile physician = physicians.get(physPos);

            int slotId = rgTimeSlots.getCheckedRadioButtonId();
            if (slotId == -1) {
                showError("Please select a time slot.");
                return;
            }
            RadioButton rbSlot = findViewById(slotId);
            String timeSlot = rbSlot != null ? rbSlot.getText().toString() : "10:00 AM - 10:30 AM";

            String notes = etAppointmentNotes.getText() != null ? etAppointmentNotes.getText().toString().trim() : "";

            UserProfile currentUser = RepositoryLocator.getAuthRepository().getCurrentUser();
            if (currentUser == null) {
                showError("User session expired. Please sign in again.");
                return;
            }

            Appointment newAppointment = new Appointment(
                    null,
                    currentUser.getUid(),
                    currentUser.getPseudonym(),
                    physician.getUid(),
                    physician.getPseudonym(),
                    selectedDateFormatted,
                    timeSlot,
                    AppointmentStatus.PENDING,
                    notes,
                    System.currentTimeMillis()
            );

            setLoading(true);
            tvBookingError.setVisibility(View.GONE);

            RepositoryLocator.getAppointmentRepository().createAppointment(newAppointment, new Callback<Appointment>() {
                @Override
                public void onSuccess(Appointment result) {
                    setLoading(false);
                    Toast.makeText(BookAppointmentActivity.this, "Appointment scheduled successfully!", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    setLoading(false);
                    showError(exception.getMessage() != null ? exception.getMessage() : "Failed to book appointment.");
                }
            });
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmitBooking.setEnabled(!loading);
    }

    private void showError(String msg) {
        tvBookingError.setText(msg);
        tvBookingError.setVisibility(View.VISIBLE);
    }
}
