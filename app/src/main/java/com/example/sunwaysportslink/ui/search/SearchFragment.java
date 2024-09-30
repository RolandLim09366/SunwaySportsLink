package com.example.sunwaysportslink.ui.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.model.Event;

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        // Populate the event list (you can replace this with actual data fetching later)
        populateEvents();

        return view;
    }

    // Sample method to populate events (replace with actual data fetching)
    private void populateEvents() {
        eventList.add(new Event("Football Tournament", "2024-10-01", "Sunway Stadium"));
        eventList.add(new Event("Basketball Match", "2024-10-05", "Sunway Court"));
        eventList.add(new Event("Badminton Championship", "2024-10-10", "Sunway Sports Complex"));

        // Notify the adapter that the data has changed
        eventAdapter.notifyDataSetChanged();
    }
}
