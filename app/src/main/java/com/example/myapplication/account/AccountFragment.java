package com.example.myapplication.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountFragment extends Fragment {

    private TextView fullName, email, phone, address, initials, memberSince;
    private Button btnSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullName = view.findViewById(R.id.fullName);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        address = view.findViewById(R.id.address);
        initials = view.findViewById(R.id.initials);
        memberSince = view.findViewById(R.id.memberSince);
        btnSettings = view.findViewById(R.id.settingsButton);

        loadUserData();

        btnSettings.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new SettingsFragment())
                    .addToBackStack("setting")
                    .commit();


        });

    }

    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String mail = doc.getString("email");
                        String phoneNum = doc.getString("phone");
                        String addr = doc.getString("address");

                        // Llenar UI
                        fullName.setText(name != null ? name : "Unknown");
                        email.setText(mail != null ? mail : "No email");
                        phone.setText(phoneNum != null ? phoneNum : "No phone");
                        address.setText(addr != null ? addr : "No address");

                        // Iniciales
                        if (name != null && name.contains(" ")) {
                            String[] parts = name.split(" ");
                            initials.setText(
                                    ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase()
                            );
                        } else if (name != null && name.length() >= 2) {
                            initials.setText(name.substring(0, 2).toUpperCase());
                        }

                        // Fecha de creación
                        Timestamp created = doc.getTimestamp("createdAt");
                        if (created != null) {
                            Date date = created.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                            memberSince.setText("Member since " + sdf.format(date));
                        } else {
                            memberSince.setText("Member since —");
                        }
                    }
                });
    }
}