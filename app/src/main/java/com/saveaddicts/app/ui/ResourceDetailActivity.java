package com.saveaddicts.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.EducationalResource;

public class ResourceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_detail);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        EducationalResource resource = (EducationalResource) getIntent().getSerializableExtra("resource");
        if (resource == null) {
            Toast.makeText(this, "Resource not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvDetailType = findViewById(R.id.tvDetailType);
        TextView tvDetailSubstance = findViewById(R.id.tvDetailSubstance);
        TextView tvDetailAge = findViewById(R.id.tvDetailAge);
        TextView tvSampleContentAlert = findViewById(R.id.tvSampleContentAlert);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailAuthor = findViewById(R.id.tvDetailAuthor);
        TextView tvDetailCultural = findViewById(R.id.tvDetailCultural);
        TextView tvDetailBody = findViewById(R.id.tvDetailBody);

        tvDetailType.setText(resource.getResourceType().getLabel());
        tvDetailSubstance.setText(resource.getSubstanceType().getDisplayName());
        tvDetailAge.setText(resource.getAgeGroup().getLabel());
        tvSampleContentAlert.setVisibility(resource.isSampleContent() ? View.VISIBLE : View.GONE);
        tvDetailTitle.setText(resource.getTitle());
        tvDetailAuthor.setText("Source: " + resource.getAuthorOrSource());
        tvDetailCultural.setText("Cultural Context: " + resource.getCulturalContext());
        tvDetailBody.setText(resource.getContentBody());
    }
}
