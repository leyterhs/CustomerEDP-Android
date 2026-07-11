package com.customeredp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.customeredp.Config;
import com.customeredp.R;
import com.customeredp.utils.TokenManager;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientFormActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, phoneInput, companyInput;
    private Button saveButton;
    private TokenManager tokenManager;
    private RequestQueue queue;
    private int clientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_form);

        tokenManager = new TokenManager(this);
        queue = Volley.newRequestQueue(this);

        nameInput = findViewById(R.id.clientNameInput);
        emailInput = findViewById(R.id.clientEmailInput);
        phoneInput = findViewById(R.id.clientPhoneInput);
        companyInput = findViewById(R.id.clientCompanyInput);
        saveButton = findViewById(R.id.saveClientButton);

        clientId = getIntent().getIntExtra("client_id", -1);
        if (clientId != -1) {
            loadClient();
        }

        saveButton.setOnClickListener(v -> saveClient());
    }

    private void loadClient() {
        String token = tokenManager.getToken();
        if (token == null) return;

        String url = Config.BASE_URL + "api/clients/" + clientId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        nameInput.setText(response.getString("name"));
                        emailInput.setText(response.optString("email", ""));
                        phoneInput.setText(response.optString("phone", ""));
                        companyInput.setText(response.optString("company", ""));
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading client", Toast.LENGTH_SHORT).show()) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    private void saveClient() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String company = companyInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("email", email);
            body.put("phone", phone);
            body.put("company", company);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = clientId == -1
                ? Config.BASE_URL + "api/clients"
                : Config.BASE_URL + "api/clients/" + clientId;
        int method = clientId == -1 ? Request.Method.POST : Request.Method.PUT;

        JsonObjectRequest request = new JsonObjectRequest(method, url, body,
                response -> {
                    Toast.makeText(this, clientId == -1 ? "Client created!" : "Client updated!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String msg = "Error saving client";
                    if (error.networkResponse != null) {
                        msg += " - Status: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}