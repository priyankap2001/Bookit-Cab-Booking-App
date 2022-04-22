package com.example.test.cabbooking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.cabbooking.R;
import com.example.test.cabbooking.model.BookingList;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder> {

    private Context context;
    private List<BookingList> mBookingList;

    public static class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView txtUName,txtUMobile,txtDName,txtDMobile,txtDate,txtStatus,txtAmount,txtDistance;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUName = itemView.findViewById(R.id.txtUName);
            txtUMobile = itemView.findViewById(R.id.txtUMobile);
            txtDName = itemView.findViewById(R.id.txtDName);
            txtDMobile = itemView.findViewById(R.id.txtDMobile);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDistance = itemView.findViewById(R.id.txtDistance);
        }
    }

    public BookingListAdapter(Context context, List<BookingList> mBookingList) {
        this.context = context;
        this.mBookingList = mBookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bookinglist_item,viewGroup,false);
        BookingViewHolder bvh = new BookingViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int i) {
        final BookingList b = mBookingList.get(i);
        holder.txtUName.setText("User: "+b.getName());
        holder.txtUMobile.setText(b.getMobileNo());
        holder.txtDName.setText("Driver: "+b.getDriverName());
        holder.txtDMobile.setText(b.getDriverMobileNo());
        holder.txtDate.setText("Date: "+b.getBookingDate());
        holder.txtStatus.setText("Status: "+b.getStatus());
        holder.txtAmount.setText("Amount: ₹"+b.getAmount());
        holder.txtDistance.setText("Distance: "+b.getDistance());
    }

    @Override
    public int getItemCount() {
        return mBookingList.size();
    }
}
