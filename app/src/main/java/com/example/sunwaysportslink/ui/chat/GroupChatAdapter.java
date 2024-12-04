package com.example.sunwaysportslink.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.GroupChat;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {

    private final List<GroupChat> groupChatList;
    private final OnGroupChatClickListener listener;

    public GroupChatAdapter(List<GroupChat> groupChatList, OnGroupChatClickListener listener) {
        this.groupChatList = groupChatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteme_group_chat, parent, false);
        return new GroupChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatViewHolder holder, int position) {
        GroupChat groupChat = groupChatList.get(position);
        holder.bind(groupChat, listener);
    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    public static class GroupChatViewHolder extends RecyclerView.ViewHolder {

        private final ImageView groupIcon;
        private final TextView groupName;
        private final TextView lastMessage;
        private final TextView timestamp;

        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            groupIcon = itemView.findViewById(R.id.groupIcon);
            groupName = itemView.findViewById(R.id.groupName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
        }

        public void bind(GroupChat groupChat, OnGroupChatClickListener listener) {
            groupName.setText(groupChat.getGroupName());
            lastMessage.setText(groupChat.getLastMessage());
            timestamp.setText(groupChat.getTimestamp());

            // Use if-else for checking group names and setting icons
            String groupNameLower = groupChat.getGroupName().toLowerCase();
            if (groupNameLower.contains("basketball")) {
                groupIcon.setImageResource(R.drawable.iv_basketball);
            } else if (groupNameLower.contains("football")) {
                groupIcon.setImageResource(R.drawable.iv_football);
            } else if (groupNameLower.contains("tennis")) {
                groupIcon.setImageResource(R.drawable.iv_tennis);
            } else if (groupNameLower.contains("futsal")) {
                groupIcon.setImageResource(R.drawable.iv_futsal);
            } else if (groupNameLower.contains("volleyball")) {
                groupIcon.setImageResource(R.drawable.iv_volleyball);
            } else {
                groupIcon.setImageResource(R.drawable.iv_sports);
            }

            itemView.setOnClickListener(v -> listener.onGroupChatClick(groupChat));
        }
    }

    public interface OnGroupChatClickListener {
        void onGroupChatClick(GroupChat groupChat);
    }
}