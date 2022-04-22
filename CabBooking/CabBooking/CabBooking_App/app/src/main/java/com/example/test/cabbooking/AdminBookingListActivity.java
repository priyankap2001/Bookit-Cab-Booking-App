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
import com.example.test.cabbooking.Adapter.BookingListAdapter;
import com.example.test.cabbooking.model.Booking;
import com.example.test.cabbooking.model.BookingList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdminBookingListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ApiInterface apiInterface;
    Retrofit retrofit;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    BookingListAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking_list);

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

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        progressDialog.show();

        Call<List<BookingList>> call = apiInterface.getBooking();
        call.enqueue(new Callback<List<BookingList>>() {
            @Override
            public void onResponse(Call<List<BookingList>> call, Response<List<BookingList>> response) {
                if(response.isSuccessful()){
                    List<BookingList> mBookingList = response.body();
                    if (!mBookingList.isEmpty())
                        fillRecyclerView(mBookingList);
                    else{
                        Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<BookingList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void fillRecyclerView(List<BookingList> mBookingList) {
        recyclerView = findViewById(R.id.rvBooking);
        adapter = new BookingListAdapter(this,mBookingList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
