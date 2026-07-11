package com.customeredp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.customeredp.Config;
import com.customeredp.R;
import com.customeredp.utils.TokenManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EngagementFormActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, statusInput, priorityInput, dueDateInput, clientIdInput;
    private Button saveButton;
    private TokenManager tokenManager;
    private int engagementId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engagement_form);

        tokenManager = new TokenManager(this);

        titleInput = findViewById(R.id.engagementTitleInput);
        descriptionInput = findViewById(R.id.engagementDescriptionInput);
        statusInput = findViewById(R.id.engagementStatusInput);
        priorityInput = findViewById(R.id.engagementPriorityInput);
        dueDateInput = findViewById(R.id.engagementDueDateInput);
        clientIdInput = findViewById(R.id.engagementClientIdInput);
        saveButton = findViewById(R.id.saveEngagementButton);

        engagementId = getIntent().getIntExtra("engagement_id", -1);
        if (engagementId != -1) {
            loadEngagement();
        }

        saveButton.setOnClickListener(v -> saveEngagement());
    }

    private void loadEngagement() {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(Config.BASE_URL + "api/engagements/" + engagementId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "PostmanRuntime/7.41.2");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }
                    in.close();
                    JSONObject obj = new JSONObject(content.toString());
                    runOnUiThread(() -> {
                        try {
                            titleInput.setText(obj.getString("title"));
                            descriptionInput.setText(obj.optString("description", ""));
                            statusInput.setText(obj.optString("status", ""));
                            priorityInput.setText(obj.optString("priority", ""));
                            dueDateInput.setText(obj.optString("dueDate", ""));
                            if (obj.has("client")) {
                                JSONObject clientObj = obj.getJSONObject("client");
                                clientIdInput.setText(String.valueOf(clientObj.getInt("id")));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error loading engagement: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void saveEngagement() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String status = statusInput.getText().toString().trim();
        String priority = priorityInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();
        String clientIdStr = clientIdInput.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (clientIdStr.isEmpty()) {
            Toast.makeText(this, "Client ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("title", title);
            body.put("description", description);
            body.put("status", status);
            body.put("priority", priority);
            body.put("dueDate", dueDate);
            body.put("clientId", Integer.parseInt(clientIdStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String urlStr = engagementId == -1
                ? Config.BASE_URL + "api/engagements"
                : Config.BASE_URL + "api/engagements/" + engagementId;
        String method = engagementId == -1 ? "POST" : "PUT";

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "PostmanRuntime/7.41.2");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.toString().getBytes("UTF-8"));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                Log.d("EngagementForm", "Response code: " + responseCode);
                if (responseCode >= 200 && responseCode < 300) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, engagementId == -1 ? "Engagement created!" : "Engagement updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    String errorMsg = "Error " + responseCode;
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                        String line;
                        StringBuilder sb = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        errorMsg += ": " + sb.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final String finalError = errorMsg;
                    runOnUiThread(() -> Toast.makeText(this, finalError, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}