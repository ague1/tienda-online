package com.example.myapplication.features.auth.infrastructure.datasource;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.features.auth.domain.model.OtpResult;
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


public class OtpDataSource {

    private final OkHttpClient client;

    @Inject
    public OtpDataSource(
            OkHttpClient client
    ) {
        this.client = client;
    }

    public Task<Void> sendOtp(String email) {

        TaskCompletionSource<Void> source =
                new TaskCompletionSource<>();

        try {

            JSONObject json = new JSONObject();
            json.put("email", email);

            RequestBody body =
                    RequestBody.create(
                            MediaType.get(
                                    "application/json; charset=utf-8"
                            ),
                            json.toString()
                    );

            Request request =
                    new Request.Builder()
                            .url(
                                    BuildConfig.BASE_URL
                                            + "/send-otp"
                            )
                            .post(body)
                            .build();

            client.newCall(request)
                    .enqueue(new okhttp3.Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e
                        ) {
                            source.setException(e);
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) throws IOException {

                            if (response.isSuccessful()) {
                                source.setResult(null);
                            } else {
                                source.setException(
                                        new Exception(
                                                "SEND_OTP_ERROR"
                                        )
                                );
                            }
                        }
                    });

        } catch (Exception e) {

            source.setException(e);
        }

        return source.getTask();
    }

    public Task<OtpResult> verifyOtp(
            String email,
            String otp
    ) {

        TaskCompletionSource<OtpResult> source =
                new TaskCompletionSource<>();

        try {

            JSONObject json = new JSONObject();

            json.put("email", email);
            json.put("otp", otp);

            RequestBody body =
                    RequestBody.create(
                            MediaType.get(
                                    "application/json; charset=utf-8"
                            ),
                            json.toString()
                    );

            Request request =
                    new Request.Builder()
                            .url(
                                    BuildConfig.BASE_URL
                                            + "/verify-otp"
                            )
                            .post(body)
                            .build();

            client.newCall(request)
                    .enqueue(new okhttp3.Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e
                        ) {

                            source.setException(e);
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) throws IOException {

                            if (!response.isSuccessful()) {

                                source.setException(
                                        new Exception(
                                                "OTP_INVALID"
                                        )
                                );

                                return;
                            }

                            if (response.body() == null) {

                                source.setException(
                                        new Exception(
                                                "EMPTY_RESPONSE"
                                        )
                                );

                                return;
                            }

                            try {

                                String responseBody =
                                        response.body().string();

                                JSONObject responseJson =
                                        new JSONObject(
                                                responseBody
                                        );

                                boolean verified =
                                        responseJson.getBoolean(
                                                "verified"
                                        );

                                if (!verified) {

                                    source.setException(
                                            new Exception(
                                                    "OTP_INVALID"
                                            )
                                    );

                                    return;
                                }

                                String resetToken =
                                        responseJson.getString(
                                                "resetToken"
                                        );

                                String challengeId =
                                        responseJson.getString(
                                                "challengeId"
                                        );

                                source.setResult(
                                        new OtpResult(
                                                resetToken,
                                                challengeId
                                        )
                                );

                            } catch (Exception e) {

                                source.setException(e);
                            }
                        }
                    });

        } catch (Exception e) {

            source.setException(e);
        }

        return source.getTask();
    }

}

