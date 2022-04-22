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
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.cabbooking.ApiClient;
import com.example.test.cabbooking.ApiInterface;
import com.example.test.cabbooking.DriverDashboardActivity;
import com.example.test.cabbooking.MapsActivity;
import com.example.test.cabbooking.PaymentActivity;
import com.example.test.cabbooking.R;
import com.example.test.cabbooking.UserDashboardActivity;
import com.example.test.cabbooking.model.Booking;
import com.example.test.cabbooking.model.Cal;
import com.example.test.cabbooking.model.ResponseMsg;
import com.example.test.cabbooking.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    List<Booking> mBookingList;
    ApiInterface apiInterface;
    Retrofit retrofit;
    ProgressDialog progressDialog;

    public static class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView txtDate,txtName,txtMobileNo,txtStatus,txtDistance,txtOTP,txtAmount;
        Button btnAccept,btnReject,btnSource,btnDest,btnStart,btnEnd,btnPay;
        ImageView imgLocation;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtBookingDate);
            txtName = itemView.findViewById(R.id.txtName);
            txtMobileNo = itemView.findViewById(R.id.txtMobileNo);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtOTP = itemView.findViewById(R.id.txtOTP);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            imgLocation = itemView.findViewById(R.id.imgLocation);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnSource = itemView.findViewById(R.id.btnSource);
            btnDest = itemView.findViewById(R.id.btnDest);
            btnStart = itemView.findViewById(R.id.btnStart);
            btnEnd = itemView.findViewById(R.id.btnEnd);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }

    public BookingAdapter(Context context, List<Booking> mBookingList) {
        this.context = context;
        this.mBookingList = mBookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.booking_item,viewGroup,false);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Alert");
        progressDialog.setMessage("Please wait...");

        retrofit = ApiClient.getClient();
        apiInterface = retrofit.create(ApiInterface.class);
        BookingViewHolder bvh = new BookingViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int i) {
        final Booking b = mBookingList.get(i);
        holder.txtDate.setText(b.getBookingDate());
        holder.txtName.setText(b.getName());
        holder.txtMobileNo.setText(b.getMobileNo());
        holder.txtStatus.setText(b.getStatus());
        holder.txtDistance.setText(b.getDistance());
        holder.txtOTP.setText("OTP:"+b.getOTP());
        holder.txtAmount.setText("₹"+b.getAmount());
        //holder.txtOTP.setText(b.get());
        //holder.txtDate.setText(b.getBookingDate());

        holder.btnAccept.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.btnSource.setVisibility(View.GONE);
        holder.btnDest.setVisibility(View.GONE);
        holder.btnStart.setVisibility(View.GONE);
        holder.btnEnd.setVisibility(View.GONE);
        holder.txtOTP.setVisibility(View.GONE);
        holder.btnPay.setVisibility(View.GONE);

        final String BookingID = b.getBookingID();
        String BookingStatusID = b.getBookingStatusID();
        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);
        String UserTypeID = prefs.getString("UserTypeID","");
        if (UserTypeID.equals("1")) {
            holder.imgLocation.setVisibility(View.VISIBLE);
            if (Integer.parseInt(b.BookingStatusID) > 2)
                holder.txtOTP.setVisibility(View.VISIBLE);

            if (Integer.parseInt(b.BookingStatusID) == 5)
                holder.btnPay.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgLocation.setVisibility(View.GONE);
            holder.txtOTP.setVisibility(View.GONE);

            if (BookingStatusID.equals("1")){
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
            }
            else if (BookingStatusID.equals("3")){
                holder.btnSource.setVisibility(View.VISIBLE);
                holder.btnDest.setVisibility(View.VISIBLE);
                holder.btnStart.setVisibility(View.VISIBLE);
            }
            else if (BookingStatusID.equals("4")){
                holder.btnSource.setVisibility(View.VISIBLE);
                holder.btnDest.setVisibility(View.VISIBLE);
                holder.btnEnd.setVisibility(View.VISIBLE);
            }
        }

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(BookingID,"3");
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(BookingID,"2");
            }
        });

        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOTP(BookingID);
                //updateStatus(BookingID,"4");
            }
        });

        holder.btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(BookingID,"5");
            }
        });

        holder.btnSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MapsActivity.class);
                intent.putExtra("Latitude",b.getSourceLatitude());
                intent.putExtra("Longitude",b.getSourceLongitude());
                context.startActivity(intent);
            }
        });

        holder.btnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MapsActivity.class);
                intent.putExtra("Latitude",b.getDestinationLatitude());
                intent.putExtra("Longitude",b.getDestinationLongitude());
                context.startActivity(intent);
            }
        });

        holder.imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,MapsActivity.class);
                intent.putExtra("Latitude",b.getDriverLatitude());
                intent.putExtra("Longitude",b.getDriverLongitude());
                context.startActivity(intent);
            }
        });

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PaymentActivity.class);
                intent.putExtra("Amount",b.getAmount());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookingList.size();
    }


    private void updateStatus(final String BookingID, final String StatusID){

        SharedPreferences prefs = context.getSharedPreferences("user", MODE_PRIVATE);
        final String DriverID = prefs.getString("UserID","");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure ?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (!ApiClient.checkNetworkAvailable(context))
                    Toast.makeText(context,R.string.con_msg,Toast.LENGTH_LONG).show();
                else{
                    progressDialog.show();
                    Call<ResponseMsg> call = apiInterface.updateStatus(BookingID,StatusID,DriverID);
                    call.enqueue(new Callback<ResponseMsg>() {
                        @Override
                        public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                            if (response.isSuccessful()){
                                String Status = response.body().getStatus();
                                String Message = response.body().getMessage();
                                Toast.makeText(context,Message,Toast.LENGTH_LONG).show();
                                if (Status.equals("200")){
                                    ((Activity)context).finish();
                                    //context.startActivity(new Intent(context,DriverDashboardActivity.class));
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
                dialog.dismiss();
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

    private void verifyOTP(final String BookingID) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Enter OTP");

        final EditText input = new EditText(context);
        //input.setInputType();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alert.setView(input); // uncomment this line

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String OTP = input.getText().toString();
                //Toast.makeText(context,OTP,Toast.LENGTH_LONG).show();
                dialog.dismiss();

                if (!ApiClient.checkNetworkAvailable(context))
                    Toast.makeText(context,R.string.con_msg,Toast.LENGTH_LONG).show();
                else{
                    progressDialog.show();
                    Call<ResponseMsg> call = apiInterface.verifyOTP(BookingID,OTP);
                    call.enqueue(new Callback<ResponseMsg>() {
                        @Override
                        public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                            if (response.isSuccessful()){
                                String Status = response.body().getStatus();
                                String Message = response.body().getMessage();
                                Toast.makeText(context,Message,Toast.LENGTH_LONG).show();
                                if (Status.equals("200")){
                                    updateStatus(BookingID,"4");
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

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
