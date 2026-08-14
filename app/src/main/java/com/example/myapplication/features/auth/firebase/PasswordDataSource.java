package com.example.myapplication.features.auth.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PasswordDataSource {
    private final OkHttpClient client;

    @Inject
    public PasswordDataSource() {
        client = new OkHttpClient();
    }

    public void resetPassword(
            String email,
            String password,
            Callback callback
    ) {
        try {

            JSONObject json = new JSONObject();

            json.put("email", email);
            json.put("password", password);

            Log.d("RESET_PASSWORD", "EMAIL: " + email);
            Log.d("RESET_PASSWORD", "URL: "
                    + BuildConfig.BASE_URL + "/reset-password");

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_URL + "/reset-password")
                    .post(body)
                    .build();

            //client.newCall(request).enqueue(callback);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(
                        @NonNull Call call,
                        @NonNull IOException e
                ) {
                    Log.e(
                            "RESET_PASSWORD",
                            "CONNECTION ERROR",
                            e
                    );

                    callback.onFailure(call, e);
                }

                @Override
                public void onResponse(
                        @NonNull Call call,
                        @NonNull Response response
                ) throws IOException {

                    String result = response.body() != null
                            ? response.body().string()
                            : "";

                    Log.d(
                            "RESET_PASSWORD",
                            "HTTP CODE: " + response.code()
                    );

                    Log.d(
                            "RESET_PASSWORD",
                            "SERVER RESPONSE: " + result
                    );

                    // IMPORTANTE:
                    // Ya consumimos el body, así que no debemos
                    // pasar el mismo Response al callback.

                    Response newResponse = response.newBuilder()
                            .body(
                                    ResponseBody.create(
                                            result,
                                            MediaType.get("application/json")
                                    )
                            )
                            .build();

                    callback.onResponse(call, newResponse);
                }
            });

        } catch (Exception e) {

            callback.onFailure(
                    null,
                    new IOException(e)
            );
        }
    }
}
