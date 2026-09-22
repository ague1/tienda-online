package com.example.myapplication.features.auth.infrastructure.datasource;

import com.example.myapplication.core.network.ApiEndpoints;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.json.JSONObject;
import java.io.IOException;
import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PasswordDataSource {

    private final OkHttpClient client;

    @Inject
    public PasswordDataSource(OkHttpClient client) {
        this.client = client;
    }

    public Task<Void> resetPassword(
            String challengeId,
            String resetToken,
            String password
    ) {

        TaskCompletionSource<Void> source =
                new TaskCompletionSource<>();

        try {

            JSONObject json = new JSONObject();

            json.put("challengeId", challengeId);
            json.put("resetToken", resetToken);
            json.put("password", password);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(ApiEndpoints.RESET_PASSWORD)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(
                        Call call,
                        IOException e
                ) {

                    source.setException(
                            new Exception(
                                    "Error de conexión con servidor",
                                    e
                            )
                    );
                }

                @Override
                public void onResponse(
                        Call call,
                        Response response
                ) throws IOException {

                    try {

                        if (response.isSuccessful()) {

                            source.setResult(null);

                        } else {

                            source.setException(
                                    new Exception(
                                            "No se pudo actualizar la contraseña"
                                    )
                            );
                        }

                    } finally {

                        response.close();
                    }
                }
            });

        } catch (Exception e) {

            source.setException(e);
        }

        return source.getTask();
    }
}