package com.example.myapplication.features.auth.firebase;

import com.example.myapplication.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class OtpDataSource {
    private final OkHttpClient client;

    @Inject
    public OtpDataSource() {
        client = new OkHttpClient();
    }

    public void verifyOtp(
            String email,
            String otp,
            Callback callback
    ) {

        try {

            JSONObject json = new JSONObject();

            json.put("email", email);
            json.put("otp", otp);

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_URL + "/verify-otp")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);

        } catch (Exception e) {

            callback.onFailure(null, new IOException(e));
        }
    }

}
