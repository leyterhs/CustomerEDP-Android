package com.customeredp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.customeredp.Config;
import com.customeredp.R;
import com.customeredp.adapters.ClientAdapter;
import com.customeredp.models.Client;
import com.customeredp.utils.TokenManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ClientListActivity extends AppCompatActivity implements ClientAdapter.OnClientActionListener {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private List<Client> clientList = new ArrayList<>();
    private TokenManager tokenManager;
    private RequestQueue queue;
    private Button btnAddClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        tokenManager = new TokenManager(this);
        queue = Volley.newRequestQueue(this);

        recyclerView = findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientAdapter(clientList, this);
        recyclerView.setAdapter(adapter);

        btnAddClient = findViewById(R.id.btnAddClient);
        btnAddClient.setOnClickListener(v -> {
            startActivity(new Intent(ClientListActivity.this, ClientFormActivity.class));
        });

        loadClients();
    }

    private void loadClients() {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.BASE_URL + "api/clients";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    clientList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Client client = new Client();
                            client.setId(obj.getInt("id"));
                            client.setName(obj.getString("name"));
                            client.setEmail(obj.optString("email", ""));
                            client.setPhone(obj.optString("phone", ""));
                            client.setCompany(obj.optString("company", ""));
                            clientList.add(client);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    String msg = "Error loading clients";
                    if (error.networkResponse != null) {
                        msg += " - Status: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("User-Agent", "PostmanRuntime/7.41.2");
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClients();
    }

    @Override
    public void onEdit(Client client) {
        Intent intent = new Intent(this, ClientFormActivity.class);
        intent.putExtra("client_id", client.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Client client) {
        deleteClient(client);
    }

    private void deleteClient(Client client) {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.BASE_URL + "api/clients/" + client.getId();

        com.android.volley.toolbox.StringRequest request = new com.android.volley.toolbox.StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(this, "Client deleted", Toast.LENGTH_SHORT).show();
                    loadClients();
                },
                error -> {
                    String msg = "Delete failed";
                    if (error.networkResponse != null) {
                        msg += " - Status: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("User-Agent", "PostmanRuntime/7.41.2");
                return headers;
            }
        };

        queue.add(request);
    }
}