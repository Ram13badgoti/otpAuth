package com.spring.boot.Example.Models;

public class OtpVerificationRequest {

    private String enteredOtp;

    // Default constructor, getters, and setters

    public OtpVerificationRequest() {
    }

    public String getEnteredOtp() {
        return enteredOtp;
    }

    public void setEnteredOtp(String enteredOtp) {
        this.enteredOtp = enteredOtp;
    }
}