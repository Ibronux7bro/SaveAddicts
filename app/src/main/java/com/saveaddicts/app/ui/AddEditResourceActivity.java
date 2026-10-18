package com.saveaddicts.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.saveaddicts.app.R;
import com.saveaddicts.app.models.*;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;
import com.saveaddicts.app.security.AccessControl;

import java.util.ArrayList;
import java.util.List;

public class AddEditResourceActivity extends AppCompatActivity {

    private EducationalResource existingResource;

    private TextView tvHeaderTitle, tvErrorMessage;
    private TextInputEditText etTitle, etDescription, etCulturalContext, etAuthor, etContentBody;
    private Spinner spResourceType, spSubstanceType, spAgeGroup;
    private Button btnSaveResource;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_resource);

        UserProfile currentAdmin = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentAdmin == null || !AccessControl.canManageEducationalContent(currentAdmin.getRole())) {
            Toast.makeText(this, "Unauthorized access.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        existingResource = (EducationalResource) getIntent().getSerializableExtra("resource");

        initViews();
        setupSpinners();
        populateIfEditing();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etCulturalContext = findViewById(R.id.etCulturalContext);
        etAuthor = findViewById(R.id.etAuthor);
        etContentBody = findViewById(R.id.etContentBody);
        spResourceType = findViewById(R.id.spResourceType);
        spSubstanceType = findViewById(R.id.spSubstanceType);
        spAgeGroup = findViewById(R.id.spAgeGroup);
        btnSaveResource = findViewById(R.id.btnSaveResource);
        progressBar = findViewById(R.id.progressBar);

        btnSaveResource.setOnClickListener(v -> saveResource());
    }

    private void setupSpinners() {
        // Resource Type
        List<String> types = new ArrayList<>();
        for (ResourceType t : ResourceType.values()) {
            types.add(t.getLabel());
        }
        spResourceType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));

        // Substance Type
        List<String> substances = new ArrayList<>();
        for (SubstanceType s : SubstanceType.values()) {
            substances.add(s.getDisplayName());
        }
        spSubstanceType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, substances));

        // Age Group
        List<String> ageGroups = new ArrayList<>();
        for (AgeGroup a : AgeGroup.values()) {
            ageGroups.add(a.getLabel());
        }
        spAgeGroup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ageGroups));
    }

    private void populateIfEditing() {
        if (existingResource != null) {
            tvHeaderTitle.setText("Edit Educational Resource");
            etTitle.setText(existingResource.getTitle());
            etDescription.setText(existingResource.getDescription());
            etCulturalContext.setText(existingResource.getCulturalContext());
            etAuthor.setText(existingResource.getAuthorOrSource());
            etContentBody.setText(existingResource.getContentBody());

            spResourceType.setSelection(existingResource.getResourceType().ordinal());
            spSubstanceType.setSelection(existingResource.getSubstanceType().ordinal());
            spAgeGroup.setSelection(existingResource.getAgeGroup().ordinal());
        } else {
            tvHeaderTitle.setText("Add Educational Resource");
            etCulturalContext.setText("GCC & International Clinical Context");
            etAuthor.setText("Educational Contributor");
        }
    }

    private void saveResource() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String cultural = etCulturalContext.getText() != null ? etCulturalContext.getText().toString().trim() : "";
        String author = etAuthor.getText() != null ? etAuthor.getText().toString().trim() : "";
        String body = etContentBody.getText() != null ? etContentBody.getText().toString().trim() : "";

        if (title.isEmpty()) {
            showError("Please enter a resource title.");
            return;
        }

        if (description.isEmpty()) {
            showError("Please enter a brief summary.");
            return;
        }

        ResourceType selectedType = ResourceType.values()[Math.max(0, spResourceType.getSelectedItemPosition())];
        SubstanceType selectedSubstance = SubstanceType.values()[Math.max(0, spSubstanceType.getSelectedItemPosition())];
        AgeGroup selectedAge = AgeGroup.values()[Math.max(0, spAgeGroup.getSelectedItemPosition())];

        setLoading(true);
        tvErrorMessage.setVisibility(View.GONE);

        if (existingResource != null) {
            existingResource.setTitle(title);
            existingResource.setDescription(description);
            existingResource.setResourceType(selectedType);
            existingResource.setSubstanceType(selectedSubstance);
            existingResource.setAgeGroup(selectedAge);
            existingResource.setCulturalContext(cultural);
            existingResource.setAuthorOrSource(author);
            existingResource.setContentBody(body);

            RepositoryLocator.getEducationalRepository().updateResource(existingResource, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    setLoading(false);
                    Toast.makeText(AddEditResourceActivity.this, "Resource updated successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    setLoading(false);
                    showError(exception.getMessage());
                }
            });
        } else {
            EducationalResource newResource = new EducationalResource(
                    null,
                    title,
                    description,
                    selectedType,
                    selectedSubstance,
                    selectedAge,
                    cultural,
                    author,
                    body,
                    true, // Sample until officially medically reviewed
                    System.currentTimeMillis()
            );

            RepositoryLocator.getEducationalRepository().addResource(newResource, new Callback<EducationalResource>() {
                @Override
                public void onSuccess(EducationalResource result) {
                    setLoading(false);
                    Toast.makeText(AddEditResourceActivity.this, "Resource added successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    setLoading(false);
                    showError(exception.getMessage());
                }
            });
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSaveResource.setEnabled(!loading);
    }

    private void showError(String msg) {
        tvErrorMessage.setText(msg);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
