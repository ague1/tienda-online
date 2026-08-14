package com.example.myapplication.features.profiles.repository;

import com.example.myapplication.features.profiles.firebase.ProfileDataSource;
import com.example.myapplication.features.profiles.model.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class ProfileRepository {

    private ProfileDataSource profileDataSource;
    @Inject
    public ProfileRepository(ProfileDataSource profileDataSource){
        this.profileDataSource = profileDataSource;
    }


    public Task<Void> updateProfile(Profile profile) {

        Map<String,Object> map = new HashMap<>();

        map.put("name", profile.getName());
        map.put("email", profile.getEmail());
        map.put("phone", profile.getPhone());
        map.put("address", profile.getAddress());

        return profileDataSource.updateProfile(
                profile.getUid(),
                map
        );
    }


    public Task<Profile> getProfile(String uid){

        return profileDataSource.getProfile(uid)
                .continueWith(task -> {

                    DocumentSnapshot doc = task.getResult();

                    Profile profile = new Profile();

                    profile.setUid(uid);
                    profile.setName(doc.getString("name"));
                    profile.setEmail(doc.getString("email"));
                    profile.setPhone(doc.getString("phone"));
                    profile.setAddress(doc.getString("address"));
                    Timestamp timestamp = doc.getTimestamp("createdAt");

                    if(timestamp != null){
                        profile.setCreatedAt(timestamp.toDate());
                    }

                    return profile;
                });
    }

    public Task<Void> createProfile(Profile profile){

        Map<String,Object> data = new HashMap<>();

        data.put("uid", profile.getUid());
        data.put("name", profile.getName());
        data.put("email", profile.getEmail());
        data.put("createdAt", FieldValue.serverTimestamp());


        return profileDataSource.createProfile(
                profile.getUid(),
                data
        );
    }
}
