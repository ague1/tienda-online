package com.example.myapplication.features.profiles.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.example.myapplication.features.profiles.presentation.viewmodel.ProfileViewModel;
import com.example.myapplication.features.profiles.domain.model.Profile;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPhone;
    private EditText inputAddress;
    private Button saveProfileButton;

    private ProfileViewModel viewModel;

    @Inject
    AuthRepository authRepository;

    private Profile currentProfile;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_edit_profile,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);

        initView(view);
        observeProfile();
        observeUpdateProfile();
        loadProfile();

        saveProfileButton.setOnClickListener(v ->
                saveProfile()
        );
    }

    private void initView(View view) {

        inputName = view.findViewById(R.id.inputProfileName);
        inputEmail = view.findViewById(R.id.inputProfileEmail);
        inputPhone = view.findViewById(R.id.inputProfilePhone);
        inputAddress = view.findViewById(R.id.inputProfileAddress);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);
    }

    private void loadProfile() {

        String uid = authRepository.getCurrentUserId();

        if (uid == null || uid.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Usuario no autenticado",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        viewModel.loadProfile(uid);
    }

    private void observeProfile() {

        viewModel.getProfileState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) {
                        return;
                    }

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:
                            saveProfileButton.setEnabled(false);
                            break;

                        case SUCCESS:

                            currentProfile = state.getData();

                            if (currentProfile != null) {
                                showProfile(currentProfile);
                            }

                            saveProfileButton.setEnabled(true);
                            break;

                        case ERROR:

                            saveProfileButton.setEnabled(true);

                            Toast.makeText(
                                    requireContext(),
                                    "Error loading profile",
                                    Toast.LENGTH_SHORT
                            ).show();

                            break;
                    }
                }
        );
    }

    private void observeUpdateProfile() {

        viewModel.getUpdateProfileState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) {
                        return;
                    }

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:

                            saveProfileButton.setEnabled(false);
                            saveProfileButton.setText("Saving...");

                            break;

                        case SUCCESS:

                            saveProfileButton.setEnabled(true);
                            saveProfileButton.setText("Save changes");

                            Toast.makeText(
                                    requireContext(),
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .popBackStack();

                            break;

                        case ERROR:

                            saveProfileButton.setEnabled(true);
                            saveProfileButton.setText("Save changes");

                            Toast.makeText(
                                    requireContext(),
                                    "Error updating profile: " +
                                            state.getError(),
                                    Toast.LENGTH_LONG
                            ).show();

                            break;
                    }
                }
        );
    }

    private void saveProfile() {

        if (currentProfile == null) {

            Toast.makeText(
                    requireContext(),
                    "Profile not loaded",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String name =
                inputName.getText()
                        .toString()
                        .trim();

        String email =
                inputEmail.getText()
                        .toString()
                        .trim();

        String phone =
                inputPhone.getText()
                        .toString()
                        .trim();

        String address =
                inputAddress.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {
            inputName.setError("Required");
            return;
        }

        if (email.isEmpty()) {
            inputEmail.setError("Required");
            return;
        }

        if (phone.isEmpty()) {
            inputPhone.setError("Required");
            return;
        }

        if (address.isEmpty()) {
            inputAddress.setError("Required");
            return;
        }

        currentProfile.setName(name);
        currentProfile.setEmail(email);
        currentProfile.setPhone(phone);
        currentProfile.setAddress(address);

        viewModel.updateProfile(currentProfile);
    }

    private void showProfile(Profile profile) {

        inputName.setText(
                profile.getName() != null
                        ? profile.getName()
                        : ""
        );

        inputEmail.setText(
                profile.getEmail() != null
                        ? profile.getEmail()
                        : ""
        );

        inputPhone.setText(
                profile.getPhone() != null
                        ? profile.getPhone()
                        : ""
        );

        inputAddress.setText(
                profile.getAddress() != null
                        ? profile.getAddress()
                        : ""
        );
    }
}