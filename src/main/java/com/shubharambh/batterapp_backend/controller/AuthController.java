package com.shubharambh.batterapp_backend.controller;

import com.shubharambh.batterapp_backend.emailservice.EmailService;
import com.shubharambh.batterapp_backend.entity.UserOtp;
import com.shubharambh.batterapp_backend.repository.UserOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EmailService emailService;
    @Autowired private UserOtpRepository otpRepository;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP

        // Send OTP email
       emailService.sendOtp(email, otp);

        // Save OTP to DB with 5 min expiry
        UserOtp userOtp = new UserOtp();
        userOtp.setEmail(email);
        userOtp.setOtp(otp);
        userOtp.setCreatedAt(LocalDateTime.now());
        userOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(userOtp);

        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String inputOtp = request.get("otp");

        Optional<UserOtp> latestOtpOpt = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (latestOtpOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No OTP found");
        }

        UserOtp latestOtp = latestOtpOpt.get();
        if (!latestOtp.getOtp().equals(inputOtp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        if (latestOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP expired");
        }

        // Generate fake session token (for demo)
        String sessionToken = UUID.randomUUID().toString();

        // You can save this to a `login_sessions` table if needed

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "sessionToken", sessionToken
        ));
    }
}
