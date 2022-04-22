package com.example.test.cabbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.cabbooking.model.RespLogin;
import com.example.test.cabbooking.model.ResponseMsg;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText etEmail,etPass;
    ApiInterface apiInterface;
    TextView goToSignup,txtForgot;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLogEmail);
        etPass = findViewById(R.id.etLogPass);
        goToSignup = findViewById(R.id.tvGotoSignup);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgot = findViewById(R.id.txtForgot);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EmailID = etEmail.getText().toString().trim();
                if(EmailID.equalsIgnoreCase("")){
                    Toast.makeText(LoginActivity.this,"Please enter Email ID",Toast.LENGTH_LONG).show();
                }
                else{
                    progressDialog.show();
                    Call<ResponseMsg> call = apiInterface.forgotPassword(EmailID);
                    call.enqueue(new Callback<ResponseMsg>() {
                        @Override
                        public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                            if (response.isSuccessful()){
                                String message = response.body().getMessage();
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                            }
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseMsg> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString().trim();

                if (!ApiClient.checkNetworkAvailable(LoginActivity.this))
                {
                    Toast.makeText(LoginActivity.this,R.string.con_msg,Toast.LENGTH_LONG).show();
                }
                else if (email.equals("") || pass.equals(""))
                {
                    Toast.makeText(LoginActivity.this,R.string.fill_msg,Toast.LENGTH_LONG).show();
                }
                else
                {
                    login(email,pass);
                }
            }
        });
    }

    private void login(String email, String pass) {
        progressDialog.show();

        Call<RespLogin> call = apiInterface.login(email,pass);
        call.enqueue(new Callback<RespLogin>() {
            @Override
            public void onResponse(Call<RespLogin> call, Response<RespLogin> response) {
                if (response.isSuccessful()){
                    String message = response.body().getMessage();
                    Toast.makeText(LoginActivity.this,message,Toast.LENGTH_LONG).show();
                    if(message.trim().equalsIgnoreCase("Login successful")) {
                        String UserID = response.body().getUserID();
                        String UserTypeID = response.body().getUserTypeID();
                        storeDetails(UserID,UserTypeID);
                    }
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<RespLogin> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    void storeDetails(String UserID, String UserTypeID)
    {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("UserID",UserID);
        editor.putString("UserTypeID",UserTypeID);
        editor.apply();
        finish();
        if (UserTypeID.equals("1"))
            startActivity(new Intent(LoginActivity.this,UserDashboardActivity.class));
        else if (UserTypeID.equals("2"))
            startActivity(new Intent(LoginActivity.this,DriverDashboardActivity.class));
        else
            startActivity(new Intent(LoginActivity.this,AdminDashboardActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
