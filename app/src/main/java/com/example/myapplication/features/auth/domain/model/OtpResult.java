package com.example.myapplication.features.auth.domain.model;

public class OtpResult {

    private final String resetToken;
    private final String challengeId;

    public OtpResult(
            String resetToken,
            String challengeId
    ) {
        this.resetToken = resetToken;
        this.challengeId = challengeId;
    }

    public String getResetToken() {
        return resetToken;
    }

    public String getChallengeId() {
        return challengeId;
    }
}

