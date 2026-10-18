package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.adapters.EducationalResourceAdapter;
import com.saveaddicts.app.models.EducationalResource;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;
import com.saveaddicts.app.security.AccessControl;

import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private UserProfile currentAdmin;
    private RecyclerView rvAdminResources;
    private EducationalResourceAdapter adapter;
    private Button btnAddResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        currentAdmin = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentAdmin == null || !AccessControl.canManageEducationalContent(currentAdmin.getRole())) {
            Toast.makeText(this, "Security Violation: Administrative access required.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadAllResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllResources();
    }

    private void initViews() {
        rvAdminResources = findViewById(R.id.rvAdminResources);
        btnAddResource = findViewById(R.id.btnAddResource);

        btnAddResource.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, AddEditResourceActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("End your administrative management session?")
                    .setPositiveButton("Sign Out", (dialog, which) -> {
                        RepositoryLocator.getAuthRepository().logout();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupRecyclerView() {
        rvAdminResources.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EducationalResourceAdapter(true, new EducationalResourceAdapter.OnResourceClickListener() {
            @Override
            public void onItemClick(EducationalResource resource) {
                Intent intent = new Intent(AdminMainActivity.this, ResourceDetailActivity.class);
                intent.putExtra("resource", resource);
                startActivity(intent);
            }

            @Override
            public void onEdit(EducationalResource resource) {
                Intent intent = new Intent(AdminMainActivity.this, AddEditResourceActivity.class);
                intent.putExtra("resource", resource);
                startActivity(intent);
            }

            @Override
            public void onDelete(EducationalResource resource) {
                new AlertDialog.Builder(AdminMainActivity.this)
                        .setTitle("Delete Educational Resource")
                        .setMessage("Are you sure you want to remove \"" + resource.getTitle() + "\" from the educational library?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            RepositoryLocator.getEducationalRepository().deleteResource(resource.getId(), new Callback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(AdminMainActivity.this, "Resource deleted.", Toast.LENGTH_SHORT).show();
                                    loadAllResources();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Toast.makeText(AdminMainActivity.this, "Delete failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        rvAdminResources.setAdapter(adapter);
    }

    private void loadAllResources() {
        RepositoryLocator.getEducationalRepository().getAllResources(new Callback<List<EducationalResource>>() {
            @Override
            public void onSuccess(List<EducationalResource> list) {
                adapter.setItems(list);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(AdminMainActivity.this, "Failed to load resources: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
