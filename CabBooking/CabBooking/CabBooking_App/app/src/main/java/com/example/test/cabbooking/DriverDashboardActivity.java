package com.example.test.cabbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.test.cabbooking.model.Driver;

public class DriverDashboardActivity extends AppCompatActivity {

    ImageView imgBooking,imgLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        startService(new Intent(this, BackgroundService.class));

        imgBooking = findViewById(R.id.imgBooking);

        imgLogout = findViewById(R.id.imgLogout);

        imgBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverDashboardActivity.this,BookingListActivity.class));
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(DriverDashboardActivity.this, BackgroundService.class));
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                finish();
                startActivity(new Intent(DriverDashboardActivity.this,LoginActivity.class));
            }
        });
    }
}
