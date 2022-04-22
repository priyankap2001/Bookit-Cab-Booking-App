package com.example.test.cabbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.test.cabbooking.model.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try
        {
            Thread thread=new Thread()
            {
                public void run()
                {
                    try
                    {
                        sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        getData();
                    }
                }
            };
            thread.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void  getData()
    {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String UserTypeID = prefs.getString("UserTypeID","");
        finish();
        if (UserTypeID.equals(""))
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        else if (UserTypeID.equals("1"))
            startActivity(new Intent(SplashActivity.this,UserDashboardActivity.class));
        else if (UserTypeID.equals("2"))
            startActivity(new Intent(SplashActivity.this,DriverDashboardActivity.class));
        else
            startActivity(new Intent(SplashActivity.this,AdminDashboardActivity.class));
    }
}
