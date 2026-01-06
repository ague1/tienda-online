package com.example.myapplication.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.user_auth.Login;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    Button redictionLogout;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        redictionLogout = view.findViewById(R.id.logoutButton);

        redictionLogout.setOnClickListener(v -> {
            // Cierra la sesión del usuario
            FirebaseAuth.getInstance().signOut();

            // Redirige a Login
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Cierra la actividad actual
            getActivity().finish();
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}