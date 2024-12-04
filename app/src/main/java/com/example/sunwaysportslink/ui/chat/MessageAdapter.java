package com.example.sunwaysportslink.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.ChatMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<ChatMessage> messageList;
    private final String currentUserId;

    public MessageAdapter(List<ChatMessage> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // Current user's message
            holder.senderMessageLayout.setVisibility(View.GONE);
            holder.myMessageLayout.setVisibility(View.VISIBLE);
            holder.textViewMyMessage.setText(message.getMessageText());
        } else {
            // Other user's message
            holder.myMessageLayout.setVisibility(View.GONE);
            holder.senderMessageLayout.setVisibility(View.VISIBLE);
            holder.textViewSenderMessage.setText(message.getMessageText());
            holder.textViewSenderName.setText(message.getSenderName());
            Glide.with(holder.itemView.getContext())
                    .load(message.getSenderProfilePictureUrl())
                    .placeholder(R.drawable.iv_default_profile)
                    .into(holder.imageViewSenderProfile);        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout myMessageLayout;
        LinearLayout senderMessageLayout;
        TextView textViewSenderMessage, textViewMyMessage, textViewSenderName;
        ImageView imageViewSenderProfile;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessageLayout = itemView.findViewById(R.id.myMessageLayout);
            senderMessageLayout = itemView.findViewById(R.id.senderMessageLayout);
            textViewMyMessage = itemView.findViewById(R.id.textViewMyMessage);
            textViewSenderMessage = itemView.findViewById(R.id.textViewSenderMessage);
            textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
            imageViewSenderProfile = itemView.findViewById(R.id.imageViewSenderProfile);
        }
    }
}