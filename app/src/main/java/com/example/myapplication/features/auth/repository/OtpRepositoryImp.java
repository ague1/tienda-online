package com.example.myapplication.features.auth.repository;

import com.example.myapplication.features.auth.firebase.OtpDataSource;

import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Response;

public class OtpRepositoryImp implements OtpRepository{
    private final OtpDataSource dataSource;

    @Inject
    public OtpRepositoryImp(
            OtpDataSource dataSource
    ) {
        this.dataSource = dataSource;
    }

    @Override
    public void verifyOtp(String email, String otp,OtpCallback callback) {

        dataSource.verifyOtp(email, otp, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {

                    callback.onError("Error del servidor");
                    return;
                }

                if (response.body() == null) {

                    callback.onError("Respuesta vacía");
                    return;
                }

                try {

                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    boolean valid = json.getBoolean("valid");

                    if (valid) {callback.onSuccess();
                    } else {
                        callback.onError("Código incorrecto o expirado");
                    }
                } catch (Exception e) {
                    callback.onError("Respuesta inválida");
                }
            }
        });
    }
}
