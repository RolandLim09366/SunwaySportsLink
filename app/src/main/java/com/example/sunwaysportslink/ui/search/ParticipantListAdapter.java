package com.example.sunwaysportslink.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.UserViewHolder> {
    private final Context context;
    private final List<User> userList;

    public ParticipantListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_partipant_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvNum.setText(String.valueOf(position + 1));  // Display the item number
        holder.tvName.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());

        // Get Firebase reference
        FirebaseService firebaseService = FirebaseService.getInstance();
        String userId = user.getUserId(); // Assuming you have userId in User model

        // Load profile image directly from database
        firebaseService.getUserRef(userId).child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageUrl = snapshot.getValue(String.class);
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(context).load(profileImageUrl).placeholder(R.drawable.iv_default_profile).error(R.drawable.iv_default_profile).into(holder.ivProfilePic);
                } else {
                    holder.ivProfilePic.setImageResource(R.drawable.iv_default_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                holder.ivProfilePic.setImageResource(R.drawable.iv_default_profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvName, tvEmail, tvNum;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tv_number);
            ivProfilePic = itemView.findViewById(R.id.iv_profile_pic);
            tvName = itemView.findViewById(R.id.tv_username);
            tvEmail = itemView.findViewById(R.id.tv_email);
        }
    }
}