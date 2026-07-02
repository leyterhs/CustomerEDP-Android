package com.customeredp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private RequestQueue queue;

    private EditText newUsernameInput, newEmailInput, newPasswordInput;
    private Spinner roleSpinner;
    private Button createUserButton;
    private LinearLayout createUserLayout;
    private TextView accessDeniedText;

    private String userRole = "MEMBER"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tokenManager = new TokenManager(this);
        queue = Volley.newRequestQueue(this);

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "No token found! Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Βρες τον ρόλο του χρήστη (από το token ή από την απάντηση του login)
        // Για τώρα, θα τον πάρουμε από το SharedPreferences
        userRole = getSharedPreferences("app_prefs", MODE_PRIVATE).getString("user_role", "MEMBER");

        // Logout
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            tokenManager.clearToken();
            finish();
        });

        // Views
        createUserLayout = findViewById(R.id.createUserLayout);
        accessDeniedText = findViewById(R.id.accessDeniedText);
        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList, userId -> deleteUser(userId));
        recyclerView.setAdapter(adapter);

        newUsernameInput = findViewById(R.id.newUsernameInput);
        newEmailInput = findViewById(R.id.newEmailInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        roleSpinner = findViewById(R.id.roleSpinner);
        createUserButton = findViewById(R.id.createUserButton);

        createUserButton.setOnClickListener(v -> createUser());

        // Έλεγχος ρόλου
        if (userRole.equals("ADMIN")) {
            createUserLayout.setVisibility(View.VISIBLE);
            accessDeniedText.setVisibility(View.GONE);
            loadUsers();
        } else {
            createUserLayout.setVisibility(View.GONE);
            accessDeniedText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void loadUsers() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.BASE_URL + "api/admin/users";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    userList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String username = obj.getString("username");
                            String email = obj.getString("email");
                            String role = obj.getString("role");
                            userList.add(new User(id, username, email, role));
                        }
                        adapter.updateUsers(userList);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading users", Toast.LENGTH_LONG).show()) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    private void createUser() {
        String username = newUsernameInput.getText().toString().trim();
        String email = newEmailInput.getText().toString().trim();
        String password = newPasswordInput.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.BASE_URL + "api/admin/users";
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("email", email);
            body.put("password", password);
            body.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating user data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show();
                    newUsernameInput.setText("");
                    newEmailInput.setText("");
                    newPasswordInput.setText("");
                    loadUsers();
                },
                error -> Toast.makeText(this, "Error creating user", Toast.LENGTH_LONG).show()) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    private void deleteUser(int userId) {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.BASE_URL + "api/admin/users/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                    loadUsers();
                },
                error -> Toast.makeText(this, "Error deleting user", Toast.LENGTH_LONG).show()) {
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