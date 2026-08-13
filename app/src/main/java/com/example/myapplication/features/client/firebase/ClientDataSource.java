package com.example.myapplication.features.client.firebase;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class ClientDataSource {

    private FirebaseFirestore db;

    @Inject
    public ClientDataSource(){

        db = FirebaseFirestore.getInstance();

    }

}
