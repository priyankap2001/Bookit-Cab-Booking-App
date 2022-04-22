package com.example.test.cabbooking.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.cabbooking.R;
import com.example.test.cabbooking.model.User;

import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> mUserList;

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        TextView txtName,txtMobileNo,txtType;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtMobileNo = itemView.findViewById(R.id.txtMobile);
            txtType = itemView.findViewById(R.id.txtType);
        }
    }

    public UserAdapter(Context context, List<User> mUserList) {
        this.context = context;
        this.mUserList = mUserList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item,viewGroup,false);
        UserViewHolder uvh = new UserViewHolder(v);
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int i) {
        final User u = mUserList.get(i);
        holder.txtName.setText(u.getName());
        holder.txtMobileNo.setText(u.getMobileNo());
        holder.txtType.setText(u.getUserType());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
