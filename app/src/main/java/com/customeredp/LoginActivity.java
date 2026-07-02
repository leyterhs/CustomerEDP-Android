package com.customeredp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton;
    private TextView statusText;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);

        if (tokenManager.getToken() != null) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        statusText = findViewById(R.id.statusText);

        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusText.setText("Please fill all fields");
            return;
        }

        HttpsTrustManager.allowAllSSL();

        statusText.setText("Logging in...");
        loginButton.setEnabled(false);

        //http://10.0.2.2:8080 default
        String url = "https://tapioca-resistant-grooving.ngrok-free.dev/api/auth/login";
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        String token = response.getString("token");
                        tokenManager.saveToken(token);
                        String role = response.getString("role");
                        getSharedPreferences("app_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("user_role", role)
                                .apply();
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        finish();
                    } catch (JSONException e) {
                        statusText.setText("Error parsing response");
                    }
                    loginButton.setEnabled(true);
                },
                error -> {
                    statusText.setText("Login failed! Check credentials.");
                    loginButton.setEnabled(true);
                });

        queue.add(request);
    }
}