package com.example.myapplication.features.profiles.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

import javax.inject.Inject;

public class ProfileDataSource {

    private FirebaseFirestore db;
    @Inject
    public ProfileDataSource(){
        db = FirebaseFirestore.getInstance();
    }

    public static Task<DocumentSnapshot> getProfile(String uid) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get();
    }

    public Task<Void> updateProfile(
            String uid,
            Map<String, Object> userUpdate
    ) {

        return db.collection("users")
                .document(uid)
                .set(userUpdate, SetOptions.merge());
    }

    public Task<Void> createProfile(String uid, Map<String, Object> data) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(data);
    }
}
