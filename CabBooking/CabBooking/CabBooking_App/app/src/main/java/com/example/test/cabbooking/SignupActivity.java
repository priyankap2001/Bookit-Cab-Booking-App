package com.example.test.cabbooking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.cabbooking.model.ResponseMsg;
import com.example.test.cabbooking.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {

    Button btnSignup;
    EditText etEmail,etMobileNo,etPass,etCpass,etUsername,etVehicleNo;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    Retrofit retrofit;
    String mobilePattern = "[6-9][0-9]{9}";
    TextView goToLogin;
    RadioButton rbUser,rbDriver;
    String type = "1";
    Pattern vehPattern = Pattern.compile("^[A-Z]{2}\\s[0-9]{2}\\s[A-Z]{1,2}\\s[0-9]{1,4}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        rbUser = findViewById(R.id.radioUser);
        rbDriver = findViewById(R.id.radioDriver);

        etEmail = findViewById(R.id.etEmail);
        etMobileNo = findViewById(R.id.etContact);
        etPass = findViewById(R.id.etPass);
        etCpass = findViewById(R.id.etCPass);
        etVehicleNo = findViewById(R.id.etVehicleNo);
        btnSignup = findViewById(R.id.btnSignup);
        etUsername = findViewById(R.id.etName);
        goToLogin = findViewById(R.id.txtLogin);

        etVehicleNo.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });

        rbDriver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    etVehicleNo.setVisibility(View.VISIBLE);
                    type = "2";
                }
                else {
                    etVehicleNo.setVisibility(View.GONE);
                    type = "1";
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString().trim();
                String mobileno = etMobileNo.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String cpass = etCpass.getText().toString().trim();
                String name = etUsername.getText().toString().trim();
                String vehicleNo = etVehicleNo.getText().toString().trim();
                Matcher m = vehPattern.matcher(vehicleNo);

                if(!ApiClient.checkNetworkAvailable(SignupActivity.this))
                {
                    Toast.makeText(SignupActivity.this,R.string.con_msg,Toast.LENGTH_LONG).show();
                }
                else if(name.length() == 0 || email.length() == 0 || mobileno.length() == 0 ||
                        pass.length() == 0 || cpass.length() == 0)
                {
                    Toast.makeText(SignupActivity.this,R.string.fill_msg,Toast.LENGTH_LONG).show();
                }
                else if (type.equals("2") && vehicleNo.length() == 0){
                    Toast.makeText(SignupActivity.this,R.string.fill_msg,Toast.LENGTH_LONG).show();
                }
                else if (type.equals("2") && !m.matches()){
                    Toast.makeText(SignupActivity.this,"Invalid vehicle no",Toast.LENGTH_LONG).show();
                }
                else if (!email.matches(emailPattern)) {
                    etEmail.setError("Invalid email");
                }
                else if (pass.length() < 6)
                {
                    etPass.setError("Password must contain at least 6 characters");
                }
                else if (!pass.equals(cpass))
                {
                    etCpass.setError("Password's do not match");
                }
                else if (!mobileno.matches(mobilePattern))
                {
                    etMobileNo.setError("Invalid mobile no");
                }
                else
                {
                    User u = new User();
                    u.setName(name);
                    u.setEmailID(email);
                    u.setPassword(pass);
                    u.setMobileNo(mobileno);
                    u.setVehicleNo(vehicleNo);
                    u.setUserTypeID(type);
                    register(u);
                }
            }
        });
    }

    private void register(User u) {
        progressDialog.show();
        Retrofit retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        Call<ResponseMsg> call = apiInterface.register(u);
        call.enqueue(new Callback<ResponseMsg>() {
            @Override
            public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                if (response.isSuccessful()){
                    String Message = response.body().getMessage();
                    String Status = response.body().getStatus();
                    Toast.makeText(SignupActivity.this,Message,Toast.LENGTH_LONG).show();
                    if (Status.trim().equalsIgnoreCase("200")){
                        finish();
                        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                    }
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseMsg> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(SignupActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
