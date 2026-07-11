package com.customeredp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.customeredp.R;
import com.customeredp.utils.TokenManager;

public class AdminActivity extends AppCompatActivity {

    private Button btnViewClients, btnAddClient, btnViewEngagements, btnLogout;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tokenManager = new TokenManager(this);

        btnViewClients = findViewById(R.id.btnViewClients);
        btnAddClient = findViewById(R.id.btnAddClient);
        btnViewEngagements = findViewById(R.id.btnViewEngagements);
        btnLogout = findViewById(R.id.btnLogout);

        btnViewClients.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ClientListActivity.class));
        });

        btnAddClient.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ClientFormActivity.class));
        });

        btnViewEngagements.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, EngagementListActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}