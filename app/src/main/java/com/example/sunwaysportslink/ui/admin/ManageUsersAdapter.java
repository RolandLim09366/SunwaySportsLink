package com.example.sunwaysportslink.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.User;

import java.util.List;

public class ManageUsersAdapter extends RecyclerView.Adapter<ManageUsersAdapter.EventViewHolder> {

    private final List<User> userList;
    private final OnDeleteClickListener deleteClickListener;
    private final Context context;

    public ManageUsersAdapter(List<User> userList, Context context, OnDeleteClickListener deleteClickListener) {
        this.userList = userList;
        this.context = context;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false); // Assuming item_center.xml is your layout
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView, userNumTextView;
        private final TextView emailTextView;
        private final TextView lastUpdatedTextView;
        private final androidx.appcompat.widget.AppCompatButton deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            userNumTextView = itemView.findViewById(R.id.tv_users_number);
            usernameTextView = itemView.findViewById(R.id.tv_username);
            emailTextView = itemView.findViewById(R.id.tv_email);
            lastUpdatedTextView = itemView.findViewById(R.id.tv_last_online_time);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(User user, int position) {
            userNumTextView.setText(String.valueOf(position + 1));
            usernameTextView.setText(user.getUsername()); // Assuming event type is the title
            emailTextView.setText(user.getEmail());
            lastUpdatedTextView.setText(user.getLastOnlineTime());

            // Delete button click handling
            deleteButton.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(user, position);
                }
            });
        }
    }

    // Interface for handling delete button click events
    public interface OnDeleteClickListener {
        void onDeleteClick(User user, int position);
    }
}
