package com.spring.boot.Example.controller;

import com.spring.boot.Example.Models.OtpVerificationRequest;
import com.spring.boot.Example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class homeController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String home() {

        return "home controller";
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<?> sendEmail() {

        try {
            emailService.sendEmail("srbadgoti13@gmail.com", "siyaram meena", "OTP");
            return ResponseEntity.ok("OTP SEND SUCCESSFUL......");

        } catch (Exception e) {
//            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email domain not found");
        }
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        try {
            String enteredOtp = otpVerificationRequest.getEnteredOtp();
            boolean isValid = emailService.verifyOTP(enteredOtp);
            System.out.println(isValid);
            if (isValid) {
                emailService.sendEmail("srbadgoti13@gmail.com", "siyaram meena", "Registration successful");
                return ResponseEntity.ok("OTP verification successful.....");
            } else {
                String verificationFailureReason = emailService.getVerificationFailureReason();
                System.out.println(verificationFailureReason+" reason");
                if ("mismatch".equals(verificationFailureReason)) {
                    return ResponseEntity.status(400).body("OTP mismatch");
                } else if ("timeout".equals(verificationFailureReason)) {
                    return ResponseEntity.status(408).body("OTP verification timeout");
                } else {
                    return ResponseEntity.status(400).body("OTP verification failed");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }

    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendOtp() {
        try {

            emailService.sendEmail("srbadgoti13@gmail.com", "siyaram meena", "OTP");
            return ResponseEntity.ok("OTP resent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }


}
