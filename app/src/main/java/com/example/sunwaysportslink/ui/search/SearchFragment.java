package com.example.sunwaysportslink.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
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
import java.util.Collections;
import java.util.Comparator;
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
    private FragmentSearchBinding binding;
    private List<Event> displayedList; // This will keep track of the currently displayed list
    private ProgressBar progressBar; // Add ProgressBar reference


    private enum SortOption {
        DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC
    }

    private SortOption currentSortOption = SortOption.DATE_ASC;

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

        binding = FragmentSearchBinding.inflate(inflater, container, false);

        // Initialize RecyclerView
        recyclerView = binding.recylerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter
        eventList = new ArrayList<>();
        displayedList = new ArrayList<>();  // Initialize displayedList
        eventAdapter = new EventAdapter(displayedList, this::onEventClick);  // Pass the click listener
        recyclerView.setAdapter(eventAdapter);

        progressBar = binding.progressBar;

        firebaseService = FirebaseService.getInstance();

        // Fetch events from Firebase
        fetchEventsFromFirebase();

        binding.tvSort.setOnClickListener(v -> showSortOptions());
        binding.tvFilter.setOnClickListener(v -> showFilterOptions());
        binding.btnClearFilter.setOnClickListener(v -> clearFilters()); // Add this line

        binding.etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterEventsBySearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return binding.getRoot();
    }

    // Method to filter events based on the search input
    private void filterEventsBySearch(String query) {
        List<Event> searchResults = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getCreatedBy().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(event);
            }
        }
        eventAdapter.updateEvents(searchResults);
    }

    // Method to clear all filters and show the original list
    private void clearFilters() {
        displayedList.clear();
        displayedList.addAll(eventList);  // Reset displayedList to the original list

        selectedSport = null;  // Reset selected sport
        selectedDate = null;   // Reset selected date
        selectedSortOption = null;

        // Notify the adapter to reflect changes
        eventAdapter.notifyDataSetChanged();
    }

    private void showFilterOptions() {
        String[] filterOptions = {"Sports", "Date"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter By");
        builder.setItems(filterOptions, (dialog, which) -> {
            switch (which) {
                case 0: // Filter by sports
                    showSportsOptions(); // Show available sports options
                    break;
                case 1: // Filter by date
                    showDateOptions(); // Show available date options
                    break;
            }
        });
        builder.show();
    }

    private String selectedSport = null; // To track the currently selected sport

    // Method to show available sports options for filtering
    private void showSportsOptions() {
        // Assuming you have a predefined list of sports in your app
        String[] sportsOptions = {"Football", "Basketball", "Futsal", "Tennis", "Volleyball"};
        int checkedItem = -1; // Initially, no sport is selected

        // Find the index of the previously selected sport, if any
        if (selectedSport != null) {
            for (int i = 0; i < sportsOptions.length; i++) {
                if (sportsOptions[i].equals(selectedSport)) {
                    checkedItem = i;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Sport");
        builder.setSingleChoiceItems(sportsOptions, checkedItem, (dialog, which) -> {
            selectedSport = sportsOptions[which]; // Update the selected sport
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedSport != null) {
                filterEvents(selectedSport, null); // Filter by the selected sport
                selectedDate = null;   // Reset selected date
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private String selectedDate = null; // To track the currently selected date

    // Method to show available dates options for filtering
    private void showDateOptions() {
        // Assuming you're fetching or storing available dates dynamically
        // Here I am using some placeholder dates for demonstration
        List<String> availableDates = getAvailableDatesFromEvents(); // Fetch from events list

        if (availableDates.isEmpty()) {
            availableDates.add("No available dates");
        }

        String[] dateOptions = availableDates.toArray(new String[0]);
        int checkedItem = -1; // Initially, no date is selected

        // Find the index of the previously selected date, if any
        if (selectedDate != null) {
            for (int i = 0; i < dateOptions.length; i++) {
                if (dateOptions[i].equals(selectedDate)) {
                    checkedItem = i;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Date");
        builder.setSingleChoiceItems(dateOptions, checkedItem, (dialog, which) -> {
            selectedDate = dateOptions[which]; // Update the selected date
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedDate != null && !selectedDate.equals("No available dates")) {
                filterEvents(null, selectedDate); // Filter by the selected date
                selectedSport = null;   // Reset selected date
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Helper method to get available dates from events
    private List<String> getAvailableDatesFromEvents() {
        List<String> dates = new ArrayList<>();
        for (Event event : eventList) {
            if (!dates.contains(event.getDate())) {
                dates.add(event.getDate()); // Avoid duplicate dates
            }
        }
        return dates;
    }

    private void filterEvents(String sportsType, String date) {
        binding.tvNoEvents.setVisibility(View.GONE);

        List<Event> filteredList = new ArrayList<>();

        for (Event event : eventList) {
            // If filtering by sports type
            if (event.getTitle().equals(sportsType)) {
                filteredList.add(event);
            }
            // If filtering by date
            else if (event.getDate().equals(date)) {
                filteredList.add(event);
            }
        }

        if (filteredList.isEmpty()) {
            binding.tvNoEvents.setVisibility(View.VISIBLE);
        }

        displayedList.clear();
        displayedList.addAll(filteredList);
        eventAdapter.notifyDataSetChanged(); // Notify adapter to update RecyclerView
    }

    private SortOption selectedSortOption = null; // To track the currently selected sort option

    private void showSortOptions() {
        String[] sortOptions = {"Date (Ascending)", "Date (Descending)", "Name (Ascending)", "Name (Descending)"};
        int checkedItem = -1; // Initially, no sort option is selected

        // Find the index of the previously selected sort option, if any
        if (selectedSortOption != null) {
            switch (selectedSortOption) {
                case DATE_ASC:
                    checkedItem = 0;
                    break;
                case DATE_DESC:
                    checkedItem = 1;
                    break;
                case NAME_ASC:
                    checkedItem = 2;
                    break;
                case NAME_DESC:
                    checkedItem = 3;
                    break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sort By");
        builder.setSingleChoiceItems(sortOptions, checkedItem, (dialog, which) -> {
            switch (which) {
                case 0:
                    selectedSortOption = SortOption.DATE_ASC;
                    break;
                case 1:
                    selectedSortOption = SortOption.DATE_DESC;
                    break;
                case 2:
                    selectedSortOption = SortOption.NAME_ASC;
                    break;
                case 3:
                    selectedSortOption = SortOption.NAME_DESC;
                    break;
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedSortOption != null) {
                currentSortOption = selectedSortOption; // Update the current sort option
                sortEvents(); // Sort events based on the selected option
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Method to sort events based on the selected option
    private void sortEvents() {
        switch (currentSortOption) {
            case DATE_ASC:
                Collections.sort(displayedList, Comparator.comparing(Event::getDate));
                break;
            case DATE_DESC:
                Collections.sort(displayedList, (e1, e2) -> e2.getDate().compareTo(e1.getDate()));
                break;
            case NAME_ASC:
                Collections.sort(displayedList, Comparator.comparing(Event::getCreatedBy));
                break;
            case NAME_DESC:
                Collections.sort(displayedList, (e1, e2) -> e2.getCreatedBy().compareTo(e1.getCreatedBy()));
                break;
        }
        eventAdapter.notifyDataSetChanged(); // Notify adapter to update RecyclerView
    }

    private void fetchEventsFromFirebase() {

        progressBar.setVisibility(View.VISIBLE);
        firebaseService.getEventsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {  // Only proceed if there is data
                    eventList.clear(); // Clear the list to avoid duplicates
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Event event = snapshot.getValue(Event.class);
                        if (event != null) {
                            eventList.add(event); // Add event to the list
                        }
                    }

                    // Check if the eventList is empty and show the "No Events" text accordingly
                    if (eventList.isEmpty()) {
                        binding.tvNoEvents.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvNoEvents.setVisibility(View.GONE);
                    }

                    // Clear the displayedList and then add all events from eventList
                    displayedList.clear();
                    displayedList.addAll(eventList);

                    // Notify the adapter that data has changed
                    eventAdapter.updateEvents(displayedList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchEventsFromFirebase(); // Reload the events when the fragment becomes visible
    }

    private void onEventClick(Event event) {
        // Navigate to the EventDetailsActivity
        EventDetailsActivity.startIntent(getContext(), event, false);  // Start the EventDetailsActivity
    }
}