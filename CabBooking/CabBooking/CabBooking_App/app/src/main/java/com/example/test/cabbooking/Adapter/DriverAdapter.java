package com.example.test.cabbooking.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.cabbooking.ApiClient;
import com.example.test.cabbooking.ApiInterface;
import com.example.test.cabbooking.R;
import com.example.test.cabbooking.UserDashboardActivity;
import com.example.test.cabbooking.model.Booking;
import com.example.test.cabbooking.model.Cal;
import com.example.test.cabbooking.model.Driver;
import com.example.test.cabbooking.model.ResponseMsg;

import org.w3c.dom.Text;

import java.util.List;
import java.util.SortedMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder>{

    private Context context;
    private List<Driver> mDriverList;
    private static ClickListener clickListener;
    SharedPreferences prefs;
    ApiInterface apiInterface;
    Retrofit retrofit;
    ProgressDialog progressDialog;

    public static class DriverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtName,txtMobileNo,txtVehicleNo,txtDistance,txtAmount;
        Button btnBookCab;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtMobileNo = itemView.findViewById(R.id.txtMobile);
            txtVehicleNo = itemView.findViewById(R.id.txtVehicleNo);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            btnBookCab = itemView.findViewById(R.id.btnBookCab);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public DriverAdapter(Context context, List<Driver> mDriverList) {
        this.context = context;
        this.mDriverList = mDriverList;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.driver_item,viewGroup,false);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);

        DriverViewHolder dvh = new DriverViewHolder(v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int i) {
        final Driver d = mDriverList.get(i);
        holder.txtName.setText(d.getName());
        holder.txtMobileNo.setText("+91 "+d.getMobileNo());
        holder.txtVehicleNo.setText(d.getVehicleNo());
        holder.txtDistance.setText(d.getDistance());
        holder.txtAmount.setText("₹"+d.getAmount());

        holder.btnBookCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to book this cab ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        prefs = context.getSharedPreferences("location", MODE_PRIVATE);
                        String SourceLatitude = prefs.getString("SourceLatitude","");
                        String SourceLongitude = prefs.getString("SourceLongitude","");
                        String DestLatitude = prefs.getString("DestLatitude","");
                        String DestLongitude = prefs.getString("DestLongitude","");

                        prefs = context.getSharedPreferences("user", MODE_PRIVATE);
                        String UserID = prefs.getString("UserID","");
                        String DriverID = d.getUserID();
                        String Amount = d.getAmount();
                        String Distance = d.getDistance();

                        Booking b = new Booking();
                        b.setUserID(UserID);
                        b.setDriverID(DriverID);
                        b.setSourceLatitude(SourceLatitude);
                        b.setSourceLongitude(SourceLongitude);
                        b.setDestinationLatitude(DestLatitude);
                        b.setDestinationLongitude(DestLongitude);
                        b.setDistance(Distance);
                        b.setAmount(Amount);

                        if (!ApiClient.checkNetworkAvailable(context))
                            Toast.makeText(context,R.string.con_msg,Toast.LENGTH_LONG).show();
                        else{
                            progressDialog.show();
                            Call<ResponseMsg> call = apiInterface.bookCab(b);
                            call.enqueue(new Callback<ResponseMsg>() {
                                @Override
                                public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                                    if (response.isSuccessful()){
                                        String Message = response.body().getMessage();
                                        String Status = response.body().getStatus();
                                        Toast.makeText(context,Message,Toast.LENGTH_LONG).show();
                                        if (Status.equals("200")){
                                            SharedPreferences prefs = context.getSharedPreferences("location", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.clear();
                                            editor.apply();
                                            ((Activity)context).finish();
                                            context.startActivity(new Intent(context,UserDashboardActivity.class));
                                        }
                                    }
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<ResponseMsg> call, Throwable t) {
                                    Toast.makeText(context,t.toString(),Toast.LENGTH_LONG).show();
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DriverAdapter.clickListener = clickListener;
    }



    @Override
    public int getItemCount() {
        return mDriverList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
}
