package com.example.network;

import com.example.myapplication.BuildConfig;

public class ApiEndpoints {
    public static final String SEND_OTP =
            BuildConfig.BASE_URL + "/send-otp";
    public static final String VERIFY_OTP = BuildConfig.BASE_URL + "/verify-otp";
}
