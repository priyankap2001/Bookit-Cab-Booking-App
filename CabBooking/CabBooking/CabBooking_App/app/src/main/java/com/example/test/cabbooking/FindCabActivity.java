package com.example.test.cabbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.cabbooking.Adapter.DriverAdapter;
import com.example.test.cabbooking.model.Driver;
import com.google.android.gms.drive.Drive;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FindCabActivity extends AppCompatActivity {

    String SourceLatitude,SourceLongitude,DestLatitude,DestLongitude,Address;
    EditText etSource,etDestination;
    Button btnFind;
    ApiInterface apiInterface;
    Retrofit retrofit;
    ProgressDialog progressDialog;
    List<Driver> mDriverList;
    DriverAdapter adapter;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_cab);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");

        etSource = findViewById(R.id.etSource);
        etDestination = findViewById(R.id.etDestination);
        btnFind = findViewById(R.id.btnFind);

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        SharedPreferences prefs = getSharedPreferences("location", MODE_PRIVATE);
        type = prefs.getString("type","");
        SourceLatitude = prefs.getString("SourceLatitude","");
        SourceLongitude = prefs.getString("SourceLongitude","");
        DestLatitude = prefs.getString("DestLatitude","");
        DestLongitude = prefs.getString("DestLongitude","");

        //Toast.makeText(getApplicationContext(),Latitude+" "+Longitude,Toast.LENGTH_LONG).show();

        if (!SourceLatitude.equals("") && !SourceLongitude.equals("")){
            Address = getCompleteAddressString(Double.valueOf(SourceLatitude),Double.valueOf(SourceLongitude));
            etSource.setText(Address);
        }

        if (!DestLatitude.equals("") && !DestLongitude.equals("")){
            Address = getCompleteAddressString(Double.valueOf(DestLatitude),Double.valueOf(DestLongitude));
            etDestination.setText(Address);
        }

//        if (getIntent().getExtras() != null){
//            Intent intent = getIntent();
//            Latitude = intent.getStringExtra("Latitude");
//            Longitude = intent.getStringExtra("Longitude");
//            type = intent.getStringExtra("type");
//            Address = getCompleteAddressString(Double.valueOf(Latitude),Double.valueOf(Longitude));
//
//            if (type.equals("source"))
//                etSource.setText(Address);
//            else if (type.equals("dest"))
//                etDestination.setText(Address);
//            //Toast.makeText(getApplicationContext(),Address,Toast.LENGTH_LONG).show();
//        }

        etSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindCabActivity.this,PinLocationActivity.class);
                intent.putExtra("type","source");
                //finish();
                startActivity(intent);

                //startActivity(new Intent(FindCabActivity.this,PinLocationActivity.class));
            }
        });

        etDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindCabActivity.this,PinLocationActivity.class);
                intent.putExtra("type","dest");
                //finish();
                startActivity(intent);
                //startActivity(new Intent(FindCabActivity.this,PinLocationActivity.class));
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Source = etSource.getText().toString();
                String Destination = etDestination.getText().toString();

                if (!ApiClient.checkNetworkAvailable(FindCabActivity.this))
                    Toast.makeText(FindCabActivity.this,R.string.con_msg,Toast.LENGTH_LONG).show();
                else if (Source.equals("") || Destination.equals(""))
                    Toast.makeText(FindCabActivity.this,R.string.fill_msg,Toast.LENGTH_LONG).show();
                else {
                    getNearbyDriver(SourceLatitude,SourceLongitude,DestLatitude,DestLongitude);
                }
            }
        });


    }

    private void getNearbyDriver(String SourceLatitude, String SourceLongitude,
                                 String DestLatitude, String DestLongitude) {
        progressDialog.show();
        Call<List<Driver>> call = apiInterface.getNearbyDriver(SourceLatitude,SourceLongitude,DestLatitude,DestLongitude);
        call.enqueue(new Callback<List<Driver>>() {
            @Override
            public void onResponse(Call<List<Driver>> call, Response<List<Driver>> response) {
                if (response.isSuccessful()){
                    mDriverList = response.body();
                    if (!mDriverList.isEmpty())
                        fillRecyclerView(mDriverList);
                    else
                        Toast.makeText(FindCabActivity.this,"No data found",Toast.LENGTH_LONG).show();
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Driver>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void fillRecyclerView(final List<Driver> mDriverList) {
        recyclerView = findViewById(R.id.rvDriver);
        adapter = new DriverAdapter(this,mDriverList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

//        adapter.setOnItemClickListener(new DriverAdapter.ClickListener() {
//            @Override
//            public void onItemClick(int position, View v) {
//                String DriverID = mDriverList.get(position).getUserID();
//                Toast.makeText(getApplicationContext(),DriverID,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onItemLongClick(int position, View v) {
//
//            }
//        });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                //Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                //Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences prefs = getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        finish();
    }
}
