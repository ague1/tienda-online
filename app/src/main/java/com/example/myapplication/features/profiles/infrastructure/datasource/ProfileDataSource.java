package com.example.myapplication.features.profiles.infrastructure.datasource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

import javax.inject.Inject;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileDataSource {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    @Inject
    public ProfileDataSource(
            FirebaseFirestore db,
            FirebaseAuth auth
    ) {
        this.db = db;
        this.auth = auth;
    }

    public Task<DocumentSnapshot> getProfile(String uid) {

        String currentUid =
                getAuthenticatedUid(uid);

        return db.collection("users")
                .document(currentUid)
                .get();
    }

    public Task<Void> updateProfile(
            String uid,
            Map<String, Object> userUpdate
    ) {

        String currentUid =
                getAuthenticatedUid(uid);

        return db.collection("users")
                .document(currentUid)
                .set(
                        userUpdate,
                        SetOptions.merge()
                );
    }

    public Task<Void> createProfile(
            String uid,
            Map<String, Object> data
    ) {

        String currentUid =
                getAuthenticatedUid(uid);

        return db.collection("users")
                .document(currentUid)
                .set(data);
    }

    private String getAuthenticatedUid(String uid) {

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException(
                    "USER_NOT_AUTHENTICATED"
            );
        }

        String currentUid =
                currentUser.getUid();

        if (!currentUid.equals(uid)) {
            throw new IllegalStateException(
                    "UID_MISMATCH"
            );
        }

        return currentUid;
    }
}
