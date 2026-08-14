package com.example.myapplication.features.profiles.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.repository.AuthRepository;
import com.example.myapplication.features.profiles.model.Profile;
import com.example.myapplication.features.profiles.repository.ProfileRepository;
import com.example.myapplication.features.profiles.usecase.GetProfileUseCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends Fragment {

    private TextView fullName, email, phone, address, initials, memberSince;
    private Button btnSettings;
    @Inject
    GetProfileUseCase getProfileUseCase;
    @Inject
    AuthRepository auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        loadUserData();
        btnSettings.setOnClickListener(v -> openSettings());

    }

    private void openSettings() {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SettingsFragment())
                .addToBackStack("setting")
                .commit();
    }

    private void initView(View view) {

        fullName = view.findViewById(R.id.fullName);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        initials = view.findViewById(R.id.initials);
        memberSince = view.findViewById(R.id.memberSince);
        btnSettings = view.findViewById(R.id.settingsButton);

    }

    private void loadUserData() {

        getProfileUseCase.execute(auth.getCurrentUserId()).addOnSuccessListener(profile ->{
            if (profile == null) {
                return;
            }
            showProfile(profile);
        })
        .addOnFailureListener(e -> Toast.makeText(
                requireContext(),
                "Error loading profile",
                Toast.LENGTH_SHORT)
                .show()
        );
    }
    private void showProfile(Profile profile) {
        fullName.setText(profile.getName() != null ? profile.getName() : "Unknown");
        email.setText(profile.getEmail() != null ? profile.getEmail() : "No email");
        phone.setText(profile.getPhone() != null ? profile.getPhone() : "No phone");
        address.setText( profile.getAddress() != null ? profile.getAddress() : "No address");
        showInitials(profile.getName());
        showMemberSince(profile.getCreatedAt());
    }

    private void showMemberSince(Date createdAt) {
        if (createdAt == null) {
            memberSince.setText("Member since —");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy",
                Locale.getDefault());
        memberSince.setText("Member since " + sdf.format(createdAt));
    }

    private void showInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            initials.setText("");
            return;
        }

        String[] parts = name.trim().split(" ");

        if (parts.length >= 2) {
            initials.setText(
                    ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase()
            );
        } else {
            initials.setText(
                    name.substring(0, Math.min(2, name.length())).toUpperCase()
            );
        }
    }

}