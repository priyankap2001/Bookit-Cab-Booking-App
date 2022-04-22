package com.example.test.cabbooking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.test.cabbooking.Adapter.BookingAdapter;
import com.example.test.cabbooking.model.Booking;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ApiInterface apiInterface;
    Retrofit retrofit;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    BookingAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");


        if(!ApiClient.checkNetworkAvailable(this))
            Toast.makeText(this,R.string.con_msg,Toast.LENGTH_LONG).show();
        else
            getList();
    }

    private void getList(){

        final SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String UserID = prefs.getString("UserID","");
        final String UserTypeID = prefs.getString("UserTypeID","");

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        progressDialog.show();

        Call<List<Booking>> call = apiInterface.getBookingList(UserID,UserTypeID);
        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if(response.isSuccessful()){
                    List<Booking> mBookingList = response.body();
                    if (!mBookingList.isEmpty())
                        fillRecyclerView(mBookingList);
                    else{
                        Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_LONG).show();
                        finish();
//                        if (UserTypeID.equals("1"))
//                            startActivity(new Intent(BookingListActivity.this,UserDashboardActivity.class));
//                        else
//                            startActivity(new Intent(BookingListActivity.this,DriverDashboardActivity.class));
                    }
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void fillRecyclerView(List<Booking> mBookingList) {
        recyclerView = findViewById(R.id.rvBooking);
        adapter = new BookingAdapter(this,mBookingList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
