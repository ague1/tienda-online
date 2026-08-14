package com.example.myapplication.features.auth.firebase;

import com.example.network.ApiEndpoints;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import javax.inject.Inject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthDataSource {
    private final FirebaseAuth auth;

    @Inject
    public AuthDataSource(){
        auth = FirebaseAuth.getInstance();
    }



    public String getCurrentUserId(){

        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            return user.getUid();
        }

        return null;
    }

    public void logout(){

        auth.signOut();

    }

    public void sendOtp(String email, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                json.toString()
        );

        Request request = new Request.Builder()
                .url(ApiEndpoints.SEND_OTP)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }


    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(
                email,
                password
        );
    }

    public Task<AuthResult> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(
                email,
                password
        );
    }

}
