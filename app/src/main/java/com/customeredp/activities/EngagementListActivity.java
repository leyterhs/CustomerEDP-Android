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
import com.customeredp.adapters.EngagementAdapter;
import com.customeredp.models.Engagement;
import com.customeredp.utils.TokenManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class EngagementListActivity extends AppCompatActivity implements EngagementAdapter.OnEngagementActionListener {

    private RecyclerView recyclerView;
    private EngagementAdapter adapter;
    private List<Engagement> engagementList = new ArrayList<>();
    private TokenManager tokenManager;
    private RequestQueue queue;
    // private Button btnAddEngagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engagement_list);

        tokenManager = new TokenManager(this);
        queue = Volley.newRequestQueue(this);

        recyclerView = findViewById(R.id.engagementRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EngagementAdapter(engagementList, this);
        recyclerView.setAdapter(adapter);

/*         btnAddEngagement = findViewById(R.id.btnAddEngagement);
        btnAddEngagement.setOnClickListener(v -> {
            startActivity(new Intent(EngagementListActivity.this, EngagementFormActivity.class));
        }); */

        loadEngagements();
    }

    private void loadEngagements() {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Config.BASE_URL + "api/engagements";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    engagementList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Engagement engagement = new Engagement();
                            engagement.setId(obj.getInt("id"));
                            engagement.setTitle(obj.getString("title"));
                            engagement.setDescription(obj.optString("description", ""));
                            engagement.setStatus(obj.optString("status", ""));
                            engagement.setPriority(obj.optString("priority", ""));
                            engagement.setDueDate(obj.optString("dueDate", ""));
                            if (obj.has("client")) {
                                JSONObject clientObj = obj.getJSONObject("client");
                                engagement.setClientId(clientObj.getInt("id"));
                            }
                            engagementList.add(engagement);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    String msg = "Error loading engagements";
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
        loadEngagements();
    }

    @Override
    public void onEdit(Engagement engagement) {
        Intent intent = new Intent(this, EngagementFormActivity.class);
        intent.putExtra("engagement_id", engagement.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Engagement engagement) {
        Toast.makeText(this, "Delete: " + engagement.getTitle(), Toast.LENGTH_SHORT).show();
    }
}