package com.example.sunwaysportslink.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunwaysportslink.databinding.FragmentSearchBinding;
import com.example.sunwaysportslink.firebase.FirebaseService;
import com.example.sunwaysportslink.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.sunwaysportslink.ui.search.SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseService firebaseService;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static com.example.sunwaysportslink.ui.search.SearchFragment newInstance(String param1, String param2) {
        com.example.sunwaysportslink.ui.search.SearchFragment fragment = new com.example.sunwaysportslink.ui.search.SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentSearchBinding binding = FragmentSearchBinding.inflate(inflater, container, false);

        // Initialize RecyclerView
        recyclerView = binding.recylerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this::onEventClick);  // Pass the click listener
        recyclerView.setAdapter(eventAdapter);


        firebaseService = FirebaseService.getInstance();

        // Fetch events from Firebase
        fetchEventsFromFirebase(binding);

        return binding.getRoot();
    }

    private void fetchEventsFromFirebase(FragmentSearchBinding binding) {
        firebaseService.getEventsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear(); // Clear list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event); // Add event to list
                    }
                }

                // Show "No Events" message if list is empty
                if (eventList.isEmpty()) {
                    binding.tvNoEvents.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoEvents.setVisibility(View.GONE);
                }

                eventAdapter.notifyDataSetChanged(); // Notify adapter to refresh RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void onEventClick(Event event) {
        // Navigate to the EventDetailsActivity
        EventDetailsActivity.startIntent(getContext(), event);  // Start the EventDetailsActivity
    }
}
