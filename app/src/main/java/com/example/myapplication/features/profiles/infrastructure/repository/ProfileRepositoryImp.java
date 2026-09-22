package com.example.myapplication.features.profiles.infrastructure.repository;


import com.example.myapplication.features.profiles.infrastructure.datasource.ProfileDataSource;
import com.example.myapplication.features.profiles.domain.model.Profile;
import com.example.myapplication.features.profiles.domain.port.ProfileRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class ProfileRepositoryImp implements ProfileRepository {

    private final ProfileDataSource profileDataSource;

    @Inject
    public ProfileRepositoryImp(
            ProfileDataSource profileDataSource
    ) {
        this.profileDataSource = profileDataSource;
    }

    @Override
    public Task<Void> updateProfile(
            Profile profile
    ) {

        Map<String, Object> map = new HashMap<>();

        map.put("name", profile.getName());
        map.put("email", profile.getEmail());
        map.put("phone", profile.getPhone());
        map.put("address", profile.getAddress());

        return profileDataSource.updateProfile(
                profile.getUid(),
                map
        );
    }

    @Override
    public Task<Profile> getProfile(
            String uid
    ) {

        return profileDataSource
                .getProfile(uid)
                .continueWith(task -> {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    DocumentSnapshot doc =
                            task.getResult();

                    if (
                            doc == null ||
                                    !doc.exists()
                    ) {
                        throw new IllegalStateException(
                                "PROFILE_NOT_FOUND"
                        );
                    }

                    Profile profile =
                            new Profile();

                    profile.setUid(
                            doc.getId()
                    );

                    profile.setName(
                            doc.getString("name")
                    );

                    profile.setEmail(
                            doc.getString("email")
                    );

                    profile.setPhone(
                            doc.getString("phone")
                    );

                    profile.setAddress(
                            doc.getString("address")
                    );

                    Timestamp timestamp =
                            doc.getTimestamp("createdAt");

                    if (timestamp != null) {
                        profile.setCreatedAt(
                                timestamp.toDate()
                        );
                    }

                    return profile;
                });
    }

    @Override
    public Task<Void> createProfile(
            Profile profile
    ) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "uid",
                profile.getUid()
        );

        data.put(
                "name",
                profile.getName()
        );

        data.put(
                "email",
                profile.getEmail()
        );

        data.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        return profileDataSource.createProfile(
                profile.getUid(),
                data
        );
    }
}

