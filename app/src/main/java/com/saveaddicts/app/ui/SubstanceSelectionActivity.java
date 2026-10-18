package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.SubstanceType;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

public class SubstanceSelectionActivity extends AppCompatActivity {

    private RadioGroup rgSubstances;
    private Button btnSaveSubstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substance_selection);

        rgSubstances = findViewById(R.id.rgSubstances);
        btnSaveSubstance = findViewById(R.id.btnSaveSubstance);

        btnSaveSubstance.setOnClickListener(v -> saveSubstanceSelection());
    }

    private void saveSubstanceSelection() {
        SubstanceType selected = SubstanceType.OPIOIDS;
        int checkedId = rgSubstances.getCheckedRadioButtonId();

        if (checkedId == R.id.rbAlcohol) {
            selected = SubstanceType.ALCOHOL;
        } else if (checkedId == R.id.rbOpioids) {
            selected = SubstanceType.OPIOIDS;
        } else if (checkedId == R.id.rbCannabis) {
            selected = SubstanceType.CANNABIS;
        } else if (checkedId == R.id.rbOther) {
            selected = SubstanceType.OTHER;
        } else if (checkedId == R.id.rbAll) {
            selected = SubstanceType.ALL;
        }

        final SubstanceType finalSelected = selected;
        RepositoryLocator.getAuthRepository().updateSubstancePreference(finalSelected, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(SubstanceSelectionActivity.this,
                        "Substance focus set to " + finalSelected.getDisplayName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SubstanceSelectionActivity.this, PatientMainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(SubstanceSelectionActivity.this,
                        "Failed to update preference: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
