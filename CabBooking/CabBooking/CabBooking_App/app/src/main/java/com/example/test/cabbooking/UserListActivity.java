package com.example.test.cabbooking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.test.cabbooking.Adapter.BookingListAdapter;
import com.example.test.cabbooking.Adapter.UserAdapter;
import com.example.test.cabbooking.model.BookingList;
import com.example.test.cabbooking.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ApiInterface apiInterface;
    Retrofit retrofit;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    UserAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

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

        Call<List<User>> call = apiInterface.getUser();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    List<User> mUserList = response.body();
                    if (!mUserList.isEmpty())
                        fillRecyclerView(mUserList);
                    else{
                        Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_LONG).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void fillRecyclerView(List<User> mUserList) {
        recyclerView = findViewById(R.id.rvUser);
        adapter = new UserAdapter(this,mUserList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
