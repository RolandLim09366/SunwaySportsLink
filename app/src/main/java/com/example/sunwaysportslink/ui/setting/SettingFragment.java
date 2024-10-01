package com.example.sunwaysportslink.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sunwaysportslink.R;
import com.example.sunwaysportslink.databinding.FragmentSettingBinding;
import com.example.sunwaysportslink.ui.login.LoginActivity;
import com.example.sunwaysportslink.ui.setting.changepassword.ChangePasswordActivity;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.sunwaysportslink.ui.home.HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SettingViewModel viewModel;
    private FirebaseAuth mAuth; // Declare FirebaseAuth instance

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static com.example.sunwaysportslink.ui.home.HomeFragment newInstance(String param1, String param2) {
        com.example.sunwaysportslink.ui.home.HomeFragment fragment = new com.example.sunwaysportslink.ui.home.HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout with DataBindingUtil
        FragmentSettingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        Button logoutButton = binding.btnLogOut; // Assuming btn_log_out is in the FragmentSettingBinding layout

        ImageView changePasswordButton = binding.icBlackArrow3;

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.logout();
                LoginActivity.startIntent(getActivity());
                Toast.makeText(getActivity(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
                getActivity().finish(); // Close the current activity
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordActivity.startIntent(getActivity());
            }
        });

        // Bind ViewModel to the layout
        binding.setViewModel(viewModel);

        // Set lifecycle owner so that the layout can observe LiveData
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }
}
