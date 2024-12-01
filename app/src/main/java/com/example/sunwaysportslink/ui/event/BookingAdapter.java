package com.example.sunwaysportslink.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_slots, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvSlotId.setText(String.valueOf(position + 1));  // Display the item number
        holder.tvBookingSlot.setText(booking.getTimeSlot());
        holder.tvBookedBy.setText(booking.getBookedBy());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvSlotId, tvBookingSlot, tvBookedBy;

        public BookingViewHolder(View itemView) {
            super(itemView);
            tvSlotId = itemView.findViewById(R.id.tv_id);
            tvBookingSlot = itemView.findViewById(R.id.tv_time_slot);
            tvBookedBy = itemView.findViewById(R.id.tv_user);
        }
    }
}
