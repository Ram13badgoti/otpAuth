package com.spring.boot.Example.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private HttpSession httpSession;

    public void sendEmail(String toEmail, String userName, String subject) {
        if (!isEmailDomainValid(toEmail)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email domain not found");
        }

        String otp = generateOTP();

        long expirationTimeMillis = System.currentTimeMillis() + (5 * 60 * 1000); // 5 minutes
        httpSession.setAttribute("otp", otp);
        httpSession.setAttribute("otpExpirationTime", expirationTimeMillis);


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);

        if(subject.equals("OTP"))
        {
            mailMessage.setSubject("OTP verification");
            mailMessage.setText("Hi "+userName+", "+"your OTP is: "+otp);
        }else
        {
            mailMessage.setSubject("Registration Successful");
            mailMessage.setText("You have successful register ");

        }


        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending email", e);
        }

    }

    private boolean isEmailDomainValid(String email) {
        try {
            String[] parts = email.split("@");
            String domain = parts[1];
            InetAddress.getByName(domain);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyOTP(String enteredOTP) {

//        boolean isAlreadyUsed = (boolean) httpSession.getAttribute("otpUsed");
//        System.out.println("isALreadyUsed "+isAlreadyUsed);
//           if (isAlreadyUsed) {
//            return false;
//            }
        String storedOTP = (String) httpSession.getAttribute("otp");
        Long expirationTimeMillis = (Long) httpSession.getAttribute("otpExpirationTime");
        System.out.println("storedOTP-  " + storedOTP.toString() + " enteredOTP - " + enteredOTP + " time- " + expirationTimeMillis + " currTime- " + System.currentTimeMillis());
        System.out.println(storedOTP.equals(enteredOTP));

        if (storedOTP != null && expirationTimeMillis != null && System.currentTimeMillis() < expirationTimeMillis) {
            boolean isOtpValid = storedOTP.equals(enteredOTP);
            if (isOtpValid) {
                httpSession.setAttribute("otpUsed", true);
            }else
            {
                httpSession.setAttribute("verificationFailureReason", "mismatch");
            }
            return isOtpValid;
        } else {

            if (storedOTP == null || System.currentTimeMillis() > expirationTimeMillis) {
                httpSession.setAttribute("verificationFailureReason", "timeout");
            } else {
                httpSession.setAttribute("verificationFailureReason", "mismatch");
            }
            return false;
        }

    }

    public String getVerificationFailureReason() {
        String resendReason = (String) httpSession.getAttribute("verificationFailureReason");
        httpSession.removeAttribute("verificationFailureReason");
        return resendReason;
    }

    public String generateOTP() {
        Random rand = new Random();
        int otpValue = 100000 + rand.nextInt(900000);
        return String.valueOf(otpValue);
    }

}