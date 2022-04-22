package com.example.test.cabbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UserDashboardActivity extends AppCompatActivity {

    ImageView imgCab,imgBooking;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        imgCab = findViewById(R.id.imgCab);
        imgBooking = findViewById(R.id.imgBooking);

        btnLogout = findViewById(R.id.btnLogout);

        imgCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDashboardActivity.this,FindCabActivity.class));
            }
        });

        imgBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDashboardActivity.this,BookingListActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                finish();
                startActivity(new Intent(UserDashboardActivity.this,LoginActivity.class));
            }
        });
    }
}
