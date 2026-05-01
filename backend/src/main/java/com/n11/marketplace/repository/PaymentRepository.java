package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByOrderIdAndOrderUserEmail(Long orderId, String email);

    Optional<Payment> findByIyzicoToken(String token);
}
