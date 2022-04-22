package com.example.test.cabbooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    Button btnPay;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    ApiInterface apiInterface;

    EditText etCardNo,etHolderName,etCode,etExpiry,etAmount;
    String cardRegex = "^[0-9]{16}$";
    String cvvRegex = "^[0-9]{3,4}$";
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        String Amount = intent.getStringExtra("Amount");

        btnPay = findViewById(R.id.btnPay);
        etCardNo = findViewById(R.id.etCardNo);
        etCode = findViewById(R.id.etCvv);
        etExpiry = findViewById(R.id.etExpiry);
        etAmount = findViewById(R.id.etAmount);
        btnPay = findViewById(R.id.btnPay);

        etAmount.setText(Amount);
        etAmount.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                updateLabel();
            }

        };

        etExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PaymentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String cardNo = etCardNo.getText().toString();
                String cvv = etCode.getText().toString();
                String expiry = etExpiry.getText().toString();
                String amount = etAmount.getText().toString();


                Date date = new Date();
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String year  = String.valueOf(localDate.getYear());
                String month = String.valueOf(localDate.getMonthValue());


                String date1 = month +"-"+year;
                //Toast.makeText(getApplicationContext(),String.valueOf(date),Toast.LENGTH_LONG).show();

                Date expDate=null,currDate=null;
                try {
                    expDate = new SimpleDateFormat("MM-yy").parse(expiry);
                    currDate = new SimpleDateFormat("MM-yy").parse(date1);

                    //Toast.makeText(getApplicationContext(),String.valueOf(currDate.getMonth() +1),Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),String.valueOf(expDate.getMonth()+ 1),Toast.LENGTH_LONG).show();
                }
                catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
                }

                if (!ApiClient.checkNetworkAvailable(PaymentActivity.this))
                    Toast.makeText(PaymentActivity.this,R.string.con_msg,Toast.LENGTH_LONG).show();
                else if(cardNo.equals("") || cvv.equals("") || expiry.equals("") || amount.equals(""))
                    Toast.makeText(PaymentActivity.this,"Please fill all the fields",Toast.LENGTH_LONG).show();
                else if (!cardNo.matches(cardRegex))
                    etCardNo.setError("Invalid account number");
                else if (!cvv.matches(cvvRegex))
                    etCode.setError("Invalid cvv code");
                else if (expDate.getMonth() < currDate.getMonth()){
                    Toast.makeText(getApplicationContext(),"Expiry date is less than current date",Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),"Payment successful",Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(PaymentActivity.this,UserDashboardActivity.class));

                        }
                    }, 3000); // 3000 milliseconds delay
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etExpiry.setText(sdf.format(myCalendar.getTime()));
    }
}
