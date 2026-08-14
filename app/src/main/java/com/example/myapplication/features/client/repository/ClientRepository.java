package com.example.myapplication.features.client.repository;

import com.example.myapplication.features.client.firebase.ClientDataSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class ClientRepository {
    private ClientDataSource dataSource;

    @Inject
    public ClientRepository(ClientDataSource dataSource){

        this.dataSource=dataSource;

    }

}
