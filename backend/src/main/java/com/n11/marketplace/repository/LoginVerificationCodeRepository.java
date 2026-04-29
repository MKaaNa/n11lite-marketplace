package com.n11.marketplace.repository;

import com.n11.marketplace.entity.LoginVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginVerificationCodeRepository extends JpaRepository<LoginVerificationCode, Long> {
}
