package com.customeredp.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.customeredp.R;

public class DeliveryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list);

        TextView message = findViewById(R.id.messageTextView);
        message.setText("Η λειτουργία των Παραδόσεων είναι υπό κατασκευή.");
    }
}