package com.shubharambh.batterapp_backend.repository;

import com.shubharambh.batterapp_backend.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    Optional<UserOtp> findTopByEmailOrderByCreatedAtDesc(String email);
}
