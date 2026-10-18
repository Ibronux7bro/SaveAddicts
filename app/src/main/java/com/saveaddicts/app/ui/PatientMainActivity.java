package com.saveaddicts.app.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.adapters.AppointmentAdapter;
import com.saveaddicts.app.adapters.ConversationAdapter;
import com.saveaddicts.app.adapters.EducationalResourceAdapter;
import com.saveaddicts.app.models.*;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;
import com.saveaddicts.app.ui.components.OptimismGraphView;

import java.util.List;

public class PatientMainActivity extends AppCompatActivity {

    private UserProfile currentUser;

    // Header views
    private TextView tvUserAvatar, tvPseudonymHeader, tvSubstanceBadgeHeader;
    private View btnChangeSubstance, btnLogout;

    // Tab Views
    private View viewTabRecovery, viewTabAppointments, viewTabMessages, viewTabLibrary;
    private View navItemRecovery, navItemAppointments, navItemMessages, navItemLibrary;
    private ImageView ivNavRecovery, ivNavAppointments, ivNavMessages, ivNavLibrary;
    private TextView tvNavRecovery, tvNavAppointments, tvNavMessages, tvNavLibrary;

    // Recovery Tab views (FR5)
    private OptimismGraphView optimismGraphView;
    private TextView tvHealthStatusDisplay, tvLatestNotes;

    // Appointments Tab views (FR4)
    private RecyclerView rvPatientAppointments;
    private AppointmentAdapter appointmentAdapter;
    private TextView tvEmptyAppointments;
    private Button btnBookAppointment;

    // Messages Tab views (FR3)
    private RecyclerView rvConversations;
    private ConversationAdapter conversationAdapter;
    private TextView tvEmptyMessages;

    // Library Tab views (FR6)
    private RecyclerView rvEducationalResources;
    private EducationalResourceAdapter resourceAdapter;
    private TextView tvEmptyLibrary;
    private TextView chipFilterSubstance, chipFilterAll, chipFilterVideos, chipFilterBooks;
    private SubstanceType currentSubstanceFilter = SubstanceType.OPIOIDS;
    private ResourceType currentTypeFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        currentUser = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupHeader();
        setupNavigation();
        setupRecoveryTab();
        setupAppointmentsTab();
        setupMessagesTab();
        setupLibraryTab();

        // Start on Recovery Tab
        selectTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllData();
    }

    private void initViews() {
        tvUserAvatar = findViewById(R.id.tvUserAvatar);
        tvPseudonymHeader = findViewById(R.id.tvPseudonymHeader);
        tvSubstanceBadgeHeader = findViewById(R.id.tvSubstanceBadgeHeader);
        btnChangeSubstance = findViewById(R.id.btnChangeSubstance);
        btnLogout = findViewById(R.id.btnLogout);

        viewTabRecovery = findViewById(R.id.viewTabRecovery);
        viewTabAppointments = findViewById(R.id.viewTabAppointments);
        viewTabMessages = findViewById(R.id.viewTabMessages);
        viewTabLibrary = findViewById(R.id.viewTabLibrary);

        navItemRecovery = findViewById(R.id.navItemRecovery);
        navItemAppointments = findViewById(R.id.navItemAppointments);
        navItemMessages = findViewById(R.id.navItemMessages);
        navItemLibrary = findViewById(R.id.navItemLibrary);

        ivNavRecovery = findViewById(R.id.ivNavRecovery);
        ivNavAppointments = findViewById(R.id.ivNavAppointments);
        ivNavMessages = findViewById(R.id.ivNavMessages);
        ivNavLibrary = findViewById(R.id.ivNavLibrary);

        tvNavRecovery = findViewById(R.id.tvNavRecovery);
        tvNavAppointments = findViewById(R.id.tvNavAppointments);
        tvNavMessages = findViewById(R.id.tvNavMessages);
        tvNavLibrary = findViewById(R.id.tvNavLibrary);

        optimismGraphView = findViewById(R.id.optimismGraphView);
        tvHealthStatusDisplay = findViewById(R.id.tvHealthStatusDisplay);
        tvLatestNotes = findViewById(R.id.tvLatestNotes);

        rvPatientAppointments = findViewById(R.id.rvPatientAppointments);
        tvEmptyAppointments = findViewById(R.id.tvEmptyAppointments);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

        rvConversations = findViewById(R.id.rvConversations);
        tvEmptyMessages = findViewById(R.id.tvEmptyMessages);

        rvEducationalResources = findViewById(R.id.rvEducationalResources);
        tvEmptyLibrary = findViewById(R.id.tvEmptyLibrary);
        chipFilterSubstance = findViewById(R.id.chipFilterSubstance);
        chipFilterAll = findViewById(R.id.chipFilterAll);
        chipFilterVideos = findViewById(R.id.chipFilterVideos);
        chipFilterBooks = findViewById(R.id.chipFilterBooks);
    }

    private void setupHeader() {
        tvPseudonymHeader.setText(currentUser.getPseudonym());
        tvUserAvatar.setText(currentUser.getInitials());
        tvSubstanceBadgeHeader.setText("Substance: " + currentUser.getSubstancePreference().getDisplayName());

        btnChangeSubstance.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubstanceSelectionActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out of your confidential session?")
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
        navItemRecovery.setOnClickListener(v -> selectTab(0));
        navItemAppointments.setOnClickListener(v -> selectTab(1));
        navItemMessages.setOnClickListener(v -> selectTab(2));
        navItemLibrary.setOnClickListener(v -> selectTab(3));

        // Quick action cards in Tab 0
        findViewById(R.id.cardQuickAppointments).setOnClickListener(v -> selectTab(1));
        findViewById(R.id.cardQuickMessaging).setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int index) {
        viewTabRecovery.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        viewTabAppointments.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        viewTabMessages.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
        viewTabLibrary.setVisibility(index == 3 ? View.VISIBLE : View.GONE);

        int activeColor = ContextCompat.getColor(this, R.color.accent_teal);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_secondary);

        ivNavRecovery.setColorFilter(index == 0 ? activeColor : inactiveColor);
        tvNavRecovery.setTextColor(index == 0 ? activeColor : inactiveColor);

        ivNavAppointments.setColorFilter(index == 1 ? activeColor : inactiveColor);
        tvNavAppointments.setTextColor(index == 1 ? activeColor : inactiveColor);

        ivNavMessages.setColorFilter(index == 2 ? activeColor : inactiveColor);
        tvNavMessages.setTextColor(index == 2 ? activeColor : inactiveColor);

        ivNavLibrary.setColorFilter(index == 3 ? activeColor : inactiveColor);
        tvNavLibrary.setTextColor(index == 3 ? activeColor : inactiveColor);
    }

    private void setupRecoveryTab() {
        loadRecoveryRecords();
    }

    private void loadRecoveryRecords() {
        RepositoryLocator.getRecoveryRepository().getRecoveryRecordsForPatient(currentUser.getUid(), new Callback<List<RecoveryRecord>>() {
            @Override
            public void onSuccess(List<RecoveryRecord> records) {
                optimismGraphView.setRecords(records);
                if (records != null && !records.isEmpty()) {
                    RecoveryRecord latest = records.get(records.size() - 1);
                    tvHealthStatusDisplay.setText(latest.getHealthStatus().getLabel());
                    try {
                        tvHealthStatusDisplay.setTextColor(Color.parseColor(latest.getHealthStatus().getColorHex()));
                    } catch (Exception ignored) {}
                    tvLatestNotes.setText("Physician Assessment (" + latest.getFormattedDate() + "):\n" + latest.getClinicalNotes());
                } else {
                    tvHealthStatusDisplay.setText("Awaiting Initial Consultation");
                    tvLatestNotes.setText("Your consulting physician will record your clinical recovery trajectory following your medical assessment.");
                }
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PatientMainActivity.this, "Error loading recovery data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAppointmentsTab() {
        rvPatientAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(UserRole.PATIENT, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onConfirm(Appointment appointment) {
                // Not applicable for patient
            }

            @Override
            public void onCancel(Appointment appointment) {
                new AlertDialog.Builder(PatientMainActivity.this)
                        .setTitle("Cancel Appointment")
                        .setMessage("Are you sure you want to cancel this consultation with " + appointment.getPhysicianName() + "?")
                        .setPositiveButton("Cancel Consultation", (dialog, which) -> {
                            RepositoryLocator.getAppointmentRepository().cancelAppointment(appointment.getId(), "Cancelled by patient", new Callback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(PatientMainActivity.this, "Consultation cancelled.", Toast.LENGTH_SHORT).show();
                                    loadAppointments();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Toast.makeText(PatientMainActivity.this, "Cancellation failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Keep", null)
                        .show();
            }
        });
        rvPatientAppointments.setAdapter(appointmentAdapter);

        btnBookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(PatientMainActivity.this, BookAppointmentActivity.class);
            startActivity(intent);
        });

        loadAppointments();
    }

    private void loadAppointments() {
        RepositoryLocator.getAppointmentRepository().getAppointmentsForPatient(currentUser.getUid(), new Callback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> list) {
                appointmentAdapter.setItems(list);
                tvEmptyAppointments.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PatientMainActivity.this, "Error loading appointments: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMessagesTab() {
        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        conversationAdapter = new ConversationAdapter(UserRole.PATIENT, conversation -> {
            Intent intent = new Intent(PatientMainActivity.this, ChatActivity.class);
            intent.putExtra("conversation", conversation);
            startActivity(intent);
        });
        rvConversations.setAdapter(conversationAdapter);

        loadConversations();
    }

    private void loadConversations() {
        RepositoryLocator.getMessagingRepository().getConversationsForUser(currentUser.getUid(), new Callback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> list) {
                conversationAdapter.setItems(list);
                tvEmptyMessages.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PatientMainActivity.this, "Error loading messages: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLibraryTab() {
        rvEducationalResources.setLayoutManager(new LinearLayoutManager(this));
        resourceAdapter = new EducationalResourceAdapter(false, new EducationalResourceAdapter.OnResourceClickListener() {
            @Override
            public void onItemClick(EducationalResource resource) {
                Intent intent = new Intent(PatientMainActivity.this, ResourceDetailActivity.class);
                intent.putExtra("resource", resource);
                startActivity(intent);
            }

            @Override
            public void onEdit(EducationalResource resource) {}

            @Override
            public void onDelete(EducationalResource resource) {}
        });
        rvEducationalResources.setAdapter(resourceAdapter);

        currentSubstanceFilter = currentUser.getSubstancePreference();
        chipFilterSubstance.setText("My Focus (" + currentSubstanceFilter.getDisplayName() + ")");

        chipFilterSubstance.setOnClickListener(v -> {
            currentSubstanceFilter = currentUser.getSubstancePreference();
            updateFilterChips();
            loadLibraryResources();
        });

        chipFilterAll.setOnClickListener(v -> {
            currentSubstanceFilter = SubstanceType.ALL;
            updateFilterChips();
            loadLibraryResources();
        });

        chipFilterVideos.setOnClickListener(v -> {
            currentTypeFilter = currentTypeFilter == ResourceType.VIDEO ? null : ResourceType.VIDEO;
            updateFilterChips();
            loadLibraryResources();
        });

        chipFilterBooks.setOnClickListener(v -> {
            currentTypeFilter = currentTypeFilter == ResourceType.BOOK ? null : ResourceType.BOOK;
            updateFilterChips();
            loadLibraryResources();
        });

        loadLibraryResources();
    }

    private void updateFilterChips() {
        boolean isMyFocus = currentSubstanceFilter == currentUser.getSubstancePreference();
        chipFilterSubstance.setBackgroundResource(isMyFocus ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterSubstance.setTextColor(isMyFocus ? Color.WHITE : ContextCompat.getColor(this, R.color.text_primary));

        boolean isAll = currentSubstanceFilter == SubstanceType.ALL;
        chipFilterAll.setBackgroundResource(isAll ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterAll.setTextColor(isAll ? Color.WHITE : ContextCompat.getColor(this, R.color.text_primary));

        boolean isVideo = currentTypeFilter == ResourceType.VIDEO;
        chipFilterVideos.setBackgroundResource(isVideo ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterVideos.setTextColor(isVideo ? Color.WHITE : ContextCompat.getColor(this, R.color.text_primary));

        boolean isBook = currentTypeFilter == ResourceType.BOOK;
        chipFilterBooks.setBackgroundResource(isBook ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipFilterBooks.setTextColor(isBook ? Color.WHITE : ContextCompat.getColor(this, R.color.text_primary));
    }

    private void loadLibraryResources() {
        RepositoryLocator.getEducationalRepository().getFilteredResources(currentSubstanceFilter, currentTypeFilter, AgeGroup.ALL,
                new Callback<List<EducationalResource>>() {
                    @Override
                    public void onSuccess(List<EducationalResource> list) {
                        resourceAdapter.setItems(list);
                        tvEmptyLibrary.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(PatientMainActivity.this, "Error loading library: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshAllData() {
        currentUser = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentUser != null) {
            setupHeader();
            loadRecoveryRecords();
            loadAppointments();
            loadConversations();
            loadLibraryResources();
        }
    }
}
